package com.flexicore.product.interfaces;

import com.flexicore.interfaces.InitPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.iot.request.FlexiCoreIOTRequest;
import com.flexicore.product.iot.response.CloseFlexiCoreGatewayResponse;
import com.flexicore.product.iot.response.FlexiCoreIOTResponse;
import com.flexicore.product.iot.response.OpenFlexiCoreGatewayResponse;
import com.flexicore.product.model.FlexiCoreGateway;

import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.function.Consumer;

public interface IIOTService extends ServicePlugin, InitPlugin {

    OpenFlexiCoreGatewayResponse openFlexiCoreGateway(FlexiCoreGateway flexiCoreGateway) throws IOException, DeploymentException;

    CloseFlexiCoreGatewayResponse closeFlexiCoreGateway(String communicationId) throws IOException;

    void executeViaFlexiCoreGateway(String communicationId, FlexiCoreIOTRequest flexiCoreExecutionRequest, Consumer<FlexiCoreIOTResponse> callback) throws IOException, EncodeException;
}