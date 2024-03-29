package ca.ulaval.glo4003;

import ca.ulaval.glo4003.domain.Dao;
import ca.ulaval.glo4003.domain.ticketing.TicketDao;
import ca.ulaval.glo4003.domain.ticketing.TransactionDao;
import ca.ulaval.glo4003.domain.user.UserDao;
import ca.ulaval.glo4003.domain.boostrap.BootstrapperInteractor;
import ca.ulaval.glo4003.domain.event.*;
import ca.ulaval.glo4003.domain.ticketing.Ticket;
import ca.ulaval.glo4003.domain.ticketing.TicketFactory;
import ca.ulaval.glo4003.domain.user.User;
import com.google.inject.Inject;
import org.joda.time.LocalDateTime;

public class TestBootstrapperInteractor implements BootstrapperInteractor {

    private final int NUMBER_OF_TICKETS_1 = 120;
    private final int NUMBER_OF_TICKETS_2 = 1200;
    private final int NUMBER_OF_TICKETS_3 = 20;
    private final double FIRST_CATEGORY_PRICE = 12.0;
    private final double SECOND_CATEGORY_PRICE = 8.0;
    private final int FIRST_CATEGORY_ID = 0;
    private final int SECOND_CATEGORY_ID = 1;

    private final EventDao eventDao;
    private final Dao<Sport> sportDao;
    private final UserDao userDao;
    private final TicketDao ticketDao;
    private final TransactionDao transactionDao;

    @Inject
    public TestBootstrapperInteractor(EventDao eventDao, SportDao sportDao, UserDao userDao, TicketDao ticketDao,
                                      TransactionDao transactionDao) {
        this.eventDao = eventDao;
        this.sportDao = sportDao;
        this.userDao = userDao;
        this.ticketDao = ticketDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public void initData() {
        Sport soccer = new Sport("Soccer");
        Sport golf = new Sport("Golf");
        sportDao.create(soccer);
        sportDao.create(golf);

        Event event1 = new Event(soccer, Gender.MALE);
        event1.setDate(new LocalDateTime());
        Category category1 = new Category(FIRST_CATEGORY_PRICE, FIRST_CATEGORY_ID, CategoryType.GENERAL_ADMISSION);
        Category category2 = new Category(SECOND_CATEGORY_PRICE, SECOND_CATEGORY_ID, CategoryType.GENERAL_ADMISSION);

        event1.addCategory(category1);
        event1.addCategory(category2);
        eventDao.create(event1);
        initTicketForCategory(category1, event1, NUMBER_OF_TICKETS_1);
        initTicketForCategory(category2, event1, NUMBER_OF_TICKETS_2);

        Event event2 = new Event(soccer, Gender.FEMALE);
        event2.setDate(new LocalDateTime());
        Category category3 = new Category(FIRST_CATEGORY_PRICE, FIRST_CATEGORY_ID, CategoryType.GENERAL_ADMISSION);
        Category category4 = new Category(SECOND_CATEGORY_PRICE, SECOND_CATEGORY_ID, CategoryType.SEAT);

        event2.addCategory(category3);
        event2.addCategory(category4);
        eventDao.create(event2);
        initTicketForCategory(category3, event2, NUMBER_OF_TICKETS_1);
        initTicketForCategory(category4, event2, NUMBER_OF_TICKETS_3);

        initUsers();
    }

    private void initUsers() {
        for (int i = 0; i < 5; i++) {
            User user = new User("user" + i + "@example.com", "secret", false);
            userDao.create(user);
        }

        User user = new User("admin@example.com", "secret", true);
        userDao.create(user);
    }

    @Override
    public void deleteAll() {
        eventDao.deleteAll();
        sportDao.deleteAll();
        userDao.deleteAll();
        ticketDao.deleteAll();
        transactionDao.deleteAll();
    }

    private void initTicketForCategory(Category category, Event event, int numberOfTickets) {
        while (numberOfTickets > 0) {
            Ticket ticket;
            if (category.getType() == CategoryType.SEAT) {
                ticket = TicketFactory.createAvailableSeatTicket(
                        event.getId(),
                        category.getId(),
                        (numberOfTickets % 2 == 0) ? "Niveau 100" : "Niveau 200",
                        numberOfTickets);
            } else {
                ticket = TicketFactory.createAvailableGeneralAdmissionTicket(
                        event.getId(),
                        category.getId());
            }
            ticketDao.create(ticket);
            numberOfTickets--;
        }
    }
}
