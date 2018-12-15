package com.flexicore.product.iot.server;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Update;
import com.flexicore.annotations.rest.Write;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.WebSocketPlugin;
import com.flexicore.product.iot.encoders.FlexiCoreIOTRequestMessageDecoder;
import com.flexicore.product.iot.encoders.FlexiCoreIOTResponseMessageEncoder;
import com.flexicore.product.iot.request.FlexiCoreIOTRequest;
import com.flexicore.product.service.IOTService;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Asaf on 31/08/2016.
 */
@ServerEndpoint(value = "/iotWS/{authenticationKey}",
        encoders = {FlexiCoreIOTResponseMessageEncoder.class},
        decoders ={FlexiCoreIOTRequestMessageDecoder.class})
@Interceptors({SecurityImposer.class})
@PluginInfo(version = 1)
public class FlexiCoreIOTWSServer implements WebSocketPlugin {

    @Inject
    private Logger logger;


    @OnMessage
    @Update
    public void receiveMessage(@PathParam("authenticationKey") String authenticationKey, FlexiCoreIOTRequest message, Session session) {
        SecurityContext securityContext= (SecurityContext) session.getUserProperties().get("securityContext");

        logger.info("Received : " + message + ", session:" + session.getId());
        IOTService.onMessage(message.setSessionReceivedFrom(session).setSecurityContext(securityContext));
    }

    @OnOpen
    @Write

    public void open(@PathParam("authenticationKey") String authenticationKey, Session session) {
        logger.info("Opening:" + session.getId());

        IOTService.registerSession(session);
    }

    @OnClose
    @Update
    public void close(Session session, CloseReason c) {
        logger.info("Closing:" + session.getId());
        IOTService.unregisterSession(session);

    }


}
