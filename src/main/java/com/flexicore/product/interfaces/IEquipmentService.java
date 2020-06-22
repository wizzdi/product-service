package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.InitPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.iot.model.FlexiCoreServer;
import com.flexicore.model.FileResource;
import com.flexicore.product.containers.request.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import com.flexicore.product.model.*;
import com.flexicore.product.request.CreateMultiLatLonEquipment;
import com.flexicore.product.request.LatLonFilter;
import com.flexicore.product.request.ProductTypeToProductStatusFilter;
import com.flexicore.security.SecurityContext;

import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IEquipmentService extends ServicePlugin, InitPlugin {

	ProductType getGatewayProductType();

	ProductStatus getOnProductStatus();

	ProductStatus getOffProductStatus();

	ProductStatus getCommErrorProductStatus();

	ProductStatus getError();

	ProductType getBuildingProductType();

	FlexiCoreServer getFlexiCoreServerToSync(Equipment equipment);

	FlexiCoreGateway getThisFlexiCoreGateway(SecurityContext securityContext);

	<T extends Equipment> PaginationResponse<T> getAllEquipments(Class<T> c,
			EquipmentFiltering filtering, SecurityContext securityContext);

	<T extends Equipment> List<T> listAllEquipments(Class<T> c,
			EquipmentFiltering filtering, SecurityContext securityContext);

	List<FlexiCoreGateway> listAllFlexiCoreGateways(
			FlexiCoreGatewayFiltering filtering, SecurityContext securityContext);

	<T extends Equipment> PaginationResponse<EquipmentGroupHolder> getAllEquipmentsGrouped(
			Class<T> c, EquipmentGroupFiltering filtering,
			SecurityContext securityContext);

	<T extends Equipment> T createEquipment(Class<T> c,
			EquipmentCreate equipmentCreate, SecurityContext securityContext);

	<T extends Equipment> T createEquipmentNoMerge(Class<T> c,
			EquipmentCreate equipmentCreate, SecurityContext securityContext);

	Gateway createGatewayNoMerge(GatewayCreate equipmentCreate,
			SecurityContext securityContext);

	<T extends Gateway> boolean updateGatewayNoMerge(
			GatewayCreate equipmentCreate, T equipment);

	void updateProductStatus(Product light,
			List<ProductToStatus> allExistingStatus,
			SecurityContext securityContext, List<Object> toMerge,
			ProductStatus newStatus);

	void updateProductStatus(Product product,
			List<ProductToStatus> allExistingStatus,
			SecurityContext securityContext, List<Object> toMerge,
			List<ProductStatus> newStatuses);

	EquipmentToGroup createEquipmentToGroup(LinkToGroup linkToGroup,
			SecurityContext securityContext);

	void validateEquipmentCreate(EquipmentCreate equipmentCreate,
			SecurityContext securityContext);

	void validateProductCreate(ProductCreate equipmentCreate,
			SecurityContext securityContext);

	boolean updateProductNoMerge(ProductCreate equipmentCreate,
			Product equipment);

	boolean updateEquipmentNoMerge(EquipmentCreate equipmentCreate,
			Equipment equipment);

	void generateGeoHash(Equipment equipment);

	Equipment updateEquipment(EquipmentUpdate equipmentUpdate,
			SecurityContext securityContext);

	PaginationResponse<ProductType> getAllProductTypes(
			ProductTypeFiltering productTypeFiltering,
			SecurityContext securityContext);

	List<ProductType> listAllProductTypes(
			ProductTypeFiltering productTypeFiltering,
			SecurityContext securityContext);

	PaginationResponse<ProductStatus> getAllProductStatus(
			ProductStatusFiltering productTypeFiltering,
			SecurityContext securityContext);

	ProductType getOrCreateProductType(ProductTypeCreate productTypeCreate,
			SecurityContext securityContext);

	ProductStatus getOrCreateProductStatus(
			ProductStatusCreate productStatusCreate,
			SecurityContext securityContext);

	ProductTypeToProductStatus linkProductTypeToProductStatus(
			ProductStatusToTypeCreate productStatusCreate,
			SecurityContext securityContext);

	ProductTypeToProductStatus createProductTypeToProductStatusLink(
			ProductStatusToTypeCreate productStatusCreate,
			SecurityContext securityContext);

	ProductToStatus linkProductToProductStatusNoMerge(
			ProductStatusToProductCreate productStatusCreate,
			SecurityContext securityContext);

	List<ProductToStatus> getProductToStatusLinks(
			ProductStatusToProductCreate productStatusCreate,
			SecurityContext securityContext);

	ProductToStatus linkProductToProductStatus(
			ProductStatusToProductCreate productStatusCreate,
			SecurityContext securityContext);

	ProductToStatus createProductToProductStatusLinkNoMerge(
			ProductStatusToProductCreate productStatusCreate,
			SecurityContext securityContext);

	ProductToStatus createProductToProductStatusLink(
			ProductStatusToProductCreate productStatusCreate,
			SecurityContext securityContext);

	ProductStatus createProductStatus(ProductStatusCreate productStatusCreate,
			SecurityContext securityContext);

	List<ProductToStatus> getAvailableProductStatus(Product product,
			SecurityContext securityContext);

	ProductType createProductType(ProductTypeCreate productTypeCreate,
			SecurityContext securityContext);

	ProductType createProductTypeNoMerge(ProductTypeCreate productTypeCreate,
			SecurityContext securityContext);

	<T extends Equipment> Class<T> validateFiltering(
			EquipmentFiltering filtering,
			@Context SecurityContext securityContext);

	<T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatus(
			Class<T> c, EquipmentFiltering equipmentFiltering,
			SecurityContext securityContext);

	boolean updateMultiLatLonEquipmentNoMerge(
			CreateMultiLatLonEquipment createMultiLatLonEquipment,
			MultiLatLonEquipment multiLatLonEquipment);

	MultiLatLonEquipment createMultiLatLonEquipmentNoMerge(
			CreateMultiLatLonEquipment createMultiLatLonEquipment,
			SecurityContext securityContext);

	List<LatLon> listAllLatLons(LatLonFilter latLonFilter,
			SecurityContext securityContext);

	List<ProductToStatus> getStatusLinks(Set<String> collect);

	List<ProductToStatus> getCurrentStatusLinks(Set<String> collect);

	Map<String, String> buildSpecificStatusIconMap(
			Map<String, String> typeSpecificStatusToIcon,
			List<ProductStatus> status);

	List<ProductTypeToProductStatus> getAllProductTypeToStatusLinks(
			Set<String> statusIds);

	void massMerge(List<?> toMerge);

	FlexiCoreGateway createFlexiCoreGateway(
			FlexiCoreGatewayCreate gatewayCreate,
			SecurityContext securityContext);

	<T extends Equipment> List<EquipmentStatusGroup> getProductGroupedByStatusAndType(
			Class<T> c, EquipmentFiltering equipmentFiltering,
			SecurityContext securityContext);

	List<ProductTypeToProductStatus> listAllProductTypeToProductStatus(
			ProductTypeToProductStatusFilter filter,
			SecurityContext securityContext);
}
