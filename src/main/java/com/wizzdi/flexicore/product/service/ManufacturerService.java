package com.wizzdi.flexicore.product.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.product.data.ManufacturerRepository;
import com.wizzdi.flexicore.product.model.Manufacturer;
import com.wizzdi.flexicore.product.request.ManufacturerCreate;
import com.wizzdi.flexicore.product.request.ManufacturerFilter;
import com.wizzdi.flexicore.product.request.ManufacturerUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class ManufacturerService implements Plugin {

    @Autowired
    private ManufacturerRepository repository;

    @Autowired
    private BasicService basicService;

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return repository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return repository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return repository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        repository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }

    public void validateFiltering(ManufacturerFilter filtering,
                                  SecurityContextBase securityContext) {
        basicService.validate(filtering, securityContext);
    }

    public PaginationResponse<Manufacturer> getAllManufacturers(
            SecurityContextBase securityContext, ManufacturerFilter filtering) {
        List<Manufacturer> list = listAllManufacturers(securityContext, filtering);
        long count = repository.countAllManufacturers(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Manufacturer> listAllManufacturers(SecurityContextBase securityContext, ManufacturerFilter filtering) {
        return repository.getAllManufacturers(securityContext, filtering);
    }

    public Manufacturer createManufacturer(ManufacturerCreate creationContainer,
                                           SecurityContextBase securityContext) {
        Manufacturer manufacturer = createManufacturerNoMerge(creationContainer, securityContext);
        repository.merge(manufacturer);
        return manufacturer;
    }

    public Manufacturer createManufacturerNoMerge(ManufacturerCreate creationContainer,
                                                  SecurityContextBase securityContext) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(Baseclass.getBase64ID());

        updateManufacturerNoMerge(manufacturer, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(manufacturer, securityContext);
        return manufacturer;
    }

    public boolean updateManufacturerNoMerge(Manufacturer manufacturer,
                                             ManufacturerCreate creationContainer) {
        boolean update = basicService.updateBasicNoMerge(creationContainer, manufacturer);

        return update;
    }

    public Manufacturer updateManufacturer(ManufacturerUpdate updateContainer,
                                           SecurityContextBase securityContext) {
        Manufacturer manufacturer = updateContainer.getManufacturer();
        if (updateManufacturerNoMerge(manufacturer, updateContainer)) {
            repository.merge(manufacturer);
        }
        return manufacturer;
    }

    public void validate(ManufacturerCreate creationContainer,
                         SecurityContextBase securityContext) {
        basicService.validate(creationContainer, securityContext);
    }
}