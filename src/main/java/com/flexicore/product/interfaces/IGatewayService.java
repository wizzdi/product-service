package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.containers.request.GatewayCreate;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.GatewayFiltering;
import com.flexicore.product.request.GatewayUpdate;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IGatewayService extends ServicePlugin {
    void validate(GatewayFiltering filtering, SecurityContext securityContext);

    PaginationResponse<Gateway> getAllGateways(GatewayFiltering filtering, SecurityContext securityContext);

    List<Gateway> listAllGateways(GatewayFiltering filtering, SecurityContext securityContext);

    void validateCreate(GatewayCreate gatewayCreate, SecurityContext securityContext);

    void validateUpdate(GatewayUpdate gatewayCreate, SecurityContext securityContext);

    Gateway createGateway(GatewayCreate gatewayCreate, SecurityContext securityContext);

    Gateway createGatewayNoMerge(GatewayCreate gatewayCreate, SecurityContext securityContext);

    boolean updateGatewayNoMerge(GatewayCreate gatewayCreate, Gateway gateway);

    Gateway updateGateway(GatewayUpdate gatewayUpdate, SecurityContext securityContext);
}
