package com.flexicore.product.response;

import com.flexicore.model.FileResource;
import com.flexicore.product.model.ProductTypeToProductStatus;
import com.flexicore.product.model.StatusLinkToImage;

public class StatusLinkToImageContainer {

    private String id;
    private String name;
    private String description;
    private FileResource image;
    private ProductTypeToProductStatus statusLink;
    private String productTypeId;
    private String productStatusId;

    public StatusLinkToImageContainer(StatusLinkToImage other) {
        this.id = other.getId();
        this.name = other.getName();
        this.description = other.getDescription();
        this.image = other.getImage();
        this.statusLink = other.getStatusLink();
        this.productTypeId = other.getStatusLink()!=null&&other.getStatusLink().getLeftside()!=null?other.getStatusLink().getLeftside().getId():null;
        this.productStatusId =  other.getStatusLink()!=null&&other.getStatusLink().getRightside()!=null?other.getStatusLink().getRightside().getId():null;
    }

    public String getId() {
        return id;
    }

    public <T extends StatusLinkToImageContainer> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public <T extends StatusLinkToImageContainer> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getDescription() {
        return description;
    }

    public <T extends StatusLinkToImageContainer> T setDescription(String description) {
        this.description = description;
        return (T) this;
    }

    public FileResource getImage() {
        return image;
    }

    public <T extends StatusLinkToImageContainer> T setImage(FileResource image) {
        this.image = image;
        return (T) this;
    }

    public ProductTypeToProductStatus getStatusLink() {
        return statusLink;
    }

    public <T extends StatusLinkToImageContainer> T setStatusLink(ProductTypeToProductStatus statusLink) {
        this.statusLink = statusLink;
        return (T) this;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public <T extends StatusLinkToImageContainer> T setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
        return (T) this;
    }

    public String getProductStatusId() {
        return productStatusId;
    }

    public <T extends StatusLinkToImageContainer> T setProductStatusId(String productStatusId) {
        this.productStatusId = productStatusId;
        return (T) this;
    }
}
