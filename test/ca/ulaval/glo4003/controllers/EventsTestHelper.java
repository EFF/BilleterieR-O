package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.models.Sport;

public class EventsTestHelper {

    public static Event createRandomEventGivenSport(int id, String sport) {
        Sport soccer = new Sport(1, sport);
        Event event1 = new Event(id, soccer, Gender.MALE);
        Category category1 = new Category(1, 12.0, 120);
        Category category2 = new Category(2, 8.0, 1200);

        event1.addCategory(category1);
        event1.addCategory(category2);

        return event1;
    }

}
