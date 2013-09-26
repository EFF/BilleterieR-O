package ca.ulaval.glo4003.unittests;

import ca.ulaval.glo4003.Bootstrap;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.EventDaoInMemory;
import ca.ulaval.glo4003.dataaccessobjects.SportDaoInMemory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.FakeApplication;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;

public class BootstrapTest {

    private EventDao eventDao;
    private SportDaoInMemory sportDao;
    private Bootstrap bootstrap;
    private FakeApplication app;

    @Before
    public void setUp() {
        eventDao = new EventDaoInMemory();
        sportDao = new SportDaoInMemory();
        bootstrap = new Bootstrap(eventDao, sportDao);
        app = Helpers.fakeApplication();

        Helpers.start(app);
    }

    @After
    public void tearDown() {
        Helpers.stop(app);
    }

    @Test
    public void initDataShouldAddItems() {
        //Arrange

        //Act
        bootstrap.initData();

        //Assert
        assertEquals(2, eventDao.count());
        assertEquals(2, sportDao.count());
    }

    @Test
    public void deleteDataShouldRemoveAllItems() {
        //Arrange
        bootstrap.initData();

        //Act
        bootstrap.deleteAll();

        //Assert
        assertEquals(0, eventDao.count());
        assertEquals(0, sportDao.count());
    }
}
