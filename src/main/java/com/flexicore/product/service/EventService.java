package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Tenant;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.data.EventNoSQLRepository;
import com.flexicore.product.interfaces.IEventService;
import com.flexicore.product.model.Alert;
import com.flexicore.product.model.Event;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class EventService implements IEventService {

    @Inject
    @PluginInfo(version = 1)
    private EventNoSQLRepository repository;

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;


    @Override
    public void merge(Event event) {
        repository.merge(event);
    }

    @Override
    public void massMergeEvents(List<? extends Event> o) {
        repository.massMergeEvents(o);
    }

    @Override
    public <T extends Event> PaginationResponse<T> getAllEvents(EventFiltering eventFiltering, Class<T> c ) {

        List<T> list = repository.getAllEvents(eventFiltering,c);
        long count = repository.countAllEvents(eventFiltering);
        return new PaginationResponse<>(list, eventFiltering, count);
    }

    @Override
    public <T extends Alert> PaginationResponse<T> getAllAlerts(AlertFiltering eventFiltering, Class<T> c ) {

        List<T> list = repository.getAllAlerts(eventFiltering,c);
        long count = repository.countAllAlerts(eventFiltering);
        return new PaginationResponse<>(list, eventFiltering, count);
    }

    @Override
    public void validateFiltering(EventFiltering eventFiltering, SecurityContext securityContext) {
        List<Baseclass> baseclasses= eventFiltering.getBaseclassIds().isEmpty()?new ArrayList<>(): equipmentService.listByIds(Baseclass.class, eventFiltering.getBaseclassIds(),securityContext);
        eventFiltering.getBaseclassIds().removeAll(baseclasses.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!eventFiltering.getBaseclassIds().isEmpty()){
            throw new BadRequestException(" no baseclass with ids "+ eventFiltering.getBaseclassIds().parallelStream().collect(Collectors.joining(",")));
        }
        eventFiltering.setBaseclass(baseclasses);

        if(eventFiltering.getClazzName()!=null){
            eventFiltering.setClazz(Baseclass.getClazzbyname(eventFiltering.getClazzName()));
            if(eventFiltering.getClazz()==null){
                throw new BadRequestException("No Clazz by name "+ eventFiltering.getClazzName());
            }

        }
        List<Tenant> tenants= eventFiltering.getTenantIds().isEmpty()?new ArrayList<>(): equipmentService.listByIds(Tenant.class, eventFiltering.getTenantIds(),securityContext);
        eventFiltering.getTenantIds().removeAll(baseclasses.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!eventFiltering.getTenantIds().isEmpty()){
            throw new BadRequestException(" no tenants with ids "+ eventFiltering.getTenantIds().parallelStream().collect(Collectors.joining(",")));
        }
        eventFiltering.setTenants(tenants);
    }
}