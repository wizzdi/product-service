package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.FileResource;
import com.flexicore.model.TenantIdFiltering;
import com.flexicore.model.User;
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
import com.flexicore.product.request.AckEventsRequest;
import com.flexicore.product.request.EquipmentStatusEventFilter;
import com.flexicore.product.response.AckEventsResponse;
import com.flexicore.request.TenantFilter;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassService;
import com.flexicore.service.FileResourceService;
import com.flexicore.service.MediaService;
import com.flexicore.service.TenantService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Inject
    private Logger logger;
    @Inject
    private FileResourceService fileResourceService;
    private static AtomicBoolean init=new AtomicBoolean(false);


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

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

    public FileResource exportEquipmentStatusEventsToCSV(EquipmentStatusEventFilter lightStatusEventFilter, SecurityContext securityContext) {
        List<EquipmentByStatusEvent> events = getAllEvents(lightStatusEventFilter, EquipmentByStatusEvent.class).getList();

        Map<String, String> statusMap = equipmentService.getAllProductStatus(new ProductStatusFiltering(), null).getList().parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f.getName(), (a, b) -> a));
        File file = new File(MediaService.generateNewPathForFileResource("EquipmentStatusReport", securityContext.getUser()) + ".csv");
        CSVFormat format = CSVFormat.DEFAULT.withHeader("Entry Date", "Status", "Count");
        try (CSVPrinter csvPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(file)), format)) {
            for (EquipmentByStatusEvent lightsByStatusEvent : events) {
                for (EquipmentByStatusEntry entry : lightsByStatusEvent.getEntries()) {
                    String date = lightsByStatusEvent.getEventDate().toInstant().atZone(ZoneId.systemDefault()).format(dateTimeFormatter);
                    String name = statusMap.get(entry.getProductStatus());
                    long count = entry.getTotal();
                    csvPrinter.printRecord(date, name, count);

                }
            }
            csvPrinter.flush();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "unable to create csv");
        }
        FileResource fileResource = fileResourceService.createDontPersist(file.getAbsolutePath(), securityContext);
        fileResource.setKeepUntil(LocalDateTime.now().plusMinutes(30));
        fileResourceService.merge(fileResource);
        return fileResource;
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
        Set<String> sourceBaseclassIds = eventFiltering.getBaseclassIds();
        List<Baseclass> baseclasses = sourceBaseclassIds.isEmpty() ? new ArrayList<>() : baseclassService.listByIds(Baseclass.class, sourceBaseclassIds, securityContext);
        sourceBaseclassIds.removeAll(baseclasses.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
        if (!sourceBaseclassIds.isEmpty()) {
            throw new BadRequestException(" no baseclass with ids " + sourceBaseclassIds.parallelStream().collect(Collectors.joining(",")));
        }
        eventFiltering.setBaseclass(baseclasses);

        if (eventFiltering.getClazzName() != null) {
            eventFiltering.setClazz(Baseclass.getClazzbyname(eventFiltering.getClazzName()));
            if (eventFiltering.getClazz() == null) {
                throw new BadRequestException("No Clazz by name " + eventFiltering.getClazzName());
            }
        }
        Set<String> userIds=eventFiltering.getAckedUsersIds();
        Map<String, User> userMap=userIds.isEmpty()?new HashMap<>():baseclassService.listByIds(User.class,userIds,securityContext).parallelStream().collect(Collectors.toMap(f->f.getId(),f->f));
        userIds.removeAll(userMap.keySet());
        if(!userIds.isEmpty()){
            throw new BadRequestException("No Users with ids "+userIds);
        }
        eventFiltering.setAckedUsers(new ArrayList<>(userMap.values()));

        Set<String> targetBaseclassIds = eventFiltering.getTargetBaseclassIds();
        List<Baseclass> targetBaseclasses = targetBaseclassIds.isEmpty() ? new ArrayList<>() : baseclassService.listByIds(Baseclass.class, targetBaseclassIds, securityContext);
        targetBaseclassIds.removeAll(targetBaseclasses.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
        if (!targetBaseclassIds.isEmpty()) {
            throw new BadRequestException(" no baseclass with ids " + targetBaseclassIds.parallelStream().collect(Collectors.joining(",")));
        }
        eventFiltering.setTargetBaseclass(targetBaseclasses);

    }

    @Override
    public AckEventsResponse ackEvents(AckEventsRequest ackEventsRequest, SecurityContext securityContext) {
        long updated = repository.ackEvents(ackEventsRequest, securityContext);
        return new AckEventsResponse().setUpdated(updated);
    }

    public void validate(EquipmentStatusEventFilter lightStatusEventFilter, SecurityContext securityContext) {
            if(lightStatusEventFilter.getTenantIds()==null || lightStatusEventFilter.getTenantIds().isEmpty()){
                lightStatusEventFilter.setTenantIds(securityContext.getTenants().parallelStream().map(f->new TenantIdFiltering().setId(f.getId())).collect(Collectors.toList()));
            }

    }
}
