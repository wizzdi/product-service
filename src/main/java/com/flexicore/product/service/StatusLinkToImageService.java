package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.CreatePermissionGroupLinkRequest;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.events.BaseclassCreated;
import com.flexicore.events.BaseclassUpdated;
import com.flexicore.model.Baseclass;
import com.flexicore.model.FileResource;
import com.flexicore.model.PermissionGroup;
import com.flexicore.model.PermissionGroupToBaseclass;
import com.flexicore.product.data.StatusLinkToImageRepository;
import com.flexicore.product.interfaces.IStatusLinkToImageService;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductType;
import com.flexicore.product.model.ProductTypeToProductStatus;
import com.flexicore.product.model.StatusLinkToImage;
import com.flexicore.product.request.StatusLinksToImageCreate;
import com.flexicore.product.request.StatusLinksToImageFilter;
import com.flexicore.product.request.StatusLinksToImageUpdate;
import com.flexicore.request.PermissionGroupsFilter;
import com.flexicore.security.SecurityContext;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.service.PermissionGroupService;
import com.wizzdi.flexicore.security.request.PermissionGroupToBaseclassCreate;
import com.wizzdi.flexicore.security.request.PermissionGroupToBaseclassFilter;
import com.wizzdi.flexicore.security.service.PermissionGroupToBaseclassService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class StatusLinkToImageService implements IStatusLinkToImageService {

	private static final Logger logger= LoggerFactory.getLogger(StatusLinkToImageService.class);

	@PluginInfo(version = 1)
	@Autowired
	private StatusLinkToImageRepository repository;

	@Autowired
	@Qualifier("adminSecurityContext")
	private SecurityContextBase<?, ?, ?, ?> securityContextBase;

	@Autowired
	private PermissionGroupToBaseclassService permissionGroupToBaseclassService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batchString, securityContext);
	}

	@Override
	public void validate(StatusLinksToImageFilter filtering,
			SecurityContext securityContext) {
		Set<String> statusIds = filtering.getStatusIds();
		Map<String, ProductStatus> statusMap = statusIds.isEmpty()
				? new HashMap<>()
				: repository
						.listByIds(ProductStatus.class, statusIds,
								securityContext).parallelStream()
						.collect(Collectors.toMap(f -> f.getId(), f -> f));
		statusIds.removeAll(statusMap.keySet());
		if (!statusIds.isEmpty()) {
			logger.warn("No ProductStatus ids " + statusIds);

			// throw new BadRequestException("No ProductStatus ids " +
			// statusIds);
		}
		filtering.setStatus(new ArrayList<>(statusMap.values()));

		Set<String> statusLinkIds = filtering.getStatusLinkIds();
		Map<String, ProductTypeToProductStatus> productTypeToProductStatusMap = statusLinkIds
				.isEmpty() ? new HashMap<>() : repository
				.listByIds(ProductTypeToProductStatus.class, statusLinkIds,
						securityContext).parallelStream()
				.collect(Collectors.toMap(f -> f.getId(), f -> f));
		statusLinkIds.removeAll(productTypeToProductStatusMap.keySet());
		if (!statusLinkIds.isEmpty()) {
			logger.warn("No ProductTypeToProductStatus ids " + statusLinkIds);

			// throw new
			// BadRequestException("No ProductTypeToProductStatus ids " +
			// statusLinkIds);
		}
		filtering.setStatusLinks(new ArrayList<>(productTypeToProductStatusMap
				.values()));

		Set<String> productTypeIds = filtering.getProductTypeIds();
		Map<String, ProductType> productTypeMap = productTypeIds.isEmpty()
				? new HashMap<>()
				: repository
						.listByIds(ProductType.class, productTypeIds,
								securityContext).parallelStream()
						.collect(Collectors.toMap(f -> f.getId(), f -> f));
		productTypeIds.removeAll(productTypeMap.keySet());
		if (!productTypeIds.isEmpty()) {
			logger.warn("No ProductType ids " + statusLinkIds);

			// throw new BadRequestException("No ProductType ids " +
			// productTypeIds);
		}
		filtering.setProductTypes(new ArrayList<>(productTypeMap.values()));

	}

	@Override
	public PaginationResponse<StatusLinkToImage> getAllStatusLinksToImage(
			StatusLinksToImageFilter filtering, SecurityContext securityContext) {
		List<StatusLinkToImage> list = listAllStatusLinksToImage(filtering,
				securityContext);
		long count = repository.countAllStatusLinksToImage(filtering,
				securityContext);
		return new PaginationResponse<>(list, filtering, count);
	}

	@Override
	public List<StatusLinkToImage> listAllStatusLinksToImage(
			StatusLinksToImageFilter filtering, SecurityContext securityContext) {
		return repository.listAllStatusLinksToImage(filtering, securityContext);
	}

	public void populate(StatusLinksToImageCreate statusLinksToImageCreate,
			SecurityContext securityContext) {
		String statusLinkId = statusLinksToImageCreate.getStatusLinkId();
		ProductTypeToProductStatus statusLink = statusLinkId == null
				? null
				: getByIdOrNull(statusLinkId, ProductTypeToProductStatus.class,
						null, securityContext);
		statusLinksToImageCreate.setProductTypeToProductStatus(statusLink);

		String imageId = statusLinksToImageCreate.getImageId();
		FileResource image = imageId == null ? null : getByIdOrNull(imageId,
				FileResource.class, null, securityContext);
		statusLinksToImageCreate.setImage(image);
	}

	@Override
	public void validateCreate(
			StatusLinksToImageCreate statusLinksToImageCreate,
			SecurityContext securityContext) {
		populate(statusLinksToImageCreate, securityContext);
		ProductTypeToProductStatus statusLink = statusLinksToImageCreate
				.getProductTypeToProductStatus();
		if (statusLink == null) {
			throw new BadRequestException(
					"No ProductTypeToProductStatus with id "
							+ statusLinksToImageCreate.getStatusLinkId());
		}
		FileResource image = statusLinksToImageCreate.getImage();
		if (image == null) {
			throw new BadRequestException("No FileResource with id "
					+ statusLinksToImageCreate.getImageId());
		}
	}

	@Override
	public void validateUpdate(
			StatusLinksToImageUpdate statusLinksToImageCreate,
			SecurityContext securityContext) {
		populate(statusLinksToImageCreate, securityContext);
		ProductTypeToProductStatus statusLink = statusLinksToImageCreate
				.getProductTypeToProductStatus();
		if (statusLink == null
				&& statusLinksToImageCreate.getStatusLinkId() != null) {
			throw new BadRequestException(
					"No ProductTypeToProductStatus with id "
							+ statusLinksToImageCreate.getStatusLinkId());
		}
		FileResource image = statusLinksToImageCreate.getImage();
		if (image == null && statusLinksToImageCreate.getImageId() != null) {
			throw new BadRequestException("No FileResource with id "
					+ statusLinksToImageCreate.getImageId());
		}
	}

	@Override
	public StatusLinkToImage createStatusLinkToImage(
			StatusLinksToImageCreate statusLinksToImageCreate,
			SecurityContext securityContext) {
		StatusLinkToImage statusLinkToImage = createStatusLinkToImageNoMerge(
				statusLinksToImageCreate, securityContext);
		repository.merge(statusLinkToImage);

		return statusLinkToImage;
	}


	@Override
	public StatusLinkToImage createStatusLinkToImageNoMerge(
			StatusLinksToImageCreate statusLinksToImageCreate,
			SecurityContext securityContext) {
		StatusLinkToImage statusLinkToImage = new StatusLinkToImage(statusLinksToImageCreate.getName(), securityContext);
		updateStatusLinkToImageNoMerge(statusLinksToImageCreate, statusLinkToImage);
		return statusLinkToImage;
	}

	@Override
	public boolean updateStatusLinkToImageNoMerge(
			StatusLinksToImageCreate statusLinksToImageCreate,
			StatusLinkToImage statusLinkToImage) {
		boolean update = false;
		if (statusLinksToImageCreate.getName() != null
				&& !statusLinksToImageCreate.getName().equals(
						statusLinkToImage.getName())) {
			statusLinkToImage.setName(statusLinksToImageCreate.getName());
			update = true;
		}
		if (statusLinksToImageCreate.getImage() != null
				&& (statusLinkToImage.getImage() == null || !statusLinksToImageCreate
						.getImage().getId()
						.equals(statusLinkToImage.getImage().getId()))) {
			statusLinkToImage.setImage(statusLinksToImageCreate.getImage());
			update = true;
		}

		if (statusLinksToImageCreate.getProductTypeToProductStatus() != null
				&& (statusLinkToImage.getStatusLink() == null || !statusLinksToImageCreate
						.getProductTypeToProductStatus().getId()
						.equals(statusLinkToImage.getStatusLink().getId()))) {
			statusLinkToImage.setStatusLink(statusLinksToImageCreate
					.getProductTypeToProductStatus());
			update = true;
		}
		return update;

	}

	@Override
	public StatusLinkToImage updateStatusLinkToImage(
			StatusLinksToImageUpdate statusLinksToImageUpdate,
			SecurityContext securityContext) {
		StatusLinkToImage statusLinkToImage = statusLinksToImageUpdate
				.getStatusLinkToImage();
		if (updateStatusLinkToImageNoMerge(statusLinksToImageUpdate,
				statusLinkToImage)) {
			repository.merge(statusLinkToImage);
		}

		return statusLinkToImage;
	}


	@Async
	@org.springframework.context.event.EventListener
	public void onPermissionGroupToBaseclassCreated(BaseclassUpdated<StatusLinkToImage> baseclassUpdated) {
		List<PermissionGroupToBaseclass> permissionGroupToBaseclasses = permissionGroupToBaseclassService.listAllPermissionGroupToBaseclass(new PermissionGroupToBaseclassFilter().setRightside(Collections.singletonList(baseclassUpdated.getBaseclass())), null);
		addProductTypeToStatusItemsToPermissionGroup(permissionGroupToBaseclasses);
	}

	@Async
	@EventListener
	public void onPermissionGroupToBaseclassCreated(BaseclassCreated<PermissionGroupToBaseclass> baseclassBaseclassCreated) {
		PermissionGroupToBaseclass link = baseclassBaseclassCreated.getBaseclass();
		if (link.getRightside() instanceof StatusLinkToImage) {
			addProductTypeToStatusItemsToPermissionGroup(Collections.singletonList(link));
		}
	}

	private void addProductTypeToStatusItemsToPermissionGroup(List<PermissionGroupToBaseclass> links) {
		List<Baseclass> stuffToAdd = new ArrayList<>();
		List<PermissionGroup> permissionGroups=links.stream().map(f->f.getLeftside()).collect(Collectors.toList());
		for (PermissionGroupToBaseclass link : links) {
			StatusLinkToImage productType = (StatusLinkToImage) link.getRightside();
			if (productType.getStatusLink() != null) {
				stuffToAdd.add(productType.getStatusLink());
			}
			if (productType.getImage() != null) {
				stuffToAdd.add(productType.getImage());
			}
		}

		if (!stuffToAdd.isEmpty()) {
			Map<String,Map<String, PermissionGroupToBaseclass>> map = permissionGroupToBaseclassService.listAllPermissionGroupToBaseclass(new PermissionGroupToBaseclassFilter().setLeftside(new ArrayList<>(permissionGroups)).setRightside(stuffToAdd), null).stream().collect(Collectors.groupingBy(f -> f.getRightside().getId(), Collectors.toMap(f->f.getLeftside().getId(), f->f,(a, b)->a)));
			List<Object> toMerge = new ArrayList<>();
			for (Baseclass baseclass : stuffToAdd) {
				Map<String,PermissionGroupToBaseclass> permissionGroupToBaseclassMap = map.computeIfAbsent(baseclass.getId(),f->new HashMap<>());
				for (PermissionGroup permissionGroup : permissionGroups) {
					PermissionGroupToBaseclass permissionGroupToBaseclass=permissionGroupToBaseclassMap.get(permissionGroup.getId());
					PermissionGroupToBaseclassCreate permissionGroupToBaseclassCreate = new PermissionGroupToBaseclassCreate()
							.setBaseclass(baseclass)
							.setPermissionGroup(permissionGroup);
					if (permissionGroupToBaseclass == null) {
						permissionGroupToBaseclass = permissionGroupToBaseclassService.createPermissionGroupToBaseclassNoMerge(permissionGroupToBaseclassCreate, securityContextBase);
						toMerge.add(permissionGroupToBaseclass);
					}
				}

			}
			logger.info("added total of " + toMerge.size() + " to permission groups " + permissionGroups.stream().map(f->f.getName()+"("+f.getId()+")").collect(Collectors.joining(",")));
			permissionGroupToBaseclassService.massMerge(toMerge);

		}
	}
}
