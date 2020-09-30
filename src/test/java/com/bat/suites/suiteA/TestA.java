package com.bat.suites.suiteA;

import com.aventstack.extentreports.Status;
import com.bat.suites.base.BaseTest;
import com.bat.util.Constants;
import com.bat.util.DataUtil;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Hashtable;

public class TestA extends BaseTest {

    @Test(dataProvider = "getData")
    public void tester(Hashtable<String, String> table) throws Exception {
        test.log(Status.INFO, "Checking run mode before starting " + testName);
        if(DataUtil.isRunTetCase(testName, xlsReader) && table.get(Constants.RUN_MODE_COL).equals(Constants.RUN_MODE_YES)) {
            test.log(Status.INFO, "Starting test " + testName);
            driverScript.executeKeywords(testName, xlsReader, table);
            System.out.println(table);
        }  else {
            test.log(Status.SKIP, "Skipping test with data : " + table);
            throw new SkipException("Skip test.");
        }
    }
}
