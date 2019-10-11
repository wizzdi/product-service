package com.flexicore.product.interfaces;

import com.flexicore.interfaces.PluginRepository;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.GatewayFiltering;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public interface IGatewayRepository extends PluginRepository {

    static void addGatewayPredicates(List<Predicate> preds, Root<Gateway> r, CriteriaBuilder cb, GatewayFiltering filtering) {
        IEquipmentRepository.addEquipmentFiltering(filtering,cb,r,preds);
    }
}
