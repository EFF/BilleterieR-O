package ca.ulaval.glo4003;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.models.*;
import com.google.inject.Inject;
import org.joda.time.LocalDateTime;

import java.util.Random;

public class TestBootstrapper implements Bootstrapper {

    private final EventDao eventDao;
    private final SportDao sportDao;
    private final UserDao userDao;
    private final TicketDao ticketDao;

    @Inject
    public TestBootstrapper(EventDao eventDao, SportDao sportDao, UserDao userDao, TicketDao ticketDao) {
        this.eventDao = eventDao;
        this.sportDao = sportDao;
        this.userDao = userDao;
        this.ticketDao = ticketDao;
    }

    @Override
    public void initData() {
        Sport soccer = new Sport("Soccer");
        Sport golf = new Sport("Golf");
        sportDao.create(soccer);
        sportDao.create(golf);

        Event event1 = new Event(soccer, Gender.MALE);
        event1.setDate(new LocalDateTime());
        Category category1 = new Category(12.0, 120, 1, CategoryType.GENERAL_ADMISSION);
        Category category2 = new Category(8.0, 1200, 2, CategoryType.GENERAL_ADMISSION);

        event1.addCategory(category1);
        event1.addCategory(category2);
        eventDao.create(event1);

        Event event2 = new Event(soccer, Gender.FEMALE);
        event2.setDate(new LocalDateTime());
        Category category3 = new Category(12.0, 120, 3, CategoryType.GENERAL_ADMISSION);
        Category category4 = new Category(8.0, 1200, 4, CategoryType.GENERAL_ADMISSION);

        event2.addCategory(category3);
        event2.addCategory(category4);

        eventDao.create(event2);
        initTicket();

        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setEmail("user" + i + "@example.com");
            user.setPassword("secret");
            userDao.create(user);
        }
    }

    @Override
    public void deleteAll() {
        eventDao.deleteAll();
        sportDao.deleteAll();
        userDao.deleteAll();
        ticketDao.deleteAll();
    }

    private void initTicket() {
        for (Event event : eventDao.list()) {
            for (Category category : event.getCategories()) {
                int numberOfTickets = category.getNumberOfTickets();
                while (numberOfTickets > 0) {
                    String strSection = "";
                    int seat = -1;
                    if (category.getType() == CategoryType.SEAT) {
                        strSection = "Niveau " + (new Random().nextInt(2) + 1) * 100;
                        seat = numberOfTickets;
                    }
                    Ticket ticket = new Ticket(event.getId(),
                            category.getId(),
                            strSection,
                            seat);

                    ticketDao.create(ticket);
                    numberOfTickets--;
                }
            }
        }
    }
}
