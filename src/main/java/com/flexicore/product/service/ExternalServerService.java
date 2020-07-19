package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.iot.ExternalServer;
import com.flexicore.iot.ExternalServerUser;
import com.flexicore.product.containers.request.ExternalServerFiltering;
import com.flexicore.product.data.ExternalServerRepository;
import com.flexicore.product.interfaces.IExternalServerService;
import com.flexicore.product.iot.request.ExternalServerCreate;
import com.flexicore.product.iot.request.ExternalServerUserCreate;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.EncryptionService;

import org.springframework.transaction.annotation.Transactional;
import javax.ws.rs.BadRequestException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class ExternalServerService implements IExternalServerService {

	public static final String SALT = "test";
	@Autowired
	private Logger logger;

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentService equipmentService;

	@PluginInfo(version = 1)
	@Autowired
	private ExternalServerRepository externalServerRepository;

	@Autowired
	private EncryptionService encryptionService;

	@Override
	public boolean updateExternalServerNoMerge(
			ExternalServerCreate externalServerCreate,
			ExternalServer externalServer) {
		boolean update = equipmentService.updateEquipmentNoMerge(
				externalServerCreate, externalServer);
		if (externalServerCreate.getUrl() != null
				&& !externalServerCreate.getUrl().equals(
						externalServer.getUrl())) {
			externalServer.setUrl(externalServerCreate.getUrl());
			update = true;
		}

		if (externalServerCreate.getInspectIntervalMs() != null
				&& !externalServerCreate.getInspectIntervalMs().equals(
						externalServer.getInspectIntervalMs())) {
			externalServer.setInspectIntervalMs(externalServerCreate
					.getInspectIntervalMs());
			update = true;
		}

		if (externalServerCreate.getLastInspectAttempt() != null
				&& !externalServerCreate.getLastInspectAttempt().equals(
						externalServer.getLastInspectAttempt())) {
			externalServer.setLastInspectAttempt(externalServerCreate
					.getLastInspectAttempt());
			update = true;
		}

		if (externalServerCreate.getLastSuccessfulInspect() != null
				&& !externalServerCreate.getLastSuccessfulInspect().equals(
						externalServer.getLastSuccessfulInspect())) {
			externalServer.setLastSuccessfulInspect(externalServerCreate
					.getLastSuccessfulInspect());
			update = true;
		}
		return update;
	}

	public void validate(ExternalServerFiltering externalServerFiltering,
			SecurityContext securityContext) {

	}

	@Override
	public void validate(ExternalServerUserCreate externalServerUserCreate,
			SecurityContext securityContext) {
		ExternalServer externalServer = equipmentService.getByIdOrNull(
				externalServerUserCreate.getExternalServerId(),
				ExternalServer.class, null, securityContext);
		if (externalServer == null) {
			throw new BadRequestException("No External Server "
					+ externalServerUserCreate.getExternalServerId());
		}
		externalServerUserCreate.setExternalServer(externalServer);
	}

	@Override
	public boolean updateExternalServerUserNoMerge(
			ExternalServerUserCreate externalServerCreate,
			ExternalServerUser externalServerUser) {
		boolean update = false;
		if (externalServerCreate.getName() != null
				&& !externalServerCreate.getName().equals(
						externalServerUser.getName())) {
			externalServerUser.setName(externalServerCreate.getName());
			update = true;
		}
		if (externalServerCreate.getDescription() != null
				&& !externalServerCreate.getDescription().equals(
						externalServerUser.getDescription())) {
			externalServerUser.setDescription(externalServerCreate
					.getDescription());
			update = true;
		}
		if (externalServerCreate.getUsername() != null
				&& !externalServerCreate.getUsername().equals(
						externalServerUser.getUsername())) {
			externalServerUser.setUsername(externalServerCreate.getUsername());
			update = true;
		}
		if (externalServerCreate.getExternalServer() != null
				&& (externalServerUser.getExternalServer() == null || !externalServerCreate
						.getExternalServer().getId()
						.equals(externalServerUser.getExternalServer().getId()))) {
			externalServerUser.setExternalServer(externalServerCreate
					.getExternalServer());
			update = true;
		}
		String password = externalServerCreate.getPassword();

		try {
			String encryptedPassword = Base64.getEncoder().encodeToString(
					encryptionService.encrypt(
							password.getBytes(StandardCharsets.UTF_8),
							SALT.getBytes()));
			if (!encryptedPassword.equals(externalServerUser.getPassword())) {
				externalServerUser.setPassword(encryptedPassword);
				update = true;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "could not encrypt password", e);
		}
		return update;
	}

	@Override
	public String getDecryptedPassword(String encryptedPassword)
			throws GeneralSecurityException {
		return new String(
				encryptionService.decrypt(
						Base64.getDecoder().decode(encryptedPassword),
						SALT.getBytes()), StandardCharsets.UTF_8);
	}

	public PaginationResponse<ExternalServer> getAllExternalServers(
			ExternalServerFiltering externalServerFiltering,
			SecurityContext securityContext) {
		List<ExternalServer> list = externalServerRepository
				.listAllExternalServers(externalServerFiltering,
						securityContext);
		long count = externalServerRepository.countAllExternalServers(
				externalServerFiltering, securityContext);
		return new PaginationResponse<>(list, externalServerFiltering, count);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		externalServerRepository.massMerge(toMerge);
	}
}
