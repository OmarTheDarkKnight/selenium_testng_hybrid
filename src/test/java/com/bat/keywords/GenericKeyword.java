package com.bat.keywords;

import com.bat.configurations.SpringConfig;
import com.bat.util.Constants;
import com.bat.webdrivers.provider.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class GenericKeyword {
    public WebDriver driver;
    public Properties prop;
    public String objectKey;
    public String data;

    public void setProp(Properties prop) {
        this.prop = prop;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void openBrowser() throws Exception {
        driver = new AnnotationConfigApplicationContext(SpringConfig.class)
                    .getBean("webDriverProvider", WebDriverProvider.class)
                    .getWebDriver(data);
        driver.manage().window().maximize();
    }

    public void closeBrowser() {
        if(driver != null) driver.quit();
    }

    public void navigate() {
        driver.get(prop.getProperty(objectKey));
    }

    private By getObjectByLocator(String locatorKey) {
        if(locatorKey.endsWith(Constants.XPATH_LOCATOR_KEY)) return By.xpath(prop.getProperty(locatorKey));
        else if(locatorKey.endsWith(Constants.ID_LOCATOR_KEY)) return By.id(prop.getProperty(locatorKey));
        else if(locatorKey.endsWith(Constants.NAME_LOCATOR_KEY)) return By.name(prop.getProperty(locatorKey));
        else if(locatorKey.endsWith(Constants.CSS_LOCATOR_KEY)) return By.cssSelector(prop.getProperty(locatorKey));
        else reportFailure("Invalid object key : " + objectKey + ".");
        return null;
    }

    private WebElement getObject(String objectKey) {
        WebElement element = null;
        try{
            // Find element
            element = driver.findElement(getObjectByLocator(objectKey));
            // define explicit wait
            WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            // check for the visibility of the element
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            // check if the element is clickable
            explicitWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch(Exception e) {
            reportFailure("Object not found");
        }
        return element;
    }

    public void type() {
        getObject(objectKey).sendKeys(data);
    }

    public void click() {
        getObject(objectKey).click();
    }

    public void validateTitle() {
        String expectedTitle = prop.getProperty(objectKey);
        String actualTitle = driver.getTitle();
        if(!expectedTitle.equals(actualTitle)) {
            reportFailure("Title did not match. Got the title as " + actualTitle);
        }
    }

    private boolean isElementPresent(String objectKey) {
        List<WebElement> elementList = driver.findElements(getObjectByLocator(objectKey));
        return elementList.size() != 0;
    }

    public void validateElementPresent() {
        if(!isElementPresent(objectKey)) {
            reportFailure("Element not found " + objectKey);
        }
    }

    public void reportFailure(String failureMessage) {}
}
