package com.flexicore.product.iot.response;

public class CloseFlexiCoreGatewayResponse {

    private boolean closed;

    public boolean isClosed() {
        return closed;
    }

    public CloseFlexiCoreGatewayResponse setClosed(boolean closed) {
        this.closed = closed;
        return this;
    }
}
