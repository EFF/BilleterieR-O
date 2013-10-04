package ca.ulaval.glo4003.unittests.helpers;

import ca.ulaval.glo4003.models.*;

import java.util.Random;

public class EventsTestHelper {

    public static final String FIRST_RANDOM_SPORT = "Football";
    public static final String SECOND_RANDOM_SPORT = "Rugby";
    public static final String A_RANDOM_TEAM_NAME = "Rouge et Or";
    public static final String A_SECOND_RANDOM_TEAM_NAME = "Vert et Or";
    public static final long A_CATEGORY_ID = new Random().nextLong();
    public static final int AN_INT = new Random().nextInt(Integer.MAX_VALUE - 1) + 1;
    public static final double A_DOUBLE = new Random().nextDouble();


    public static Event createRandomEventtWithCategoryGivenSport(String sport) {
        Sport soccer = new Sport(sport);
        Event event1 = new Event(soccer, Gender.MALE);
        Category category1 = new Category(A_DOUBLE, AN_INT, A_CATEGORY_ID);
        Category category2 = new Category(A_DOUBLE, AN_INT, A_CATEGORY_ID + 1);

        event1.addCategory(category1);
        event1.addCategory(category2);

        return event1;
    }

    public static Event createRandomEventGivenTeam(String teamName) {
        Event event1 = new Event(null, Gender.MALE);
        Team team = new Team(teamName);
        event1.setTeam(team);

        return event1;
    }

    public static Event createRandomEventGivenACategory(Category category){
        Event event = new Event(null, null);
        event.addCategory(category);

        return event;
    }
}
