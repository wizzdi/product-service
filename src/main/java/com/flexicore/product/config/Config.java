package com.flexicore.product.config;

import com.flexicore.annotations.InjectProperties;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.InitPlugin;

import javax.inject.Inject;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@PluginInfo(version = 1,autoInstansiate = true)
public class Config implements InitPlugin {

    private static AtomicBoolean init=new AtomicBoolean(false);
    private static int SYNC_MAX_THREADS=1;
    private static int SYNC_MAX_THREADS_FOR_HANDLERS=5;
    private static long SYNCER_CHECK_INTERVAL=1000*60;//every 1 min




    @Inject
    @InjectProperties
    private Properties properties;

    @Override
    public void init() {
        if(init.compareAndSet(false,true)){
            SYNCER_CHECK_INTERVAL=Long.parseLong(properties.getProperty("SYNCER_CHECK_INTERVAL",SYNCER_CHECK_INTERVAL+""));

            SYNC_MAX_THREADS=Integer.parseInt(properties.getProperty("SYNC_MAX_THREADS",SYNC_MAX_THREADS+""));
            SYNC_MAX_THREADS_FOR_HANDLERS=Integer.parseInt(properties.getProperty("SYNC_MAX_THREADS_FOR_HANDLERS",SYNC_MAX_THREADS_FOR_HANDLERS+""));




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