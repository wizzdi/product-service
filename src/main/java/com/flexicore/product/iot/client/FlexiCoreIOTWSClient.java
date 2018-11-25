package com.flexicore.product.iot.client;

import com.flexicore.product.iot.encoders.FlexiCoreIOTRequestMessageDecoder;
import com.flexicore.product.iot.encoders.FlexiCoreIOTRequestMessageEncoder;
import com.flexicore.product.iot.encoders.FlexiCoreIOTResponseMessageDecoder;
import com.flexicore.product.iot.encoders.FlexiCoreIOTResponseMessageEncoder;
import com.flexicore.product.iot.response.FlexiCoreIOTResponse;
import com.flexicore.product.service.IOTService;

import javax.websocket.*;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

@ClientEndpoint(
        encoders = {FlexiCoreIOTRequestMessageEncoder.class},
        decoders = {FlexiCoreIOTResponseMessageDecoder.class})
public class FlexiCoreIOTWSClient {

    private Logger logger;
    private Map<String, Consumer<FlexiCoreIOTResponse>> responseCallbackMap;

    public FlexiCoreIOTWSClient(Logger logger, Map<String, Consumer<FlexiCoreIOTResponse>> responseCallbackMap) {
        this.logger = logger;
        this.responseCallbackMap = responseCallbackMap;
    }

    @OnClose
    public void closed(Session session) {
        System.out.println("Session " + session + " closed");

    }

    @OnError
    public void error(Throwable error) {
        System.out.println("Error: " + error.getMessage());

    }

    @OnMessage
    public void onMessage( FlexiCoreIOTResponse message ){
        logger.info("received response "+message);
        Consumer<FlexiCoreIOTResponse> callback= responseCallbackMap.remove(message.getRequestId());
        if(callback!=null){
            callback.accept(message);
        }
        else{
            logger.warning("No Callback for response");
        }

    }
}
