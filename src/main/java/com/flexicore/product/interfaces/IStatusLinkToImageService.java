package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.model.StatusLinkToImage;
import com.flexicore.product.request.StatusLinksToImageCreate;
import com.flexicore.product.request.StatusLinksToImageFilter;
import com.flexicore.product.request.StatusLinksToImageUpdate;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IStatusLinkToImageService extends ServicePlugin {
    void validate(StatusLinksToImageFilter filtering, SecurityContext securityContext);

    PaginationResponse<StatusLinkToImage> getAllStatusLinksToImage(StatusLinksToImageFilter filtering, SecurityContext securityContext);

    List<StatusLinkToImage> listAllStatusLinksToImage(StatusLinksToImageFilter filtering, SecurityContext securityContext);

    void validateCreate(StatusLinksToImageCreate statusLinksToImageCreate, SecurityContext securityContext);

    void validateUpdate(StatusLinksToImageUpdate statusLinksToImageCreate, SecurityContext securityContext);

    StatusLinkToImage createStatusLinkToImage(StatusLinksToImageCreate statusLinksToImageCreate, SecurityContext securityContext);

    StatusLinkToImage createStatusLinkToImageNoMerge(StatusLinksToImageCreate statusLinksToImageCreate, SecurityContext securityContext);

    boolean updateStatusLinkToImageNoMerge(StatusLinksToImageCreate statusLinksToImageCreate, StatusLinkToImage statusLinkToImage);

    StatusLinkToImage updateStatusLinkToImage(StatusLinksToImageUpdate statusLinksToImageUpdate, SecurityContext securityContext);
}
