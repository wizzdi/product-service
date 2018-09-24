package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.Invoker;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.model.Job;
import com.flexicore.product.containers.request.ImportCSVRequest;
import com.flexicore.product.containers.response.EquipmentShort;
import com.flexicore.product.containers.response.ImportCSVResponse;
import com.flexicore.security.SecurityContext;

import java.util.List;
import java.util.Map;

public interface EquipmentShortListerInvoker<T extends EquipmentShort,E extends FilteringInformationHolder> extends Invoker {

    PaginationResponse<T> listAllShort(E filter, SecurityContext securityContext);
    Class<E> getShortFilteringClass();
    Class<T> getShortClass();

}
