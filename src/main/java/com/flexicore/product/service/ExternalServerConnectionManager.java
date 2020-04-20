package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.InitPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.iot.ExternalServer;
import com.flexicore.product.config.Config;
import com.flexicore.product.containers.request.ProductStatusCreate;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentFiltering;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductToStatus;
import com.flexicore.product.request.ConnectionHolder;
import com.flexicore.product.request.ExternalServerConnectionConfiguration;
import com.flexicore.product.request.ProductToStatusFilter;
import com.flexicore.product.response.GenericInspectResponse;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.SecurityService;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private ExternalServerService externalServerService;

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Inject
    @PluginInfo(version = 1)
    private ProductToStatusService productToStatusService;

    @Inject
    private SecurityService securityService;

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
                        executorService.execute(() -> handleConfiguration(configuration, securityContext));
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
                }

            }
            logger.info("Generic Connection Manager Stopped");

        }
    }

    private <S extends ExternalServer, C extends ConnectionHolder<S>> void handleConfiguration(
            ExternalServerConnectionConfiguration<?, ?> configuration, SecurityContext securityContext) {
        ExternalServerConnectionConfiguration<S, C> configurationCasted = (ExternalServerConnectionConfiguration<S, C>) configuration;
        if (configurationCasted.getCache() == null || ((System.currentTimeMillis() - configurationCasted.getLastCacheRefresh()) > 60000)) {
            configurationCasted.setCache(configurationCasted.getOnRefresh().get());

        }
        for (S externalServer : configurationCasted.getCache()) {
            if (externalServer.getLastInspectAttempt() == null || ((System.currentTimeMillis() - externalServer.getLastInspectAttempt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) > externalServer.getInspectIntervalMs())) {
                logger.info("Starting connection handling for "+externalServer.getName()+" ("+externalServer.getId()+")");
                List<Object> toMerge = new ArrayList<>();
                List<Equipment> connectedEquipment = new ArrayList<>();
                List<Equipment> disconnectedEquipment = new ArrayList<>();
                boolean success=false;
                try {
                    String id = externalServer.getId();
                    C connectionHolder = configurationCasted.getConnectionHolders().get(id);
                    if (connectionHolder == null) {
                        logger.info("Getting Connection Holder for "+externalServer.getName()+" ("+externalServer.getId()+")");

                        connectionHolder = configurationCasted.getOnConnect().apply(externalServer);
                        if (connectionHolder == null) {
                            logger.warning("Failed Getting Connection Holder for "+externalServer.getName()+" ("+externalServer.getId()+")");
                            continue;
                        }
                        configurationCasted.getConnectionHolders().put(id, connectionHolder);
                        if (connectionHolder.getSecurityContext() == null) {
                            connectionHolder.setSecurityContext(securityContext);
                        }
                    }
                    logger.info("Starting Inspect for "+externalServer.getName()+" ("+externalServer.getId()+")");
                    GenericInspectResponse genericInspectResponse = configurationCasted.getOnInspect().apply(connectionHolder);
                    logger.info("Inspect ended with"+genericInspectResponse+" for "+externalServer.getName()+" ("+externalServer.getId()+")");

                    connectedEquipment = genericInspectResponse.getConnectedEquipment();
                    success = genericInspectResponse.isSuccess();

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "failed inspecting server " + externalServer.getName() + " (" + externalServer.getId() + ")", e);
                } finally {
                    LocalDateTime inspectTime = LocalDateTime.now();
                    externalServer.setLastInspectAttempt(inspectTime);
                    if (success) {
                        externalServer.setLastSuccessfulInspect(inspectTime);
                        connectedEquipment.add(externalServer);
                    } else {
                        disconnectedEquipment.add(externalServer);
                    }
                    toMerge.add(externalServer);
                    disconnectedEquipment.addAll(equipmentService.listAllEquipments(Equipment.class, new EquipmentFiltering().setExternalServers(Collections.singletonList(externalServer)).setExcludingIds(connectedEquipment.stream().map(f -> f.getId()).collect(Collectors.toSet())), securityContext));

                    Map<String, List<ProductToStatus>> allStatusesToSetConnect = connectedEquipment.isEmpty() ? new HashMap<>() : productToStatusService.listAllProductToStatus(new ProductToStatusFilter().setProducts(connectedEquipment).setStatuses(Arrays.asList(connected, disconnected)), securityContext).stream().collect(Collectors.groupingBy(f -> f.getLeftside().getId()));
                    for (Equipment equipment : connectedEquipment) {
                        List<ProductToStatus> statuses = allStatusesToSetConnect.getOrDefault(equipment.getId(), new ArrayList<>());
                        equipmentService.updateProductStatus(equipment, statuses, securityContext, toMerge, connected);
                    }

                    Map<String, List<ProductToStatus>> allStatusesToSetDisconnect = disconnectedEquipment.isEmpty() ? new HashMap<>() : productToStatusService.listAllProductToStatus(new ProductToStatusFilter().setProducts(disconnectedEquipment).setStatuses(Arrays.asList(connected, disconnected)), securityContext).stream().collect(Collectors.groupingBy(f -> f.getLeftside().getId()));
                    for (Equipment equipment : disconnectedEquipment) {
                        List<ProductToStatus> statuses = allStatusesToSetDisconnect.getOrDefault(equipment.getId(), new ArrayList<>());
                        equipmentService.updateProductStatus(equipment, statuses, securityContext, toMerge, disconnected);
                    }


                    externalServerService.massMerge(toMerge);
                }
            }

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
