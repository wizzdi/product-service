package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Tenant;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.data.AlertNoSQLRepository;
import com.flexicore.product.interfaces.IAlertService;
import com.flexicore.product.model.Alert;
import com.flexicore.product.rest.AlertRESTService;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class AlertService implements IAlertService {

    @Inject
    @PluginInfo(version = 1)
    private AlertNoSQLRepository repository;

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;


    @Override
    public void merge(Alert alert) {
        repository.merge(alert);
    }

    @Override
    public void massMergeAlerts(List<Alert> o) {
        repository.massMergeAlerts(o);
    }

    @Override
    public <T extends Alert> PaginationResponse<T> getAllAlerts(AlertFiltering alertFiltering,Class<T> c ) {

        List<T> list = repository.getAllAlerts(alertFiltering,c);
        long count = repository.countAllAlerts(alertFiltering);
        return new PaginationResponse<>(list, alertFiltering, count);
    }

    @Override
    public void validateFiltering(AlertFiltering alertFiltering,  SecurityContext securityContext) {
        List<Baseclass> baseclasses=alertFiltering.getBaseclassIds().isEmpty()?new ArrayList<>(): equipmentService.listByIds(Baseclass.class,alertFiltering.getBaseclassIds(),securityContext);
        alertFiltering.getBaseclassIds().removeAll(baseclasses.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!alertFiltering.getBaseclassIds().isEmpty()){
            throw new BadRequestException(" no baseclass with ids "+alertFiltering.getBaseclassIds().parallelStream().collect(Collectors.joining(",")));
        }
        alertFiltering.setBaseclass(baseclasses);

        if(alertFiltering.getClazzName()!=null){
            alertFiltering.setClazz(Baseclass.getClazzbyname(alertFiltering.getClazzName()));
            if(alertFiltering.getClazz()==null){
                throw new BadRequestException("No Clazz by name "+alertFiltering.getClazzName());
            }

        }
        List<Tenant> tenants=alertFiltering.getTenantIds().isEmpty()?new ArrayList<>(): equipmentService.listByIds(Tenant.class,alertFiltering.getTenantIds(),securityContext);
        alertFiltering.getTenantIds().removeAll(baseclasses.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!alertFiltering.getTenantIds().isEmpty()){
            throw new BadRequestException(" no tenants with ids "+alertFiltering.getTenantIds().parallelStream().collect(Collectors.joining(",")));
        }
        alertFiltering.setTenants(tenants);
    }
}
