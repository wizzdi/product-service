package com.flexicore.product.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.product.interfaces.IEquipmentRepository;
import com.flexicore.product.model.Building;
import com.flexicore.product.model.BuildingFiltering;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
public class BuildingRepository extends AbstractRepositoryPlugin {


    public List<Building> listAllBuildings(BuildingFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Building> q = cb.createQuery(Building.class);
        Root<Building> r = q.from(Building.class);
        List<Predicate> preds = new ArrayList<>();
        addBuildingPredicates(preds, r, cb, filtering);
        QueryInformationHolder<Building> queryInformationHolder = new QueryInformationHolder<>(filtering, Building.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);


    }

    public long countAllBuildings(BuildingFiltering filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Building> r = q.from(Building.class);
        List<Predicate> preds = new ArrayList<>();
        addBuildingPredicates(preds, r, cb, filtering);
        QueryInformationHolder<Building> queryInformationHolder = new QueryInformationHolder<>(filtering, Building.class, securityContext);
        return countAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    private void addBuildingPredicates(List<Predicate> preds, Root<Building> r, CriteriaBuilder cb, BuildingFiltering filtering) {
        IEquipmentRepository.addEquipmentFiltering(filtering,cb,r,preds);
    }
}
