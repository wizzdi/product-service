package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.model.*;
import com.flexicore.product.model.Event;
import com.flexicore.product.model.LocationArea;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventFiltering extends FilteringInformationHolder {


    @FieldInfo(displayName = "Search Readable Text")
    private String humanReadableTextLike;

    private String eventType;
    @FieldInfo(displayName = "Event Sub Type")
    private String eventSubType;
    @IdRefFieldInfo(displayName = "Source",refType = Baseclass.class)
    private Set<String> baseclassIds=new HashSet<>();
    @JsonIgnore
    private List<Baseclass> baseclass;
    private String clazzName;
    @JsonIgnore
    private Clazz clazz;

    @FieldInfo(displayName = "Location")
    private LocationArea locationArea;
    @FieldInfo(displayName = "Source name")
    private String baseclassNameLike;
    @FieldInfo(displayName = "acknowledged")
    private Boolean acked;
    @IdRefFieldInfo(refType = User.class)
    private Set<String> ackedUsersIds =new HashSet<>();
    @JsonIgnore
    private List<User> ackedUsers;

    @IdRefFieldInfo(displayName = "Target",refType = Baseclass.class)
    private Set<String> targetBaseclassIds=new HashSet<>();
    @JsonIgnore
    private List<Baseclass> targetBaseclass;

    public EventFiltering(EventFiltering other) {
        this.humanReadableTextLike = other.humanReadableTextLike;
        this.eventType = other.eventType;
        this.eventSubType = other.eventSubType;
        this.baseclassIds = other.baseclassIds;
        this.baseclass = other.baseclass;
        this.clazzName = other.clazzName;
        this.clazz = other.clazz;
        this.locationArea = other.locationArea;
        this.baseclassNameLike = other.baseclassNameLike;
        this.acked=other.acked;
        this.ackedUsers=other.ackedUsers;
        this.ackedUsersIds=other.ackedUsersIds;
        this.targetBaseclass=other.targetBaseclass;
        this.targetBaseclassIds=other.targetBaseclassIds;
    }

    public EventFiltering() {
        eventType=Event.class.getCanonicalName();
    }

    public String getHumanReadableTextLike() {
        return humanReadableTextLike;
    }

    public EventFiltering setHumanReadableTextLike(String humanReadableTextLike) {
        this.humanReadableTextLike = humanReadableTextLike;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public EventFiltering setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }


    public Set<String> getBaseclassIds() {
        return baseclassIds;
    }

    public EventFiltering setBaseclassIds(Set<String> baseclassIds) {
        this.baseclassIds = baseclassIds;
        return this;
    }

    @JsonIgnore
    public List<Baseclass> getBaseclass() {
        return baseclass;
    }

    public EventFiltering setBaseclass(List<Baseclass> baseclass) {
        this.baseclass = baseclass;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public EventFiltering setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }
    @JsonIgnore
    public Clazz getClazz() {
        return clazz;
    }

    public EventFiltering setClazz(Clazz clazz) {
        this.clazz = clazz;
        return this;
    }


    public String getBaseclassNameLike() {
        return baseclassNameLike;
    }

    public EventFiltering setBaseclassNameLike(String baseclassNameLike) {
        this.baseclassNameLike = baseclassNameLike;
        return this;
    }

    public String getEventSubType() {
        return eventSubType;
    }

    public EventFiltering setEventSubType(String eventSubType) {
        this.eventSubType = eventSubType;
        return this;
    }

    public LocationArea getLocationArea() {
        return locationArea;
    }

    public EventFiltering setLocationArea(LocationArea locationArea) {
        this.locationArea = locationArea;
        return this;
    }

    @Override
    public FilteringInformationHolder setTenantIds(List<TenantIdFiltering> tenantIds) {
        return super.setTenantIds(tenantIds);
    }

    public Boolean getAcked() {
        return acked;
    }

    public <T extends EventFiltering> T setAcked(Boolean acked) {
        this.acked = acked;
        return (T) this;
    }

    public Set<String> getAckedUsersIds() {
        return ackedUsersIds;
    }

    public <T extends EventFiltering> T setAckedUsersIds(Set<String> ackedUsersIds) {
        this.ackedUsersIds = ackedUsersIds;
        return (T) this;
    }

    @JsonIgnore
    public List<User> getAckedUsers() {
        return ackedUsers;
    }

    public <T extends EventFiltering> T setAckedUsers(List<User> ackedUsers) {
        this.ackedUsers = ackedUsers;
        return (T) this;
    }

    public Set<String> getTargetBaseclassIds() {
        return targetBaseclassIds;
    }

    public <T extends EventFiltering> T setTargetBaseclassIds(Set<String> targetBaseclassIds) {
        this.targetBaseclassIds = targetBaseclassIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Baseclass> getTargetBaseclass() {
        return targetBaseclass;
    }

    public <T extends EventFiltering> T setTargetBaseclass(List<Baseclass> targetBaseclass) {
        this.targetBaseclass = targetBaseclass;
        return (T) this;
    }
}
