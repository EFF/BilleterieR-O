package ca.ulaval.glo4003.unittests.controllers;

import org.junit.After;
import org.junit.Before;
import play.libs.Json;
import play.mvc.Http;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.fakeRequest;

public abstract class BaseControllerTest {
    protected Http.Context mockedContext;
    protected Http.Session mockedSession;
    protected Http.Request mockedRequest;
    protected Http.RequestBody mockedBody;

    @Before
    public void setupMocks() {
        mockedBody = mock(Http.RequestBody.class);
        mockedContext = mock(Http.Context.class);
        mockedSession = mock(Http.Session.class);
        mockedRequest = mock(Http.Request.class);

        when(mockedBody.asJson()).thenReturn(Json.newObject());
        when(mockedRequest.body()).thenReturn(mockedBody);
        when(mockedContext.request()).thenReturn(mockedRequest);
        when(mockedContext.session()).thenReturn(mockedSession);

        Http.Context.current.set(mockedContext);
    }

    @After
    public void tearDown() {
        Http.Context.current.remove();
    }
}
