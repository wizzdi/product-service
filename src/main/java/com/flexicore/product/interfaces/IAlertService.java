package com.flexicore.product.interfaces;

import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.model.Alert;

import java.util.List;

public interface IAlertService extends ServicePlugin {
    void merge(Alert alert);

    void massMergeAlerts(List<Alert> o);

    List<Alert> getAllAlerts(AlertFiltering alertFiltering);
}
