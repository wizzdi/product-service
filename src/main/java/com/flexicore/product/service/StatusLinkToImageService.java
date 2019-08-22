package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.FileResource;
import com.flexicore.product.data.StatusLinkToImageRepository;
import com.flexicore.product.interfaces.IStatusLinkToImageService;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductType;
import com.flexicore.product.model.ProductTypeToProductStatus;
import com.flexicore.product.model.StatusLinkToImage;
import com.flexicore.product.request.StatusLinksToImageCreate;
import com.flexicore.product.request.StatusLinksToImageFilter;
import com.flexicore.product.request.StatusLinksToImageUpdate;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class StatusLinkToImageService implements IStatusLinkToImageService {

    @Inject
    @PluginInfo(version = 1)
    private StatusLinkToImageRepository repository;

    @Inject
    private Logger logger;

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batchString, securityContext);
    }

    @Override
    public void validate(StatusLinksToImageFilter filtering, SecurityContext securityContext) {
        Set<String> statusIds = filtering.getStatusIds();
        Map<String, ProductStatus> statusMap = statusIds.isEmpty() ? new HashMap<>() : repository.listByIds(ProductStatus.class, statusIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        statusIds.removeAll(statusMap.keySet());
        if (!statusIds.isEmpty()) {
            logger.warning("No ProductStatus ids " + statusIds);

            //throw new BadRequestException("No ProductStatus ids " + statusIds);
        }
        filtering.setStatus(new ArrayList<>(statusMap.values()));

        Set<String> statusLinkIds = filtering.getStatusLinkIds();
        Map<String, ProductTypeToProductStatus> productTypeToProductStatusMap = statusLinkIds.isEmpty() ? new HashMap<>() : repository.listByIds(ProductTypeToProductStatus.class, statusLinkIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        statusLinkIds.removeAll(productTypeToProductStatusMap.keySet());
        if (!statusLinkIds.isEmpty()) {
            logger.warning("No ProductTypeToProductStatus ids " + statusLinkIds);

            //throw new BadRequestException("No ProductTypeToProductStatus ids " + statusLinkIds);
        }
        filtering.setStatusLinks(new ArrayList<>(productTypeToProductStatusMap.values()));

        Set<String> productTypeIds = filtering.getProductTypeIds();
        Map<String, ProductType> productTypeMap = productTypeIds.isEmpty() ? new HashMap<>() : repository.listByIds(ProductType.class, productTypeIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        productTypeIds.removeAll(productTypeMap.keySet());
        if (!productTypeIds.isEmpty()) {
            logger.warning("No ProductType ids " + statusLinkIds);

            //throw new BadRequestException("No ProductType ids " + productTypeIds);
        }
        filtering.setProductTypes(new ArrayList<>(productTypeMap.values()));

    }

    @Override
    public PaginationResponse<StatusLinkToImage> getAllStatusLinksToImage(StatusLinksToImageFilter filtering, SecurityContext securityContext) {
        List<StatusLinkToImage> list = listAllStatusLinksToImage(filtering, securityContext);
        long count = repository.countAllStatusLinksToImage(filtering, securityContext);
        return new PaginationResponse<>(list, filtering, count);
    }

    @Override
    public List<StatusLinkToImage> listAllStatusLinksToImage(StatusLinksToImageFilter filtering, SecurityContext securityContext) {
        return repository.listAllStatusLinksToImage(filtering, securityContext);
    }

    public void populate(StatusLinksToImageCreate statusLinksToImageCreate, SecurityContext securityContext) {
        String statusLinkId = statusLinksToImageCreate.getStatusLinkId();
        ProductTypeToProductStatus statusLink = statusLinkId == null ? null : getByIdOrNull(statusLinkId, ProductTypeToProductStatus.class, null, securityContext);
        statusLinksToImageCreate.setProductTypeToProductStatus(statusLink);

        String imageId = statusLinksToImageCreate.getImageId();
        FileResource image = imageId == null ? null : getByIdOrNull(imageId, FileResource.class, null, securityContext);
        statusLinksToImageCreate.setImage(image);
    }


    @Override
    public void validateCreate(StatusLinksToImageCreate statusLinksToImageCreate, SecurityContext securityContext) {
        populate(statusLinksToImageCreate, securityContext);
        ProductTypeToProductStatus statusLink = statusLinksToImageCreate.getProductTypeToProductStatus();
        if (statusLink == null) {
            throw new BadRequestException("No ProductTypeToProductStatus with id " + statusLinksToImageCreate.getStatusLinkId());
        }
        FileResource image = statusLinksToImageCreate.getImage();
        if (image == null) {
            throw new BadRequestException("No FileResource with id " + statusLinksToImageCreate.getImageId());
        }
    }

    @Override
    public void validateUpdate(StatusLinksToImageUpdate statusLinksToImageCreate, SecurityContext securityContext) {
        populate(statusLinksToImageCreate, securityContext);
        ProductTypeToProductStatus statusLink = statusLinksToImageCreate.getProductTypeToProductStatus();
        if (statusLink == null && statusLinksToImageCreate.getStatusLinkId() != null) {
            throw new BadRequestException("No ProductTypeToProductStatus with id " + statusLinksToImageCreate.getStatusLinkId());
        }
        FileResource image = statusLinksToImageCreate.getImage();
        if (image == null && statusLinksToImageCreate.getImageId() != null) {
            throw new BadRequestException("No FileResource with id " + statusLinksToImageCreate.getImageId());
        }
    }

    @Override
    public StatusLinkToImage createStatusLinkToImage(StatusLinksToImageCreate statusLinksToImageCreate, SecurityContext securityContext) {
        StatusLinkToImage statusLinkToImage = createStatusLinkToImageNoMerge(statusLinksToImageCreate, securityContext);
        repository.merge(statusLinkToImage);
        return statusLinkToImage;
    }

    @Override
    public StatusLinkToImage createStatusLinkToImageNoMerge(StatusLinksToImageCreate statusLinksToImageCreate, SecurityContext securityContext) {
        StatusLinkToImage statusLinkToImage = StatusLinkToImage.s().CreateUnchecked(statusLinksToImageCreate.getName(), securityContext);
        statusLinkToImage.Init();
        updateStatusLinkToImageNoMerge(statusLinksToImageCreate, statusLinkToImage);
        return statusLinkToImage;
    }

    @Override
    public boolean updateStatusLinkToImageNoMerge(StatusLinksToImageCreate statusLinksToImageCreate, StatusLinkToImage statusLinkToImage) {
        boolean update = false;
        if (statusLinksToImageCreate.getName() != null && !statusLinksToImageCreate.getName().equals(statusLinkToImage.getName())) {
            statusLinkToImage.setName(statusLinksToImageCreate.getName());
            update = true;
        }
        if (statusLinksToImageCreate.getImage() != null && (statusLinkToImage.getImage() == null || !statusLinksToImageCreate.getImage().getId().equals(statusLinkToImage.getImage().getId()))) {
            statusLinkToImage.setImage(statusLinksToImageCreate.getImage());
            update = true;
        }

        if (statusLinksToImageCreate.getProductTypeToProductStatus() != null && (statusLinkToImage.getStatusLink() == null || !statusLinksToImageCreate.getProductTypeToProductStatus().getId().equals(statusLinkToImage.getStatusLink().getId()))) {
            statusLinkToImage.setStatusLink(statusLinksToImageCreate.getProductTypeToProductStatus());
            update = true;
        }
        return update;

    }

    @Override
    public StatusLinkToImage updateStatusLinkToImage(StatusLinksToImageUpdate statusLinksToImageUpdate, SecurityContext securityContext) {
        StatusLinkToImage statusLinkToImage=statusLinksToImageUpdate.getStatusLinkToImage();
        if(updateStatusLinkToImageNoMerge(statusLinksToImageUpdate,statusLinkToImage)){
            repository.merge(statusLinkToImage);
        }
        return statusLinkToImage;
    }
}
