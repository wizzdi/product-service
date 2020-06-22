package com.flexicore.product.websocket.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.model.Event;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

@PluginInfo(version = 1)
@ApplicationScoped
@Extension
@Component
public class EventSender implements ServicePlugin {

	@Autowired
	private Logger logger;
	private static Queue<Session> sessions = new LinkedBlockingQueue<>();

	@EventListener
	public void sendEvent(Event event) {
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
