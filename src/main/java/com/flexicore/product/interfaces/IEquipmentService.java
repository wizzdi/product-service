package com.flexicore.product.interfaces;

import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentToGroup;
import com.flexicore.product.model.ProductType;
import com.flexicore.security.SecurityContext;

import javax.ws.rs.core.Context;
import java.util.List;

public interface IEquipmentService extends ServicePlugin {
    <T extends Equipment> List<T> getAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext);

    <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c, EquipmentGroupFiltering filtering, SecurityContext securityContext);

    <T extends Equipment> T createEquipment(Class<T> c, EquipmentCreate equipmentCreate, SecurityContext securityContext);

    EquipmentToGroup createEquipmentToGroup(LinkToGroup linkToGroup, SecurityContext securityContext);

    boolean updateEquipmentNoMerge(EquipmentCreate equipmentCreate, Equipment equipment);

    Equipment updateEquipment(EquipmentUpdate equipmentUpdate, SecurityContext securityContext);

    List<ProductType> getAllProductTypes(ProductTypeFiltering productTypeFiltering, SecurityContext securityContext);

    ProductType createProductType(ProductTypeCreate productTypeCreate, SecurityContext securityContext);

    <T extends Equipment> Class<T> validateFiltering(EquipmentFiltering filtering, @Context SecurityContext securityContext);
}
