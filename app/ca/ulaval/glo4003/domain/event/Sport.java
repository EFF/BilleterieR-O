package ca.ulaval.glo4003.domain.event;

import ca.ulaval.glo4003.domain.Record;

import java.io.Serializable;

public class Sport extends Record implements Serializable {

    private String name;

    public Sport(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
