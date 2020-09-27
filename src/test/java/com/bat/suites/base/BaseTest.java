package com.bat.suites.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.bat.driver.DriverScript;
import com.bat.extent_report.ExtentManager;
import com.bat.util.DataUtil;
import com.bat.util.ExcelFileReader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.asserts.SoftAssert;

import java.io.FileInputStream;
import java.util.Properties;

public class BaseTest {
    public Properties envProperties;
    public Properties prop;

    public String testName;
    public ExcelFileReader xlsReader;
    public DriverScript driverScript = new DriverScript();

    public ExtentReports report;
    public ExtentTest test;
    public SoftAssert softAssert;

    @BeforeTest
    public void initBeforeTest() {
        // load the properties files
        envProperties = new Properties();
        prop = new Properties();
        try {
            FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"//src//test//resources//env.properties");
            envProperties.load(fs);
            String env = envProperties.getProperty("env");
            fs = new FileInputStream(System.getProperty("user.dir")+"//src//test//resources//"+env+".properties");
            prop.load(fs);

            // testName is the name of the test in the xls file.
            // It will be extracted dynamically for each test case.
            // That's why the test class names should be the same as TCID in xls file.
            testName = this.getClass().getSimpleName();

            // The keys that contains the xls file location in the .properties file should also be the same as the package name
            // i.e. for 'suiteA' package, the (key:value) pair of the properties file will be suiteA_xls = "file_location"
            // It is important to follow this to make the xls file loading operation dynamic
            String[] packageNameArr = this.getClass().getPackage().getName().split("\\.");
            xlsReader = new ExcelFileReader(prop.getProperty(packageNameArr[packageNameArr.length - 1] + "_xlsx"));

            // set the prop variable of the driver script class
            driverScript.setProp(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeMethod
    public void initBeforeMethod() {
        report = ExtentManager.getReport(prop.getProperty("report_path"));
        test = report.createTest(testName);
        driverScript.setTest(test);
    }

    @AfterMethod
    public void cleanUpAfterMethod() {
        driverScript.quit();

        if(report != null)
            report.flush();
    }

    @DataProvider
    public Object[][] getData() {
        return DataUtil.getTestData(testName, xlsReader);
    }
}
