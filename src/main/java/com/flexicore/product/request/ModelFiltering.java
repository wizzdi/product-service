package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.organization.model.Manufacturer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelFiltering extends FilteringInformationHolder {

    private Set<String> manufacturersIds=new HashSet<>();
    private List<Manufacturer> manufacturers;

    public Set<String> getManufacturersIds() {
        return manufacturersIds;
    }

    public ModelFiltering setManufacturersIds(Set<String> manufacturersIds) {
        this.manufacturersIds = manufacturersIds;
        return this;
    }

    @JsonIgnore
    public List<Manufacturer> getManufacturers() {
        return manufacturers;
    }

    public ModelFiltering setManufacturers(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
        return this;
    }
}
