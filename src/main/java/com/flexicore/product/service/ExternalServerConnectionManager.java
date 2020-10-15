package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.events.PluginsLoadedEvent;
import com.flexicore.interfaces.ServicePlugin;
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
import com.flexicore.service.PluginService;
import com.flexicore.service.SecurityService;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pf4j.Extension;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

@PluginInfo(version = 1)
@ApplicationScoped
@Extension
@Component
public class ExternalServerConnectionManager implements ServicePlugin {

	private static final AtomicBoolean init = new AtomicBoolean(false);

	private static final LinkedBlockingQueue<ExternalServerConnectionConfiguration<?, ?>> configurations = new LinkedBlockingQueue<>();
	private boolean stop;

	@Autowired
	private Logger logger;

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private PluginService pluginService;

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

	@EventListener
	public void onExternalServerConnectionRegistration(
			ExternalServerConnectionConfiguration<?, ?> externalServerConnectionRegistration) {
		configurations.add(externalServerConnectionRegistration);

	}

	public static ProductStatus getConnected() {
		return connected;
	}

	public static ProductStatus getDisconnected() {
		return disconnected;
	}

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
					if (startedConnections.contains(configuration)
							|| (configuration.getCache() != null && configuration
									.getCache()
									.stream()
									.noneMatch(
											f -> ExternalServerConnectionManager
													.connectionManagerShouldCheck(
															f,
															configuration
																	.getSingleInspectJobs())))) {
						continue;
					}
					try {
						startedConnections.add(configuration);
						ExternalServerConnectionHandler connectionHandler = pluginService
								.instansiate(
										ExternalServerConnectionHandler.class,
										null);
						CompletableFuture
								.runAsync(
										() -> connectionHandler.handleConfiguration(
												configuration, securityContext,
												connected, disconnected),
										executorService)
								.exceptionally(
										e -> {
											logger.log(
													Level.SEVERE,
													"configuration handling ended with exception",
													e);
											return null;
										})
								.thenRun(
										() -> {
											startedConnections
													.remove(configuration);
											pluginService
													.cleanUpInstance(connectionHandler);
										});

					} catch (Exception e) {
						logger.log(Level.SEVERE,
								"failed calling connection manager", e);
					}
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					logger.log(
							Level.WARNING,
							"inturrpted while waiting for next connection check",
							e);
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
		LocalDateTime now = LocalDateTime.now();
		return singleInspectJobs.stream().anyMatch(
				f -> f.getTimeToInspect().isBefore(now));
	}

	public static boolean shouldInspectAll(ExternalServer externalServer) {
		return externalServer.getLastInspectAttempt() == null
				|| ((System.currentTimeMillis() - externalServer
						.getLastInspectAttempt().atZone(ZoneId.systemDefault())
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
					logger.log(Level.SEVERE,
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
