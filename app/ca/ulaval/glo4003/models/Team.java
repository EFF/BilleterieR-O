package ca.ulaval.glo4003.models;

import java.io.Serializable;

public class Team extends Record implements Serializable {

    private String name = "";

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
