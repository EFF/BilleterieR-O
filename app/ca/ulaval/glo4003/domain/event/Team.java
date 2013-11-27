package ca.ulaval.glo4003.domain.event;

import ca.ulaval.glo4003.domain.Record;

import javax.persistence.Column;
import java.io.Serializable;

public class Team extends Record implements Serializable {

    @Column(unique = true)
    private String name = "";

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
