package ca.ulaval.glo4003.unittests.helpers;

import ca.ulaval.glo4003.models.*;

public class EventsTestHelper {

    public static final String FIRST_RANDOM_SPORT = "Football";
    public static final String SECOND_RANDOM_SPORT = "Rugby";
    public static final String A_RANDOM_TEAM_NAME = "Rouge et Or";
    public static final String A_SECOND_RANDOM_TEAM_NAME = "Vert et Or";

    public static Event createRandomEventGivenSport(String sport) {
        Sport soccer = new Sport(sport);
        Event event1 = new Event(soccer, Gender.MALE);
        Category category1 = new Category(12.0, 120);
        Category category2 = new Category(8.0, 1200);

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
}
