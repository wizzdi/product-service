package com.flexicore.product.service;

import ch.hsr.geohash.GeoHash;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.rest.Read;
import com.flexicore.building.model.BuildingFloor;
import com.flexicore.building.model.Room;
import com.flexicore.data.jsoncontainers.CreatePermissionGroupLinkRequest;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.iot.ExternalServer;
import com.flexicore.model.*;
import com.flexicore.model.territories.*;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.containers.response.EquipmentSpecificTypeGroup;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import com.flexicore.product.data.EquipmentRepository;
import com.flexicore.product.data.EventNoSQLRepository;
import com.flexicore.product.interfaces.IEquipmentService;
import com.flexicore.product.model.*;
import com.flexicore.product.request.*;
import com.flexicore.product.response.ProductStatusEntry;
import com.flexicore.product.response.TypeHolder;
import com.flexicore.request.GetClassInfo;
import com.flexicore.security.RunningUser;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.impl.*;
import com.flexicore.territories.request.NeighbourhoodFilter;
import com.flexicore.territories.request.StreetFilter;
import com.flexicore.territories.service.NeighbourhoodService;
import com.flexicore.territories.service.StreetService;
import com.flexicore.utils.InheritanceUtils;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class EquipmentService implements IEquipmentService {

    private static final String DESCRIMINATOR = "";
    @PluginInfo(version = 1)
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private NeighbourhoodService neighbourhoodService;

    @Autowired
    private StreetService streetService;

    @Autowired
    private BaselinkService baselinkService;

    @Autowired
    private BaseclassNewService baseclassNewService;

    @PluginInfo(version = 1)
    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SecuredBasicRepository securedBasicRepository;


    private static final Logger logger = LoggerFactory.getLogger(EquipmentService.class);

    @Autowired
    private PermissionGroupService permissionGroupService;

    @PluginInfo(version = 1)
    @Autowired
    private StatusLinkToImageService statusLinkToImageService;

    @PluginInfo(version = 1)
    @Autowired
    private EventNoSQLRepository repository;

    @Autowired
    private EncryptionService encryptionService;

    private static Map<String, Method> setterCache = new ConcurrentHashMap<>();
    private static AtomicBoolean init = new AtomicBoolean(false);
    private static ProductType gatewayProductType;
    private static ProductStatus onProductStatus;
    private static ProductStatus offProductStatus;
    private static ProductStatus commErrorProductStatus;
    private static ProductType buildingProductType;
    private static ProductStatus error;

    @EventListener
    public void init(ContextRefreshedEvent e) {
        if (init.compareAndSet(false, true)) {
            SecurityContext securityContext = securityService
                    .getAdminUserSecurityContext();

            createDefaultProductStatusAndType(securityContext);

        }
    }

    @Override
    public ProductType getGatewayProductType() {
        return gatewayProductType;
    }

    @Override
    public ProductStatus getOnProductStatus() {
        return onProductStatus;
    }

    @Override
    public ProductStatus getOffProductStatus() {
        return offProductStatus;
    }

    @Override
    public ProductStatus getCommErrorProductStatus() {
        return commErrorProductStatus;
    }

    @Override
    public ProductStatus getError() {
        return error;
    }

    private void createDefaultProductStatusAndType(
            SecurityContext securityContext) {

        gatewayProductType = getOrCreateProductType(new ProductTypeCreate()
                .setName("Gateway").setDescription("Gateway"), securityContext);
        buildingProductType = getOrCreateProductType(new ProductTypeCreate()
                        .setName("Building").setDescription("Building"),
                securityContext);

        onProductStatus = getOrCreateProductStatus(new ProductStatusCreate()
                .setName("ON").setDescription("on"), securityContext);
        offProductStatus = getOrCreateProductStatus(new ProductStatusCreate()
                .setName("OFF").setDescription("off"), securityContext);
        commErrorProductStatus = getOrCreateProductStatus(
                new ProductStatusCreate().setName("Communication Error")
                        .setDescription("Communication Error"), securityContext);
        error = getOrCreateProductStatus(
                new ProductStatusCreate().setName("Error").setDescription(
                        "Error"), securityContext);

        linkProductTypeToProductStatus(
                new ProductStatusToTypeCreate().setProductType(
                        gatewayProductType).setProductStatus(onProductStatus),
                securityContext);
        linkProductTypeToProductStatus(
                new ProductStatusToTypeCreate().setProductType(
                        gatewayProductType).setProductStatus(offProductStatus),
                securityContext);
        linkProductTypeToProductStatus(
                new ProductStatusToTypeCreate().setProductType(
                        gatewayProductType).setProductStatus(
                        commErrorProductStatus), securityContext);


    }

    @Value("${flexicore.iot.id:@null}")
    private String iotExternalId;

    @Override
    public ProductType getBuildingProductType() {
        return buildingProductType;
    }



    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids,
                                                   SecurityContext securityContext) {
        return equipmentRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
                                                 List<String> batchString, SecurityContext securityContext) {
        return equipmentRepository.getByIdOrNull(id, c, batchString,
                securityContext);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return equipmentRepository.findByIdOrNull(type, id);
    }

    @Override
    public <T extends Equipment> PaginationResponse<T> getAllEquipments(
            Class<T> c, EquipmentFiltering filtering,
            SecurityContext securityContext) {
        List<T> list = listAllEquipments(c, filtering, securityContext);
        long total = countAllEquipments(c, filtering, securityContext);
        return new PaginationResponse<>(list, filtering, total);
    }

    @Override
    public <T extends Equipment> List<T> listAllEquipments(Class<T> c,
                                                           EquipmentFiltering filtering, SecurityContext securityContext) {
        return equipmentRepository.getAllEquipments(c, filtering,
                securityContext);
    }

    public PaginationResponse<Gateway> getAllGateways(
            GatewayFiltering filtering, SecurityContext securityContext) {
        List<Gateway> list = equipmentRepository.getAllGateways(filtering,
                securityContext);
        long total = equipmentRepository.countAllGateways(filtering,
                securityContext);
        return new PaginationResponse<>(list, filtering, total);
    }


    public <T extends Equipment> long countAllEquipments(Class<T> c,
                                                         EquipmentFiltering filtering, SecurityContext securityContext) {
        return equipmentRepository.countAllEquipments(c, filtering,
                securityContext);
    }

    @Override
    public <T extends Equipment> PaginationResponse<EquipmentGroupHolder> getAllEquipmentsGrouped(
            Class<T> c, EquipmentGroupFiltering filtering,
            SecurityContext securityContext) {
        filtering.setPageSize(null).setCurrentPage(null);
        List<EquipmentGroupHolder> l = equipmentRepository
                .getAllEquipmentsGrouped(c, filtering, securityContext);
        return new PaginationResponse<>(l, filtering, l.size());
    }

    @Override
    public <T extends Equipment> T createEquipment(Class<T> c,
                                                   EquipmentCreate equipmentCreate, SecurityContext securityContext) {
        T equipment = createEquipmentNoMerge(c, equipmentCreate,
                securityContext);
        equipmentRepository.merge(equipment);
        return equipment;
    }

    @Override
    public <T extends Equipment> T createEquipmentNoMerge(Class<T> c,
                                                          EquipmentCreate equipmentCreate, SecurityContext securityContext) {
        T equipment = null;
        try {
            Constructor<T> constructor = c.getConstructor(String.class, SecurityContext.class);
            equipment = constructor.newInstance(c, equipmentCreate.getName(), securityContext);
        } catch (Exception e) {
            logger.error("failed creating equipment", e);
        }
        updateEquipmentNoMerge(equipmentCreate, equipment);
        return equipment;
    }

    @Override
    public Gateway createGatewayNoMerge(GatewayCreate equipmentCreate,
                                        SecurityContext securityContext) {
        Gateway equipment = new Gateway(
                equipmentCreate.getName(), securityContext);
        updateGatewayNoMerge(equipmentCreate, equipment);
        return equipment;
    }

    @Override
    @Deprecated
    public <T extends Gateway> boolean updateGatewayNoMerge(
            GatewayCreate equipmentCreate, T equipment) {
        boolean update = updateEquipmentNoMerge(equipmentCreate, equipment);
        if (equipmentCreate.getIp() != null
                && !equipmentCreate.getIp().equals(equipment.getId())) {
            equipment.setIp(equipmentCreate.getIp());
            update = true;
        }

        if (equipmentCreate.getPort() != null
                && equipmentCreate.getPort() != equipment.getPort()) {
            equipment.setPort(equipmentCreate.getPort());
            update = true;
        }
        if (equipmentCreate.getUsername() != null
                && !equipmentCreate.getUsername().equals(
                equipment.getUsername())) {
            equipment.setUsername(equipmentCreate.getUsername());
            update = true;
        }

        String password = equipmentCreate.getPassword();
        if (password != null) {
            try {
                String encryptedPassword = Base64.getEncoder().encodeToString(
                        encryptionService.encrypt(
                                password.getBytes(StandardCharsets.UTF_8),
                                "test".getBytes()));
                if (!encryptedPassword.equals(equipment.getEncryptedPassword())) {
                    equipment.setEncryptedPassword(encryptedPassword);
                    update = true;
                }
            } catch (Exception e) {
                logger.error("could not encrypt password", e);
            }

        }
        return update;
    }

    @Override
    public void updateProductStatus(Product product,
                                    List<ProductToStatus> allExistingStatus,
                                    SecurityContext securityContext, List<Object> toMerge,
                                    ProductStatus newStatus) {
        if (newStatus != null) {
            updateProductStatus(product, allExistingStatus, securityContext,
                    toMerge, Collections.singletonList(newStatus));

        }
    }

    @Override
    public void updateProductStatus(Product product,
                                    List<ProductToStatus> allExistingStatus,
                                    SecurityContext securityContext, List<Object> toMerge,
                                    List<ProductStatus> newStatuses) {
        Map<String, ProductToStatus> available = allExistingStatus
                .parallelStream().collect(
                        Collectors.toMap(f -> f.getRightside().getId(), f -> f,
                                (a, b) -> a));
        Set<String> ids = newStatuses.stream().map(f -> f.getId())
                .collect(Collectors.toSet());
        for (ProductStatus newStatus : newStatuses) {
            ProductToStatus link = available.get(newStatus.getId());
            if (link == null) {
                link = createProductToProductStatusLinkNoMerge(
                        new ProductStatusToProductCreate().setProduct(product)
                                .setProductStatus(newStatus), securityContext);
                available.put(newStatus.getId(), link);
                allExistingStatus.add(link);
                toMerge.add(link);
            }

        }

        for (Map.Entry<String, ProductToStatus> entry : available.entrySet()) {
            boolean statusEnabled = ids.contains(entry.getKey());
            if (entry.getValue().isEnabled() != statusEnabled) {
                entry.getValue().setEnabled(statusEnabled);
                toMerge.add(entry.getValue());
            }
        }
    }

    @Override
    public EquipmentToGroup createEquipmentToGroup(LinkToGroup linkToGroup,
                                                   SecurityContext securityContext) {
        EquipmentToGroup equipmentToGroup = baselinkService.linkEntities(
                linkToGroup.getEquipment(), linkToGroup.getEquipmentGroup(),
                EquipmentToGroup.class);
        PermissionGroup relatedPermissionGroup = linkToGroup
                .getEquipmentGroup().getRelatedPermissionGroup();
        if (relatedPermissionGroup != null) {
            permissionGroupService.connectPermissionGroupsToBaseclasses(
                    new CreatePermissionGroupLinkRequest().setBaseclasses(
                            Collections.singletonList(linkToGroup
                                    .getEquipment())).setPermissionGroups(
                            Collections.singletonList(relatedPermissionGroup)),
                    securityContext);
        }
        return equipmentToGroup;

    }

    @Override
    public void validateEquipmentCreate(EquipmentCreate equipmentCreate,
                                        SecurityContext securityContext) {

        validateProductCreate(equipmentCreate, securityContext);
        Gateway gateway = equipmentCreate.getCommunicationGatewayId() != null
                ? getByIdOrNull(equipmentCreate.getCommunicationGatewayId(),
                Gateway.class, null, securityContext) : null;
        if (gateway == null
                && equipmentCreate.getCommunicationGatewayId() != null) {
            throw new BadRequestException("No Gateway with Id "
                    + equipmentCreate.getCommunicationGatewayId());
        }
        equipmentCreate.setGateway(gateway);

        String buildingFloorId = equipmentCreate.getBuildingFloorId();
        BuildingFloor buildingFloor = buildingFloorId != null ? getByIdOrNull(buildingFloorId, BuildingFloor.class, null, securityContext) : null;
        if (buildingFloor == null && buildingFloorId != null) {
            throw new BadRequestException("No BuildingFloor with Id " + buildingFloorId);
        }
        equipmentCreate.setBuildingFloor(buildingFloor);

        String roomId = equipmentCreate.getRoomId();
        Room room = roomId != null ? getByIdOrNull(roomId, Room.class, null, securityContext) : null;
        if (room == null && roomId != null) {
            throw new BadRequestException("No Room with Id " + roomId);
        }
        equipmentCreate.setRoom(room);

        Address address = equipmentCreate.getAddressId() != null
                ? securedBasicRepository.getByIdOrNull(equipmentCreate.getAddressId(), Address.class,
                Address_.security, securityContext) : null;
        if (address == null && equipmentCreate.getAddressId() != null) {
            throw new BadRequestException("No address with Id "
                    + equipmentCreate.getAddressId());
        }
        equipmentCreate.setAddress(address);

        ExternalServer externalServer = equipmentCreate.getExternalServerId() != null
                ? getByIdOrNull(equipmentCreate.getExternalServerId(),
                ExternalServer.class, null, securityContext) : null;
        if (externalServer == null
                && equipmentCreate.getExternalServerId() != null) {
            throw new BadRequestException("No address with Id "
                    + equipmentCreate.getExternalServerId());
        }
        equipmentCreate.setExternalServer(externalServer);

    }

    @Override
    public void validateProductCreate(ProductCreate equipmentCreate,
                                      SecurityContext securityContext) {
        ProductType productType = equipmentCreate.getProductTypeId() != null
                ? getByIdOrNull(equipmentCreate.getProductTypeId(),
                ProductType.class, null, securityContext) : null;
        if (productType == null && equipmentCreate.getProductTypeId() != null) {
            throw new BadRequestException("No Product type with Id "
                    + equipmentCreate.getProductTypeId());
        }
        equipmentCreate.setProductType(productType);

        Model model = equipmentCreate.getModelId() != null ? getByIdOrNull(
                equipmentCreate.getModelId(), Model.class, null,
                securityContext) : null;
        if (model == null && equipmentCreate.getModelId() != null) {
            throw new BadRequestException("No model with Id "
                    + equipmentCreate.getProductTypeId());
        }
        equipmentCreate.setModel(model);

    }


    @Override
    public boolean updateProductNoMerge(ProductCreate productCreate,
                                        Product product) {
        boolean update = baseclassNewService.updateBaseclassNoMerge(
                productCreate, product);

        if (productCreate.getSku() != null
                && !productCreate.getSku().equals(product.getSku())) {
            product.setSku(productCreate.getSku());
            update = true;
        }
        if (productCreate.getProductType() != null
                && (product.getProductType() == null || !product
                .getProductType().getId()
                .equals(productCreate.getProductType().getId()))) {
            product.setProductType(productCreate.getProductType());
            update = true;
        }

        if (productCreate.getModel() != null
                && (product.getModel() == null || !product.getModel().getId()
                .equals(productCreate.getModel().getId()))) {
            product.setModel(productCreate.getModel());
            update = true;
        }

        if (productCreate.getTenant() != null
                && (product.getTenant() == null || !product.getTenant().getId()
                .equals(productCreate.getTenant().getId()))) {
            product.setTenant(productCreate.getTenant());
            update = true;
        }

        return update;
    }

    @Override
    public boolean updateEquipmentNoMerge(EquipmentCreate equipmentCreate,
                                          Equipment equipment) {
        boolean update = updateProductNoMerge(equipmentCreate, equipment);

        if (equipmentCreate.getWarrantyExpiration() != null
                && !equipmentCreate.getWarrantyExpiration().equals(
                equipment.getWarrantyExpiration())) {
            equipment.setWarrantyExpiration(equipmentCreate
                    .getWarrantyExpiration());
            update = true;
        }

        boolean updateLatLon = false;
        if (equipmentCreate.getLat() != null
                && !equipmentCreate.getLat().equals(equipment.getLat())) {
            equipment.setLat(equipmentCreate.getLat());
            update = true;
            updateLatLon = true;
        }

        if (equipmentCreate.getLon() != null
                && !equipmentCreate.getLon().equals(equipment.getLon())) {
            equipment.setLon(equipmentCreate.getLon());
            update = true;
            updateLatLon = true;
        }
        if (updateLatLon) {
            generateGeoHash(equipment);

        }

        if (equipmentCreate.getSerial() != null
                && !equipmentCreate.getSerial().equals(equipment.getSerial())) {
            equipment.setSerial(equipmentCreate.getSerial());
            update = true;
        }

        if (equipmentCreate.getDescriptor3D() != null
                && !equipmentCreate.getDescriptor3D().equals(
                equipment.getDescriptor3D())) {
            equipment.setDescriptor3D(equipmentCreate.getDescriptor3D());
            update = true;
        }

        if (equipmentCreate.getEnable() != null
                && equipmentCreate.getEnable() != equipment.isEnable()) {
            equipment.setEnable(equipmentCreate.getEnable());
            update = true;
        }

        if (equipmentCreate.getX() != null
                && equipmentCreate.getX() != equipment.getX()) {
            equipment.setX(equipmentCreate.getX());
            update = true;
        }
        if (equipmentCreate.getY() != null
                && equipmentCreate.getY() != equipment.getY()) {
            equipment.setY(equipmentCreate.getY());
            update = true;
        }

        if (equipmentCreate.getGateway() != null && (equipment.getCommunicationGateway() == null || !equipment.getCommunicationGateway().getId().equals(equipmentCreate.getGateway().getId()))) {
            equipment.setCommunicationGateway(equipmentCreate.getGateway());
            update = true;
        }

        if (equipmentCreate.getRoom() != null && (equipment.getRoom() == null || !equipment.getRoom().getId().equals(equipmentCreate.getRoom().getId()))) {
            equipment.setRoom(equipmentCreate.getRoom());
            update = true;
        }
        if (equipmentCreate.getBuildingFloor() != null && (equipment.getBuildingFloor() == null || !equipment.getBuildingFloor().getId().equals(equipmentCreate.getBuildingFloor().getId()))) {
            equipment.setBuildingFloor(equipmentCreate.getBuildingFloor());
            update = true;
        }
        if (equipmentCreate.getAddress() != null
                && (equipment.getAddress() == null || !equipment.getAddress()
                .getId().equals(equipmentCreate.getAddress().getId()))) {
            equipment.setAddress(equipmentCreate.getAddress());
            update = true;
        }

        if (equipmentCreate.getSku() != null
                && !equipmentCreate.getSku().equals(equipment.getSku())) {
            equipment.setSku(equipmentCreate.getSku());
            update = true;

        }

        if (equipmentCreate.getExternalId() != null
                && !equipmentCreate.getExternalId().equals(
                equipment.getExternalId())) {
            equipment.setExternalId(equipmentCreate.getExternalId());
            update = true;

        }

        if (equipmentCreate.getExternalServer() != null
                && (equipment.getExternalServer() == null || !equipment
                .getExternalServer().getId()
                .equals(equipmentCreate.getExternalServer().getId()))) {
            equipment.setExternalServer(equipmentCreate.getExternalServer());
            update = true;
        }

        return update;

    }

    private Method getSetterOrNull(String name) {
        try {
            return Equipment.class.getMethod(name, String.class);
        } catch (NoSuchMethodException e) {
            logger.error("unable to get setter", e);
        }
        return null;
    }

    @Override
    public void generateGeoHash(Equipment equipment) {
        try {
            for (int i = 1; i < 13; i++) {
                String setterName = "setGeoHash" + i;
                try {
                    String geoHash = GeoHash
                            .geoHashStringWithCharacterPrecision(
                                    equipment.getLat(), equipment.getLon(), i);
                    Method method = setterCache.computeIfAbsent(setterName,
                            f -> getSetterOrNull(f));
                    if (method != null) {
                        method.invoke(equipment, geoHash);

                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    logger.error("could not set property "
                            + setterName + " via setter");
                }

            }
        } catch (Exception e) {
            logger.error("unable to generate geo hash for equipment " + equipment.getId() + " (" + equipment.getExternalId() + ")");
        }
    }

    @Override
    public Equipment updateEquipment(EquipmentUpdate equipmentUpdate,
                                     SecurityContext securityContext) {
        if (updateEquipmentNoMerge(equipmentUpdate,
                equipmentUpdate.getEquipment())) {
            equipmentRepository.merge(equipmentUpdate.getEquipment());
        }
        return equipmentUpdate.getEquipment();
    }

    @Override
    public PaginationResponse<ProductType> getAllProductTypes(
            ProductTypeFiltering productTypeFiltering,
            SecurityContext securityContext) {
        List<ProductType> list = listAllProductTypes(productTypeFiltering,
                securityContext);
        long count = equipmentRepository.countAllProductTypes(
                productTypeFiltering, securityContext);
        return new PaginationResponse<>(list, productTypeFiltering, count);
    }

    @Override
    public List<ProductType> listAllProductTypes(
            ProductTypeFiltering productTypeFiltering,
            SecurityContext securityContext) {
        return equipmentRepository.listAllProductTypes(productTypeFiltering,
                securityContext);
    }

    @Override
    public PaginationResponse<ProductStatus> getAllProductStatus(
            ProductStatusFiltering productStatusFiltering,
            SecurityContext securityContext) {
        List<ProductStatus> list = equipmentRepository.getAllProductStatus(
                productStatusFiltering, securityContext);
        long count = equipmentRepository.countAllProductStatus(
                productStatusFiltering, securityContext);
        return new PaginationResponse<>(list, productStatusFiltering, count);

    }

    @Override
    public ProductType getOrCreateProductType(
            ProductTypeCreate productTypeCreate, SecurityContext securityContext) {
        ProductType productType = equipmentRepository.getFirstByName(
                productTypeCreate.getName(), ProductType.class, null,
                securityContext);
        if (productType == null) {
            productType = createProductType(productTypeCreate, securityContext);
            equipmentRepository.merge(productType);
        }
        return productType;
    }

    @Override
    public ProductStatus getOrCreateProductStatus(
            ProductStatusCreate productStatusCreate,
            SecurityContext securityContext) {
        ProductStatus productType = equipmentRepository.getFirstByName(
                productStatusCreate.getName(), ProductStatus.class, null,
                securityContext);
        if (productType == null) {
            productType = createProductStatus(productStatusCreate,
                    securityContext);
            equipmentRepository.merge(productType);
        }
        return productType;
    }

    @Override
    public ProductTypeToProductStatus linkProductTypeToProductStatus(
            ProductStatusToTypeCreate productStatusCreate,
            SecurityContext securityContext) {
        List<ProductTypeToProductStatus> list = baselinkService.findAllBySides(
                ProductTypeToProductStatus.class,
                productStatusCreate.getProductType(),
                productStatusCreate.getProductStatus(), securityContext);
        return list.isEmpty() ? createProductTypeToProductStatusLink(
                productStatusCreate, securityContext) : list.get(0);
    }

    @Override
    public ProductTypeToProductStatus createProductTypeToProductStatusLink(
            ProductStatusToTypeCreate productStatusCreate,
            SecurityContext securityContext) {
        String name = "link";
        if (productStatusCreate.getProductStatus() != null
                && productStatusCreate.getProductType() != null) {
            name = productStatusCreate.getProductType().getName() + "To"
                    + productStatusCreate.getProductStatus().getName();
        }
        ProductTypeToProductStatus productTypeToProductStatus = new ProductTypeToProductStatus
                (name, securityContext);
        productTypeToProductStatus.setLeftside(productStatusCreate.getProductType());
        productTypeToProductStatus.setRightside(productStatusCreate.getProductStatus());

        productTypeToProductStatus.setId(getProductTypeToStatusId(productTypeToProductStatus));
        baselinkService.merge(productTypeToProductStatus);
        return productTypeToProductStatus;
    }

    private String getProductTypeToStatusId(
            ProductTypeToProductStatus productTypeToProductStatus) {
        return Baseclass.generateUUIDFromString("ProductTypeToProductStatus-"
                + productTypeToProductStatus.getLeftside().getId() + "-"
                + productTypeToProductStatus.getRightside().getId());
    }

    @Override
    public ProductToStatus linkProductToProductStatusNoMerge(
            ProductStatusToProductCreate productStatusCreate,
            SecurityContext securityContext) {
        List<ProductToStatus> list = getProductToStatusLinks(
                productStatusCreate, securityContext);
        return list.isEmpty() ? createProductToProductStatusLinkNoMerge(
                productStatusCreate, securityContext) : list.get(0);
    }

    @Override
    public List<ProductToStatus> getProductToStatusLinks(
            ProductStatusToProductCreate productStatusCreate,
            SecurityContext securityContext) {
        return baselinkService.findAllBySides(ProductToStatus.class,
                productStatusCreate.getProduct(),
                productStatusCreate.getProductStatus(), securityContext);
    }

    @Override
    public ProductToStatus linkProductToProductStatus(
            ProductStatusToProductCreate productStatusCreate,
            SecurityContext securityContext) {
        ProductToStatus productToStatus = linkProductToProductStatusNoMerge(
                productStatusCreate, securityContext);
        baselinkService.merge(productToStatus);
        return productToStatus;
    }

    @Override
    public ProductToStatus createProductToProductStatusLinkNoMerge(
            ProductStatusToProductCreate productStatusCreate,
            SecurityContext securityContext) {
        ProductToStatus productToStatus = new ProductToStatus(
                "link", securityContext);
        productToStatus.setLeftside(productStatusCreate.getProduct());
        productToStatus.setRightside(productStatusCreate.getProductStatus());
        productToStatus.setId(getProductToStatusId(productToStatus));
        productToStatus.setEnabled(true);
        return productToStatus;
    }

    private String getProductToStatusId(ProductToStatus productToStatus) {
        return Baseclass.generateUUIDFromString("ProductToStatus-"
                + productToStatus.getLeftside().getId() + "-"
                + productToStatus.getRightside().getId());
    }

    @Override
    public ProductToStatus createProductToProductStatusLink(
            ProductStatusToProductCreate productStatusCreate,
            SecurityContext securityContext) {
        ProductToStatus productToStatus = createProductToProductStatusLinkNoMerge(
                productStatusCreate, securityContext);
        baselinkService.merge(productStatusCreate);
        return productToStatus;
    }

    @Override
    public ProductStatus createProductStatus(
            ProductStatusCreate productStatusCreate,
            SecurityContext securityContext) {
        ProductStatus productStatus = new ProductStatus(
                productStatusCreate.getName(), securityContext);
        productStatus.setId(getProductStatusId(productStatusCreate.getName()));
        productStatus.setDescription(productStatusCreate.getDescription());
        return productStatus;
    }

    private String getProductStatusId(String name) {
        return Baseclass.generateUUIDFromString("ProductStatus-" + name);
    }

    public SecurityContext getAdminSecurityContext() {
        User user = userService.getAdminUser();
        RunningUser runningUser = userService.registerUserIntoSystem(user,
                OffsetDateTime.now().plusYears(30));
        String adminToken = runningUser.getAuthenticationkey().getKey();
        return verifyLoggedIn(adminToken);
    }

    public SecurityContext verifyLoggedIn(String userToken) {
        String opId = Baseclass.generateUUIDFromString(Read.class
                .getCanonicalName());
        return securityService.getSecurityContext(userToken, opId);
    }

    @Override
    public List<ProductToStatus> getAvailableProductStatus(Product product,
                                                           SecurityContext securityContext) {
        return baselinkService.findAllBySide(ProductToStatus.class, product,
                false, securityContext);
    }

    @Override
    public ProductType createProductType(ProductTypeCreate productTypeCreate,
                                         SecurityContext securityContext) {
        ProductType productType = createProductTypeNoMerge(productTypeCreate,
                securityContext);
        equipmentRepository.merge(productType);
        return productType;
    }

    @Override
    public ProductType createProductTypeNoMerge(
            ProductTypeCreate productTypeCreate, SecurityContext securityContext) {
        ProductType productType = new ProductType(
                productTypeCreate.getName(), securityContext);
        productType.setId(getProductTypeId(productTypeCreate.getName()));
        updateProductTypeNoMerge(productTypeCreate, productType);
        return productType;
    }

    private String getProductTypeId(String name) {
        return Baseclass.generateUUIDFromString("ProductType-" + name);
    }

    @Override
    public <T extends Equipment> Class<T> validateFiltering(
            EquipmentFiltering filtering, SecurityContext securityContext) {
        Class<T> c = (Class<T>) Equipment.class;
        if (filtering.getResultType() != null
                && !filtering.getResultType().isEmpty()) {
            try {
                Class<?> aClass = Class.forName(filtering.getResultType());
                if (Equipment.class.isAssignableFrom(aClass)) {
                    c = (Class<T>) aClass;

                }

            } catch (ClassNotFoundException e) {
                logger.error("unable to get class: " + filtering.getResultType());
                throw new BadRequestException("No Class with name " + filtering.getResultType());

            }
        }
        if (!filtering.getTypesToReturn().isEmpty()) {
            Set<String> canonicalNames = filtering.getTypesToReturnIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            List<Class<?>> classes = InheritanceUtils
                    .listInheritingClassesWithFilter(new GetClassInfo().setClassName(c.getCanonicalName()))
                    .getList()
                    .parallelStream()
                    .filter(f -> canonicalNames.contains(f.getClazz()
                            .getCanonicalName())).map(f -> f.getClazz())
                    .collect(Collectors.toList());
            filtering.setTypesToReturn(classes);

        }
        if (filtering.getGroupIds() != null
                && !filtering.getGroupIds().isEmpty()) {
            Set<String> groupIds = filtering.getGroupIds().parallelStream()
                    .map(f -> f.getId()).collect(Collectors.toSet());
            List<EquipmentGroup> groups = filtering.getGroupIds().isEmpty()
                    ? new ArrayList<>()
                    : groupService.listByIds(EquipmentGroup.class, groupIds,
                    securityContext);
            groupIds.removeAll(groups.parallelStream().map(f -> f.getId())
                    .collect(Collectors.toSet()));
            if (!groupIds.isEmpty()) {
                throw new BadRequestException("could not find groups with ids "
                        + filtering.getGroupIds().parallelStream()
                        .map(f -> f.getId())
                        .collect(Collectors.joining(",")));
            }
            filtering.setEquipmentGroups(groups);
        }
        ProductType productType = filtering.getProductTypeId() != null
                && filtering.getProductTypeId().getId() != null
                ? getByIdOrNull(filtering.getProductTypeId().getId(),
                ProductType.class, null, null) : null;
        if (filtering.getProductTypeId() != null && productType == null) {
            throw new BadRequestException("No Product type with id "
                    + filtering.getProductTypeId());
        }
        filtering.setProductType(productType);
        if (filtering.getProductStatusIds() != null
                && !filtering.getProductStatusIds().isEmpty()) {
            Set<String> statusIds = filtering.getProductStatusIds()
                    .parallelStream().map(f -> f.getId())
                    .collect(Collectors.toSet());
            List<ProductStatus> status = filtering.getProductStatusIds()
                    .isEmpty() ? new ArrayList<>() : listByIds(
                    ProductStatus.class, statusIds, null);
            statusIds.removeAll(status.parallelStream().map(f -> f.getId())
                    .collect(Collectors.toSet()));
            if (!statusIds.isEmpty()) {
                throw new BadRequestException("could not find status with ids "
                        + filtering.getProductStatusIds().parallelStream()
                        .map(f -> f.getId())
                        .collect(Collectors.joining(",")));
            }
            filtering.setProductStatusList(status);
        }

        if (filtering.getNeighbourhoodIds() != null && !filtering.getNeighbourhoodIds().isEmpty()) {
            Set<String> ids = filtering.getNeighbourhoodIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            List<Neighbourhood> neighbourhoods = getNeighbourhoods(ids, securityContext);
            ids.removeAll(neighbourhoods.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
            if (!ids.isEmpty()) {
                throw new BadRequestException("No Neighbourhood with ids " + ids);
            }
            filtering.setNeighbourhoods(neighbourhoods);
        }

        if (filtering.getStreetIds() != null
                && !filtering.getStreetIds().isEmpty()) {
            Set<String> ids = filtering.getStreetIds().parallelStream()
                    .map(f -> f.getId()).collect(Collectors.toSet());
            List<Street> streets = getStreets(ids, securityContext);
            ids.removeAll(streets.parallelStream().map(f -> f.getId())
                    .collect(Collectors.toSet()));
            if (!ids.isEmpty()) {
                throw new BadRequestException("No Streets with ids " + ids);
            }
            filtering.setStreets(streets);
        }

        if (filtering.getGatewayIds() != null
                && !filtering.getGatewayIds().isEmpty()) {
            Set<String> ids = filtering.getGatewayIds().parallelStream()
                    .map(f -> f.getId()).collect(Collectors.toSet());
            List<Gateway> gateways = getGateways(ids, securityContext);
            ids.removeAll(gateways.parallelStream().map(f -> f.getId())
                    .collect(Collectors.toSet()));
            if (!ids.isEmpty()) {
                throw new BadRequestException("No Gateways with ids " + ids);
            }
            filtering.setGateways(gateways);
        }

        if (filtering.getExternalServerIds() != null
                && !filtering.getExternalServerIds().isEmpty()) {
            Set<String> ids = filtering.getExternalServerIds().parallelStream()
                    .map(f -> f.getId()).collect(Collectors.toSet());
            List<ExternalServer> externalServers = listByIds(
                    ExternalServer.class, ids, securityContext);
            ids.removeAll(externalServers.parallelStream().map(f -> f.getId())
                    .collect(Collectors.toSet()));
            if (!ids.isEmpty()) {
                throw new BadRequestException("No ExternalServer with ids "
                        + ids);
            }
            filtering.setExternalServers(externalServers);
        }

        if (filtering.getEquipmentByStatusEntryIds() != null
                && !filtering.getEquipmentByStatusEntryIds().isEmpty()) {
            Set<String> ids = filtering.getEquipmentByStatusEntryIds()
                    .parallelStream().map(f -> f.getId())
                    .collect(Collectors.toSet());
            Set<EquipmentIdFiltering> equipmentIds = listAllDetailedEquipmentStatus(
                    new DetailedEquipmentFilter()
                            .setEquipmentByStatusEntryIds(ids))
                    .parallelStream()
                    .map(f -> new EquipmentIdFiltering().setId(f
                            .getEquipmentId())).collect(Collectors.toSet());
            filtering.setEquipmentIds(equipmentIds);
        }
        if (filtering.getBuildingFloorIds() != null && !filtering.getBuildingFloorIds().isEmpty()) {
            Set<String> ids = filtering.getBuildingFloorIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Map<String, BuildingFloor> buildingFloors = ids.isEmpty() ? new HashMap<>() : listByIds(BuildingFloor.class, ids, securityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
            ids.removeAll(buildingFloors.keySet());
            if (!ids.isEmpty()) {
                throw new BadRequestException("No BuildingFloors with ids " + ids);
            }
            filtering.setBuildingFloors(new ArrayList<>(buildingFloors.values()));
        }
        if (filtering.getRoomIds() != null && !filtering.getRoomIds().isEmpty()) {
            Set<String> roomIds = filtering.getRoomIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Map<String, Room> rooms = roomIds.isEmpty() ? new HashMap<>() : listByIds(Room.class, roomIds, securityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
            roomIds.removeAll(rooms.keySet());
            if (!roomIds.isEmpty()) {
                throw new BadRequestException("No Room with ids " + roomIds);
            }
            filtering.setRooms(new ArrayList<>(rooms.values()));
        }
        return c;
    }

    private List<Street> getStreets(Set<String> ids,
                                    SecurityContext securityContext) {
        return securedBasicRepository
                .listByIds(Street.class, ids, Street_.security, securityContext);
    }

    private List<Gateway> getGateways(Set<String> ids,
                                      SecurityContext securityContext) {
        return equipmentRepository.listByIds(Gateway.class, ids,
                securityContext);
    }

    private List<Neighbourhood> getNeighbourhoods(Set<String> ids,
                                                  SecurityContext securityContext) {
        return securedBasicRepository.listByIds(Neighbourhood.class, ids,Neighbourhood_.security,
                securityContext);
    }

    @Override
    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatus(
            Class<T> c, EquipmentFiltering equipmentFiltering,
            SecurityContext securityContext) {
        return equipmentRepository.getProductGroupedByStatus(c,
                equipmentFiltering, securityContext);
    }

    @Override
    public boolean updateMultiLatLonEquipmentNoMerge(
            CreateMultiLatLonEquipment createMultiLatLonEquipment,
            MultiLatLonEquipment multiLatLonEquipment) {
        boolean update = updateEquipmentNoMerge(createMultiLatLonEquipment,
                multiLatLonEquipment);

        if (createMultiLatLonEquipment.getContextString() != null
                && !createMultiLatLonEquipment.getContextString().equals(
                multiLatLonEquipment.getContextString())) {
            multiLatLonEquipment.setContextString(createMultiLatLonEquipment
                    .getContextString());
            update = true;
        }
        return update;
    }

    @Override
    public MultiLatLonEquipment createMultiLatLonEquipmentNoMerge(
            CreateMultiLatLonEquipment createMultiLatLonEquipment,
            SecurityContext securityContext) {
        MultiLatLonEquipment multiLatLonEquipment = new MultiLatLonEquipment(createMultiLatLonEquipment.getName(),
                securityContext);
        updateMultiLatLonEquipmentNoMerge(createMultiLatLonEquipment,
                multiLatLonEquipment);
        return multiLatLonEquipment;
    }

    public void validateLatLon(CreateLatLon createLatLon,
                               SecurityContext securityContext) {
        MultiLatLonEquipment multiLatLonEquipment = equipmentRepository
                .getByIdOrNull(createLatLon.getMultiLatLonEquipmentId(),
                        MultiLatLonEquipment.class, null, securityContext);
        if (multiLatLonEquipment == null
                && createLatLon.getMultiLatLonEquipmentId() != null) {
            throw new BadRequestException("No MultiLatLon Equipment with id "
                    + createLatLon.getMultiLatLonEquipmentId());
        }
        createLatLon.setMultiLatLonEquipment(multiLatLonEquipment);

    }

    public void validateLatLon(LatLonFilter latLonFilter,
                               SecurityContext securityContext) {
        Set<String> equipmentIds = latLonFilter.getMultiLatLonEquipmentIds();
        Map<String, MultiLatLonEquipment> map = equipmentIds.isEmpty()
                ? new HashMap<>()
                : equipmentRepository
                .listByIds(MultiLatLonEquipment.class, equipmentIds,
                        securityContext).parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
        equipmentIds.removeAll(map.keySet());
        if (!equipmentIds.isEmpty()) {
            throw new BadRequestException("No MultiLatLon Equipment with id "
                    + equipmentIds);
        }
        latLonFilter.setMultiLatLonEquipments(new ArrayList<>(map.values()));

    }

    public LatLon updateLatLon(UpdateLatLon updateLatLon,
                               SecurityContext securityContext) {
        LatLon latLon = updateLatLon.getLatLon();
        int ordinalBefore = latLon.getOrdinal();
        if (updateLatLonNoMerge(updateLatLon, latLon)) {
            List<Object> toMerge = new ArrayList<>();
            toMerge.add(latLon);
            int ordinalAfter = latLon.getOrdinal();
            if (!updateLatLon.isManualUpdateOrdinal()
                    && ordinalBefore != ordinalAfter) {
                MultiLatLonEquipment multiLatLonEquipment = latLon
                        .getMultiLatLonEquipment();
                if (multiLatLonEquipment != null) {
                    List<LatLon> latLons = listAllLatLons(
                            new LatLonFilter().setMultiLatLonEquipments(Collections
                                    .singletonList(multiLatLonEquipment)),
                            securityContext);
                    for (LatLon lon : latLons) {
                        if (lon.getId().equals(latLon.getId())) {
                            continue;
                        }
                        if (lon.getOrdinal() < ordinalBefore
                                && lon.getOrdinal() >= ordinalAfter) {
                            lon.setOrdinal(lon.getOrdinal() + 1);
                            toMerge.add(lon);
                        }
                    }
                }

            }
            equipmentRepository.massMerge(toMerge);

        }
        return latLon;
    }

    public PaginationResponse<LatLon> getAllLatLons(LatLonFilter latLonFilter,
                                                    SecurityContext securityContext) {
        List<LatLon> list = listAllLatLons(latLonFilter, securityContext);
        long count = equipmentRepository.countAllLatLons(latLonFilter,
                securityContext);
        return new PaginationResponse<>(list, latLonFilter, count);
    }

    @Override
    public List<LatLon> listAllLatLons(LatLonFilter latLonFilter,
                                       SecurityContext securityContext) {
        return equipmentRepository.getAllLatLons(latLonFilter, securityContext);
    }

    public LatLon createLatLon(CreateLatLon createLatLon,
                               SecurityContext securityContext) {
        LatLon latLon = createLatLonNoMerge(createLatLon, securityContext);
        equipmentRepository.merge(latLon);
        return latLon;
    }

    public LatLon createLatLonNoMerge(CreateLatLon createLatLon,
                                      SecurityContext securityContext) {
        LatLon latLon = new LatLon("LatLon", securityContext);
        updateLatLonNoMerge(createLatLon, latLon);
        return latLon;
    }

    private boolean updateLatLonNoMerge(CreateLatLon createLatLon, LatLon latLon) {
        boolean update = false;

        if (createLatLon.getMultiLatLonEquipment() != null
                && (latLon.getMultiLatLonEquipment() == null || createLatLon
                .getMultiLatLonEquipment().getId()
                .equals(latLon.getMultiLatLonEquipment().getId()))) {
            latLon.setMultiLatLonEquipment(createLatLon
                    .getMultiLatLonEquipment());
            update = true;
        }
        if (createLatLon.getLat() != null
                && createLatLon.getLat() != latLon.getLat()) {
            latLon.setLat(createLatLon.getLat());
            update = true;
        }
        if (createLatLon.getLon() != null
                && createLatLon.getLon() != latLon.getLon()) {
            latLon.setLon(createLatLon.getLon());
            update = true;
        }
        if (createLatLon.getOrdinal() != null
                && createLatLon.getOrdinal() != latLon.getOrdinal()) {
            latLon.setOrdinal(createLatLon.getOrdinal());
            update = true;
        }

        if (createLatLon.getSoftDelete() != null
                && createLatLon.getSoftDelete() != latLon.isSoftDelete()) {
            latLon.setSoftDelete(createLatLon.getSoftDelete());
            update = true;
        }
        return update;
    }

    @Override
    public List<ProductToStatus> getStatusLinks(Set<String> collect) {
        return collect.isEmpty() ? new ArrayList<>() : equipmentRepository
                .getStatusLinks(collect);
    }

    @Override
    public List<ProductToStatus> getCurrentStatusLinks(Set<String> collect) {
        return collect.isEmpty() ? new ArrayList<>() : equipmentRepository
                .getCurrentStatusLinks(collect);
    }

    public <T extends Equipment> PaginationResponse<EquipmentShort> getAllEquipmentsShort(
            Class<T> c, EquipmentFiltering filtering,
            SecurityContext securityContext) {
        List<T> list = equipmentRepository.getAllEquipments(c, filtering,
                securityContext);
        List<ProductToStatus> statusLinks = getCurrentStatusLinks(list
                .parallelStream().map(f -> f.getId())
                .collect(Collectors.toSet()));
        StatusLinksToImageFilter statusLinksToImageFilter = new StatusLinksToImageFilter();
        statusLinksToImageFilter.setNameLike("%map%");
        Map<String, List<ProductStatus>> statusLinksMap = statusLinks
                .parallelStream().collect(
                        Collectors.groupingBy(f -> f.getLeftside().getId(),
                                ConcurrentHashMap::new, Collectors.mapping(
                                        f -> f.getRightside(),
                                        Collectors.toList())));

        Map<String, Map<String, String>> statusLinkToImages = statusLinkToImageService
                .listAllStatusLinksToImage(statusLinksToImageFilter, null)
                .parallelStream()
                .collect(
                        Collectors.groupingBy(f -> f.getStatusLink()
                                .getLeftside().getId(), Collectors.toMap(f -> f
                                .getStatusLink().getRightside().getId(), f -> f
                                .getImage().getId(), (a, b) -> a)));

        long total = countAllEquipments(c, filtering, securityContext);

        return new PaginationResponse<>(
                list.parallelStream()
                        .map(f -> new EquipmentShort(f, statusLinksMap.get(f
                                .getId()),
                                buildSpecificStatusIconMap(
                                        f.getProductType() != null
                                                ? statusLinkToImages.get(f
                                                .getProductType()
                                                .getId()) : null,
                                        statusLinksMap.get(f.getId()))))
                        .collect(Collectors.toList()), filtering, total);
    }

    @Override
    public Map<String, String> buildSpecificStatusIconMap(
            Map<String, String> typeSpecificStatusToIcon,
            List<ProductStatus> status) {
        Map<String, String> result = new HashMap<>();
        result = (typeSpecificStatusToIcon == null || status == null)
                ? new HashMap<>()
                : status.parallelStream()
                .filter(f -> typeSpecificStatusToIcon.get(f.getId()) != null)
                .collect(
                        Collectors.toMap(f -> f.getId(),
                                f -> typeSpecificStatusToIcon.get(f
                                        .getId()), (a, b) -> a));
        return result;
    }

    @Override
    public List<ProductTypeToProductStatus> getAllProductTypeToStatusLinks(
            Set<String> statusIds) {
        return statusIds.isEmpty() ? new ArrayList<>() : equipmentRepository
                .getAllProductTypeToStatusLinks(statusIds);
    }

    public ProductType updateProductType(UpdateProductType updateProductType,
                                         SecurityContext securityContext) {
        if (updateProductTypeNoMerge(updateProductType,
                updateProductType.getProductType())) {
            equipmentRepository.merge(updateProductType.getProductType());
        }
        return updateProductType.getProductType();
    }

    public boolean updateProductTypeNoMerge(
            ProductTypeCreate updateProductType, ProductType productType) {
        boolean update = false;
        if (updateProductType.getName() != null
                && !updateProductType.getName().equals(productType.getName())) {
            productType.setName(updateProductType.getName());
            update = true;
        }

        if (updateProductType.getDescription() != null
                && !updateProductType.getDescription().equals(
                productType.getDescription())) {
            productType.setDescription(updateProductType.getDescription());
            update = true;
        }

        if (updateProductType.getIcon() != null
                && (productType.getImage() == null || !productType.getImage()
                .getId().equals(updateProductType.getIcon().getId()))) {
            productType.setImage(updateProductType.getIcon());
            update = true;
        }

        if (updateProductType.getDiagram3D() != null
                && (productType.getDiagram3D() == null || !productType
                .getDiagram3D().getId()
                .equals(updateProductType.getDiagram3D().getId()))) {
            productType.setDiagram3D(updateProductType.getDiagram3D());
            update = true;
        }

        return update;
    }

    public ProductTypeToProductStatus updateProductStatusToType(
            UpdateProductStatusToType updateProductStatus,
            SecurityContext securityContext) {
        List<ProductTypeToProductStatus> list = baselinkService.findAllBySides(
                ProductTypeToProductStatus.class,
                updateProductStatus.getProductType(),
                updateProductStatus.getProductStatus(), securityContext);
        if (list.isEmpty()) {
            throw new BadRequestException("status "
                    + updateProductStatus.getProductStatusId() + " and "
                    + updateProductStatus.getProductTypeId()
                    + " are not linked");
        }
        List<Object> toMerge = new ArrayList<>();
        for (ProductTypeToProductStatus productTypeToProductStatus : list) {
            boolean update = updateProductStatusToType(updateProductStatus,
                    productTypeToProductStatus);
            if (update) {
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

    public boolean updateProductStatusToType(
            UpdateProductStatusToType updateProductStatus,
            ProductTypeToProductStatus productTypeToProductStatus) {
        boolean update = false;
        if (productTypeToProductStatus.getImage() == null
                || !productTypeToProductStatus.getImage().getId()
                .equals(updateProductStatus.getIcon().getId())) {
            productTypeToProductStatus.setImage(updateProductStatus.getIcon());
            update = true;

        }
        return update;
    }

    public void validateProductStatusFiltering(
            ProductStatusFiltering productStatusFiltering,
            SecurityContext securityContext) {
        ProductType productType = productStatusFiltering.getProductTypeId() != null
                ? getByIdOrNull(productStatusFiltering.getProductTypeId()
                .getId(), ProductType.class, null, securityContext)
                : null;
        if (productType == null
                && productStatusFiltering.getProductTypeId() != null) {
            throw new BadRequestException("No Product Type with id "
                    + productStatusFiltering.getProductTypeId().getId());
        }
        productStatusFiltering.setProductType(productType);
        EquipmentFiltering equipmentFiltering = productStatusFiltering
                .getEquipmentFiltering();
        if (equipmentFiltering != null) {
            validateFiltering(equipmentFiltering, securityContext);
        }
    }

    public PaginationResponse<Neighbourhood> getAllNeighbourhoods(
            NeighbourhoodFilter neighbourhoodFiltering,
            SecurityContext securityContext) {
        return new PaginationResponse<>(neighbourhoodService.getAllNeighbourhoods(securityContext,neighbourhoodFiltering));
    }

    public PaginationResponse<Street> getAllStreets(
            StreetFilter streetFiltering, SecurityContext securityContext) {
      return new PaginationResponse<>(streetService.getAllStreets(securityContext,streetFiltering));
    }

    public List<Equipment> getEquipmentByIds(Set<String> ids,
                                             SecurityContext securityContext) {
        return equipmentRepository.listByIds(Equipment.class, ids,
                securityContext);
    }

    public List<Equipment> enableEquipment(EnableEquipments enableLights,
                                           SecurityContext securityContext) {
        List<Object> toMerge = new ArrayList<>();
        for (Equipment equipment : enableLights.getEquipmentList()) {
            if (equipment.isEnable() != enableLights.isEnable()) {
                equipment.setEnable(enableLights.isEnable());
                toMerge.add(equipment);

            }
        }
        equipmentRepository.massMerge(toMerge);
        return enableLights.getEquipmentList();
    }

    public void validate(UpdateProductStatus updateProductStatus,
                         SecurityContext securityContext) {
        ProductStatus productStatus = updateProductStatus.getStatusId() != null
                ? getByIdOrNull(updateProductStatus.getStatusId(),
                ProductStatus.class, null, securityContext) : null;
        if (productStatus == null) {
            throw new BadRequestException("no productStatus with id "
                    + updateProductStatus.getStatusId());
        }
        updateProductStatus.setProductStatus(productStatus);

        Equipment equipment = updateProductStatus.getEquipmentId() != null
                ? getByIdOrNull(updateProductStatus.getEquipmentId(),
                Equipment.class, null, securityContext) : null;
        if (equipment == null) {
            throw new BadRequestException("no Equipment with id "
                    + updateProductStatus.getEquipmentId());
        }
        updateProductStatus.setEquipment(equipment);
    }

    public void validate(MassUpsertLatLonRequest gatewayCreate,
                         SecurityContext securityContext) {

        MultiLatLonEquipment multiLatLonEquipment = gatewayCreate
                .getMultiLatLonEquipmentId() != null ? getByIdOrNull(
                gatewayCreate.getMultiLatLonEquipmentId(),
                MultiLatLonEquipment.class, null, securityContext) : null;
        if (multiLatLonEquipment == null) {
            throw new BadRequestException("No MultiLatLonEquipment with Id "
                    + gatewayCreate.getMultiLatLonEquipmentId());
        }
        gatewayCreate.setMultiLatLonEquipment(multiLatLonEquipment);
    }


    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatusAndTenant(
            Class<T> c, EquipmentFiltering equipmentFiltering,
            SecurityContext securityContext) {
        return equipmentRepository.getProductGroupedByStatusAndTenant(c,
                equipmentFiltering, securityContext);
    }

    public <T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatusAndType(
            Class<T> c, EquipmentFiltering equipmentFiltering,
            SecurityContext securityContext) {
        List<EquipmentStatusGroup> productGroupedByStatusAndTypeList = equipmentRepository
                .getProductGroupedByStatusAndType(c, equipmentFiltering,
                        securityContext);
        // add missing product types and statuses
        Map<String, Map<String, EquipmentStatusGroup>> productGroupedByStatusAndType = productGroupedByStatusAndTypeList
                .parallelStream()
                .collect(
                        Collectors.groupingBy(f -> f.getProductTypeId(),
                                Collectors.toMap(f -> f.getStatusId(), f -> f)));
        List<ProductTypeToProductStatus> productToStatusList = equipmentRepository
                .getAllFiltered(new QueryInformationHolder<>(
                        ProductTypeToProductStatus.class, securityContext));
        for (ProductTypeToProductStatus productTypeToProductStatus : productToStatusList) {
            ProductType productType = productTypeToProductStatus.getLeftside();
            Map<String, EquipmentStatusGroup> statusMap = productGroupedByStatusAndType
                    .computeIfAbsent(productType.getId(), f -> new HashMap<>());
            ProductStatus productStatus = productTypeToProductStatus
                    .getRightside();
            EquipmentStatusGroup item = statusMap.get(productStatus.getId());
            if (item == null) {
                item = new EquipmentStatusGroup(0L, productStatus.getId(),
                        productStatus.getName(),
                        productStatus.getDescription(), productType.getId(),
                        productType.getName());
                statusMap.put(productStatus.getId(), item);
                productGroupedByStatusAndTypeList.add(item);
            }

        }

        return productGroupedByStatusAndTypeList;
    }

    public <T extends Equipment> List<EquipmentSpecificTypeGroup> getProductGroupedBySpecificType(
            Class<T> c, EquipmentFiltering equipmentFiltering,
            SecurityContext securityContext) {
        return equipmentRepository.getProductGroupedBySpecificType(c,
                equipmentFiltering, securityContext);
    }

    public List<LatLon> massUpsertLatLons(
            MassUpsertLatLonRequest massUpsertLatLonRequest,
            SecurityContext securityContext) {
        MultiLatLonEquipment multiLatLonEquipment = massUpsertLatLonRequest
                .getMultiLatLonEquipment();
        LatLonFilter latLonFilter = new LatLonFilter()
                .setMultiLatLonEquipments(
                        Collections.singletonList(multiLatLonEquipment))
                .setFetchSoftDelete(true);
        List<LatLon> existing = listAllLatLons(latLonFilter, securityContext)
                .parallelStream()
                .sorted(Comparator.comparing(f -> f.getOrdinal()))
                .collect(Collectors.toList());
        int i = 0;
        List<Object> toMerge = new ArrayList<>();
        Map<String, LatLon> afterUpdate = new HashMap<>();
        for (LatLonContainer latLonContainer : massUpsertLatLonRequest
                .getList()) {
            CreateLatLon createLatLon = getCreateLatLon(latLonContainer, i);
            createLatLon.setMultiLatLonEquipment(multiLatLonEquipment);
            createLatLon.setSoftDelete(false);
            LatLon latLon = i < existing.size() ? existing.get(i) : null;
            i++;
            if (latLon == null) {
                latLon = createLatLonNoMerge(createLatLon, securityContext);
                toMerge.add(latLon);
            } else {
                if (updateLatLonNoMerge(createLatLon, latLon)) {
                    toMerge.add(latLon);
                }
            }
            afterUpdate.put(latLon.getId(), latLon);
        }
        List<LatLon> toDel = existing.parallelStream()
                .filter(f -> !afterUpdate.containsKey(f.getId()))
                .collect(Collectors.toList());
        for (LatLon latLon : toDel) {
            latLon.setSoftDelete(true);
            toMerge.add(latLon);
        }
        if (massUpsertLatLonRequest.getContextString() != null
                && !massUpsertLatLonRequest.getContextString().equals(
                multiLatLonEquipment.getContextString())) {
            multiLatLonEquipment.setContextString(massUpsertLatLonRequest
                    .getContextString());
            toMerge.add(multiLatLonEquipment);
        }
        equipmentRepository.massMerge(toMerge);
        return new ArrayList<>(afterUpdate.values());
    }

    private CreateLatLon getCreateLatLon(LatLonContainer latLonContainer,
                                         int ordinal) {
        return new CreateLatLon().setLat(latLonContainer.getLat())
                .setLon(latLonContainer.getLon()).setOrdinal(ordinal);
    }

    public void validate(ProductTypeToProductStatusFilter filtering,
                         SecurityContext securityContext) {

        Set<String> statusIds = filtering.getStatusIds();
        Map<String, ProductStatus> statusMap = statusIds.isEmpty()
                ? new HashMap<>()
                : equipmentRepository
                .listByIds(ProductStatus.class, statusIds,
                        securityContext).parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
        statusIds.removeAll(statusMap.keySet());
        if (!statusIds.isEmpty()) {
            throw new BadRequestException("No ProductStatus ids " + statusIds);
        }
        filtering.setStatus(new ArrayList<>(statusMap.values()));

        Set<String> productTypeIds = filtering.getProductTypeIds();
        Map<String, ProductType> productTypeMap = productTypeIds.isEmpty()
                ? new HashMap<>()
                : equipmentRepository
                .listByIds(ProductType.class, productTypeIds,
                        securityContext).parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
        productTypeIds.removeAll(productTypeMap.keySet());
        if (!productTypeIds.isEmpty()) {
            throw new BadRequestException("No ProductType ids "
                    + productTypeIds);
        }
        filtering.setProductTypes(new ArrayList<>(productTypeMap.values()));
    }

    public PaginationResponse<ProductTypeToProductStatus> getAllProductTypeToProductStatus(
            ProductTypeToProductStatusFilter filter,
            SecurityContext securityContext) {
        List<ProductTypeToProductStatus> list = listAllProductTypeToProductStatus(
                filter, securityContext);
        long count = equipmentRepository.countAllProductTypeToProductStatus(
                filter, securityContext);
        return new PaginationResponse<>(list, filter, count);
    }

    @Override
    public List<ProductTypeToProductStatus> listAllProductTypeToProductStatus(
            ProductTypeToProductStatusFilter filter,
            SecurityContext securityContext) {
        return equipmentRepository.listAllProductTypeToProductStatus(filter,
                securityContext);

    }

    public void validate(ProductStatusToProductFilter productTypeFiltering,
                         SecurityContext securityContext) {
        Set<String> ids = productTypeFiltering.getProductIds();
        Map<String, Product> products = ids.isEmpty()
                ? new HashMap<>()
                : listByIds(Product.class, ids, securityContext)
                .parallelStream().collect(
                        Collectors.toMap(f -> f.getId(), f -> f));
        ids.removeAll(products.keySet());
        if (!ids.isEmpty()) {
            throw new BadRequestException("No Product ids " + ids);
        }
        productTypeFiltering.setProducts(new ArrayList<>(products.values()));
    }

    public PaginationResponse<ProductStatusEntry> getProductStatusForProducts(
            ProductStatusToProductFilter productTypeFiltering,
            SecurityContext securityContext) {
        List<ProductToStatus> links = equipmentRepository
                .getStatusLinks(productTypeFiltering.getProducts()
                        .parallelStream().map(f -> f.getId())
                        .collect(Collectors.toSet()));
        List<ProductStatusEntry> collect = links.parallelStream()
                .map(f -> new ProductStatusEntry(f))
                .collect(Collectors.toList());
        return new PaginationResponse<>(collect, productTypeFiltering,
                collect.size());
    }

    public void validate(ProductTypeCreate updateProductType,
                         SecurityContext securityContext) {
        String iconId = updateProductType.getIconId();
        FileResource newIcon = iconId != null ? getByIdOrNull(iconId,
                FileResource.class, null, securityContext) : null;
        if (newIcon == null && iconId != null) {
            throw new BadRequestException("no file resource with id " + iconId);
        }
        updateProductType.setIcon(newIcon);

        String diagram3DId = updateProductType.getDiagram3DId();
        FileResource diagram3d = diagram3DId != null ? getByIdOrNull(
                diagram3DId, FileResource.class, null, securityContext) : null;
        if (diagram3d == null && diagram3DId != null) {
            throw new BadRequestException("no file resource with id "
                    + diagram3DId);
        }
        updateProductType.setDiagram3D(diagram3d);
    }

    public List<EquipmentByStatusEvent> createEquipmentStatusEvent(
            EquipmentFiltering lightFiltering, SecurityContext securityContext) {
        List<EquipmentByStatusEvent> events = new ArrayList<>();
        List<DetailedEquipmentStatus> detailedEquipmentStatuses = new ArrayList<>();
        List<EquipmentByStatusEntry> entries = new ArrayList<>();

        Map<String, String> productTypeToName = new HashMap<>();
        Map<String, String> productStatusToName = new HashMap<>();
        for (Tenant tenant : securityContext.getTenants()) {
            EquipmentFiltering filteringInformationHolder = lightFiltering;
            lightFiltering
                    .setTenantIds(Collections
                            .singletonList(new TenantIdFiltering().setId(tenant
                                    .getId())));
            List<EquipmentAndType> part;
            Map<String, Map<String, Set<String>>> grouping = new HashMap<>();
            filteringInformationHolder.setPageSize(200);
            filteringInformationHolder.setCurrentPage(0);
            while (!(part = equipmentRepository.getEquipmentAndType(
                    Equipment.class, filteringInformationHolder,
                    securityContext)).isEmpty()) {
                Set<String> ids = part.parallelStream().map(f -> f.getId())
                        .collect(Collectors.toSet());
                Map<String, List<ProductStatusNoProductContainer>> statusMap = equipmentRepository
                        .getCurrentStatusLinksContainers(ids).parallelStream()
                        .collect(Collectors.groupingBy(f -> f.getProductId()));
                for (EquipmentAndType equipment : part) {
                    if (equipment.getProductType() != null) {
                        String productTypeId = equipment.getProductType()
                                .getId();
                        productTypeToName.putIfAbsent(productTypeId, equipment
                                .getProductType().getName());
                        List<ProductStatusNoProductContainer> statuses = statusMap
                                .get(equipment.getId());

                        if (statuses != null) {
                            for (ProductStatusNoProductContainer status : statuses) {
                                grouping.computeIfAbsent(productTypeId,
                                        f -> new HashMap<>())
                                        .computeIfAbsent(
                                                status.getStatus().getId(),
                                                f -> new HashSet<>())
                                        .add(equipment.getId());
                                productStatusToName.putIfAbsent(status.getId(),
                                        status.getStatus().getName());
                            }
                        }
                    }

                }
                filteringInformationHolder
                        .setCurrentPage(filteringInformationHolder
                                .getCurrentPage() + 1);

            }
            Instant now = Instant.now();
            String dateString = now.toString();
            Date date = Date.from(now);

            EquipmentByStatusEvent equipmentByStatusEvent = new EquipmentByStatusEvent()
                    .setEventDate(date).setBaseclassTenantId(tenant.getId());
            events.add(equipmentByStatusEvent);
            for (Map.Entry<String, Map<String, Set<String>>> productTypeEntry : grouping
                    .entrySet()) {
                for (Map.Entry<String, Set<String>> productStatusEntry : productTypeEntry
                        .getValue().entrySet()) {
                    EquipmentByStatusEntry equipmentByStatusEntry = new EquipmentByStatusEntry()
                            .setEquipmentByStatusEventId(
                                    equipmentByStatusEvent.getId())
                            .setProductStatus(productStatusEntry.getKey())
                            .setProductTypeId(productTypeEntry.getKey())
                            .setTotal(productStatusEntry.getValue().size())
                            .setName(
                                    dateString
                                            + " "
                                            + productTypeToName
                                            .get(productTypeEntry
                                                    .getKey())
                                            + " "
                                            + productStatusToName
                                            .get(productStatusEntry
                                                    .getKey()));
                    entries.add(equipmentByStatusEntry);
                    for (String equipmentId : productStatusEntry.getValue()) {
                        detailedEquipmentStatuses
                                .add(new DetailedEquipmentStatus()
                                        .setEquipmentId(equipmentId)
                                        .setEquipmentByStatusEntry(
                                                equipmentByStatusEntry.getId()));

                    }
                }
            }

        }
        repository.massMergeEntries(entries);
        repository.massMergeEvents(events);
        repository.massMergeDetailedStatus(detailedEquipmentStatuses);

        return events;
    }

    public void validate(DetailedEquipmentFilter detailedEquipmentFilter) {
        if (detailedEquipmentFilter.getEquipmentByStatusEntryIds() == null
                || detailedEquipmentFilter.getEquipmentByStatusEntryIds()
                .isEmpty()) {
            throw new BadRequestException(
                    "EquipmentByStatusEntryIds must be non null and non empty");
        }
    }

    public PaginationResponse<DetailedEquipmentStatus> getAllDetailedEquipmentStatus(
            DetailedEquipmentFilter detailedEquipmentFilter) {
        List<DetailedEquipmentStatus> list = listAllDetailedEquipmentStatus(detailedEquipmentFilter);
        long count = repository
                .countAllDetailedEquipmentStatus(detailedEquipmentFilter);
        return new PaginationResponse<>(list, detailedEquipmentFilter, count);
    }

    private List<DetailedEquipmentStatus> listAllDetailedEquipmentStatus(
            DetailedEquipmentFilter detailedEquipmentFilter) {
        return repository
                .listAllDetailedEquipmentStatus(detailedEquipmentFilter);
    }

    public PaginationResponse<EquipmentByStatusEntry> getAllEquipmentByStatusEntries(
            EquipmentByStatusEntryFiltering equipmentByStatusEntryFiltering) {
        List<EquipmentByStatusEntry> list = repository
                .listAllEquipmentByStatusEntry(equipmentByStatusEntryFiltering);
        long count = repository
                .countAllEquipmentByStatusEntry(equipmentByStatusEntryFiltering);
        return new PaginationResponse<>(list, equipmentByStatusEntryFiltering,
                count);
    }

    public void setProductToStatusLinksName() {
        List<ProductTypeToProductStatus> productToStatusList = equipmentRepository
                .getAllFiltered(new QueryInformationHolder<>(
                        ProductTypeToProductStatus.class, null));
        List<Object> toMerge = new ArrayList<>();
        for (ProductTypeToProductStatus productTypeToProductStatus : productToStatusList) {
            if (productTypeToProductStatus.getLeftside() != null
                    && productTypeToProductStatus.getRightside() != null) {
                productTypeToProductStatus.setName(productTypeToProductStatus
                        .getLeftside().getName()
                        + "-"
                        + productTypeToProductStatus.getRightside().getName());
                toMerge.add(productTypeToProductStatus);
            }
        }
        equipmentRepository.massMerge(toMerge);
    }

    public void validate(ProductTypeFiltering productTypeFiltering,
                         SecurityContext securityContext) {
        EquipmentFiltering equipmentFiltering = productTypeFiltering
                .getEquipmentFiltering();
        if (equipmentFiltering != null) {
            validateFiltering(equipmentFiltering, securityContext);
        }
    }

    public PaginationResponse<EquipmentFiltering> getAllEquipmentFiltering(
            FilteringInformationHolder equipmentFilteringFiltering,
            SecurityContext securityContext) {
        List<EquipmentFiltering> list = equipmentRepository.getAll(
                EquipmentFiltering.class,
                equipmentFilteringFiltering.getPageSize(),
                equipmentFilteringFiltering.getCurrentPage());
        long count = equipmentRepository.countAll(EquipmentFiltering.class);
        return new PaginationResponse<>(list, equipmentFilteringFiltering,
                count);
    }

    public EquipmentFiltering createEquipmentFiltering(
            EquipmentFiltering equipmentFiltering,
            SecurityContext securityContext) {
        validateFiltering(equipmentFiltering, securityContext);
        equipmentFiltering.prepareForSave();
        equipmentRepository.merge(equipmentFiltering);
        return equipmentFiltering;
    }

    @Override
    public ProductStatus getConnectedStatus() {
        return ExternalServerConnectionManager.getConnected();
    }

    @Override
    public ProductStatus getDisconnectedStatus() {
        return ExternalServerConnectionManager.getDisconnected();
    }

    public List<TypeHolder> listAllEquipmentTypes(EquipmentFiltering equipmentFiltering, SecurityContext securityContext) {
        List<TypeHolder> typeHolders = equipmentRepository.listAllEquipmentTypes(equipmentFiltering, securityContext);
        Set<String> ids = typeHolders.stream().map(f -> f.getId()).collect(Collectors.toSet());
        Map<String, Clazz> clazzes = ids.isEmpty() ? new HashMap<>() : equipmentRepository.listByIds(Clazz.class, ids, null).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        for (TypeHolder typeHolder : typeHolders) {
            Clazz clazz = clazzes.get(typeHolder.getId());
            if (clazz != null) {
                typeHolder.setName(clazz.getName());

            }
        }
        return typeHolders;
    }
}
