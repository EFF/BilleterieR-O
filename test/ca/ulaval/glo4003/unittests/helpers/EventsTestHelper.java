package ca.ulaval.glo4003.unittests.helpers;

import ca.ulaval.glo4003.models.*;

public class EventsTestHelper {

    public static final String FIRST_RANDOM_SPORT = "Football";
    public static final String SECOND_RANDOM_SPORT = "Rugby";
    public static final String A_RANDOM_TEAM_NAME = "Rouge et Or";
    public static final String A_SECOND_RANDOM_TEAM_NAME = "Vert et Or";

    public static Event createRandomEventGivenSport(int id, String sport) {
        Sport soccer = new Sport(1, sport);
        Event event1 = new Event(id, soccer, Gender.MALE);
        Category category1 = new Category(1, 12.0, 120);
        Category category2 = new Category(2, 8.0, 1200);

        event1.addCategory(category1);
        event1.addCategory(category2);

        return event1;
    }

    public static Event createRandomEventGivenTeam(int id, String teamName) {
        Event event1 = new Event(id, null, Gender.MALE);
        Team team = new Team(1, teamName);
        event1.setTeam(team);

        return event1;
    }
}
