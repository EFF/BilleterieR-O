package ca.ulaval.glo4003;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.EventDaoInMemory;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class BootstrapTest {
    private EventDao eventDao;
    private Bootstrap bootstrap;

    @Before
    public void setUp(){
        eventDao = new EventDaoInMemory();
        bootstrap = new Bootstrap(eventDao);
    }

    @Test
    public void initDataShouldAddItems(){
        bootstrap.initData();

        assertThat(eventDao.list().size()).isEqualTo(2);
    }

    @Test
    public void deleteDataShouldRemoveAllItems(){
        bootstrap.deleteAll();
        assertThat(eventDao.list().size()).isEqualTo(0);
    }
}
