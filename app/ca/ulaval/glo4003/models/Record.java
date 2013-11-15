package ca.ulaval.glo4003.models;

import java.io.Serializable;

public abstract class Record extends Object implements Serializable {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
