package ca.ulaval.glo4003.models;


public class Team {

    private int id = 0;
    private String name = "";

    public Team(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
