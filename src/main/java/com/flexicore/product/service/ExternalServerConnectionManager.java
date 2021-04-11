package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.events.PluginsLoadedEvent;
import com.flexicore.iot.ExternalServer;
import com.flexicore.product.config.Config;
import com.flexicore.product.containers.request.ProductStatusCreate;
import com.flexicore.product.containers.request.ProductStatusToTypeCreate;
import com.flexicore.product.containers.request.ProductTypeCreate;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductType;
import com.flexicore.product.request.ExternalServerConnectionConfiguration;
import com.flexicore.product.request.SingleInspectJob;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.SecurityService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


@PluginInfo(version = 1)
@Extension
@Component
public class ExternalServerConnectionManager implements Plugin {

	private static final AtomicBoolean init = new AtomicBoolean(false);

	private static final LinkedBlockingQueue<ExternalServerConnectionConfiguration<?, ?>> configurations = new LinkedBlockingQueue<>();
	private boolean stop;

	private static final Logger logger= LoggerFactory.getLogger(ExternalServerConnectionManager.class);

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Autowired
	private SecurityService securityService;


	@Autowired
	@PluginInfo(version = 1)
	private ExternalServerConnectionHandler externalServerConnectionHandler;

	private static ProductStatus connected;
	private static ProductStatus disconnected;
	private static ProductType externalServerProductType;

	@EventListener
	public void init(ContextRefreshedEvent e) {
		if (init.compareAndSet(false, true)) {

			SecurityContext adminUserSecurityContext = securityService
					.getAdminUserSecurityContext();
			connected = equipmentService.createProductStatus(
					new ProductStatusCreate().setName("Connected")
							.setDescription("Connected"),
					adminUserSecurityContext);
			disconnected = equipmentService.createProductStatus(
					new ProductStatusCreate().setName("Disconnected")
							.setDescription("Disconnected"),
					adminUserSecurityContext);

			externalServerProductType = equipmentService.getOrCreateProductType(new ProductTypeCreate().setName("External Server").setDescription("External Server"), adminUserSecurityContext);
			equipmentService.linkProductTypeToProductStatus(new ProductStatusToTypeCreate().setProductStatus(connected).setProductType(externalServerProductType),adminUserSecurityContext);
			equipmentService.linkProductTypeToProductStatus(new ProductStatusToTypeCreate().setProductStatus(disconnected).setProductType(externalServerProductType),adminUserSecurityContext);



		}
	}

	@Async
	@EventListener
	public void init(PluginsLoadedEvent e) {

			SecurityContext adminUserSecurityContext = securityService
					.getAdminUserSecurityContext();

			ExecutorService executorService = createExecutor(
					Config.MAX_CONNECTION_MANAGER_THREADS,
					Config.MAX_CONNECTION_MANAGER_THREADS, logger, 60 * 1000);
			new Thread(new ConnectionManager(executorService,
					adminUserSecurityContext)).start();

	}

	@Async
	@EventListener
	public void onExternalServerConnectionRegistration(
			ExternalServerConnectionConfiguration<?, ?> externalServerConnectionRegistration) {
		logger.info("added external connection configuration "+externalServerConnectionRegistration);
		configurations.add(externalServerConnectionRegistration);

	}

	public static ProductStatus getConnected() {
		return connected;
	}

	public static ProductStatus getDisconnected() {
		return disconnected;
	}

	@Async
	@EventListener
	public void onSingleJobAdded(SingleInspectJob<?, ?> singleInspectJob) {
		ExternalServerConnectionConfiguration<?, ?> connectionConfiguration = configurations
				.stream()
				.filter(f -> f.getId().equals(
						singleInspectJob.getConfigurationId())).findFirst()
				.orElse(null);
		if (connectionConfiguration != null) {
			addSingleInspectJob(connectionConfiguration, singleInspectJob);
		}

	}

	private <S extends com.flexicore.iot.ExternalServer, C extends com.flexicore.product.request.ConnectionHolder<S>> void addSingleInspectJob(
			ExternalServerConnectionConfiguration<?, ?> connectionConfiguration,
			SingleInspectJob<?, ?> singleInspectJob) {
		ExternalServerConnectionConfiguration<S, C> connectionConfigurationCasted = (ExternalServerConnectionConfiguration<S, C>) connectionConfiguration;
		SingleInspectJob<S, C> singleInspectJobCasted = (SingleInspectJob<S, C>) singleInspectJob;
		connectionConfigurationCasted
				.addSingleInspectJob(singleInspectJobCasted);

	}

	class ConnectionManager implements Runnable {
		private final ExecutorService executorService;
		private final SecurityContext securityContext;

		public ConnectionManager(ExecutorService executorService,
				SecurityContext securityContext) {
			this.executorService = executorService;
			this.securityContext = securityContext;
		}

		@Override
		public void run() {
			logger.info("Generic Connection Manager Started");
			Queue<ExternalServerConnectionConfiguration<?, ?>> startedConnections = new LinkedBlockingQueue<>();
			while (!stop) {

				for (ExternalServerConnectionConfiguration<?, ?> configuration : configurations) {
					if (startedConnections.contains(configuration) || (configuration.getCache() != null && configuration.getCache().stream().noneMatch(f -> ExternalServerConnectionManager.connectionManagerShouldCheck(f, configuration.getSingleInspectJobs())))) {
						continue;
					}
					try {
						startedConnections.add(configuration);
						CompletableFuture
								.runAsync(() -> externalServerConnectionHandler.handleConfiguration(configuration, securityContext, connected, disconnected), executorService)
								.exceptionally(
										e -> { logger.error( "configuration handling ended with exception", e);return null; })
								.thenRun(() -> { startedConnections.remove(configuration);});

					} catch (Exception e) {
						logger.error( "failed calling connection manager", e);
					}
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					logger.warn( "inturrpted while waiting for next connection check", e);
					stop = true;
				}

			}
			executorService.shutdownNow();
			logger.info("Generic Connection Manager Stopped");

		}

	}

	public static <S extends com.flexicore.iot.ExternalServer, C extends com.flexicore.product.request.ConnectionHolder<S>> boolean connectionManagerShouldCheck(
			ExternalServer externalServer,
			Queue<SingleInspectJob<S, C>> singleInspectJobs) {
		return shouldInspectAll(externalServer)
				|| shouldInspectSingle(singleInspectJobs);
	}

	public static <S extends ExternalServer, C extends com.flexicore.product.request.ConnectionHolder<S>> boolean shouldInspectSingle(
			Queue<SingleInspectJob<S, C>> singleInspectJobs) {
		OffsetDateTime now = OffsetDateTime.now();
		return singleInspectJobs.stream().anyMatch(
				f -> f.getTimeToInspect().isBefore(now));
	}

	public static boolean shouldInspectAll(ExternalServer externalServer) {
		return externalServer.getLastInspectAttempt() == null
				|| ((System.currentTimeMillis() - externalServer
						.getLastInspectAttempt()
						.toInstant().toEpochMilli()) > externalServer
						.getInspectIntervalMs());
	}

	public static ExecutorService createExecutor(int maximumAcceptableThreads,
			int nParallelThreads, Logger logger, int keepAliveTimeMS) {
		BasicThreadFactory factory = new BasicThreadFactory.Builder()
				.namingPattern("connection-manager-pool-" + 1 + "-thread-%d")
				.build();
		final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(
				maximumAcceptableThreads);
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
				nParallelThreads, nParallelThreads, keepAliveTimeMS,
				TimeUnit.MILLISECONDS, queue, factory);
		// by default (unfortunately) the ThreadPoolExecutor will throw an
		// exception
		// when you submit the more than maximumAcceptableThreads job, to have
		// it block you do:
		threadPool.setRejectedExecutionHandler((r, executor) -> {
			// this will block if the queue is full
				try {
					executor.getQueue().put(r);
				} catch (InterruptedException e) {
					logger.error(
							"error while adding process to queue:", e);
					// keep the interrupt status
				Thread.currentThread().interrupt();
			}
		});
		return threadPool;
	}

	public static ProductType getExternalServerProductType() {
		return externalServerProductType;
	}
}
