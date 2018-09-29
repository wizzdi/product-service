package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.SortingOrder;
import com.flexicore.interfaces.PluginRepository;
import com.flexicore.model.Baselink_;
import com.flexicore.model.FileResource;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.SortParameter;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface IEquipmentRepository extends PluginRepository {


    static <T extends Equipment> void addEquipmentFiltering(EquipmentFiltering filtering, CriteriaBuilder cb, Root<T> r, List<Predicate> preds) {
        Set<String> ids;
        if(filtering.getEquipmentIds()!=null&&!(ids=filtering.getEquipmentIds().parallelStream().filter(f->f.getId()!=null).map(f->f.getId()).collect(Collectors.toSet())).isEmpty()){

            Predicate pred=r.get(Equipment_.id).in(ids);
            preds.add(pred);
        }
        else{
            if (!filtering.getEquipmentGroups().isEmpty()) {
                Join<T, EquipmentToGroup> join = r.join(Equipment_.equipmentToGroupList);
                Predicate pred = join.get(Baselink_.rightside).in(filtering.getEquipmentGroups());
                preds.add(pred);
            }
            if (filtering.getLocationArea() != null) {
                Predicate predicate = cb.between(r.get(Equipment_.lat), filtering.getLocationArea().getLatStart(), filtering.getLocationArea().getLatEnd());
                predicate = cb.and(predicate, cb.between(r.get(Equipment_.lon), filtering.getLocationArea().getLonStart(), filtering.getLocationArea().getLonEnd()));
                preds.add(predicate);
            }
            if (filtering.getProductType() != null) {
                Predicate predicate = cb.equal(r.get(Equipment_.productType), filtering.getProductType());
                preds.add(predicate);
            }
            if (!filtering.getProductStatusList().isEmpty()) {
                Join<T, ProductToStatus> join = r.join(Equipment_.productToStatusList);
                Predicate pred = cb.and(join.get(Baselink_.rightside).in(filtering.getProductStatusList()),cb.isTrue(join.get(ProductToStatus_.enabled)));
                preds.add(pred);
            }
            if(!filtering.getTypesToReturn().isEmpty()){
                Predicate pred = r.get("dtype").in(filtering.getTypesToReturn().parallelStream().map(f -> f.getSimpleName()).collect(Collectors.toSet()));
                preds.add(pred);
            }
        }

    }

    static <T extends Equipment> String addEquipmentGeoHashFiltering(EquipmentGroupFiltering filtering, CriteriaBuilder cb, Root<T> r, List<Predicate> preds) {
        IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);

        String geoHashField = "geoHash" + filtering.getPrecision();
        List<SortParameter> sort = new ArrayList<>();
        sort.add(new SortParameter(geoHashField, SortingOrder.ASCENDING));
        filtering.setSort(sort);
        return geoHashField;
    }



    <T extends Equipment> List<T> getAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext);

    <T extends Equipment> long countAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext);

    <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c, EquipmentGroupFiltering filtering, SecurityContext securityContext);

     List<ProductToStatus> getStatusLinks(Set<String> equipmentIds);

    List<ProductTypeToProductStatus> getAllProductTypeToStatusLinks(Set<String> statusIds);
}
