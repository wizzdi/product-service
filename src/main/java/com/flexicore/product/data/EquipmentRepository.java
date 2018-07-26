package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.containers.request.EquipmentFiltering;
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
public class EquipmentRepository extends AbstractRepositoryPlugin {

    public <T extends Equipment> List<T> getAllEquipments(Class<T> c, EquipmentFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<T> q=cb.createQuery(c);
        Root<T> r=q.from(c);

        List<Predicate> preds=new ArrayList<>();
        if(!filtering.getEquipmentGroups().isEmpty()){
            Join<T,EquipmentToGroup> join=r.join(Equipment_.equipmentToGroupList);
            Predicate pred=join.get(EquipmentToGroup_.rightside).in(filtering.getEquipmentGroups());
            preds.add(pred);
        }
        if(filtering.getLocationArea()!=null){
            Predicate predicate=cb.between(r.get(Equipment_.lat),filtering.getLocationArea().getLatStart(),filtering.getLocationArea().getLatEnd());
            predicate=cb.and(predicate,cb.between(r.get(Equipment_.lon),filtering.getLocationArea().getLonStart(),filtering.getLocationArea().getLonEnd()));
            preds.add(predicate);
        }
        if(filtering.getProductType()!=null){
            Predicate predicate=cb.equal(r.get(Equipment_.productType),filtering.getProductType());
            preds.add(predicate);
        }
        if(!filtering.getProductStatusList().isEmpty()){
            Join<T,ProductToStatus> join=r.join(Equipment_.productToStatusList);
            Predicate pred=join.get(ProductToStatus_.rightside).in(filtering.getProductStatusList());
            preds.add(pred);
        }



        QueryInformationHolder<T> queryInformationHolder=new QueryInformationHolder<>(filtering,c,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);
    }


    public void massMerge(List<Object> toMerge) {
        for (Object o : toMerge) {
            em.merge(o);
        }
    }
}
