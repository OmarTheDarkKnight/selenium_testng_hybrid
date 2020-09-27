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

    public void navigate() {
        driver.get(prop.getProperty(objectKey));
    }

    private By getObjectByLocator(String locatorKey) {
        if(locatorKey.endsWith(Constants.XPATH_LOCATOR_KEY)) return By.xpath(prop.getProperty(locatorKey));
        else if(locatorKey.endsWith(Constants.ID_LOCATOR_KEY)) return By.id(prop.getProperty(locatorKey));
        else if(locatorKey.endsWith(Constants.NAME_LOCATOR_KEY)) return By.name(prop.getProperty(locatorKey));
        else return By.cssSelector(prop.getProperty(locatorKey));
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
            // report failure
        }
        return element;
    }

    public void type() {
        getObject(objectKey).sendKeys(data);
    }

    public void click() {
        getObject(objectKey).click();
    }
}
