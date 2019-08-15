package com.flexicore.product.containers.response;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;

public class EquipmentGroupHolder {

    private double latMin;
    private double lonMin;
    private double latMax;
    private double lonMax;
    private Long count;


    public EquipmentGroupHolder() {
    }

    public EquipmentGroupHolder(String geoHash, Long count) {
        BoundingBox point = geoHash!=null?GeoHash.fromGeohashString(geoHash).getBoundingBox():null;
        latMin=point!=null?point.getMinLat():-1d;
        lonMin=point!=null?point.getMinLon():-1d;
        latMax=point!=null?point.getMaxLat():-1d;
        lonMax=point!=null?point.getMaxLon():-1d;
        this.count = count;
    }



    public Long getCount() {
        return count;
    }

    public EquipmentGroupHolder setCount(Long count) {
        this.count = count;
        return this;
    }


    public double getLatMin() {
        return latMin;
    }

    public EquipmentGroupHolder setLatMin(double latMin) {
        this.latMin = latMin;
        return this;
    }

    public double getLonMin() {
        return lonMin;
    }

    public EquipmentGroupHolder setLonMin(double lonMin) {
        this.lonMin = lonMin;
        return this;
    }

    public double getLatMax() {
        return latMax;
    }

    public EquipmentGroupHolder setLatMax(double latMax) {
        this.latMax = latMax;
        return this;
    }

    public double getLonMax() {
        return lonMax;
    }

    public EquipmentGroupHolder setLonMax(double lonMax) {
        this.lonMax = lonMax;
        return this;
    }
}
