package com.flexicore.product.service;

import ch.hsr.geohash.GeoHash;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.*;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Street;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import com.flexicore.product.data.EquipmentRepository;
import com.flexicore.product.interfaces.IEquipmentService;
import com.flexicore.product.model.*;
import com.flexicore.request.GetClassInfo;
import com.flexicore.security.RunningUser;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.*;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public PaginationResponse<Gateway> getAllGateways(GatewayFiltering filtering, SecurityContext securityContext) {
        List<Gateway> list = equipmentRepository.getAllGateways(filtering, securityContext);
        long total = equipmentRepository.countAllGateways(filtering, securityContext);
        return new PaginationResponse<>(list, filtering, total);
    }

    public <T extends Equipment> long countAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        return equipmentRepository.countAllEquipments(c, filtering, securityContext);
    }

    @Override
    public <T extends Equipment> PaginationResponse<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c, EquipmentGroupFiltering filtering, SecurityContext securityContext) {
        filtering.setPageSize(null).setCurrentPage(null);
        List<EquipmentGroupHolder> l = equipmentRepository.getAllEquipmentsGrouped(c, filtering, securityContext);
        return new PaginationResponse<>(l, filtering, l.size());
    }

    @Override
    public <T extends Equipment> T createEquipment(Class<T> c, EquipmentCreate equipmentCreate, SecurityContext securityContext) {
        T equipment = Baseclass.createUnckehcked(c, equipmentCreate.getName(), securityContext);
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
            equipmentRepository.merge(productType);
        }
        return productType;
    }

    @Override
    public ProductStatus getOrCreateProductStatus(ProductStatusCreate productStatusCreate, SecurityContext securityContext) {
        ProductStatus productType = equipmentRepository.getFirstByName(productStatusCreate.getName(), ProductStatus.class, null, securityContext);
        if (productType == null) {
            productType = createProductStatus(productStatusCreate, securityContext);
            equipmentRepository.merge(productType);
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
        ProductTypeToProductStatus productTypeToProductStatus = ProductTypeToProductStatus.s().CreateUnchecked("link", securityContext);
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
        ProductToStatus productToStatus = ProductToStatus.s().CreateUnchecked("link", securityContext);
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
        ProductStatus productStatus = ProductStatus.s().CreateUnchecked(productStatusCreate.getName(), securityContext);
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
        ProductType productType = ProductType.s().CreateUnchecked(productTypeCreate.getName(), securityContext);
        productType.Init();
        productType.setDescription(productTypeCreate.getDescription());
        return productType;
    }

    @Override
    public <T extends Equipment> Class<T> validateFiltering(EquipmentFiltering filtering, @Context SecurityContext securityContext) {
        Class<T> c = (Class<T>) Equipment.class;
        if (filtering.getResultType() != null && !filtering.getResultType().isEmpty()) {
            try {
                                Class<?> aClass = Class.forName(filtering.getResultType());
                if(Equipment.class.isAssignableFrom(aClass)){
                    c = (Class<T>) aClass;

                }

            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "unable to get class: " + filtering.getResultType());
                throw new BadRequestException("No Class with name " + filtering.getResultType());

            }
        }
        if (!filtering.getTypesToReturn().isEmpty()) {
            Set<String> canonicalNames = filtering.getTypesToReturnIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            List<Class<?>> classes = BaseclassService.listInheritingClassesWithFilter(new GetClassInfo().setClassName(c.getCanonicalName())).getList().parallelStream().filter(f -> canonicalNames.contains(f.getClazz().getCanonicalName())).map(f -> f.getClazz()).collect(Collectors.toList());
            filtering.setTypesToReturn(classes);

        }
        if (filtering.getEquipmentIds() == null || filtering.getEquipmentIds().isEmpty()) {
            Set<String> groupIds = filtering.getGroupIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            List<EquipmentGroup> groups = filtering.getGroupIds().isEmpty() ? new ArrayList<>() : groupService.listByIds(EquipmentGroup.class, groupIds, securityContext);
            groupIds.removeAll(groups.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
            if (!groupIds.isEmpty()) {
                throw new BadRequestException("could not find groups with ids " + filtering.getGroupIds().parallelStream().map(f -> f.getId()).collect(Collectors.joining(",")));
            }
            filtering.setEquipmentGroups(groups);
            ProductType productType = filtering.getProductTypeId() != null && filtering.getProductTypeId().getId() != null ? getByIdOrNull(filtering.getProductTypeId().getId(), ProductType.class, null, securityContext) : null;
            if (filtering.getProductTypeId() != null && productType == null) {
                throw new BadRequestException("No Product type with id " + filtering.getProductTypeId());
            }
            filtering.setProductType(productType);
            Set<String> statusIds = filtering.getProductStatusIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            List<ProductStatus> status = filtering.getProductStatusIds().isEmpty() ? new ArrayList<>() : listByIds(ProductStatus.class, statusIds, securityContext);
            statusIds.removeAll(status.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
            if (!statusIds.isEmpty()) {
                throw new BadRequestException("could not find status with ids " + filtering.getProductStatusIds().parallelStream().map(f -> f.getId()).collect(Collectors.joining(",")));
            }
            filtering.setProductStatusList(status);
        }

        if(filtering.getNeighbourhoodIds()!=null && !filtering.getNeighbourhoodIds().isEmpty()){
            Set<String> ids=filtering.getNeighbourhoodIds().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            List<Neighbourhood> neighbourhoods=getNeighbourhoods(ids,securityContext);
            ids.removeAll(neighbourhoods.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
            if(!ids.isEmpty()){
                throw new BadRequestException("No Neighbourhood with ids "+ids);
            }
            filtering.setNeighbourhoods(neighbourhoods);
        }

        if(filtering.getStreetIds()!=null && !filtering.getStreetIds().isEmpty()){
            Set<String> ids=filtering.getStreetIds().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            List<Street> streets=getStreets(ids,securityContext);
            ids.removeAll(streets.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
            if(!ids.isEmpty()){
                throw new BadRequestException("No Streets with ids "+ids);
            }
            filtering.setStreets(streets);
        }
        if(filtering.getGatewayIds()!=null && !filtering.getGatewayIds().isEmpty()){
            Set<String> ids=filtering.getGatewayIds().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            List<Gateway> gateways=getGateways(ids,securityContext);
            ids.removeAll(gateways.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
            if(!ids.isEmpty()){
                throw new BadRequestException("No Gateways with ids "+ids);
            }
            filtering.setGateways(gateways);
        }

        return c;
    }

    private List<Street> getStreets(Set<String> ids, SecurityContext securityContext) {
        return equipmentRepository.listByIds(Street.class,ids,securityContext);
    }
    private List<Gateway> getGateways(Set<String> ids, SecurityContext securityContext) {
        return equipmentRepository.listByIds(Gateway.class,ids,securityContext);
    }

    private List<Neighbourhood> getNeighbourhoods(Set<String> ids, SecurityContext securityContext) {
        return equipmentRepository.listByIds(Neighbourhood.class,ids,securityContext);
    }


    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatus(Class<T> c, EquipmentFiltering equipmentFiltering, SecurityContext securityContext) {
        return equipmentRepository.getProductGroupedByStatus(c, equipmentFiltering, securityContext);
    }




    @Override
    public List<ProductToStatus> getStatusLinks(Set<String> collect) {
        return collect.isEmpty()?new ArrayList<>():equipmentRepository.getStatusLinks(collect);
    }

    @Override
    public List<ProductToStatus> getCurrentStatusLinks(Set<String> collect) {
        return collect.isEmpty()?new ArrayList<>():equipmentRepository.getCurrentStatusLinks(collect);
    }

    public <T extends Equipment> PaginationResponse<EquipmentShort> getAllEquipmentsShort(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        List<T> list = equipmentRepository.getAllEquipments(c, filtering, securityContext);
        List<ProductToStatus> statusLinks = getCurrentStatusLinks(list.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
        Map<String,List<ProductStatus>> statusLinksMap= statusLinks.parallelStream().collect(Collectors.groupingBy(f->f.getLeftside().getId(),ConcurrentHashMap::new,Collectors.mapping(f->f.getRightside(),Collectors.toList())));


        Map<String,Map<String,String>> typeToStatusToIconMap = getAllProductTypeToStatusLinks(statusLinks.parallelStream().map(f -> f.getRightside().getId()).collect(Collectors.toSet()))
                .parallelStream().filter(f->f.getImage()!=null).collect(Collectors.groupingBy(f->f.getLeftside().getId(),Collectors.toMap(f->f.getRightside().getId(),f->f.getImage().getId(), (a, b) ->a)));

//        Map<String,Map<String,String>> typeToStatusToIconMap = getAllProductTypeToStatusLinks(statusLinks.parallelStream().map(f -> f.getRightside().getId()).collect(Collectors.toSet()))
//                .parallelStream().filter(f->f.getImage()!=null).collect(Collectors.groupingBy(f->f.getLeftside().getId(),Collectors.toMap(f->f.getRightside().getId(),f->f.getImage().getId())));

        long total = countAllEquipments(c, filtering, securityContext);




        return new PaginationResponse<>(list.parallelStream()
                .map(f -> new EquipmentShort(f,statusLinksMap.get(f.getId()),buildSpecificStatusIconMap(f.getProductType()!=null?typeToStatusToIconMap.get(f.getProductType().getId()):null,statusLinksMap.get(f.getId()))))
                .collect(Collectors.toList()), filtering, total);
    }

    @Override
    public Map<String,String> buildSpecificStatusIconMap(Map<String, String> typeSpecificStatusToIcon, List<ProductStatus> status){
        Map<String, String> result = new HashMap<>();
        result = (typeSpecificStatusToIcon == null || status == null) ? new HashMap<>() : status.parallelStream().filter(f -> typeSpecificStatusToIcon.get(f.getId()) != null).collect(Collectors.toMap(f -> f.getId(), f -> typeSpecificStatusToIcon.get(f.getId()), (a, b)->a));
        return result;
    }

    @Override
    public List<ProductTypeToProductStatus> getAllProductTypeToStatusLinks(Set<String> statusIds) {
        return statusIds.isEmpty()?new ArrayList<>():equipmentRepository.getAllProductTypeToStatusLinks(statusIds);
    }

    public ProductType updateProductType(UpdateProductType updateProductType, SecurityContext securityContext) {
        if (updateProductTypeNoMerge(updateProductType, securityContext)){
            equipmentRepository.merge(updateProductType.getProductType());
        }
        return updateProductType.getProductType();
    }

    public boolean updateProductTypeNoMerge(UpdateProductType updateProductType, SecurityContext securityContext) {
        boolean update = false;
        ProductType productType = updateProductType.getProductType();
        if (updateProductType.getName() != null && !updateProductType.getName().equals(productType.getName())) {
            productType.setName(updateProductType.getName());
            update = true;
        }

        if (updateProductType.getDescription() != null && !updateProductType.getDescription().equals(productType.getDescription())) {
            productType.setDescription(updateProductType.getDescription());
            update = true;
        }

        if (updateProductType.getIcon() != null && (productType.getImage() == null || !productType.getImage().getId().equals(updateProductType.getIcon().getId()))) {
            productType.setImage(updateProductType.getIcon());
            update = true;
        }

        return update;
    }


    public ProductTypeToProductStatus updateProductStatusToType(UpdateProductStatusToType updateProductStatus, SecurityContext securityContext) {
        List<ProductTypeToProductStatus> list = baselinkService.findAllBySides(ProductTypeToProductStatus.class, updateProductStatus.getProductType(), updateProductStatus.getProductStatus(), securityContext);
        if(list.isEmpty()){
            throw new BadRequestException("status "+updateProductStatus.getProductStatusId() +" and "+updateProductStatus.getProductTypeId() +" are not linked");
        }
        List<Object> toMerge=new ArrayList<>();
        for (ProductTypeToProductStatus productTypeToProductStatus : list) {
            boolean update = updateProductStatusToType(updateProductStatus, productTypeToProductStatus);
            if(update){
                toMerge.add(productTypeToProductStatus);
            }
        }
        equipmentRepository.massMerge(toMerge);
        return list.get(0);
    }

    @Override
    public void massMerge(List<?> toMerge) {
        equipmentRepository.massMerge(toMerge);
    }

    public boolean updateProductStatusToType(UpdateProductStatusToType updateProductStatus, ProductTypeToProductStatus productTypeToProductStatus) {
        boolean update =false;
        if(productTypeToProductStatus.getImage()==null||!productTypeToProductStatus.getImage().getId().equals(updateProductStatus.getIcon().getId())){
            productTypeToProductStatus.setImage(updateProductStatus.getIcon());
            update=true;

        }
        return update;
    }

    public void validateProductStatusFiltering(ProductStatusFiltering productTypeFiltering, SecurityContext securityContext) {
        ProductType productType=productTypeFiltering.getProductTypeId()!=null?getByIdOrNull(productTypeFiltering.getProductTypeId().getId(),ProductType.class,null,securityContext):null;
        if(productType==null&&productTypeFiltering.getProductTypeId()!=null){
            throw new BadRequestException("No Product Type with id "+productTypeFiltering.getProductTypeId().getId());
        }
        productTypeFiltering.setProductType(productType);
    }

    public PaginationResponse<Neighbourhood> getAllNeighbourhoods(NeighbourhoodFiltering neighbourhoodFiltering, SecurityContext securityContext) {
        QueryInformationHolder<Neighbourhood> queryInformationHolder=new QueryInformationHolder<>(neighbourhoodFiltering,Neighbourhood.class,securityContext);
        List<Neighbourhood> list=equipmentRepository.getAllFiltered(queryInformationHolder);
        long count=equipmentRepository.countAllFiltered(queryInformationHolder);
        return new PaginationResponse<>(list,neighbourhoodFiltering,count);
    }

    public PaginationResponse<Street> getAllStreets(StreetFiltering streetFiltering, SecurityContext securityContext) {
        QueryInformationHolder<Street> queryInformationHolder=new QueryInformationHolder<>(streetFiltering,Street.class,securityContext);
        List<Street> list=equipmentRepository.getAllFiltered(queryInformationHolder);
        long count=equipmentRepository.countAllFiltered(queryInformationHolder);
        return new PaginationResponse<>(list,streetFiltering,count);
    }

    public List<Equipment> getEquipmentByIds(Set<String> ids, SecurityContext securityContext) {
        return equipmentRepository.listByIds(Equipment.class,ids,securityContext);
    }

    public List<Equipment> enableEquipment(EnableEquipments enableLights, SecurityContext securityContext) {
        List<Object> toMerge=new ArrayList<>();
        for (Equipment equipment : enableLights.getEquipmentList()) {
            if(equipment.isEnable()!=enableLights.isEnable()){
                equipment.setEnable(enableLights.isEnable());
                toMerge.add(equipment);

            }
        }
        equipmentRepository.massMerge(toMerge);
        return enableLights.getEquipmentList();
    }
}
