package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.product.config.Config;
import com.flexicore.product.containers.request.InspectEquipmentRequest;
import com.flexicore.product.containers.response.InspectEquipmentResponse;
import com.flexicore.product.interfaces.EquipmentHandler;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentFiltering;
import com.flexicore.product.model.Event;
import com.flexicore.scheduling.interfaces.Scheduler;
import com.flexicore.scheduling.interfaces.SchedulingMethod;
import com.flexicore.scheduling.interfaces.SchedulingOperator;
import com.flexicore.scheduling.interfaces.SchedulingParameter;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.PluginService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Scheduler(displayName = "Equipment Operator" , description = "Operator for controlling Equipments")

public class EquipmentOperator implements SchedulingOperator {


    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Inject
    @PluginInfo(version = 1)
    private EventService eventService;

    @Inject
    private PluginService pluginService;

    @Inject
    private Logger logger;

    @SchedulingMethod(displayName = "Inspect equipments By Id", description = "inspect equipments by ids"
            , parameters = {
            @SchedulingParameter(displayName = "equipments Ids", description = "Comma Delimited List of equipment Ids")
    })
    public InspectEquipmentResponse inspectEquipment(ScheduleAction scheduleAction, SecurityContext securityContext) throws Exception {
        if(scheduleAction.getFilteringInformationHolder()!=null && !(scheduleAction.getFilteringInformationHolder() instanceof EquipmentFiltering)){
            throw new Exception("inspectEquipment, expects filtering to be of type EquipmentFiltering");

        }


        Map<String, Equipment> equipments = equipmentService.getAllEquipments(Equipment.class, (EquipmentFiltering) scheduleAction.getFilteringInformationHolder(),securityContext).getList().parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        Map<Class<? extends Equipment>,List<Equipment>> map=equipments.values().parallelStream().collect(Collectors.groupingBy(f->f.getClass()));
        Map<Class<? extends Equipment>,List<EquipmentHandler>> handlers=((Collection<EquipmentHandler>)pluginService.getPlugins(EquipmentHandler.class,null,null)).parallelStream().collect(Collectors.groupingBy(f->f.getHandlingClass()));
        InspectEquipmentResponse inspectEquipmentResponse=new InspectEquipmentResponse();
        try {
            for (Map.Entry<Class<? extends Equipment>, List<Equipment>> entry : map.entrySet()) {
                List<EquipmentHandler> handlerList = handlers.get(entry.getKey());
                if (handlerList == null || handlerList.isEmpty()) {
                    logger.warning("no handlers for equipment of type " + entry.getKey());
                    continue;
                }
                if (handlerList.size() > 1) {
                    logger.warning("had " + handlerList.size() + " handlers for equipment of type" + entry.getKey());
                }
                try {
                    EquipmentHandler equipmentHandler = handlerList.get(0);
                    InspectEquipmentRequest inspectEquipmentRequest = new InspectEquipmentRequest()
                            .setEquipments(entry.getValue())
                            .setMaxThreads(Math.max(1, Config.getSyncMaxThreadsForHandlers() / map.size()));
                    inspectEquipmentResponse.accumulate(equipmentHandler.inspect(inspectEquipmentRequest, securityContext));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "equipment handler failed while inspecting", e);
                    List<Event> toMerge = new ArrayList<>();
                    for (Equipment equipment : entry.getValue()) {
                        toMerge.add(eventService.createAlertInspectFailedNoMerge(equipment, e.getMessage()));
                    }
                    eventService.massMergeEvents(toMerge);
                }

            }
        }
        finally {
            for (List<EquipmentHandler> handlerList : handlers.values()) {
                for (EquipmentHandler equipmentHandler : handlerList) {
                    pluginService.cleanUpInstance(equipmentHandler);
                }
            }
        }



        return inspectEquipmentResponse;

    }

}
