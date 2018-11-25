package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.product.interfaces.IIOTService;
import com.flexicore.product.iot.client.FlexiCoreIOTWSClient;
import com.flexicore.product.iot.request.FlexiCoreIOTRequest;
import com.flexicore.product.iot.response.CloseFlexiCoreGatewayResponse;
import com.flexicore.product.iot.response.FlexiCoreIOTResponse;
import com.flexicore.product.iot.response.FlexiCoreIOTStatus;
import com.flexicore.product.iot.response.OpenFlexiCoreGatewayResponse;
import com.flexicore.product.model.FlexiCoreGateway;

import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOTService implements IIOTService {

    private static Map<String, Session> openSessions=new ConcurrentHashMap<>();
    private static Map<String, Consumer<FlexiCoreIOTResponse>> responseCallbackMap =new ConcurrentHashMap<>();
    private static Map<String, Queue<Consumer<FlexiCoreIOTRequest>>> listeners=new ConcurrentHashMap<>();
    private static final AtomicBoolean init=new AtomicBoolean(false);

    @Inject
    private Logger logger;
    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    public void init() {
        if(init.compareAndSet(false,true)){
            List<FlexiCoreGateway> gateways=equipmentService.getAllEnabledFCGateways();
            for (FlexiCoreGateway gateway : gateways) {
                try {
                    openFlexiCoreGateway(gateway);
                } catch (IOException |DeploymentException e) {
                    logger.log(Level.SEVERE,"unable to open connection to FlexiCore",e);
                }
            }
        }
    }

    public static void onMessage(FlexiCoreIOTRequest message) {
        Queue<Consumer<FlexiCoreIOTRequest>> queue=listeners.get(message.getClass().getCanonicalName());
        if(queue!=null){
            for (Consumer<FlexiCoreIOTRequest> flexiCoreIOTRequestConsumer : queue) {
                flexiCoreIOTRequestConsumer.accept(message);
            }
        }
    }

    public static void registerMessageListener(String messageTypeClassName,Consumer<FlexiCoreIOTRequest> callback){
        listeners.computeIfAbsent(messageTypeClassName,f->new LinkedBlockingQueue<>()).add(callback);
    }
    public static boolean unregisterMessageListener(String messageTypeClassName,Consumer<FlexiCoreIOTRequest> callback){
        Queue<Consumer<FlexiCoreIOTRequest>> queue=listeners.get(messageTypeClassName);
        if(queue!=null){
            return queue.remove(callback);
        }
        return false;
    }

    @Override
    public OpenFlexiCoreGatewayResponse openFlexiCoreGateway(FlexiCoreGateway flexiCoreGateway) throws IOException, DeploymentException {
        FlexiCoreIOTWSClient flexiCoreIOTWSClient=new FlexiCoreIOTWSClient(logger,responseCallbackMap);
        Session session=ContainerProvider.getWebSocketContainer().connectToServer(flexiCoreIOTWSClient, URI.create(flexiCoreGateway.getCommunicationWebSocketUrl()));
        openSessions.put(flexiCoreGateway.getId(),session);
        return new OpenFlexiCoreGatewayResponse().setCommunicationId(flexiCoreGateway.getId());
    }

    @Override
    public CloseFlexiCoreGatewayResponse closeFlexiCoreGateway(String communicationId) throws IOException {
        Session session=openSessions.get(communicationId);
        boolean closed=false;
        if(session!=null){
            session.close();
            closed=true;
        }
        return new CloseFlexiCoreGatewayResponse().setClosed(closed);

    }

    @Override
    public void executeViaFlexiCoreGateway(String communicationId, FlexiCoreIOTRequest flexiCoreExecutionRequest,Consumer<FlexiCoreIOTResponse> callback) throws IOException, EncodeException {
        Session session=openSessions.get(communicationId);
        if(session==null){
            FlexiCoreIOTResponse flexiCoreIOTResponse = new FlexiCoreIOTResponse().setFlexiCoreIOTStatus(FlexiCoreIOTStatus.FAILED);
            callback.accept(flexiCoreIOTResponse);
            return;
        }
        responseCallbackMap.put(flexiCoreExecutionRequest.getId(),callback);
        session.getBasicRemote().sendObject(flexiCoreExecutionRequest);
    }

    public static void registerSession(Session session){
        openSessions.put(session.getId(),session);
    }

    public static void unregisterSession(Session session){
        openSessions.remove(session.getId());
    }


}
