package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.controllers.BootstrapController;
import ca.ulaval.glo4003.interactors.BootstrapperInteractor;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import play.mvc.Http;
import play.mvc.Result;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static play.test.Helpers.status;

@RunWith(JukitoRunner.class)
public class BootstrapperControllerTest extends BaseControllerTest {

    @Inject
    private BootstrapController bootstrapController;

    @Test
    public void bootstrap(BootstrapperInteractor mockedBootstrapperInteractor) {
        Result result = bootstrapController.index();

        assertEquals(Http.Status.OK, status(result));
        InOrder inOrder = inOrder(mockedBootstrapperInteractor);
        inOrder.verify(mockedBootstrapperInteractor).deleteAll();
        inOrder.verify(mockedBootstrapperInteractor).initData();
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(BootstrapperInteractor.class);
        }
    }
}
