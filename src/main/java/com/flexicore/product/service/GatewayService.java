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

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@PluginInfo(version = 1)
public class GatewayService implements IGatewayService {

    @Inject
    @PluginInfo(version = 1)
    private GatewayRepository repository;

    @Inject
    @PluginInfo(version = 1)
    private IEquipmentService equipmentService;

    @Inject
    private Logger logger;

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batchString, securityContext);
    }

    @Override
    public void validate(GatewayFiltering filtering, SecurityContext securityContext) {

        equipmentService.validateFiltering(filtering,securityContext);

    }

    @Override
    public PaginationResponse<Gateway> getAllGateways(GatewayFiltering filtering, SecurityContext securityContext) {
        List<Gateway> list = listAllGateways(filtering, securityContext);
        long count = repository.countAllGateways(filtering, securityContext);
        return new PaginationResponse<>(list, filtering, count);
    }

    @Override
    public List<Gateway> listAllGateways(GatewayFiltering filtering, SecurityContext securityContext) {
        return repository.listAllGateways(filtering, securityContext);
    }

    public void populate(GatewayCreate gatewayCreate, SecurityContext securityContext) {

    }


    @Override
    public void validateCreate(GatewayCreate gatewayCreate, SecurityContext securityContext) {
      equipmentService.validateEquipmentCreate(gatewayCreate,securityContext);
      gatewayCreate.setProductType(equipmentService.getGatewayProductType());
    }

    @Override
    public void validateUpdate(GatewayUpdate gatewayCreate, SecurityContext securityContext) {
      equipmentService.validateEquipmentCreate(gatewayCreate,securityContext);
    }

    @Override
    public Gateway createGateway(GatewayCreate gatewayCreate, SecurityContext securityContext) {
        Gateway gateway = createGatewayNoMerge(gatewayCreate, securityContext);
        repository.merge(gateway);
        return gateway;
    }

    @Override
    public Gateway createGatewayNoMerge(GatewayCreate gatewayCreate, SecurityContext securityContext) {
        Gateway gateway = Gateway.s().CreateUnchecked(gatewayCreate.getName(), securityContext);
        gateway.Init();
        updateGatewayNoMerge(gatewayCreate, gateway);
        return gateway;
    }

    @Override
    public boolean updateGatewayNoMerge(GatewayCreate gatewayCreate, Gateway gateway) {
        boolean update = equipmentService.updateEquipmentNoMerge(gatewayCreate,gateway);
        if (gatewayCreate.getIp() != null && !gatewayCreate.getIp().equals(gateway.getId())) {
            gateway.setIp(gatewayCreate.getIp());
            update = true;
        }

        if (gatewayCreate.getPort() != null && gatewayCreate.getPort() != gateway.getPort()) {
            gateway.setPort(gatewayCreate.getPort());
            update = true;
        }
        if (gatewayCreate.getUsername() != null && !gatewayCreate.getUsername().equals(gateway.getUsername())) {
            gateway.setUsername(gatewayCreate.getUsername());
            update = true;
        }

        String password = gatewayCreate.getPassword();
        if (password != null) {
            EncryptionService.initEncryption(logger);
            try {
                String encryptedPassword = Base64.getEncoder().encodeToString(EncryptionService.getAead().encrypt(password.getBytes(StandardCharsets.UTF_8), "test".getBytes()));
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
    public Gateway updateGateway(GatewayUpdate gatewayUpdate, SecurityContext securityContext) {
        Gateway gateway=gatewayUpdate.getGatewayToUpdate();
        if(updateGatewayNoMerge(gatewayUpdate,gateway)){
            repository.merge(gateway);
        }
        return gateway;
    }
}
