package com.flexicore.product.service;

import com.flexicore.events.BaseclassCreated;
import com.flexicore.events.BaseclassUpdated;
import com.flexicore.model.Baseclass;
import com.flexicore.model.PermissionGroup;
import com.flexicore.model.PermissionGroupToBaseclass;
import com.flexicore.product.model.ProductType;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.PermissionGroupToBaseclassCreate;
import com.wizzdi.flexicore.security.request.PermissionGroupToBaseclassFilter;
import com.wizzdi.flexicore.security.service.PermissionGroupToBaseclassService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Extension
public class ProductTypeService implements Plugin {

	private static final Logger logger = LoggerFactory.getLogger(ProductTypeService.class);

	@Autowired
	private PermissionGroupToBaseclassService permissionGroupToBaseclassService;

	@Autowired
	@Qualifier("adminSecurityContext")
	private SecurityContextBase<?, ?, ?, ?> securityContextBase;

	@Async
	@EventListener
	public void onPermissionGroupToBaseclassCreated(BaseclassUpdated<ProductType> baseclassUpdated) {
		List<PermissionGroupToBaseclass> permissionGroupToBaseclasses = permissionGroupToBaseclassService.listAllPermissionGroupToBaseclass(new PermissionGroupToBaseclassFilter().setRightside(Collections.singletonList(baseclassUpdated.getBaseclass())), null);
		addProductTypeItemsToPermissionGroup(permissionGroupToBaseclasses);
	}

	@Async
	@EventListener
	public void onPermissionGroupToBaseclassCreated(BaseclassCreated<PermissionGroupToBaseclass> baseclassBaseclassCreated) {
		PermissionGroupToBaseclass link = baseclassBaseclassCreated.getBaseclass();
		if (link.getRightside() instanceof ProductType) {
			addProductTypeItemsToPermissionGroup(Collections.singletonList(link));
		}
	}

	private void addProductTypeItemsToPermissionGroup(List<PermissionGroupToBaseclass> links) {
		List<Baseclass> stuffToAdd = new ArrayList<>();
		List<PermissionGroup> permissionGroups=links.stream().map(f->f.getLeftside()).collect(Collectors.toList());
		for (PermissionGroupToBaseclass link : links) {
			ProductType productType = (ProductType) link.getRightside();
			if (productType.getDiagram3D() != null) {
				stuffToAdd.add(productType.getDiagram3D());
			}
			if (productType.getImage() != null) {
				stuffToAdd.add(productType.getImage());
			}
		}

		if (!stuffToAdd.isEmpty()) {
			Map<String,Map<String, PermissionGroupToBaseclass>> map = permissionGroupToBaseclassService.listAllPermissionGroupToBaseclass(new PermissionGroupToBaseclassFilter().setLeftside(new ArrayList<>(permissionGroups)).setRightside(stuffToAdd), null).stream().collect(Collectors.groupingBy(f -> f.getRightside().getId(), Collectors.toMap(f->f.getLeftside().getId(), f->f,(a, b)->a)));
			List<Object> toMerge = new ArrayList<>();
			for (Baseclass baseclass : stuffToAdd) {
				Map<String,PermissionGroupToBaseclass> permissionGroupToBaseclassMap = map.computeIfAbsent(baseclass.getId(),f->new HashMap<>());
				for (PermissionGroup permissionGroup : permissionGroups) {
					PermissionGroupToBaseclass permissionGroupToBaseclass=permissionGroupToBaseclassMap.get(permissionGroup.getId());
					PermissionGroupToBaseclassCreate permissionGroupToBaseclassCreate = new PermissionGroupToBaseclassCreate()
							.setBaseclass(baseclass)
							.setPermissionGroup(permissionGroup);
					if (permissionGroupToBaseclass == null) {
						permissionGroupToBaseclass = permissionGroupToBaseclassService.createPermissionGroupToBaseclassNoMerge(permissionGroupToBaseclassCreate, securityContextBase);
						toMerge.add(permissionGroupToBaseclass);
					}
				}

			}
			logger.info("added total of " + toMerge.size() + " to permission groups " + permissionGroups.stream().map(f->f.getName()+"("+f.getId()+")").collect(Collectors.joining(",")));
			permissionGroupToBaseclassService.massMerge(toMerge);

		}
	}

}

