package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FileResource;
import com.flexicore.product.model.EquipmentGroup;

public class ProductTypeCreate {

    private String name;
    private String description;
    private String diagram3DId;
    @JsonIgnore
    private FileResource diagram3D;

    private String iconId;
    @JsonIgnore
    private FileResource icon;

    public String getName() {
        return name;
    }

    public <T extends ProductTypeCreate> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getDescription() {
        return description;
    }

    public <T extends ProductTypeCreate> T setDescription(String description) {
        this.description = description;
        return (T) this;
    }

    public String getDiagram3DId() {
        return diagram3DId;
    }

    public <T extends ProductTypeCreate> T setDiagram3DId(String diagram3DId) {
        this.diagram3DId = diagram3DId;
        return (T) this;
    }

    @JsonIgnore
    public FileResource getDiagram3D() {
        return diagram3D;
    }

    public <T extends ProductTypeCreate> T setDiagram3D(FileResource diagram3D) {
        this.diagram3D = diagram3D;
        return (T) this;
    }

    public String getIconId() {
        return iconId;
    }

    public <T extends ProductTypeCreate> T setIconId(String iconId) {
        this.iconId = iconId;
        return (T) this;
    }

    @JsonIgnore
    public FileResource getIcon() {
        return icon;
    }

    public <T extends ProductTypeCreate> T setIcon(FileResource icon) {
        this.icon = icon;
        return (T) this;
    }
}
