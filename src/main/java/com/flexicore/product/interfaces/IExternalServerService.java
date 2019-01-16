package com.flexicore.product.interfaces;

import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.iot.ExternalServer;
import com.flexicore.iot.ExternalServerUser;
import com.flexicore.product.iot.request.ExternalServerCreate;
import com.flexicore.product.iot.request.ExternalServerUserCreate;

public interface IExternalServerService extends ServicePlugin {
    boolean updateExternalServerNoMerge(ExternalServerCreate externalServerCreate, ExternalServer externalServer);

    boolean updateExternalServerUserNoMerge(ExternalServerUserCreate externalServerCreate, ExternalServerUser externalServerUser);
}
