package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;

public class TestEventDaoInMemory extends EventDaoInMemory {
    public TestEventDaoInMemory() {
        super();
        Event event1 = new Event();
        event1.id = 1;
        Category category1 = new Category();
        category1.id = 1;
        category1.numberOfTickets = 120;
        category1.price = 12.0;

        Category category2 = new Category();
        category2.id = 2;
        category2.numberOfTickets = 1200;
        category2.price = 8.0;

        event1.categories.add(category1);
        event1.categories.add(category2);
        events.add(event1);

        Event event2 = new Event();
        event2.id = 2;
        Category category3 = new Category();
        category3.id = 3;
        category3.numberOfTickets = 120;
        category3.price = 12.0;

        Category category4 = new Category();
        category4.id = 4;
        category4.numberOfTickets = 1200;
        category4.price = 8.0;

        event2.categories.add(category3);
        event2.categories.add(category4);

        events.add(event2);
    }
}
