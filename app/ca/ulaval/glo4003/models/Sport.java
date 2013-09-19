package ca.ulaval.glo4003.models;

public class Sport {

    private long id;
    private String name;

    public Sport(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
