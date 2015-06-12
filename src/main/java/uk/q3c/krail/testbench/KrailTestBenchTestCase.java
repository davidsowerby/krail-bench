/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.testbench;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elementsbase.AbstractElement;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.testbench.page.object.LoginFormPageObject;
import uk.q3c.krail.testbench.page.object.LoginStatusPageObject;
import uk.q3c.util.ID;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class KrailTestBenchTestCase extends TestBenchTestCase {
    private static Logger log = LoggerFactory.getLogger(KrailTestBenchTestCase.class);
    protected final StringBuffer verificationErrors = new StringBuffer();
    protected String baseUrl = "http://localhost:8080/";
    protected LoginStatusPageObject loginStatus = new LoginStatusPageObject(this);
    protected LoginFormPageObject loginForm = new LoginFormPageObject(this);
    protected String appContext = "testapp";
    protected Locale firefoxLocale = Locale.UK;
    private int currentDriverIndex = 0;
    private List<WebDriver> drivers = new ArrayList<>();

    @Before
    public void baseSetup() throws Exception {
        System.out.println("setting up base test bench case");

        addDriver(TestBench.createDriver(createFirefoxDriver()));
        getDriver().manage()
                   .window()
                   .setPosition(new Point(0, 0));
        getDriver().manage()
                   .window()
                   .setSize(new Dimension(1024, 768));
        System.out.println("default driver added");
        System.out.println("current driver index set to " + currentDriverIndex);
    }

    /**
     * Adds a driver, and if it is the first to be added, also sets it as default driver (the default driver is the 'driver' property of the  {@link
     * TestBenchTestCase}
     *
     * @param driver
     */
    protected void addDriver(WebDriver driver) {
        System.out.println("adding driver " + drivers.size());
        WebDriver realDriver;
        if (!(driver instanceof TestBenchDriverProxy)) {
            realDriver = TestBench.createDriver(driver);
        } else {
            realDriver = driver;
        }
        drivers.add(realDriver);

        if (drivers.size() == 1) {
            this.driver = realDriver;
        }
    }

    protected WebDriver createFirefoxDriver() {
        System.out.println("Creating Firefox driver");
        FirefoxProfile profile = createFirefoxProfile(firefoxLocale);
        return new FirefoxDriver(profile);
    }

    protected FirefoxProfile createFirefoxProfile(Locale locale) {
        FirefoxProfile profile = new FirefoxProfile();
        String s1 = locale.toLanguageTag()
                          .toLowerCase()
                          .replace("_", "-");
        profile.setPreference("intl.accept_languages", s1);
        return profile;
    }

    /**
     * The same as {{@link #getDriver(int)} with index of {@link #currentDriverIndex}
     *
     * @return the WebDriver instance at index currentDriverIndex
     */
    @Override
    public WebDriver getDriver() {
        //needed because @Rule assumes that null will be returned when there is no driver
        if (drivers.size() == 0) {
            return null;
        }
        return drivers.get(currentDriverIndex);
    }

    protected WebDriver createChromeDriver() {
        System.out.println("Creating Chrome driver");
        return new ChromeDriver();
    }

    /**
     * "starts" the current driver by navigating the current driver to the {@link #rootUrl}.
     */
    protected void startDriver() {
        getDriver().get(rootUrl());
        waitForUrl("home");
    }

    protected String rootUrl() {
        String rootUrl = buildUrl(baseUrl, appContext);
        //Tomcat has issues when there is no trailing slash, so make sure it is there
        if (!rootUrl.endsWith("/")) {
            rootUrl += "/";
        }
        return rootUrl;
    }

    protected String buildUrl(String... segments) {
        StringBuilder buf = new StringBuilder();
        boolean firstSegment = true;
        for (String segment : segments) {
            if (!firstSegment) {
                buf.append("/");
            } else {
                firstSegment = false;
            }
            buf.append(segment.replace("/", ""));
        }
        String result = buf.toString();
        // slashes will have been removed
        result = result.replace("http:", "http://");
        result = result.replace("https:", "https://");
        return result;
    }

    public boolean waitForUrl(String fragment){
        long startTime= new Date().getTime();
        long elapsedTime=0;
        String expected = rootUrl() + "#" + fragment;
        String actual = getDriver().getCurrentUrl();
        while (!actual.equals(expected) && (elapsedTime < 2000)) {
            actual = getDriver().getCurrentUrl();
            elapsedTime=new Date().getTime()-startTime;
            System.out.println("waiting for url: "+fragment+" "+elapsedTime+"ms");
        }
        return elapsedTime< 2000;
    }

    @After
    public void baseTearDown() {
        System.out.println("closing all drivers");
        for (WebDriver webDriver : drivers) {
            System.out.println("closing web driver: " + webDriver.getTitle() + "");
            webDriver.close();

        }
        //        if (!drivers.contains(driver)) {
        //            driver.close();//in case it was set directly and not through addDriver
        //        }

        drivers.clear();
        pause(1000);
    }

    public void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            log.error("Sleep was interrupted");
        }
    }

    public String getAppContext() {
        return appContext;
    }

    protected void verifyUrl(String fragment) {
        String expected = rootUrl() + "#" + fragment;
        String actual = getDriver().getCurrentUrl();
        assertThat(actual).isEqualTo(expected);
    }

    protected void verifyNotUrl(String fragment) {
        String expected = rootUrl() + fragment;
        String actual = getDriver().getCurrentUrl();
        assertThat(actual).isNotEqualTo(expected);
    }

    /**
     * Navigates the current driver to {@code fragment}
     * @param fragment
     */
    protected void navigateTo(String fragment) {
        String url = url(fragment);
        getDriver().get(url);
        waitForUrl(fragment);
    }

    protected String url(String fragment) {
        return rootUrl() + "#" + fragment;
    }

    public WebDriver getDriver(int index) {
        //needed because @Rule assumes that null will be returned when there is no driver
        if (drivers.size() == 0) {
            return null;
        }
        return drivers.get(index);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected void navigateForward() {
        getDriver().navigate()
              .forward();
        pause(500);
    }

    protected void navigateBack() {
        getDriver().navigate()
              .back();
        pause(500);
    }

    protected void closeNotification() {
        notification().close();
    }

    protected NotificationElement notification() {
        NotificationElement notification = $(NotificationElement.class).get(0);
        return notification;
    }

    /**
     * shorthand method to click the login button, and fill in the login form using credentials in {@link #loginForm}
     */
    protected void login() {
        loginStatus.loginButton()
                   .click();
        System.out.println("login status button clicked");
        loginForm.login();
    }

    protected <E extends AbstractElement> E element(Class<E> elementClass, Optional<?> qualifier, Class<?>... componentClasses) {

        return element(elementClass, ID.getIdc(qualifier, componentClasses));
    }

    public <E extends AbstractElement> E element(Class<E> elementClass, String id) {
        return $(elementClass).id(id);
    }

    /**
     * Indexed from 0 (that is, the default driver is at index 0)
     *
     * @param index
     *
     * @return
     */
    public WebDriver selectDriver(int index) {
        try {
            WebDriver wd = drivers.get(index);
            currentDriverIndex = index;
            setDriver(wd);
            System.out.println("Driver index " + index + " selected");
            return driver;
        } catch (Exception e) {
            throw new RuntimeException("Driver index of " + index + " is invalid");
        }
    }

    protected WebDriver driver(int index) {
        return drivers.get(index);
    }


}
