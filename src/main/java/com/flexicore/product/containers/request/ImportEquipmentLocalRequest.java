package com.flexicore.product.containers.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportEquipmentLocalRequest {

    private List<Map<String, String>> objectToImport = new ArrayList<>();


    public List<Map<String, String>> getObjectToImport() {
        return objectToImport;
    }

    public ImportEquipmentLocalRequest setObjectToImport(List<Map<String, String>> objectToImport) {
        this.objectToImport = objectToImport;
        return this;
    }
}
