package com.flexicore.product.websocket;

import com.flexicore.annotations.Protected;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Update;
import com.flexicore.annotations.rest.Write;
import com.flexicore.interfaces.WebSocketPlugin;
import com.flexicore.product.websocket.encoders.EventsWSMessageEncoder;
import com.flexicore.product.websocket.service.EventSender;
import com.flexicore.security.SecurityContext;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Logger;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Asaf on 31/08/2016.
 */
@ServerEndpoint(value = "/FlexiCore/eventsWS/{authenticationKey}", encoders = {EventsWSMessageEncoder.class})
@Protected
@PluginInfo(version = 1)
@Extension
@Component
public class EventsWS implements WebSocketPlugin {

	@Autowired
	private Logger logger;

	@OnOpen
	@Write
	public void open(@PathParam("authenticationKey") String authenticationKey,
			Session session) {
		SecurityContext securityContext = (SecurityContext) session
				.getUserProperties().get("securityContext");

		logger.info("Opening:" + session.getId());
		EventSender.registerUISession(session);

	}

	@OnClose
	@Update
	public void close(@PathParam("authenticationKey") String authenticationKey,
			CloseReason c, Session session) {
		SecurityContext securityContext = (SecurityContext) session
				.getUserProperties().get("securityContext");

		logger.info("Closing:" + session.getId());
		EventSender.unregisterSession(session);

	}

}
