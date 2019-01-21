package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.iot.ExternalServer;
import com.flexicore.iot.ExternalServerUser;
import com.flexicore.product.containers.request.ExternalServerFiltering;
import com.flexicore.product.interfaces.IExternalServerService;
import com.flexicore.product.iot.request.ExternalServerCreate;
import com.flexicore.product.iot.request.ExternalServerUserCreate;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.EncryptionService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@PluginInfo(version = 1)
public class ExternalServerService implements IExternalServerService {

    @Inject
    private Logger logger;

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    public boolean updateExternalServerNoMerge(ExternalServerCreate externalServerCreate, ExternalServer externalServer) {
        boolean update = equipmentService.updateEquipmentNoMerge(externalServerCreate,externalServer);
        if (externalServerCreate.getUrl() != null && !externalServerCreate.getUrl().equals(externalServer.getUrl())) {
            externalServer.setUrl(externalServerCreate.getUrl());
            update = true;
        }
        return update;
    }





    @Override
    public void validate(ExternalServerUserCreate externalServerUserCreate, SecurityContext securityContext){
        ExternalServer externalServer=equipmentService.getByIdOrNull(externalServerUserCreate.getExternalServerId(),ExternalServer.class,null,securityContext);
        if(externalServer==null){
            throw new BadRequestException("No External Server "+externalServerUserCreate.getExternalServerId());
        }
        externalServerUserCreate.setExternalServer(externalServer);
    }

    @Override
    public boolean updateExternalServerUserNoMerge(ExternalServerUserCreate externalServerCreate, ExternalServerUser externalServerUser) {
        boolean update = false;
        if (externalServerCreate.getName() != null && !externalServerCreate.getName().equals(externalServerUser.getName())) {
            externalServerUser.setName(externalServerCreate.getName());
            update = true;
        }
        if (externalServerCreate.getDescription() != null && !externalServerCreate.getDescription().equals(externalServerUser.getDescription())) {
            externalServerUser.setDescription(externalServerCreate.getDescription());
            update = true;
        }
        if (externalServerCreate.getUsername() != null && !externalServerCreate.getUsername().equals(externalServerUser.getUsername())) {
            externalServerUser.setUsername(externalServerCreate.getUsername());
            update = true;
        }
        String password = externalServerCreate.getPassword();

        EncryptionService.initEncryption(logger);
        try {
            String encryptedPassword = Base64.getEncoder().encodeToString(EncryptionService.getAead().encrypt(password.getBytes(StandardCharsets.UTF_8), "test".getBytes()));
            if (!encryptedPassword.equals(externalServerUser.getPassword())) {
                externalServerUser.setPassword(encryptedPassword);
                update = true;
            }
        }
        catch (Exception e){
            logger.log(Level.SEVERE,"could not encrypt password",e);
        }
        return update;
    }
}
