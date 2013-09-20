package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.models.Sport;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class EventDaoInMemoryTest {

    private EventDao eventDao;

    @Before
    public void setUp() {
        eventDao = new EventDaoInMemory();

        Sport soccer = new Sport(1, "Soccer");
        Event event1 = new Event(1, soccer, Gender.MALE);
        Category category1 = new Category(1, 12.0, 120);
        Category category2 = new Category(2, 8.0, 1200);

        event1.addCategory(category1);
        event1.addCategory(category2);
        eventDao.create(event1);

        Event event2 = new Event(2, soccer, Gender.FEMALE);
        Category category3 = new Category(3, 12.0, 120);
        Category category4 = new Category(4, 8.0, 1200);

        event2.addCategory(category3);
        event2.addCategory(category4);

        eventDao.create(event2);

    }

    @Test
    public void testShow() {
        assertThat(eventDao.read(1).getId()).isEqualTo(1);
        assertThat(eventDao.read(1).getGender()).isEqualTo(Gender.MALE);

        assertThat(eventDao.read(2).getId()).isEqualTo(2);
        assertThat(eventDao.read(2).getGender()).isEqualTo(Gender.FEMALE);
    }

    @Test
    public void testList() {
        assertThat(eventDao.list().size()).isEqualTo(2);
    }
}
