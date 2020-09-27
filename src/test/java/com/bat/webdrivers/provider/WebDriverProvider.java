package com.bat.webdrivers.provider;

import com.bat.util.Constants;
import com.bat.webdrivers.ChromeWebDriver;
import com.bat.webdrivers.EdgeWebDriver;
import com.bat.webdrivers.FirefoxWebDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WebDriverProvider {
    @Value("${implicit_wait_time}")
    private int implicitWaitTime;

    @Autowired
    private ChromeWebDriver chromeWebDriver;

    @Autowired
    private FirefoxWebDriver firefoxWebDriver;

    @Autowired
    private EdgeWebDriver edgeWebDriver;

    private ChromeDriver getChromeWebDriver() throws Exception {
        return chromeWebDriver.getChromeDriver();
    }

    private FirefoxDriver getFirefoxWebDriver() throws Exception {
        return firefoxWebDriver.getFirefoxDriver();
    }

    private EdgeDriver getEdgeWebDriver() throws Exception {
        return edgeWebDriver.getEdgeDriver();
    }

    public WebDriver getWebDriver(String browserName) throws Exception {
        WebDriver driver;
        switch (browserName.toLowerCase()) {
            case Constants.CHROME_BROWSER:
                driver = getChromeWebDriver();
                break;
            case Constants.FIREFOX_BROWSER:
            case Constants.MOZILLA_BROWSER:
                driver = getFirefoxWebDriver();
                break;
            case Constants.EDGE_BROWSER:
                driver = getEdgeWebDriver();
                break;
            default:
                throw new Exception("Invalid browser name provided");
        }

        // add implicit wait time before returning
        driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
        return driver;
    }
}
