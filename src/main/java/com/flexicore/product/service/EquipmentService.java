package com.flexicore.product.service;

import ch.hsr.geohash.GeoHash;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Job;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.User;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import com.flexicore.product.containers.response.ImportCSVResponse;
import com.flexicore.product.data.EquipmentRepository;
import com.flexicore.product.interfaces.EquipmentInvoker;
import com.flexicore.product.interfaces.IEquipmentService;
import com.flexicore.product.model.*;
import com.flexicore.product.processors.ImportCSVProcessing;
import com.flexicore.security.RunningUser;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.*;
import org.apache.commons.csv.CSVFormat;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class EquipmentService implements IEquipmentService {

    private static final String DESCRIMINATOR = "";
    @Inject
    @PluginInfo(version = 1)
    private EquipmentRepository equipmentRepository;

    @Inject
    private BaselinkService baselinkService;

    @Inject
    @PluginInfo(version = 1)
    private GroupService groupService;

    @Inject
    private UserService userService;

    @Inject
    private SecurityService securityService;

    @Inject
    private PluginService pluginService;

    @Inject
    private Logger logger;

    @Inject
    @PluginInfo(version = 1)
    private EventService eventService;

    @Inject
    private DynamicInvokersService dynamicInvokersService;

    private static Map<String, Method> setterCache = new ConcurrentHashMap<>();


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return equipmentRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return equipmentRepository.getByIdOrNull(id, c, batchString, securityContext);
    }


    @Override
    public <T extends Equipment> PaginationResponse<T> getAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        List<T> list = equipmentRepository.getAllEquipments(c, filtering, securityContext);
        long total = countAllEquipments(c, filtering, securityContext);
        return new PaginationResponse<>(list, filtering, total);
    }

    public <T extends Equipment> long countAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        return equipmentRepository.countAllEquipments(c, filtering, securityContext);
    }

    @Override
    public <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c, EquipmentGroupFiltering filtering, SecurityContext securityContext) {
        return equipmentRepository.getAllEquipmentsGrouped(c, filtering, securityContext);
    }

    @Override
    public <T extends Equipment> T createEquipment(Class<T> c, EquipmentCreate equipmentCreate, SecurityContext securityContext) {
        T equipment = Baseclass.createUnckehcked(c, equipmentCreate.getName(), securityContext.getUser());
        equipment.Init();
        updateEquipmentNoMerge(equipmentCreate, equipment);
        equipmentRepository.merge(equipment);
        return equipment;
    }

    public List<Equipment> getEquipmentToSync(LocalDateTime now) {
        return equipmentRepository.getEquipmentToSync(now);
    }

    @Override
    public EquipmentToGroup createEquipmentToGroup(LinkToGroup linkToGroup, SecurityContext securityContext) {
        return baselinkService.linkEntities(linkToGroup.getEquipment(), linkToGroup.getEquipmentGroup(), EquipmentToGroup.class);

    }


    @Override
    public boolean updateEquipmentNoMerge(EquipmentCreate equipmentCreate, Equipment equipment) {
        boolean update = false;
        if (equipmentCreate.getName() != null && !equipmentCreate.getName().equals(equipment.getName())) {
            equipment.setName(equipmentCreate.getName());
            update = true;
        }

        if (equipmentCreate.getDescription() != null && !equipmentCreate.getDescription().equals(equipment.getDescription())) {
            equipment.setDescription(equipmentCreate.getDescription());
            update = true;
        }
        if (equipmentCreate.getWarrantyExpiration() != null && !equipmentCreate.getWarrantyExpiration().equals(equipment.getWarrantyExpiration())) {
            equipment.setWarrantyExpiration(equipmentCreate.getWarrantyExpiration());
            update = true;
        }

        boolean updateLatLon = false;
        if (equipmentCreate.getLat() != null && !equipmentCreate.getLat().equals(equipment.getLat())) {
            equipment.setLat(equipmentCreate.getLat());
            update = true;
            updateLatLon = true;
        }

        if (equipmentCreate.getLon() != null && !equipmentCreate.getLon().equals(equipment.getLon())) {
            equipment.setLon(equipmentCreate.getLon());
            update = true;
            updateLatLon = true;
        }
        if (updateLatLon) {
            generateGeoHash(equipment);

        }

        if (equipmentCreate.getSerial() != null && !equipmentCreate.getSerial().equals(equipment.getSerial())) {
            equipment.setSerial(equipmentCreate.getSerial());
            update = true;
        }
        if (equipmentCreate.getProductType() != null && (equipment.getProductType() == null || !equipment.getProductType().getId().equals(equipmentCreate.getProductType().getId()))) {
            equipment.setProductType(equipmentCreate.getProductType());
            update = true;
        }

        return update;

    }

    private Method getSetterOrNull(String name) {
        try {
            return Equipment.class.getMethod(name, String.class);
        } catch (NoSuchMethodException e) {
            logger.log(Level.SEVERE, "unable to get setter", e);
        }
        return null;
    }

    private void generateGeoHash(Equipment equipment) {
        for (int i = 1; i < 13; i++) {
            String setterName = "setGeoHash" + i;
            try {
                String geoHash = GeoHash.geoHashStringWithCharacterPrecision(equipment.getLat(), equipment.getLon(), i);
                Method method = setterCache.computeIfAbsent(setterName, f -> getSetterOrNull(f));
                if (method != null) {
                    method.invoke(equipment, geoHash);

                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                logger.log(Level.SEVERE, "could not set property " + setterName + " via setter");
            }

        }
    }

    @Override
    public Equipment updateEquipment(EquipmentUpdate equipmentUpdate, SecurityContext securityContext) {
        if (updateEquipmentNoMerge(equipmentUpdate, equipmentUpdate.getEquipment())) {
            equipmentRepository.merge(equipmentUpdate.getEquipment());
        }
        return equipmentUpdate.getEquipment();
    }

    @Override
    public PaginationResponse<ProductType> getAllProductTypes(ProductTypeFiltering productTypeFiltering, SecurityContext securityContext) {
        QueryInformationHolder<ProductType> queryInformationHolder = new QueryInformationHolder<>(productTypeFiltering, ProductType.class, securityContext);
        List<ProductType> list = equipmentRepository.getAllFiltered(queryInformationHolder);
        long count = equipmentRepository.countAllFiltered(queryInformationHolder);
        return new PaginationResponse<>(list, productTypeFiltering, count);
    }

    @Override
    public PaginationResponse<ProductStatus> getAllProductStatus(ProductStatusFiltering productStatusFiltering, SecurityContext securityContext) {
        List<ProductStatus> list = equipmentRepository.getAllProductStatus(productStatusFiltering, securityContext);
        long count = equipmentRepository.countAllProductStatus(productStatusFiltering, securityContext);
        return new PaginationResponse<>(list, productStatusFiltering, count);

    }

    @Override
    public ProductType getOrCreateProductType(ProductTypeCreate productTypeCreate, SecurityContext securityContext) {
        ProductType productType = equipmentRepository.getFirstByName(productTypeCreate.getName(), ProductType.class, null, securityContext);
        if (productType == null) {
            productType = createProductType(productTypeCreate, securityContext);
        }
        return productType;
    }

    @Override
    public ProductStatus getOrCreateProductStatus(ProductStatusCreate productStatusCreate, SecurityContext securityContext) {
        ProductStatus productType = equipmentRepository.getFirstByName(productStatusCreate.getName(), ProductStatus.class, null, securityContext);
        if (productType == null) {
            productType = createProductStatus(productStatusCreate, securityContext);
        }
        return productType;
    }

    @Override
    public ProductTypeToProductStatus linkProductTypeToProductStatus(ProductStatusToTypeCreate productStatusCreate, SecurityContext securityContext) {
        List<ProductTypeToProductStatus> list = baselinkService.findAllBySides(ProductTypeToProductStatus.class, productStatusCreate.getProductType(), productStatusCreate.getProductStatus(), securityContext);
        return list.isEmpty() ? createProductTypeToProductStatusLink(productStatusCreate, securityContext) : list.get(0);
    }

    @Override
    public ProductTypeToProductStatus createProductTypeToProductStatusLink(ProductStatusToTypeCreate productStatusCreate, SecurityContext securityContext) {
        ProductTypeToProductStatus productTypeToProductStatus = ProductTypeToProductStatus.s().CreateUnchecked("link", securityContext.getUser());
        productTypeToProductStatus.Init(productStatusCreate.getProductType(), productStatusCreate.getProductStatus());
        baselinkService.merge(productTypeToProductStatus);
        return productTypeToProductStatus;
    }

    @Override
    public ProductToStatus linkProductToProductStatusNoMerge(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext) {
        List<ProductToStatus> list = getProductToStatusLinks(productStatusCreate, securityContext);
        return list.isEmpty() ? createProductToProductStatusLinkNoMerge(productStatusCreate, securityContext) : list.get(0);
    }

    @Override
    public List<ProductToStatus> getProductToStatusLinks(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext) {
        return baselinkService.findAllBySides(ProductToStatus.class, productStatusCreate.getProduct(), productStatusCreate.getProductStatus(), securityContext);
    }


    @Override
    public ProductToStatus linkProductToProductStatus(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext) {
        ProductToStatus productToStatus = linkProductToProductStatusNoMerge(productStatusCreate, securityContext);
        baselinkService.merge(productToStatus);
        return productToStatus;
    }

    @Override
    public ProductToStatus createProductToProductStatusLinkNoMerge(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext) {
        ProductToStatus productToStatus = ProductToStatus.s().CreateUnchecked("link", securityContext.getUser());
        productToStatus.Init(productStatusCreate.getProduct(), productStatusCreate.getProductStatus());
        productToStatus.setEnabled(true);
        return productToStatus;
    }

    @Override
    public ProductToStatus createProductToProductStatusLink(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext) {
        ProductToStatus productToStatus = createProductToProductStatusLinkNoMerge(productStatusCreate, securityContext);
        baselinkService.merge(productStatusCreate);
        return productToStatus;
    }

    @Override
    public ProductStatus createProductStatus(ProductStatusCreate productStatusCreate, SecurityContext securityContext) {
        ProductStatus productStatus = ProductStatus.s().CreateUnchecked(productStatusCreate.getName(), securityContext.getUser());
        productStatus.Init();
        productStatus.setDescription(productStatusCreate.getDescription());
        return productStatus;
    }

    public SecurityContext getAdminSecurityContext() {
        User user = userService.getAdminUser();
        RunningUser runningUser = userService.registerUserIntoSystem(user, LocalDateTime.now().plusYears(30));
        String adminToken = runningUser.getAuthenticationkey().getKey();
        return verifyLoggedIn(adminToken);
    }

    public SecurityContext verifyLoggedIn(String userToken) {
        String opId = Baseclass.generateUUIDFromString(Read.class.getCanonicalName());
        return securityService.getSecurityContext(userToken, null, opId);
    }

    @Override
    public List<ProductToStatus> getAvailableProductStatus(Product product, SecurityContext securityContext) {
        return baselinkService.findAllBySide(ProductToStatus.class, product, false, securityContext);
    }


    @Override
    public ProductType createProductType(ProductTypeCreate productTypeCreate, SecurityContext securityContext) {
        ProductType productType = ProductType.s().CreateUnchecked(productTypeCreate.getName(), securityContext.getUser());
        productType.Init();
        productType.setDescription(productTypeCreate.getDescription());
        return productType;
    }

    @Override
    public <T extends Equipment> Class<T> validateFiltering(EquipmentFiltering filtering, @Context SecurityContext securityContext) {
        Class<T> c = (Class<T>) Equipment.class;
        if (filtering.getCanonicalClassName() != null && !filtering.getCanonicalClassName().isEmpty()) {
            try {
                c = (Class<T>) Class.forName(filtering.getCanonicalClassName());

            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "unable to get class: " + filtering.getCanonicalClassName());
                throw new BadRequestException("No Class with name " + filtering.getCanonicalClassName());

            }
        }
        if(filtering.getEquipmentIds()==null || filtering.getEquipmentIds().isEmpty()){
            List<EquipmentGroup> groups = filtering.getGroupIds().isEmpty() ? new ArrayList<>() : groupService.listByIds(EquipmentGroup.class, filtering.getGroupIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet()), securityContext);
            filtering.getGroupIds().removeAll(groups.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
            if (!filtering.getGroupIds().isEmpty()) {
                throw new BadRequestException("could not find groups with ids " + filtering.getGroupIds().parallelStream().map(f -> f.getId()).collect(Collectors.joining(",")));
            }
            filtering.setEquipmentGroups(groups);
            ProductType productType = filtering.getProductTypeId() != null && filtering.getProductTypeId().getId() != null ? getByIdOrNull(filtering.getProductTypeId().getId(), ProductType.class, null, securityContext) : null;
            if (filtering.getProductTypeId() != null && productType == null) {
                throw new BadRequestException("No Product type with id " + filtering.getProductTypeId());
            }
            filtering.setProductType(productType);
            List<ProductStatus> status = filtering.getProductStatusIds().isEmpty() ? new ArrayList<>() : listByIds(ProductStatus.class, filtering.getProductStatusIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet()), securityContext);
            filtering.getProductStatusIds().removeAll(status.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
            if (!filtering.getProductStatusIds().isEmpty()) {
                throw new BadRequestException("could not find status with ids " + filtering.getProductStatusIds().parallelStream().map(f -> f.getId()).collect(Collectors.joining(",")));
            }
            filtering.setProductStatusList(status);
        }

        return c;
    }


    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatus(Class<T> c, EquipmentFiltering equipmentFiltering, SecurityContext securityContext) {
        return equipmentRepository.getProductGroupedByStatus(c, equipmentFiltering, securityContext);
    }

    public ImportCSVResponse importCSV(ImportCSVRequest importCSVRequest, Job job) {
        Collection<EquipmentInvoker> plugins = (Collection<EquipmentInvoker>) pluginService.getPlugins(EquipmentInvoker.class, null, null);
        Map<String, List<EquipmentInvoker>> invokers = plugins.parallelStream().collect(Collectors.groupingBy(f -> f.getCSVDescriminator()));
        ImportCSVResponse importCSVResponse = null;
        try {
            File file = new File(importCSVRequest.getFileResource().getFullPath());
            try (Reader in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                Map<String, List<Map<String,String>>> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in).getRecords().stream().map(f->f.toMap()).collect(Collectors.groupingBy(f -> f.get(importCSVRequest.getDescriminatorFieldName())));
                for (Map.Entry<String, List<Map<String,String>>> entry : records.entrySet()) {
                    try {
                        List<EquipmentInvoker> invokerList = invokers.get(entry.getKey());
                        if (invokerList == null || invokerList.isEmpty()) {
                            job.logHistoryAndLog("no invokers for discriminator " + entry.getKey()+" "+entry.getValue().size() +" entries", logger);
                            continue;
                        }
                        if (invokerList.size() > 1) {
                            job.logHistoryAndLog(invokerList.size() + " invokers for discriminator " + entry.getKey() , logger);

                        }
                        EquipmentInvoker equipmentInvoker = invokerList.get(0);
                        List<Map<String,String>> recordsForType = records.get(entry.getKey());
                        ImportCSVResponse current = equipmentInvoker.importCSV(recordsForType, importCSVRequest, job);
                        if (importCSVResponse == null) {
                            importCSVResponse = current;
                        } else {
                            importCSVResponse.add(current);
                        }
                    }
                    catch (Exception e){
                        logger.log(Level.SEVERE,"invoker "+entry.getKey() +" failed",e);
                    }


                }

            } catch (IOException e) {
                logger.log(Level.SEVERE, "unable to parse csv", e);
            }
        } finally {
            for (EquipmentInvoker plugin : plugins) {

                pluginService.cleanUpInstance(plugin);
            }
        }
        return importCSVResponse;


    }


    public Job startImportCSVJob(ImportCSVRequest importCSVRequest, SecurityContext securityContext) {
        return JobService.startJob(importCSVRequest, ImportCSVProcessing.class, null, null, securityContext);

    }
}
