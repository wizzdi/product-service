package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.product.data.ManufacturerRepository;
import com.flexicore.product.interfaces.IManufacturerService;
import com.flexicore.product.request.ManufacturerCreate;
import com.flexicore.product.request.ManufacturerFiltering;
import com.flexicore.product.request.ManufacturerUpdate;
import com.flexicore.security.SecurityContext;

import java.util.List;
import java.util.Set;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class ManufacturerService implements IManufacturerService {

	@PluginInfo(version = 1)
	@Autowired
	private ManufacturerRepository manufacturerRepository;

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids,
			SecurityContext securityContext) {
		return manufacturerRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return manufacturerRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	@Override
	public PaginationResponse<Manufacturer> getAllManufacturers(
			ManufacturerFiltering filtering, SecurityContext securityContext) {
		List<Manufacturer> list = manufacturerRepository.getAllManufacturers(
				filtering, securityContext);
		long count = manufacturerRepository.countAllManufacturers(filtering,
				securityContext);
		return new PaginationResponse<>(list, filtering, count);
	}

	@Override
	public Manufacturer createManufacturer(
			ManufacturerCreate manufacturerCreate,
			SecurityContext securityContext) {
		Manufacturer manufacturer = createManufacturerNoMerge(
				manufacturerCreate, securityContext);
		manufacturerRepository.merge(manufacturer);
		return manufacturer;
	}

	@Override
	public Manufacturer createManufacturerNoMerge(
			ManufacturerCreate manufacturerCreate,
			SecurityContext securityContext) {
		Manufacturer manufacturer = Manufacturer.s().CreateUnchecked(
				manufacturerCreate.getName(), securityContext);
		manufacturer.Init();
		updateManufacturerNoMerge(manufacturer, manufacturerCreate);
		return manufacturer;
	}

	@Override
	public boolean updateManufacturerNoMerge(Manufacturer manufacturer,
			ManufacturerCreate manufacturerCreate) {
		boolean update = false;
		if (manufacturerCreate.getName() != null
				&& !manufacturerCreate.getName().equals(manufacturer.getName())) {
			manufacturer.setName(manufacturerCreate.getName());
			update = true;
		}

		if (manufacturerCreate.getDescription() != null
				&& !manufacturerCreate.getDescription().equals(
						manufacturer.getDescription())) {
			manufacturer.setDescription(manufacturerCreate.getDescription());
			update = true;
		}

		return update;
	}

	@Override
	public void validateManufacturerFiltering(ManufacturerFiltering filtering,
			SecurityContext securityContext) {

	}

	@Override
	public Manufacturer updateManufacturer(
			ManufacturerUpdate manufacturerUpdate,
			SecurityContext securityContext) {
		Manufacturer manufacturer = manufacturerUpdate.getManufacturer();
		if (updateManufacturerNoMerge(manufacturer, manufacturerUpdate)) {
			manufacturerRepository.merge(manufacturer);
		}
		return manufacturer;
	}

	@Override
	public void validate(ManufacturerCreate manufacturerCreate,
			SecurityContext securityContext) {

	}
}
