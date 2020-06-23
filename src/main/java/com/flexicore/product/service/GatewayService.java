package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.product.containers.request.GatewayCreate;
import com.flexicore.product.data.GatewayRepository;
import com.flexicore.product.interfaces.IGatewayService;
import com.flexicore.product.interfaces.IEquipmentService;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.GatewayFiltering;
import com.flexicore.product.request.GatewayUpdate;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.EncryptionService;

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
public class GatewayService implements IGatewayService {

	public static final String SALT = "test";
	@PluginInfo(version = 1)
	@Autowired
	private GatewayRepository repository;

	@PluginInfo(version = 1)
	@Autowired
	private IEquipmentService equipmentService;

	@Autowired
	private Logger logger;

	@Autowired
	private EncryptionService encryptionService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batchString, securityContext);
	}

	@Override
	public void validate(GatewayFiltering filtering,
			SecurityContext securityContext) {

		equipmentService.validateFiltering(filtering, securityContext);

	}

	@Override
	public PaginationResponse<Gateway> getAllGateways(
			GatewayFiltering filtering, SecurityContext securityContext) {
		List<Gateway> list = listAllGateways(filtering, securityContext);
		long count = repository.countAllGateways(filtering, securityContext);
		return new PaginationResponse<>(list, filtering, count);
	}

	@Override
	public List<Gateway> listAllGateways(GatewayFiltering filtering,
			SecurityContext securityContext) {
		return repository.listAllGateways(filtering, securityContext);
	}

	public void populate(GatewayCreate gatewayCreate,
			SecurityContext securityContext) {

	}

	@Override
	public void validateCreate(GatewayCreate gatewayCreate,
			SecurityContext securityContext) {
		equipmentService
				.validateEquipmentCreate(gatewayCreate, securityContext);
		gatewayCreate.setProductType(equipmentService.getGatewayProductType());
	}

	@Override
	public void validateUpdate(GatewayUpdate gatewayCreate,
			SecurityContext securityContext) {
		equipmentService
				.validateEquipmentCreate(gatewayCreate, securityContext);
	}

	@Override
	public Gateway createGateway(GatewayCreate gatewayCreate,
			SecurityContext securityContext) {
		Gateway gateway = createGatewayNoMerge(gatewayCreate, securityContext);
		repository.merge(gateway);
		return gateway;
	}

	@Override
	public Gateway createGatewayNoMerge(GatewayCreate gatewayCreate,
			SecurityContext securityContext) {
		Gateway gateway = new Gateway(gatewayCreate.getName(),
				securityContext);
		updateGatewayNoMerge(gatewayCreate, gateway);
		return gateway;
	}

	@Override
	public boolean updateGatewayNoMerge(GatewayCreate gatewayCreate,
			Gateway gateway) {
		boolean update = equipmentService.updateEquipmentNoMerge(gatewayCreate,
				gateway);
		if (gatewayCreate.getIp() != null
				&& !gatewayCreate.getIp().equals(gateway.getId())) {
			gateway.setIp(gatewayCreate.getIp());
			update = true;
		}

		if (gatewayCreate.getPort() != null
				&& gatewayCreate.getPort() != gateway.getPort()) {
			gateway.setPort(gatewayCreate.getPort());
			update = true;
		}
		if (gatewayCreate.getUsername() != null
				&& !gatewayCreate.getUsername().equals(gateway.getUsername())) {
			gateway.setUsername(gatewayCreate.getUsername());
			update = true;
		}

		String password = gatewayCreate.getPassword();
		if (password != null) {
			try {
				String encryptedPassword = Base64.getEncoder().encodeToString(
						encryptionService.encrypt(
								password.getBytes(StandardCharsets.UTF_8),
								SALT.getBytes()));
				if (!encryptedPassword.equals(gateway.getEncryptedPassword())) {
					gateway.setEncryptedPassword(encryptedPassword);
					update = true;
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "could not encrypt password", e);
			}

		}
		return update;

	}

	@Override
	public Gateway updateGateway(GatewayUpdate gatewayUpdate,
			SecurityContext securityContext) {
		Gateway gateway = gatewayUpdate.getGatewayToUpdate();
		if (updateGatewayNoMerge(gatewayUpdate, gateway)) {
			repository.merge(gateway);
		}
		return gateway;
	}

	@Override
	public String getDecryptedPassword(String encryptedPassword)
			throws GeneralSecurityException {
		return new String(
				encryptionService.decrypt(
						Base64.getDecoder().decode(encryptedPassword),
						SALT.getBytes()), StandardCharsets.UTF_8);
	}
}
