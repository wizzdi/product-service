package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import javax.ws.rs.core.Context;
import java.util.List;

public interface IEquipmentService extends ServicePlugin {

    <T extends Equipment> PaginationResponse<T> getAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext);

    <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c, EquipmentGroupFiltering filtering, SecurityContext securityContext);

    <T extends Equipment> T createEquipment(Class<T> c, EquipmentCreate equipmentCreate, SecurityContext securityContext);

    EquipmentToGroup createEquipmentToGroup(LinkToGroup linkToGroup, SecurityContext securityContext);

    boolean updateEquipmentNoMerge(EquipmentCreate equipmentCreate, Equipment equipment);

    Equipment updateEquipment(EquipmentUpdate equipmentUpdate, SecurityContext securityContext);

    PaginationResponse<ProductType> getAllProductTypes(ProductTypeFiltering productTypeFiltering, SecurityContext securityContext);

    PaginationResponse<ProductStatus> getAllProductStatus(ProductStatusFiltering productTypeFiltering, SecurityContext securityContext);

    ProductType getOrCreateProductType(ProductTypeCreate productTypeCreate, SecurityContext securityContext);

    ProductStatus getOrCreateProductStatus(ProductStatusCreate productStatusCreate, SecurityContext securityContext);

    ProductTypeToProductStatus linkProductTypeToProductStatus(ProductStatusToTypeCreate productStatusCreate, SecurityContext securityContext);

    ProductTypeToProductStatus createProductTypeToProductStatusLink(ProductStatusToTypeCreate productStatusCreate, SecurityContext securityContext);

    ProductToStatus linkProductToProductStatusNoMerge(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext);

    List<ProductToStatus> getProductToStatusLinks(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext);

    ProductToStatus linkProductToProductStatus(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext);

    ProductToStatus createProductToProductStatusLinkNoMerge(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext);

    ProductToStatus createProductToProductStatusLink(ProductStatusToProductCreate productStatusCreate, SecurityContext securityContext);

    ProductStatus createProductStatus(ProductStatusCreate productStatusCreate, SecurityContext securityContext);

    List<ProductToStatus> getAvailableProductStatus(Product product, SecurityContext securityContext);

    ProductType createProductType(ProductTypeCreate productTypeCreate, SecurityContext securityContext);

    <T extends Equipment> Class<T> validateFiltering(EquipmentFiltering filtering, @Context SecurityContext securityContext);

}
