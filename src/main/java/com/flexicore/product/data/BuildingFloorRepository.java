package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.interfaces.IEquipmentRepository;
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class BuildingFloorRepository extends AbstractRepositoryPlugin {


    public List<BuildingFloor> listAllBuildingFloors(BuildingFloorFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BuildingFloor> q = cb.createQuery(BuildingFloor.class);
        Root<BuildingFloor> r = q.from(BuildingFloor.class);
        List<Predicate> preds = new ArrayList<>();
        addBuildingFloorPredicates(preds, r, cb, filtering);
        QueryInformationHolder<BuildingFloor> queryInformationHolder = new QueryInformationHolder<>(filtering, BuildingFloor.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);


    }

    public long countAllBuildingFloors(BuildingFloorFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<BuildingFloor> r = q.from(BuildingFloor.class);
        List<Predicate> preds = new ArrayList<>();
        addBuildingFloorPredicates(preds, r, cb, filtering);
        QueryInformationHolder<BuildingFloor> queryInformationHolder = new QueryInformationHolder<>(filtering, BuildingFloor.class, securityContext);
        return countAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    private void addBuildingFloorPredicates(List<Predicate> preds, Root<BuildingFloor> r, CriteriaBuilder cb, BuildingFloorFiltering filtering) {
        if(filtering.getBuildings()!=null && !filtering.getBuildings().isEmpty()){
            Set<String> ids=filtering.getBuildings().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            Join<BuildingFloor, Building> join=r.join(BuildingFloor_.building);
            preds.add(join.get(Building_.id).in(ids));
        }
    }
}
