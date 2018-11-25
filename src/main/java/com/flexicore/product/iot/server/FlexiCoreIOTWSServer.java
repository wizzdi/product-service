package com.flexicore.product.iot.server;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.WebSocketPlugin;
import com.flexicore.product.iot.encoders.FlexiCoreIOTRequestMessageDecoder;
import com.flexicore.product.iot.encoders.FlexiCoreIOTResponseMessageEncoder;
import com.flexicore.product.iot.request.FlexiCoreIOTRequest;
import com.flexicore.product.service.IOTService;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Asaf on 31/08/2016.
 */
@ServerEndpoint(value = "/iotWS",
        encoders = {FlexiCoreIOTResponseMessageEncoder.class},
        decoders ={FlexiCoreIOTRequestMessageDecoder.class})
@PluginInfo(version = 1)
public class FlexiCoreIOTWSServer implements WebSocketPlugin {

    @Inject
    private Logger logger;


    @OnMessage
    public void receiveMessage(FlexiCoreIOTRequest message, Session session) {
        logger.info("Received : " + message + ", session:" + session.getId());
        IOTService.onMessage(message);
    }

    @OnOpen
    public void open(Session session) {
        logger.info("Opening:" + session.getId());

        IOTService.registerSession(session);
    }

    @OnClose
    public void close(Session session, CloseReason c) {
        logger.info("Closing:" + session.getId());
        IOTService.unregisterSession(session);

    }


}
