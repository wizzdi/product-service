package com.flexicore.product.containers.request;

public class LocationArea {

    private double lonStart;
    private double latStart;
    private double lonEnd;
    private double latEnd;


    public double getLonStart() {
        return lonStart;
    }

    public LocationArea setLonStart(double lonStart) {
        this.lonStart = lonStart;
        return this;
    }

    public double getLatStart() {
        return latStart;
    }

    public LocationArea setLatStart(double latStart) {
        this.latStart = latStart;
        return this;
    }

    public double getLonEnd() {
        return lonEnd;
    }

    public LocationArea setLonEnd(double lonEnd) {
        this.lonEnd = lonEnd;
        return this;
    }

    public double getLatEnd() {
        return latEnd;
    }

    public LocationArea setLatEnd(double latEnd) {
        this.latEnd = latEnd;
        return this;
    }
}
