package ca.ulaval.glo4003;

import org.junit.*;

import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class IntegrationTest {
	@Test
	public void testHelloWorld() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						browser.goTo("http://localhost:3333");
						assertThat(browser.pageSource()).contains(
								"Hello world!");
					}
				});
	}

	@Test
	public void testHelloUncleBob() {
		running(testServer(3333, fakeApplication(inMemoryDatabase())),
				HTMLUNIT, new Callback<TestBrowser>() {
					@Override
					public void invoke(TestBrowser browser) {
						browser.goTo("http://localhost:3333?name=Uncle%20Bob");
						assertThat(browser.pageSource()).contains(
								"Hello Uncle Bob!");
					}
				});
	}
}
