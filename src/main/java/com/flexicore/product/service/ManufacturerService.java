package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.product.data.ManufacturerRepository;
import com.flexicore.product.interfaces.IManufacturerService;
import com.flexicore.product.request.ManufacturerCreate;
import com.flexicore.product.request.ManufacturerFiltering;
import com.flexicore.product.request.ManufacturerUpdate;
import com.flexicore.security.SecurityContext;

import java.util.List;
import java.util.Set;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;

@PluginInfo(version = 1)
@Extension
@Component
public class ManufacturerService implements Plugin {

	@PluginInfo(version = 1)
	@Autowired
	private ManufacturerRepository manufacturerRepository;
	@Autowired
	private BasicService basicService;

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return manufacturerRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return manufacturerRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return manufacturerRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return manufacturerRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return manufacturerRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return manufacturerRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return manufacturerRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		manufacturerRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		manufacturerRepository.massMerge(toMerge);
	}

	
	public PaginationResponse<Manufacturer> getAllManufacturers(
			ManufacturerFiltering filtering, SecurityContext securityContext) {
		List<Manufacturer> list = manufacturerRepository.getAllManufacturers(
				filtering, securityContext);
		long count = manufacturerRepository.countAllManufacturers(filtering,
				securityContext);
		return new PaginationResponse<>(list, filtering, count);
	}

	
	public Manufacturer createManufacturer(
			ManufacturerCreate manufacturerCreate,
			SecurityContext securityContext) {
		Manufacturer manufacturer = createManufacturerNoMerge(
				manufacturerCreate, securityContext);
		manufacturerRepository.merge(manufacturer);
		return manufacturer;
	}

	
	public Manufacturer createManufacturerNoMerge(
			ManufacturerCreate manufacturerCreate,
			SecurityContext securityContext) {
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setId(Baseclass.getBase64ID());
		BaseclassService.createSecurityObjectNoMerge(manufacturer,securityContext);
		updateManufacturerNoMerge(manufacturer, manufacturerCreate);
		return manufacturer;
	}

	
	public boolean updateManufacturerNoMerge(Manufacturer manufacturer,
			ManufacturerCreate manufacturerCreate) {
		boolean update = basicService.updateBasicNoMerge(manufacturerCreate,manufacturer);


		return update;
	}

	
	public void validateManufacturerFiltering(ManufacturerFiltering filtering,
			SecurityContext securityContext) {

	}

	
	public Manufacturer updateManufacturer(
			ManufacturerUpdate manufacturerUpdate,
			SecurityContext securityContext) {
		Manufacturer manufacturer = manufacturerUpdate.getManufacturer();
		if (updateManufacturerNoMerge(manufacturer, manufacturerUpdate)) {
			manufacturerRepository.merge(manufacturer);
		}
		return manufacturer;
	}

	
	public void validate(ManufacturerCreate manufacturerCreate,
			SecurityContext securityContext) {

	}
}
