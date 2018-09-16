package com.flexicore.product.containers.response;

public class ImportCSVResponse {

    private int created;
    private int updated;

    public int getCreated() {
        return created;
    }

    public ImportCSVResponse setCreated(int created) {
        this.created = created;
        return this;
    }

    public int getUpdated() {
        return updated;
    }

    public ImportCSVResponse setUpdated(int updated) {
        this.updated = updated;
        return this;
    }

    public void add(ImportCSVResponse current) {
        created+=current.created;
        updated+=current.updated;

    }
}
