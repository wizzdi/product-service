package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.containers.request.EquipmentFiltering;
import com.flexicore.product.containers.request.GroupFiltering;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.EquipmentGroup_;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class EquipmentGroupRepository extends AbstractRepositoryPlugin {

    public List<EquipmentGroup> getAllEquipmentGroups(GroupFiltering filtering, SecurityContext securityContext) {
        Set<String> ids=filtering.getEquipmentGroups().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<EquipmentGroup> q=cb.createQuery(EquipmentGroup.class);
        Root<EquipmentGroup> r=q.from(EquipmentGroup.class);
        List<Predicate> preds=new ArrayList<>();
        if(!ids.isEmpty()){
            Predicate predicate=r.get(EquipmentGroup_.id).in(ids);
            preds.add(predicate);
        }
        QueryInformationHolder<EquipmentGroup> queryInformationHolder=new QueryInformationHolder<>(filtering,EquipmentGroup.class,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    public EquipmentGroup getRootEquipmentGroup(SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<EquipmentGroup> q=cb.createQuery(EquipmentGroup.class);
        Root<EquipmentGroup> r=q.from(EquipmentGroup.class);
        List<Predicate> preds=new ArrayList<>();

            Predicate predicate=r.get(EquipmentGroup_.parent).isNull();
            preds.add(predicate);

        QueryInformationHolder<EquipmentGroup> queryInformationHolder=new QueryInformationHolder<>(EquipmentGroup.class,securityContext);
        queryInformationHolder.setCurrentPage(0);
        queryInformationHolder.setPageSize(1);
        List<EquipmentGroup> list= getAllFiltered(queryInformationHolder,preds,cb,q,r);
        return list.isEmpty()?null:list.get(0);


    }
}
