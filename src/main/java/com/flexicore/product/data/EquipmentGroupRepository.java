package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baselink_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.model.*;
import com.flexicore.product.request.EquipmentToGroupFiltering;
import com.flexicore.security.SecurityContext;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class EquipmentGroupRepository extends AbstractRepositoryPlugin {

    public List<EquipmentGroup> getAllEquipmentGroups(GroupFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<EquipmentGroup> q=cb.createQuery(EquipmentGroup.class);
        Root<EquipmentGroup> r=q.from(EquipmentGroup.class);
        List<Predicate> preds = getAllEquipmentGroupsPredicates(filtering, r,cb);
        QueryInformationHolder<EquipmentGroup> queryInformationHolder=new QueryInformationHolder<>(filtering,EquipmentGroup.class,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    private List<Predicate> getAllEquipmentGroupsPredicates(GroupFiltering filtering, Root<EquipmentGroup> r,CriteriaBuilder cb) {
        Set<String> ids=filtering.getEquipmentGroups().parallelStream().map(f->f.getId()).collect(Collectors.toSet());

        List<Predicate> preds=new ArrayList<>();
        if(!ids.isEmpty()){
            Predicate predicate=r.get(EquipmentGroup_.id).in(ids);
            preds.add(predicate);
        }
        if(filtering.getEquipment()!=null && !filtering.getEquipment().isEmpty()){
            Join<EquipmentGroup, EquipmentToGroup> join=r.join(EquipmentGroup_.equipmentToGroupList);
            Join<EquipmentToGroup, Equipment> equipmentJoin=cb.treat(join.join(Baselink_.leftside),Equipment.class);
            List<String> equipmentIds=filtering.getEquipment().parallelStream().map(f->f.getId()).collect(Collectors.toList());
            preds.add(equipmentJoin.get(Equipment_.id).in(equipmentIds));
        }
        return preds;
    }

    public long countAllEquipmentGroups(GroupFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Long> q=cb.createQuery(Long.class);
        Root<EquipmentGroup> r=q.from(EquipmentGroup.class);
        List<Predicate> preds = getAllEquipmentGroupsPredicates(filtering, r,cb);
        QueryInformationHolder<EquipmentGroup> queryInformationHolder=new QueryInformationHolder<>(filtering,EquipmentGroup.class,securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);
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

    public List<EquipmentToGroup> getEquipmentToGroup(EquipmentToGroupFiltering equipmentToGroupFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<EquipmentToGroup> q=cb.createQuery(EquipmentToGroup.class);
        Root<EquipmentToGroup> r=q.from(EquipmentToGroup.class);
        List<Predicate> preds=new ArrayList<>();
        addEquipmentToGroupPredicates(preds,equipmentToGroupFiltering,r,cb);
       QueryInformationHolder<EquipmentToGroup> queryInformationHolder=new QueryInformationHolder<>(equipmentToGroupFiltering,EquipmentToGroup.class,securityContext);
       return getAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    private void addEquipmentToGroupPredicates(List<Predicate> preds, EquipmentToGroupFiltering equipmentToGroupFiltering, Root<EquipmentToGroup> r, CriteriaBuilder cb) {
        if(!equipmentToGroupFiltering.isRaw()&&equipmentToGroupFiltering.getEquipments()!=null && ! equipmentToGroupFiltering.getEquipments().isEmpty()){
            Set<String> ids=equipmentToGroupFiltering.getEquipments().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            Join<EquipmentToGroup,Equipment> join=cb.treat(r.join(Baselink_.leftside),Equipment.class);
            preds.add(join.get(Equipment_.id).in(ids));
        }
        if(!equipmentToGroupFiltering.isRaw()&&equipmentToGroupFiltering.getGroups()!=null && ! equipmentToGroupFiltering.getGroups().isEmpty()){
            Set<String> ids=equipmentToGroupFiltering.getGroups().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            Join<EquipmentToGroup,EquipmentGroup> join=cb.treat(r.join(Baselink_.rightside),EquipmentGroup.class);
            preds.add(join.get(EquipmentGroup_.id).in(ids));
        }

        if(equipmentToGroupFiltering.isRaw()&&equipmentToGroupFiltering.getEquipmentIds()!=null && ! equipmentToGroupFiltering.getEquipmentIds().isEmpty()){
            Set<String> ids=equipmentToGroupFiltering.getEquipmentIds();
            Join<EquipmentToGroup,Equipment> join=cb.treat(r.join(Baselink_.leftside),Equipment.class);
            preds.add(join.get(Equipment_.id).in(ids));
        }
        if(equipmentToGroupFiltering.isRaw()&&equipmentToGroupFiltering.getGroupIds()!=null && ! equipmentToGroupFiltering.getGroupIds().isEmpty()){
            Set<String> ids=equipmentToGroupFiltering.getGroupIds();
            Join<EquipmentToGroup,EquipmentGroup> join=cb.treat(r.join(Baselink_.rightside),EquipmentGroup.class);
            preds.add(join.get(EquipmentGroup_.id).in(ids));
        }


    }


}
