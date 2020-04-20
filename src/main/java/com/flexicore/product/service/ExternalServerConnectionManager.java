package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.InitPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.config.Config;
import com.flexicore.product.containers.request.ProductStatusCreate;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.request.ExternalServerConnectionConfiguration;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.SecurityService;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

@PluginInfo(version = 1, autoInstansiate = true)
@ApplicationScoped
public class ExternalServerConnectionManager implements ServicePlugin, InitPlugin {

    private static final AtomicBoolean init = new AtomicBoolean(false);

    private static final LinkedBlockingQueue<ExternalServerConnectionConfiguration<?, ?>> configurations = new LinkedBlockingQueue<>();
    private boolean stop;

    @Inject
    private Logger logger;


    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;



    @Inject
    private SecurityService securityService;
    @Inject
    @PluginInfo(version = 1)
    private ExternalServerConnectionHandler connectionHandler;

    private static ProductStatus connected;
    private static ProductStatus disconnected;


    @Override
    public void init() {
        if (init.compareAndSet(false, true)) {

            SecurityContext adminUserSecurityContext = securityService.getAdminUserSecurityContext();
            connected = equipmentService.createProductStatus(new ProductStatusCreate().setName("Connected").setDescription("Connected"), adminUserSecurityContext);
            disconnected = equipmentService.createProductStatus(new ProductStatusCreate().setName("Disconnected").setDescription("Disconnected"), adminUserSecurityContext);

            ExecutorService executorService = createExecutor(Config.MAX_CONNECTION_MANAGER_THREADS, Config.MAX_CONNECTION_MANAGER_THREADS, logger, 60 * 1000);
            new Thread(new ConnectionManager(executorService, adminUserSecurityContext)).start();
        }
    }

    public void onExternalServerConnectionRegistration(
            @ObservesAsync ExternalServerConnectionConfiguration<?,?> externalServerConnectionRegistration) {
        configurations.add(externalServerConnectionRegistration);

    }

    class ConnectionManager implements Runnable {
        private final ExecutorService executorService;
        private final SecurityContext securityContext;

        public ConnectionManager(ExecutorService executorService, SecurityContext securityContext) {
            this.executorService = executorService;
            this.securityContext = securityContext;
        }

        @Override
        public void run() {
            logger.info("Generic Connection Manager Started");
            List<ExternalServerConnectionConfiguration<?, ?>> startedConnections = new ArrayList<>();
            while (!stop) {

                for (ExternalServerConnectionConfiguration<?, ?> configuration : configurations) {
                    if (startedConnections.contains(configuration)) {
                        continue;
                    }
                    try {
                        startedConnections.add(configuration);
                        executorService.execute(() -> connectionHandler.handleConfiguration(configuration, securityContext,connected,disconnected));
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "failed calling connection manager", e);
                    } finally {
                        startedConnections.remove(configuration);
                    }
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "inturrpted while waiting for next connection check", e);
                    stop=true;
                }

            }
            executorService.shutdownNow();
            logger.info("Generic Connection Manager Stopped");

        }
    }



    public static ExecutorService createExecutor(int maximumAcceptableThreads, int nParallelThreads, Logger logger, int keepAliveTimeMS) {
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("connection-manager-pool-" + 1 + "-thread-%d").build();
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(maximumAcceptableThreads);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(nParallelThreads, nParallelThreads,
                keepAliveTimeMS, TimeUnit.MILLISECONDS, queue, factory);
        // by default (unfortunately) the ThreadPoolExecutor will throw an exception
        // when you submit the more than maximumAcceptableThreads job, to have it block you do:
        threadPool.setRejectedExecutionHandler((r, executor) -> {
            // this will block if the queue is full
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "error while adding process to queue:", e);
                // keep the interrupt status
                Thread.currentThread().interrupt();
            }
        });
        return threadPool;
    }


}
