package com.bat.driver;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.bat.keywords.ApplicationKeyword;
import com.bat.util.Constants;
import com.bat.util.ExcelFileReader;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;

public class DriverScript {
    private Properties prop;
    private ApplicationKeyword applicationKeyword;

    private ExtentTest test;

    public Properties getProp() {
        return prop;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }

    public void setTest(ExtentTest test) {
        this.test = test;
    }

    public void executeKeywords(String testName, ExcelFileReader xlsReader, Hashtable<String, String> data) throws Exception {
        Method method;
        applicationKeyword = new ApplicationKeyword();
        applicationKeyword.setProp(this.getProp());
        applicationKeyword.setTest(test);

        int rowNumber = xlsReader.getRowCount(Constants.KEYWORDS_SHEET);
        for(int rNum = 1; rNum < rowNumber; rNum++) {
            if(xlsReader.getCellData(rNum, Constants.TCID_COL).equals(testName)) {
                // read the required data from the xls file
                String keyword = xlsReader.getCellData(rNum, Constants.KEYWORD_COL);
                String objectKey = xlsReader.getCellData(rNum, Constants.OBJECT_KEY_COL);
                String dataKey = xlsReader.getCellData(rNum, Constants.DATA_KEY_COL);
                String dataValue = data.get(dataKey);
                test.log(Status.INFO, "FROM XLS FILE : " + keyword + " ---- " + prop.getProperty(objectKey) + " ---- " + dataKey + " : " + dataValue);

                String proceedOnFail = xlsReader.getCellData(rNum, Constants.PROCEED_COL).equals("N") ? "N" : "Y";
                applicationKeyword.setProceedOnFail(proceedOnFail);

                // set the object key and data value in the application key word class
                applicationKeyword.setObjectKey(objectKey);
                applicationKeyword.setData(dataValue);

                // call the keyword methods dynamically
                method = applicationKeyword.getClass().getMethod(keyword);
                method.invoke(applicationKeyword);
            }
        }
        applicationKeyword.assertAll();
    }

    public void quit() {
        if(applicationKeyword != null) applicationKeyword.closeBrowser();
    }
}
