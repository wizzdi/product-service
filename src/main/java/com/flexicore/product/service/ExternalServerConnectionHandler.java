package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.iot.ExternalServer;
import com.flexicore.model.Baseclass;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentFiltering;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductToStatus;
import com.flexicore.product.request.ConnectionHolder;
import com.flexicore.product.request.ExternalServerConnectionConfiguration;
import com.flexicore.product.request.ProductToStatusFilter;
import com.flexicore.product.response.GenericInspectResponse;
import com.flexicore.security.SecurityContext;
import com.google.common.collect.Lists;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class ExternalServerConnectionHandler implements ServicePlugin {

    @Inject
    private Logger logger;
    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Inject
    @PluginInfo(version = 1)
    private ProductToStatusService productToStatusService;


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public <S extends ExternalServer, C extends ConnectionHolder<S>> void handleConfiguration(
            ExternalServerConnectionConfiguration<?, ?> configuration, SecurityContext securityContext, ProductStatus connected, ProductStatus disconnected) {
        ExternalServerConnectionConfiguration<S, C> configurationCasted = (ExternalServerConnectionConfiguration<S, C>) configuration;
        if (configurationCasted.getCache() == null || ((System.currentTimeMillis() - configurationCasted.getLastCacheRefresh()) > 60000)) {
            configurationCasted.setCache(configurationCasted.getOnRefresh().get());

        }
        for (S externalServer : configurationCasted.getCache()) {
            if (ExternalServerConnectionManager.connectionManagerShouldCheck(externalServer)) {
                logger.info("Starting connection handling for "+externalServer.getName()+" ("+externalServer.getId()+")");
                List<Object> toMerge = new ArrayList<>();
                List<Equipment> connectedEquipmentRaw = new ArrayList<>();
                List<Equipment> disconnectedEquipmentRaw = new ArrayList<>();
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

                    connectedEquipmentRaw = genericInspectResponse.getConnectedEquipment();
                    success = genericInspectResponse.isSuccess();
                    if(genericInspectResponse.isReconnect()){
                        configurationCasted.getConnectionHolders().remove(id);
                    }

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "failed inspecting server " + externalServer.getName() + " (" + externalServer.getId() + ")", e);
                } finally {
                    LocalDateTime inspectTime = LocalDateTime.now();
                    externalServer.setLastInspectAttempt(inspectTime);
                    if (success) {
                        externalServer.setLastSuccessfulInspect(inspectTime);
                        connectedEquipmentRaw.add(externalServer);
                    } else {
                        disconnectedEquipmentRaw.add(externalServer);
                    }
                    toMerge.add(externalServer);
                    Set<String> connectedIds=connectedEquipmentRaw.stream().map(f->f.getId()).collect(Collectors.toSet());
                    List<Equipment> c = equipmentService.listAllEquipments(Equipment.class, new EquipmentFiltering().setExternalServers(Collections.singletonList(externalServer)), securityContext).stream().filter(f->!connectedIds.contains(f.getId())).collect(Collectors.toList());
                    disconnectedEquipmentRaw.addAll(c);
                    for (List<Equipment> connectedEquipment : Lists.partition(connectedEquipmentRaw,100)) {
                        Map<String, List<ProductToStatus>> allStatusesToSetConnect = connectedEquipment.isEmpty() ? new HashMap<>() : productToStatusService.listAllProductToStatus(new ProductToStatusFilter().setProducts(connectedEquipment).setStatuses(Arrays.asList(connected, disconnected)), securityContext).stream().collect(Collectors.groupingBy(f -> f.getLeftside().getId()));
                        for (Equipment equipment : connectedEquipment) {
                            List<ProductToStatus> statuses = allStatusesToSetConnect.getOrDefault(equipment.getId(), new ArrayList<>());
                            equipmentService.updateProductStatus(equipment, statuses, securityContext, toMerge, connected);
                        }
                    }

                    for (List<Equipment> disconnectedEquipment : Lists.partition(disconnectedEquipmentRaw, 100)) {
                        Map<String, List<ProductToStatus>> allStatusesToSetDisconnect = disconnectedEquipment.isEmpty() ? new HashMap<>() : productToStatusService.listAllProductToStatus(new ProductToStatusFilter().setProducts(disconnectedEquipment).setStatuses(Arrays.asList(connected, disconnected)), securityContext).stream().collect(Collectors.groupingBy(f -> f.getLeftside().getId()));
                        for (Equipment equipment : disconnectedEquipment) {
                            List<ProductToStatus> statuses = allStatusesToSetDisconnect.getOrDefault(equipment.getId(), new ArrayList<>());
                            equipmentService.updateProductStatus(equipment, statuses, securityContext, toMerge, disconnected);
                        }
                    }


                    Map<String, Baseclass> toMergeMap=toMerge.stream().filter(f->f instanceof Baseclass).map(f->(Baseclass)f).collect(Collectors.toMap(f->f.getId(),f->f,(a,b)->a));

                    productToStatusService.massMerge(new ArrayList<>(toMergeMap.values()));
                    productToStatusService.flush();
                }
            }

        }


    }

}
