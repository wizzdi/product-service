package com.flexicore.product.interfaces;

import com.flexicore.interfaces.dynamic.Invoker;
import com.flexicore.model.Job;
import com.flexicore.product.containers.request.ImportCSVRequest;
import com.flexicore.product.containers.response.ImportCSVResponse;

import java.util.List;
import java.util.Map;

public interface EquipmentInvoker extends Invoker {

    ImportCSVResponse importCSV(List<Map<String,String>> records, ImportCSVRequest importCSVRequest, Job job);
    String getCSVDescriminator();
}
