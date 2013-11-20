package ca.ulaval.glo4003.models;

import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Event extends Record implements Serializable {

    private List<Category> categories;
    private Sport sport;
    private Gender gender;
    private Team homeTeam;
    private Team visitorTeam;
    private LocalDateTime date;

    public Event(Sport sport, Gender gender) {
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

    public List<Category> getCategories() {
        return categories;
    }

    public Sport getSport() {
        return sport;
    }

    public Gender getGender() {
        return gender;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDateAsString() {
        return date == null ? null : date.toString();
    }

    public Team getVisitorTeam() {
        return visitorTeam;
    }

    public void setVisitorTeam(Team visitorTeam) {
        this.visitorTeam = visitorTeam;
    }
}
