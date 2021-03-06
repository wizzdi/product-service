package com.flexicore.product.config;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.CrossLoaderResolver;
import com.flexicore.events.PluginsLoadedEvent;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.iot.ExternalServer;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Street;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.containers.request.ExternalServerFiltering;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.interfaces.IEventService;
import com.flexicore.product.model.*;
import com.flexicore.product.request.EquipmentLocationFiltering;
import com.flexicore.product.request.UpdateEquipmentParameters;
import com.flexicore.product.response.EquipmentLocationContainer;
import com.flexicore.service.BaseclassService;
import com.flexicore.utils.InheritanceUtils;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.pf4j.Extension;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@PluginInfo(version = 1)
@Extension
@Component
public class Config implements Plugin {


	private static AtomicBoolean init = new AtomicBoolean(false);
	public static int MAX_CONNECTION_MANAGER_THREADS = 5;
	private static String keySetFilePath = "/home/flexicore/keySet.json";

	@Autowired
	private Environment properties;

	public static String getKeySetFilePath() {
		return keySetFilePath;
	}

	@EventListener
	@Order(10)
	public void init(PluginsLoadedEvent e) {
		if (init.compareAndSet(false, true)) {
			MAX_CONNECTION_MANAGER_THREADS = Integer.parseInt(properties
					.getProperty("MAX_CONNECTION_MANAGER_THREADS",
							MAX_CONNECTION_MANAGER_THREADS + ""));
			keySetFilePath = properties.getProperty("keySetFilePath",
					keySetFilePath);

			CrossLoaderResolver.registerClass(InspectEquipmentRequest.class);
			CrossLoaderResolver.registerClass(UpdateEquipmentParameters.class);
			BaseclassService.registerFilterClass(EquipmentFiltering.class,
					Equipment.class);
			BaseclassService.registerFilterClass(EquipmentFiltering.class,
					EquipmentShort.class);
			BaseclassService.registerFilterClass(GatewayFiltering.class,
					Gateway.class);
			BaseclassService.registerFilterClass(ProductStatusFiltering.class,
					ProductStatus.class);
			BaseclassService.registerFilterClass(ProductTypeFiltering.class,
					ProductType.class);
			BaseclassService.registerFilterClass(GroupFiltering.class,
					EquipmentGroup.class);
			BaseclassService.registerFilterClass(EquipmentGroupFiltering.class,
					EquipmentGroupHolder.class);
			BaseclassService.registerFilterClass(EventFiltering.class,
					Event.class);
			BaseclassService.registerFilterClass(EquipmentLocationFiltering.class,
					EquipmentLocation.class);
			BaseclassService.registerFilterClass(EquipmentLocationFiltering.class,
					EquipmentLocationContainer.class);
			BaseclassService.registerFilterClass(
					FilteringInformationHolder.class, EquipmentFiltering.class);

			CrossLoaderResolver.registerClass(EventFiltering.class);
			InheritanceUtils.registerClass(EquipmentByStatusEvent.class);
			IEventService.addClassForMongoCodec(EquipmentByStatusEvent.class);
			IEventService.addClassForMongoCodec(EquipmentByStatusEntry.class);
			BaseclassService.registerFilterClass(ExternalServerFiltering.class,
					ExternalServer.class);

		}
	}

}
