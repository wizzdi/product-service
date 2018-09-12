package com.flexicore.product.interfaces;

import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.containers.request.ImportEquipmentLocalRequest;
import com.flexicore.product.containers.request.ImportEquipmentRemoteRequest;
import com.flexicore.product.containers.request.InspectEquipmentRequest;
import com.flexicore.product.containers.response.ImportEquipmentLocalResponse;
import com.flexicore.product.containers.response.ImportEquipmentRemoteResponse;
import com.flexicore.product.containers.response.InspectEquipmentResponse;
import com.flexicore.product.model.Equipment;
import com.flexicore.security.SecurityContext;

public interface EquipmentHandler extends ServicePlugin {



    InspectEquipmentResponse inspect(InspectEquipmentRequest inspectEquipmentRequest, SecurityContext securityContext);
    ImportEquipmentRemoteResponse importEquipmentRemote(ImportEquipmentRemoteRequest importEquipmentRemoteRequest, SecurityContext securityContext);
    ImportEquipmentLocalResponse importEquipmentLocal(ImportEquipmentLocalRequest importEquipmentRemoteRequest, SecurityContext securityContext);
    String getDescriminatorName();
    Class<? extends Equipment> getHandlingClass();


}
