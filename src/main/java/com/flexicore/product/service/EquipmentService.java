package com.flexicore.product.service;

import ch.hsr.geohash.GeoHash;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.data.EquipmentRepository;
import com.flexicore.product.model.*;
import com.flexicore.product.rest.EquipmentRESTService;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaselinkService;
import org.apache.commons.beanutils.PropertyUtils;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class EquipmentService implements ServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentRepository equipmentRepository;

    @Inject
    private BaselinkService baselinkService;

    @Inject
    @PluginInfo(version = 1)
    private GroupService groupService;

    @Inject
    private Logger logger;

    private static Map<String,Method> setterCache=new ConcurrentHashMap<>();


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return equipmentRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return equipmentRepository.getByIdOrNull(id, c, batchString, securityContext);
    }

    public <T extends Equipment> List<T> getAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        return equipmentRepository.getAllEquipments(c, filtering, securityContext);
    }

    public <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c,EquipmentGroupFiltering filtering, SecurityContext securityContext) {
        return equipmentRepository.getAllEquipmentsGrouped(c,filtering, securityContext);
    }

    public <T extends Equipment> T createEquipment(Class<T> c, EquipmentCreate equipmentCreate, SecurityContext securityContext) {
        T equipment = Baseclass.createUnckehcked(c, equipmentCreate.getName(), securityContext.getUser());
        equipment.Init();
        updateEquipmentNoMerge(equipmentCreate, equipment);
        equipmentRepository.merge(equipment);
        return equipment;
    }

    public EquipmentToGroup createEquipmentToGroup(LinkToGroup linkToGroup, SecurityContext securityContext) {
        return baselinkService.linkEntities(linkToGroup.getEquipment(), linkToGroup.getEquipmentGroup(), EquipmentToGroup.class);

    }


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

    private Method getSetterOrNull(String name){
        try {
            return Equipment.class.getMethod(name, String.class);
        } catch (NoSuchMethodException e) {
            logger.log(Level.SEVERE,"unable to get setter",e);
        }
        return null;
    }

    private void generateGeoHash(Equipment equipment) {
        for (int i = 1; i < 13; i++) {
            String setterName = "setGeoHash" + i;
            try {
                String geoHash=GeoHash.geoHashStringWithCharacterPrecision(equipment.getLat(),equipment.getLon(),i);
                Method method = setterCache.computeIfAbsent(setterName,f->getSetterOrNull(f));
                if(method!=null){
                    method.invoke(equipment,geoHash);

                }
            } catch ( InvocationTargetException | IllegalAccessException e) {
                logger.log(Level.SEVERE,"could not set property "+setterName +" via setter");
            }

        }
    }

    public Equipment updateEquipment(EquipmentUpdate equipmentUpdate, SecurityContext securityContext) {
        if (updateEquipmentNoMerge(equipmentUpdate, equipmentUpdate.getEquipment())) {
            equipmentRepository.merge(equipmentUpdate.getEquipment());
        }
        return equipmentUpdate.getEquipment();
    }

    public List<ProductType> getAllProductTypes(ProductTypeFiltering productTypeFiltering, SecurityContext securityContext) {
        QueryInformationHolder<ProductType> queryInformationHolder = new QueryInformationHolder<>(productTypeFiltering, ProductType.class, securityContext);
        return equipmentRepository.getAllFiltered(queryInformationHolder);
    }

    public ProductType createProductType(ProductTypeCreate productTypeCreate, SecurityContext securityContext) {
        ProductType productType = ProductType.s().CreateUnchecked(productTypeCreate.getName(), securityContext.getUser());
        productType.Init();
        productType.setDescription(productTypeCreate.getDescription());
        return productType;
    }

    public <T extends Equipment> Class<T> validateFiltering(EquipmentFiltering filtering, @Context SecurityContext securityContext) {
        Class<T> c= (Class<T>) Equipment.class;
        if(filtering.getCanonicalClassName()!=null &&!filtering.getCanonicalClassName().isEmpty()){
            try {
                 c= (Class<T>) Class.forName(filtering.getCanonicalClassName());

            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE,"unable to get class: "+filtering.getCanonicalClassName());
                throw new BadRequestException("No Class with name "+filtering.getCanonicalClassName());

            }
        }
        List<EquipmentGroup> groups=filtering.getGroupIds().isEmpty()?new ArrayList<>(): groupService.listByIds(EquipmentGroup.class,filtering.getGroupIds(),securityContext);
        filtering.getGroupIds().removeAll(groups.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!filtering.getGroupIds().isEmpty()){
            throw new BadRequestException("could not find groups with ids "+filtering.getGroupIds().parallelStream().collect(Collectors.joining(",")));
        }
        filtering.setEquipmentGroups(groups);
        ProductType productType=filtering.getProductTypeId()!=null?null:getByIdOrNull(filtering.getProductTypeId(),ProductType.class,null,securityContext);
        if(filtering.getProductTypeId()!=null && productType==null){
            throw new BadRequestException("No Product type with id "+filtering.getProductTypeId());
        }
        filtering.setProductType(productType);
        List<ProductStatus> status=filtering.getProductStatusIds().isEmpty()?new ArrayList<>():listByIds(ProductStatus.class,filtering.getProductStatusIds(),securityContext);
        filtering.getProductStatusIds().removeAll(status.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!filtering.getProductStatusIds().isEmpty()){
            throw new BadRequestException("could not find status with ids "+filtering.getProductStatusIds().parallelStream().collect(Collectors.joining(",")));
        }
        filtering.setProductStatusList(status);
        return c;
    }


}
