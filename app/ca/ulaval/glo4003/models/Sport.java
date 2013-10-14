package ca.ulaval.glo4003.models;

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
