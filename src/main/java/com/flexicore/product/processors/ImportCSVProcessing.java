package com.flexicore.product.processors;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ProccessPlugin;
import com.flexicore.model.Job;
import com.flexicore.model.Result;
import com.flexicore.product.containers.request.ImportCSVRequest;
import com.flexicore.product.service.EquipmentService;

import javax.inject.Inject;
import java.util.logging.Logger;

@PluginInfo(version = 1)
public class ImportCSVProcessing implements ProccessPlugin {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Override
    public void process(Job job) {
        if(job.getJobInformation().getJobInfo() instanceof ImportCSVRequest){
            ImportCSVRequest importCSVRequest = (ImportCSVRequest) job.getJobInformation().getJobInfo();
            equipmentService.importCSV(importCSVRequest,job);
            job.getJobInformation().setCurrrentPhaseResult(new Result(true));
        }
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void abort() {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setLogger(Logger logger) {

    }

    @Override
    public int getTTL() {
        return 0;
    }

    @Override
    public int getOrder(Job job) {
        return job.getJobInformation().getJobInfo() instanceof ImportCSVRequest ?-1000:1000;
    }

    @Override
    public void init() {

    }
}
