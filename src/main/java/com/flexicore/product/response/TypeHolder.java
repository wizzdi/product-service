package com.flexicore.product.response;


public class TypeHolder {

    private String id;
    private String name;
    private Long count;

    public TypeHolder(String id, Long count ) {
        this.id = id;
        this.count = count;
    }

    public TypeHolder() {
    }

    public Long getCount() {
        return count;
    }

    public <T extends TypeHolder> T setCount(Long count) {
        this.count = count;
        return (T) this;
    }

    public String getId() {
        return id;
    }

    public <T extends TypeHolder> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public <T extends TypeHolder> T setName(String name) {
        this.name = name;
        return (T) this;
    }
}
