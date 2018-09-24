package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.model.FileResource;
import com.flexicore.model.Tenant;
import com.flexicore.model.dynamic.ExecutionParametersHolder;

import java.io.Serializable;

public class ImportCSVRequest extends ExecutionParametersHolder implements Serializable {

    @FieldInfo(displayName = "tenantId",description = "tenant to move objects to")
    private String tenantId;
    @FieldInfo(displayName = "csvFileResourceId",description = "csv fileresource Id")

    private String fileResourceId;
    @JsonIgnore
    private FileResource fileResource;
    @JsonIgnore
    private Tenant tenant;

    private String descriminatorFieldName;

    public String getTenantId() {
        return tenantId;
    }

    public ImportCSVRequest setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @JsonIgnore
    public Tenant getTenant() {
        return tenant;
    }

    public ImportCSVRequest setTenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public String getFileResourceId() {
        return fileResourceId;
    }

    public ImportCSVRequest setFileResourceId(String fileResourceId) {
        this.fileResourceId = fileResourceId;
        return this;
    }

    @JsonIgnore
    public FileResource getFileResource() {
        return fileResource;
    }

    public ImportCSVRequest setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
        return this;
    }

    public String getDescriminatorFieldName() {
        return descriminatorFieldName;
    }

    public ImportCSVRequest setDescriminatorFieldName(String descriminatorFieldName) {
        this.descriminatorFieldName = descriminatorFieldName;
        return this;
    }
}
