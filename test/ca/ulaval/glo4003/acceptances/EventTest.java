package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;


public class EventTest {
    @Test
    public void showCategoriesAndItsAvailableTickets() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/#!/events/1");
                browser.await().atMost(5000).until(".category").isPresent();
                FluentList<FluentWebElement> categories = browser.find(".category");

                assertThat(categories.size()).isEqualTo(2);
                assertThat(categories.get(0).getText()).contains("1");
                assertThat(categories.get(0).getText()).contains("12$");
                assertThat(categories.get(0).getText()).contains("120");
                assertThat(categories.get(1).getText()).contains("2");
                assertThat(categories.get(1).getText()).contains("8$");
                assertThat(categories.get(1).getText()).contains("1200");
            }
        });
    }
}
