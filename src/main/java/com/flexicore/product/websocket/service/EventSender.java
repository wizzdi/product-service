package com.flexicore.product.websocket.service;

import com.flexicore.annotations.Baseclassroot;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.BaseclassRepository;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.product.interfaces.IEvent;
import com.flexicore.product.model.Event;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.flexicore.security.SecurityContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

@PluginInfo(version = 1)
@ApplicationScoped
@Extension
@Component
public class EventSender implements ServicePlugin {

	private static final Logger logger= LoggerFactory.getLogger(EventSender.class);
	@Autowired
	@Baseclassroot
	private BaseclassRepository baseclassRepository;
	private static Queue<Session> sessions = new LinkedBlockingQueue<>();
	private static Cache<String,Boolean>permissionCache= CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(1000).build();

	@Async
	@EventListener
	public void sendEvent(IEvent event) {
		List<Session> toRemove = new ArrayList<>();
		for (Session session : getSessions(event)) {
			try {
				if (!session.isOpen()) {
					toRemove.add(session);
					continue;
				}
				session.getBasicRemote().sendObject(event);
			} catch (EncodeException | IOException e) {
				logger.error( "unable to send message", e);
				try {
					session.close();
				} catch (IOException e1) {
					logger.error( "unable to close session");
				}
				toRemove.add(session);
			}

		}
		sessions.removeAll(toRemove);

	}

	private Collection<Session> getSessions(IEvent event) {
		String baseclassPermission = event.getBaseclassPermissionId();
		if(baseclassPermission ==null){
			return sessions;
		}
		else{
			List<Session> toSend=new ArrayList<>();
			for (Session session : sessions) {
				SecurityContext securityContext = (SecurityContext) session
						.getUserProperties().get("securityContext");
				String key=securityContext.getUser().getId()+"|"+securityContext.getTenants().stream().map(f->f.getId()).sorted(Comparator.comparing(f->f)).collect(Collectors.joining(","))+"|"+baseclassPermission;
				try {
					Boolean val=permissionCache.get(key, () -> baseclassRepository.getByIdOrNull(baseclassPermission,Baseclass.class,null,securityContext)!=null);
					if(val!=null && val){
						toSend.add(session);
					}
				} catch (ExecutionException e) {
					logger.error("failed getting protected session for baseclass "+baseclassPermission);
				}

			}
			return toSend;
		}
	}

	public static void registerUISession(Session session) {
		sessions.add(session);
	}

	public static void unregisterSession(Session session) {
		sessions.remove(session);
	}

}
