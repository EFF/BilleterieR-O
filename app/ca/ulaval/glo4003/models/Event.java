package ca.ulaval.glo4003.models;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Event {

    private long id;
    private List<Category> categories;
    private Sport sport;
    private Gender gender;
    private Team team;
    private LocalDate date;

    public Event(long id, Sport sport, Gender gender) {
        this.id = id;
        this.sport = sport;
        this.gender = gender;
        categories = new ArrayList<>();
    }

    public int getTotalNumberOfTickets() {
        int total = 0;

        Iterator<Category> itr = categories.iterator();
        while (itr.hasNext()) {
            Category currentCategory = itr.next();
            total += currentCategory.getNumberOfTickets();
        }

        return total;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public long getId() {
        return id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Sport getSport() {
        return sport;
    }

    public Gender getGender() {
        return gender;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
