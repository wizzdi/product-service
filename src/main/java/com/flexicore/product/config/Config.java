package com.flexicore.product.config;

import com.flexicore.annotations.InjectProperties;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.CrossLoaderResolver;
import com.flexicore.interfaces.InitPlugin;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Street;
import com.flexicore.product.containers.request.FlexiCoreGatewayCreate;
import com.flexicore.product.containers.request.GatewayCreate;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.iot.request.FlexiCoreGatewayCreateParameters;
import com.flexicore.product.model.*;
import com.flexicore.service.BaseclassService;

import javax.inject.Inject;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@PluginInfo(version = 1, autoInstansiate = true)
public class Config implements InitPlugin {

    private static AtomicBoolean init = new AtomicBoolean(false);
    private static int SYNC_MAX_THREADS = 1;
    private static int SYNC_MAX_THREADS_FOR_HANDLERS = 5;
    private static long SYNCER_CHECK_INTERVAL = 1000 * 60;//every 1 min
    private static String keySetFilePath="/home/flexicore/keySet.json";


    @Inject
    @InjectProperties
    private Properties properties;

    public static String getKeySetFilePath() {
        return keySetFilePath;
    }


    @Override
    public void init() {
        if (init.compareAndSet(false, true)) {
            SYNCER_CHECK_INTERVAL = Long.parseLong(properties.getProperty("SYNCER_CHECK_INTERVAL", SYNCER_CHECK_INTERVAL + ""));
            SYNC_MAX_THREADS = Integer.parseInt(properties.getProperty("SYNC_MAX_THREADS", SYNC_MAX_THREADS + ""));
            SYNC_MAX_THREADS_FOR_HANDLERS = Integer.parseInt(properties.getProperty("SYNC_MAX_THREADS_FOR_HANDLERS", SYNC_MAX_THREADS_FOR_HANDLERS + ""));
            keySetFilePath=properties.getProperty("keySetFilePath",keySetFilePath);

            CrossLoaderResolver.registerClass(InspectEquipmentRequest.class);
            CrossLoaderResolver.registerClass(FlexiCoreGatewayCreateParameters.class);
            BaseclassService.registerFilterClass(EquipmentFiltering.class,Equipment.class);
            BaseclassService.registerFilterClass(EquipmentFiltering.class,EquipmentShort.class);
            BaseclassService.registerFilterClass(GatewayFiltering.class,Gateway.class);
            BaseclassService.registerFilterClass(ProductStatusFiltering.class,ProductStatus.class);
            BaseclassService.registerFilterClass(ProductTypeFiltering.class,ProductType.class);
            BaseclassService.registerFilterClass(GroupFiltering.class,EquipmentGroup.class);
            BaseclassService.registerFilterClass(EquipmentGroupFiltering.class,EquipmentGroupHolder.class);
            BaseclassService.registerFilterClass(NeighbourhoodFiltering.class, Neighbourhood.class);
            BaseclassService.registerFilterClass(StreetFiltering.class, Street.class);









        }
    }


    public static int getSyncMaxThreads() {
        return SYNC_MAX_THREADS;
    }

    public static int getSyncMaxThreadsForHandlers() {
        return SYNC_MAX_THREADS_FOR_HANDLERS;
    }

    public static long getSyncerCheckInterval() {
        return SYNCER_CHECK_INTERVAL;
    }
}
