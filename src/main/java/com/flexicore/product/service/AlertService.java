package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.product.containers.request.AlertFiltering;
import com.flexicore.product.data.AlertNoSQLRepository;
import com.flexicore.product.interfaces.IAlertService;
import com.flexicore.product.model.Alert;

import javax.inject.Inject;
import java.util.List;

@PluginInfo(version = 1)
public class AlertService implements IAlertService {

    @Inject
    @PluginInfo(version = 1)
    private AlertNoSQLRepository repository;


    @Override
    public void merge(Alert alert) {
        repository.merge(alert);
    }

    @Override
    public void massMergeAlerts(List<Alert> o) {
        repository.massMergeAlerts(o);
    }

    @Override
    public PaginationResponse<Alert> getAllAlerts(AlertFiltering alertFiltering) {

        List<Alert> list = repository.getAllAlerts(alertFiltering);
        long count = repository.countAllAlerts(alertFiltering);
        return new PaginationResponse<>(list, alertFiltering, count);
    }
}
