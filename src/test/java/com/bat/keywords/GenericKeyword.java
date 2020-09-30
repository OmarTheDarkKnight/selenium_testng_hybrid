package com.bat.keywords;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.bat.configurations.SpringConfig;
import com.bat.extent_report.ExtentManager;
import com.bat.util.Constants;
import com.bat.webdrivers.provider.WebDriverProvider;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.asserts.SoftAssert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class GenericKeyword {
    public WebDriver driver;
    public Properties prop;
    public String objectKey;
    public String data;
    public String objectVal;
    public String proceedOnFail;
    public SoftAssert softAssert;

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

    public void setProceedOnFail(String proceedOnFail) {
        this.proceedOnFail = proceedOnFail;
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
        log("Closing browser.");
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
        takeScreenShot(failureMessage);
        // fail the test depending on the value of proceed on failure
        softAssert.fail(failureMessage);
        if(proceedOnFail.equals("N")) {
            assertAll();
        }
    }

    public void assertAll() {
        softAssert.assertAll();
    }

    public void takeScreenShot(String title) {
        takeScreenShot(null, title);
    }

    public void takeScreenShot(WebElement element, String title) {
        String fileName = new Date().toString()
                .replaceAll(":", "-")
                .replaceAll(" ", "-")
                + ".png";
        String filePath = "./screenshots/" + fileName;

        // Get entire page screenshot
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            if(element != null) {
                BufferedImage fullImg = ImageIO.read(screenshot);
                // Get the location of element on the page
                Point point = element.getLocation();

                // Get width and height of the element
                int eleWidth = element.getSize().getWidth();
                int eleHeight = element.getSize().getHeight();

                // Crop the entire page screenshot to get only element screenshot
                BufferedImage elementScreenshot= fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
                ImageIO.write(elementScreenshot, "png", screenshot);
            }

            // Copy the element screenshot to report
            FileUtils.copyFile(screenshot, new File(ExtentManager.screenshotFolder.trim() + fileName));
            log("Taking Screenshot ->");
            test.addScreenCaptureFromPath(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
