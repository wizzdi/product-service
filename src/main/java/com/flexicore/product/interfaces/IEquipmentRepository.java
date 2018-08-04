package com.flexicore.product.interfaces;

import com.flexicore.interfaces.PluginRepository;
import com.flexicore.model.Baselink_;
import com.flexicore.product.containers.request.EquipmentFiltering;
import com.flexicore.product.containers.request.EquipmentGroupFiltering;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentToGroup;
import com.flexicore.product.model.Equipment_;
import com.flexicore.product.model.ProductToStatus;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public interface IEquipmentRepository extends PluginRepository {


    static <T extends Equipment> void addEquipmentFiltering(EquipmentFiltering filtering, CriteriaBuilder cb, Root<T> r, List<Predicate> preds) {
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
            Predicate pred = join.get(Baselink_.rightside).in(filtering.getProductStatusList());
            preds.add(pred);
        }
    }

    <T extends Equipment> List<T> getAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext);

    <T extends Equipment> long countAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext);

    <T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(Class<T> c, EquipmentGroupFiltering filtering, SecurityContext securityContext);

    void massMerge(List<Object> toMerge);
}
