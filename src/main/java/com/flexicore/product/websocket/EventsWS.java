package com.flexicore.product.websocket;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Update;
import com.flexicore.annotations.rest.Write;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.WebSocketPlugin;
import com.flexicore.product.websocket.encoders.EventsWSMessageEncoder;
import com.flexicore.product.websocket.service.EventSender;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Logger;

/**
 * Created by Asaf on 31/08/2016.
 */
@ServerEndpoint(value = "/eventsWS/{authenticationKey}",
        encoders = {EventsWSMessageEncoder.class})
@Interceptors({SecurityImposer.class})
@PluginInfo(version = 1)
public class EventsWS implements WebSocketPlugin {

    @Inject
    private Logger logger;




    @OnOpen
    @Write

    public void open(@PathParam("authenticationKey") String authenticationKey, Session session) {
        SecurityContext securityContext = (SecurityContext) session.getUserProperties().get("securityContext");

        logger.info("Opening:" + session.getId());
        EventSender.registerUISession(session);

    }

    @OnClose
    @Update
    public void close(@PathParam("authenticationKey") String authenticationKey, CloseReason c, Session session) {
        SecurityContext securityContext = (SecurityContext) session.getUserProperties().get("securityContext");

        logger.info("Closing:" + session.getId());
        EventSender.unregisterSession(session);


    }


}
