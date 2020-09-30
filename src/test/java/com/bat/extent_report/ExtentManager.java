package com.bat.extent_report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.util.Date;

public class ExtentManager {
    public static ExtentReports report;
    public static String screenshotFolder;

    public static ExtentReports getReport(String reportPath) {
        if(report == null) {
            // set the folder names
            Date date = new Date();
            String reportFolderName = reportPath + date.toString().replaceAll(":", "-");
            screenshotFolder = reportFolderName + "//screenshots//";

            // make the folders
            File file = new File(screenshotFolder);
            file.mkdirs();

            //initialize adn configure the extent spark reporter
            ExtentSparkReporter reporter = new ExtentSparkReporter(reportFolderName+"//Report.html");
            reporter.config().setReportName("TestNG Hybrid Demo Report");
            reporter.config().setDocumentTitle("Automation reports");
            reporter.config().setTheme(Theme.DARK);
            reporter.config().setEncoding("utf-8");

            //initialize the report
            report = new ExtentReports();
            report.attachReporter(reporter);
        }
        return report;
    }
}
