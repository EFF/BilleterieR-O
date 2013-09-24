package ca.ulaval.glo4003.unittests;

import ca.ulaval.glo4003.Bootstrap;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.EventDaoInMemory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.FakeApplication;
import play.test.Helpers;

import static org.fest.assertions.Assertions.assertThat;

public class BootstrapTest {
    private EventDao eventDao;
    private Bootstrap bootstrap;
    private FakeApplication app;

    @Before
    public void setUp() {
        eventDao = new EventDaoInMemory();
        bootstrap = new Bootstrap(eventDao);
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
        assertThat(eventDao.list().size()).isEqualTo(2);
    }

    @Test
    public void deleteDataShouldRemoveAllItems() {
        //Arrange
        bootstrap.initData();

        //Act
        bootstrap.deleteAll();

        //Assert
        assertThat(eventDao.list().size()).isEqualTo(0);
    }
}
