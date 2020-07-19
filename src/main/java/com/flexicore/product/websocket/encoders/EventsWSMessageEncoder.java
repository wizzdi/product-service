package com.flexicore.product.websocket.encoders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicore.data.jsoncontainers.ObjectMapperContextResolver;
import com.flexicore.data.jsoncontainers.Views;
import com.flexicore.product.interfaces.IEvent;
import com.flexicore.product.model.Event;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by Asaf on 12/02/2017.
 */
public class EventsWSMessageEncoder implements Encoder.TextStream<IEvent> {

	private static ObjectMapper objectMapper;

	@Override
	public void init(EndpointConfig config) {
		objectMapper = ObjectMapperContextResolver
				.createClassLoaderObjectMapper(getClass().getClassLoader(),
						Views.ForSwaggerOnly.class);

	}

	@Override
	public void destroy() {

	}

	@Override
	public void encode(IEvent object, Writer writer) throws IOException {
		objectMapper.writeValue(writer, object);

	}
}
