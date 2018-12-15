package com.flexicore.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.product.config.Config;
import com.flexicore.product.interfaces.IIOTService;
import com.flexicore.product.iot.client.FlexiCoreIOTWSClient;
import com.flexicore.product.iot.request.FlexiCoreIOTRequest;
import com.flexicore.product.iot.request.OpenFlexiCoreGateway;
import com.flexicore.product.iot.response.CloseFlexiCoreGatewayResponse;
import com.flexicore.product.iot.response.FlexiCoreIOTResponse;
import com.flexicore.product.iot.response.FlexiCoreIOTStatus;
import com.flexicore.product.iot.response.OpenFlexiCoreGatewayResponse;
import com.flexicore.product.model.FlexiCoreGateway;
import com.flexicore.security.AuthenticationBundle;
import com.flexicore.security.AuthenticationRequestHolder;
import com.flexicore.security.SecurityContext;
import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.config.TinkConfig;
import com.google.crypto.tink.proto.KeyTemplate;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@PluginInfo(version = 1, autoInstansiate = true)
public class IOTService implements IIOTService {

    private static final long GATEWAY_FETCH_INTEFVAL = 60 * 1000;
    private static Map<String, Session> openSessions = new ConcurrentHashMap<>();
    private static Map<String, Consumer<FlexiCoreIOTResponse>> responseCallbackMap = new ConcurrentHashMap<>();
    private static Map<String, Queue<Consumer<FlexiCoreIOTRequest>>> listeners = new ConcurrentHashMap<>();
    private static final AtomicBoolean init = new AtomicBoolean(false);

    @Inject
    private Logger logger;
    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    static {

        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Unirest.setTimeouts(5000,5000);


    }

    private IOTConnectionManager iOTConnectionManager;

    @Override
    public void init() {
        if (init.compareAndSet(false, true)) {
            EncryptionService.initEncryption(logger);
            iOTConnectionManager=new IOTConnectionManager();
            new Thread(iOTConnectionManager).start();


        }
    }

    class IOTConnectionManager implements Runnable{
        private boolean stop;

        @Override
        public void run() {
            List<FlexiCoreGateway> gateways = equipmentService.getAllEnabledFCGateways();
            long lastGatewayFetchTime=0;
            while(!stop){
                if(System.currentTimeMillis()-lastGatewayFetchTime > GATEWAY_FETCH_INTEFVAL){
                    gateways = equipmentService.getAllEnabledFCGateways();
                    lastGatewayFetchTime=System.currentTimeMillis();
                }
                for (FlexiCoreGateway gateway : gateways) {
                    Session session = openSessions.get(gateway.getId());
                    if(session==null || !session.isOpen()){
                        try {
                            openFlexiCoreGateway(gateway);
                        } catch (IOException | DeploymentException e) {
                            logger.log(Level.SEVERE, "unable to open connection to FlexiCore", e);
                        }
                    }

                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE,"interrupted while waiting for gateway connection check",e);
                }
            }
        }
    }



    public static void onMessage(FlexiCoreIOTRequest message) {
        Queue<Consumer<FlexiCoreIOTRequest>> queue = listeners.get(message.getClass().getCanonicalName());
        if (queue != null) {
            for (Consumer<FlexiCoreIOTRequest> flexiCoreIOTRequestConsumer : queue) {
                flexiCoreIOTRequestConsumer.accept(message);
            }
        }
    }

    @Override
    public <T extends FlexiCoreIOTRequest> void registerMessageListener(Class<T> c, Consumer<T> callback) {
        Queue<Consumer<FlexiCoreIOTRequest>> consumers = listeners.computeIfAbsent(c.getCanonicalName(), f -> new LinkedBlockingQueue<>());
        consumers.add((Consumer<FlexiCoreIOTRequest>) callback);
    }

    @Override
    public <T extends FlexiCoreIOTRequest> boolean unregisterMessageListener(String messageTypeClassName, Consumer<T> callback) {
        Queue<Consumer<FlexiCoreIOTRequest>> queue = listeners.get(messageTypeClassName);
        if (queue != null) {
            return queue.remove(callback);
        }
        return false;
    }

    @Override
    public OpenFlexiCoreGatewayResponse openFlexiCoreGateway(FlexiCoreGateway flexiCoreGateway) throws IOException, DeploymentException {
        String authenticationKey=loginToFlexiCoreGateway(flexiCoreGateway);
        if(authenticationKey==null){
            return null;
        }
        FlexiCoreIOTWSClient flexiCoreIOTWSClient = new FlexiCoreIOTWSClient(logger, responseCallbackMap);
        Session session = ContainerProvider.getWebSocketContainer().connectToServer(flexiCoreIOTWSClient, URI.create(flexiCoreGateway.getCommunicationWebSocketUrl()+"/"+authenticationKey));
        openSessions.put(flexiCoreGateway.getId(), session);
        return new OpenFlexiCoreGatewayResponse().setCommunicationId(flexiCoreGateway.getId());
    }

    private String loginToFlexiCoreGateway(FlexiCoreGateway flexiCoreGateway) {
        if(flexiCoreGateway.getBaseApiUrl()==null){
            logger.log(Level.SEVERE,"flexicore gateway "+flexiCoreGateway.getId() +" does not have a basepath");
            return null;
        }
        if(flexiCoreGateway.getEncryptedPassword()==null|| flexiCoreGateway.getUsername()==null){
            logger.log(Level.SEVERE,"flexicore gateway "+flexiCoreGateway.getId() +" does not have an email or password");
            return null;
        }
        if(EncryptionService.getAead()!=null){
            try {
                String email = flexiCoreGateway.getUsername();
                String password=new String(EncryptionService.getAead().decrypt(Base64.getDecoder().decode(flexiCoreGateway.getEncryptedPassword()),"test".getBytes()), StandardCharsets.UTF_8);
                AuthenticationRequestHolder authenticationRequestHolder = new AuthenticationRequestHolder()
                .setMail(email)
                .setPassword(password);
                HttpResponse<AuthenticationBundle> httpResponse=Unirest.post(flexiCoreGateway.getBaseApiUrl()+"/authentication/login")
                        .header("Content-Type","application/json")
                        .header("Accept","application/json")
                        .body(authenticationRequestHolder).asObject(AuthenticationBundle.class);

                if(httpResponse.getStatus()==200){
                    return httpResponse.getBody().getAuthenticationkey();
                }
            }
            catch (Exception e){
                logger.log(Level.SEVERE,"failed decrypting password",e);
            }
        }
        return null;

    }

    @Override
    public CloseFlexiCoreGatewayResponse closeFlexiCoreGateway(String communicationId) throws IOException {
        Session session = openSessions.get(communicationId);
        boolean closed = false;
        if (session != null) {
            session.close();
            closed = true;
        }
        return new CloseFlexiCoreGatewayResponse().setClosed(closed);

    }

    @Override
    public <T extends FlexiCoreIOTResponse> void executeViaFlexiCoreGateway(String communicationId, FlexiCoreIOTRequest flexiCoreExecutionRequest, Consumer<T> callback) throws IOException, EncodeException {
        Session session = openSessions.get(communicationId);
        if (session == null) {
            FlexiCoreIOTResponse flexiCoreIOTResponse = new FlexiCoreIOTResponse().setFlexiCoreIOTStatus(FlexiCoreIOTStatus.FAILED);
            callback.accept((T) flexiCoreIOTResponse);
            return;
        }
        responseCallbackMap.put(flexiCoreExecutionRequest.getId(), (Consumer<FlexiCoreIOTResponse>) callback);
        session.getBasicRemote().sendObject(flexiCoreExecutionRequest);
    }

    public static void registerSession(Session session) {
        openSessions.put(session.getId(), session);
    }

    public static void unregisterSession(Session session) {
        openSessions.remove(session.getId());
    }


    public void openConnectionFlexiCoreGateway(OpenFlexiCoreGateway equipmentCreate, SecurityContext securityContext) {
        try {
            openFlexiCoreGateway(equipmentCreate.getFlexiCoreGateway());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "unable to open FC gateway", e);
        }
    }
}
