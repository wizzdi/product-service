package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Tenant;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.containers.request.CreateAggregatedReport;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.containers.response.AggregationReport;
import com.flexicore.product.containers.response.AggregationReportEntry;
import com.flexicore.product.data.EventNoSQLRepository;
import com.flexicore.product.interfaces.AlertSeverity;
import com.flexicore.product.interfaces.AlertType;
import com.flexicore.product.interfaces.IEventService;
import com.flexicore.product.model.*;
import com.flexicore.request.TenantFilter;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassService;
import com.flexicore.service.TenantService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class EventService implements IEventService {

    @Inject
    @PluginInfo(version = 1)
    private EventNoSQLRepository repository;

    @Inject
    private BaseclassService baseclassService;

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;
    @Inject
    private TenantService tenantService;


    @Override
    public void merge(Event event) {
        repository.merge(event);
    }

    @Override
    public void massMergeEvents(List<? extends Event> o) {
        repository.massMergeEvents(o);
    }

    @Override
    public <T extends Event> PaginationResponse<T> getAllEvents(EventFiltering eventFiltering, Class<T> c) {

        List<T> list = repository.getAllEvents(eventFiltering, c);
        long count = repository.countAllEvents(eventFiltering);
        return new PaginationResponse<>(list, eventFiltering, count);
    }

    private List<AggregationReportEntry> cauculateReportForDate(CreateAggregatedReport filtering, LocalDateTime localDateTime, Map<String, String> statusToName, Map<String, String> typeToName,Map<String,String> tenantToName){
        List<AggregationReportEntry> dataForDate = repository.generateReport(filtering, localDateTime);
        for (AggregationReportEntry aggregationReportEntry : dataForDate) {
            aggregationReportEntry
                    .setProductStatusName(statusToName.get(aggregationReportEntry.getProductStatusId()))
                    .setProductTypeName(aggregationReportEntry.getProductTypeId()!=null?typeToName.get(aggregationReportEntry.getProductTypeId()):null)
                    .setTenantName(aggregationReportEntry.getTenantId()!=null?tenantToName.get(aggregationReportEntry.getTenantId()):null);
        }
        return dataForDate;
    }

    public AggregationReport generateReport(SecurityContext securityContext, CreateAggregatedReport filtering) {
        Map<String,String> statusToName=equipmentService.getAllProductStatus(new ProductStatusFiltering(),securityContext).getList().parallelStream().collect(Collectors.toMap(f->f.getId(), f->f.getName(),(a,b)->a,ConcurrentHashMap::new));
        Map<String,String> typeToName=equipmentService.getAllProductTypes(new ProductTypeFiltering(),securityContext).getList().parallelStream().collect(Collectors.toMap(f->f.getId(), f->f.getName(),(a,b)->a,ConcurrentHashMap::new));
        Map<String,String> tenants=tenantService.getTenants(new TenantFilter(),securityContext).getList().parallelStream().collect(Collectors.toMap(f->f.getId(),f->f.getName()));
        Map<LocalDateTime,List<AggregationReportEntry>> map =filtering.getEndTimes().parallelStream().collect(Collectors.toMap(f->f,f->cauculateReportForDate(filtering,f,statusToName,typeToName,tenants)));
            return new AggregationReport(map);
    }

    @Override
    public <T extends Alert> PaginationResponse<T> getAllAlerts(AlertFiltering eventFiltering, Class<T> c) {

        List<T> list = repository.getAllAlerts(eventFiltering, c);
        long count = repository.countAllAlerts(eventFiltering);
        return new PaginationResponse<>(list, eventFiltering, count);
    }


    public Alert createAlertInspectFailedNoMerge(Equipment equipment, String reason) {
        return new Alert(equipment)
                .setSeverity(AlertSeverity.FATAL.ordinal() + 1)
                .setEventSubType(AlertType.INSPECT_FAILED.name())
                .setHumanReadableText(AlertType.INSPECT_FAILED.name() + " on equipment " + equipment.getId()
                        + System.lineSeparator() +
                        reason);

    }

    @Override
    public void validateFiltering(EventFiltering eventFiltering, SecurityContext securityContext) {
        List<Baseclass> baseclasses = eventFiltering.getBaseclassIds().isEmpty() ? new ArrayList<>() : baseclassService.listByIds(Baseclass.class, eventFiltering.getBaseclassIds(), securityContext);
        eventFiltering.getBaseclassIds().removeAll(baseclasses.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
        if (!eventFiltering.getBaseclassIds().isEmpty()) {
            throw new BadRequestException(" no baseclass with ids " + eventFiltering.getBaseclassIds().parallelStream().collect(Collectors.joining(",")));
        }
        eventFiltering.setBaseclass(baseclasses);

        if (eventFiltering.getClazzName() != null) {
            eventFiltering.setClazz(Baseclass.getClazzbyname(eventFiltering.getClazzName()));
            if (eventFiltering.getClazz() == null) {
                throw new BadRequestException("No Clazz by name " + eventFiltering.getClazzName());
            }

        }
    }
}
