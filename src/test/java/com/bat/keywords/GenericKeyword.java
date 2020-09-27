package com.bat.keywords;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.bat.configurations.SpringConfig;
import com.bat.util.Constants;
import com.bat.webdrivers.provider.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class GenericKeyword {
    public WebDriver driver;
    public Properties prop;
    public String objectKey;
    public String data;
    public String objectVal;

    public ExtentTest test;

    public void setProp(Properties prop) {
        this.prop = prop;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTest(ExtentTest test) {
        this.test = test;
    }

    public void log(String logMessage) {
        test.log(Status.INFO, logMessage);
    }

    public void openBrowser() throws Exception {
        log("Opening browser " + data);
        driver = new AnnotationConfigApplicationContext(SpringConfig.class)
                    .getBean("webDriverProvider", WebDriverProvider.class)
                    .getWebDriver(data);
        driver.manage().window().maximize();
    }

    public void closeBrowser() {
        log("Closing browser " + data);
        if(driver != null) driver.quit();
    }

    public void navigate() {
        objectVal = prop.getProperty(objectKey);
        log("Navigating to " + objectVal);
        driver.get(objectVal);
    }

    private By getObjectByLocator(String locatorKey) {
        if(locatorKey.endsWith(Constants.XPATH_LOCATOR_KEY)) return By.xpath(prop.getProperty(locatorKey));
        else if(locatorKey.endsWith(Constants.ID_LOCATOR_KEY)) return By.id(prop.getProperty(locatorKey));
        else if(locatorKey.endsWith(Constants.NAME_LOCATOR_KEY)) return By.name(prop.getProperty(locatorKey));
        else if(locatorKey.endsWith(Constants.CSS_LOCATOR_KEY)) return By.cssSelector(prop.getProperty(locatorKey));
        else reportFailure("Invalid object key : " + locatorKey + ".");
        return null;
    }

    private WebElement getObject(String objectKey) {
        WebElement element = null;
        try{
            // Find element
            log("Finding element " + objectKey);
            element = driver.findElement(getObjectByLocator(objectKey));
            // define explicit wait
            WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            // check for the visibility of the element
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            // check if the element is clickable
            explicitWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch(Exception e) {
            reportFailure("Element not found");
        }
        return element;
    }

    public void type() {
        getObject(objectKey).sendKeys(data);
        log("Typing in " + objectKey + ". Data : " + data);
    }

    public void click() {
        getObject(objectKey).click();
        log("Clicking " + objectKey);
    }

    public void validateTitle() {
        String expectedTitle = prop.getProperty(objectKey);
        String actualTitle = driver.getTitle();
        log("Validating Title.");
        if(!expectedTitle.equals(actualTitle)) {
            reportFailure("Title did not match. Got the title as " + actualTitle);
        }
    }

    private boolean isElementPresent(String objectKey) {
        List<WebElement> elementList = driver.findElements(getObjectByLocator(objectKey));
        return elementList.size() != 0;
    }

    public void validateElementPresent() {
        log("Validating Element's presence " + objectKey);
        if(!isElementPresent(objectKey)) {
            reportFailure("Element not found " + objectKey);
        }
    }

    public void reportFailure(String failureMessage) {
        // report the failure
        test.log(Status.FAIL, failureMessage);
        // take screen shot and embed it in the report
        // fail the test
//        Assert.fail(failureMessage);
    }
}
