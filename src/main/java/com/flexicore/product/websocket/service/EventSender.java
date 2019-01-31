package com.flexicore.product.websocket.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.model.Event;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@PluginInfo(version = 1)
@ApplicationScoped
public class EventSender implements ServicePlugin {

    @Inject
    private Logger logger;
    private static Queue<Session> sessions = new LinkedBlockingQueue<>();


    public void sendEvent(@ObservesAsync Event event) {
        List<Session> toRemove = new ArrayList<>();
        for (Session session : sessions) {
            try {
                if (!session.isOpen()) {
                    toRemove.add(session);
                    continue;
                }
                session.getBasicRemote().sendObject(event);
            } catch (EncodeException | IOException e) {
                logger.log(Level.SEVERE, "unable to send message", e);
                try {
                    session.close();
                } catch (IOException e1) {
                    logger.log(Level.SEVERE, "unable to close session");
                }
                toRemove.add(session);
            }

        }
        sessions.removeAll(toRemove);


    }

    public static void registerUISession(Session session) {
        sessions.add(session);
    }

    public static void unregisterSession(Session session) {
        sessions.remove(session);
    }

}
