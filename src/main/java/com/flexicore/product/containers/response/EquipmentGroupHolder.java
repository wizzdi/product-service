package com.flexicore.product.containers.response;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;

public class EquipmentGroupHolder {

    private double lat;
    private double lon;
    private Long count;



    public EquipmentGroupHolder(String geoHash, Long count) {
        WGS84Point point = geoHash!=null?GeoHash.fromGeohashString(geoHash).getPoint():null;
        lat=point!=null?point.getLatitude():-1d;
        lon=point!=null?point.getLongitude():-1d;
        this.count = count;
    }



    public Long getCount() {
        return count;
    }

    public EquipmentGroupHolder setCount(Long count) {
        this.count = count;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public EquipmentGroupHolder setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLon() {
        return lon;
    }

    public EquipmentGroupHolder setLon(double lon) {
        this.lon = lon;
        return this;
    }
}
