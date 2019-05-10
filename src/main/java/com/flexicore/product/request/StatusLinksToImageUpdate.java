package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.StatusLinkToImage;

public class StatusLinksToImageUpdate extends StatusLinksToImageCreate{

    private String id;
    @JsonIgnore
    private StatusLinkToImage statusLinkToImage;

    public String getId() {
        return id;
    }

    public <T extends StatusLinksToImageUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public StatusLinkToImage getStatusLinkToImage() {
        return statusLinkToImage;
    }

    public <T extends StatusLinksToImageUpdate> T setStatusLinkToImage(StatusLinkToImage statusLinkToImage) {
        this.statusLinkToImage = statusLinkToImage;
        return (T) this;
    }
}
