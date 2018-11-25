package com.flexicore.product.iot.encoders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicore.data.jsoncontainers.Views;
import com.flexicore.product.iot.request.FlexiCoreIOTRequest;
import com.flexicore.product.iot.response.FlexiCoreIOTResponse;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by Asaf on 12/02/2017.
 */
public class FlexiCoreIOTResponseMessageEncoder implements Encoder.TextStream<FlexiCoreIOTResponse> {

    private static ObjectMapper objectMapper;

    @Override
    public void init(EndpointConfig config) {
        this.objectMapper=new ObjectMapper();
        this.objectMapper.setConfig(this.objectMapper.getSerializationConfig().withView(Views.Unrefined.class));

    }

    @Override
    public void destroy() {

    }

    @Override
    public void encode(FlexiCoreIOTResponse object, Writer writer) throws IOException {
        objectMapper.writeValue(writer, object);

    }
}
