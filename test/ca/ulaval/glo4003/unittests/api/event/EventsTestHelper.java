package ca.ulaval.glo4003.unittests.api.event;

import ca.ulaval.glo4003.domain.event.*;

import java.util.Random;

public class EventsTestHelper {

    public static final String FIRST_RANDOM_SPORT = "Football";
    public static final String SECOND_RANDOM_SPORT = "Rugby";
    public static final String A_RANDOM_TEAM_NAME = "Rouge et Or";
    public static final String A_SECOND_RANDOM_TEAM_NAME = "Vert et Or";
    public static final long A_CATEGORY_ID = new Random().nextLong();
    public static final double A_DOUBLE = new Random().nextDouble();

    public static Event createRandomEventWithCategoryGivenSport(String sport) {
        Sport sport1 = new Sport(sport);
        Event event1 = new Event(sport1, Gender.MALE);
        Category category1 = new Category(A_DOUBLE, A_CATEGORY_ID,CategoryType.GENERAL_ADMISSION);
        Category category2 = new Category(A_DOUBLE, A_CATEGORY_ID + 1, CategoryType.GENERAL_ADMISSION);

        event1.addCategory(category1);
        event1.addCategory(category2);

        return event1;
    }

    public static Event createRandomEventGivenTeam(String teamName) {
        Event event1 = new Event(null, Gender.MALE);
        Team team = new Team(teamName);
        event1.setHomeTeam(team);

        return event1;
    }

}
