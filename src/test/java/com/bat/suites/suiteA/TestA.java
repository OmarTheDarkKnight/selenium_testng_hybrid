package com.bat.suites.suiteA;

import com.bat.suites.base.BaseTest;
import com.bat.util.Constants;
import com.bat.util.DataUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Hashtable;

public class TestA extends BaseTest {

    @Test(dataProvider = "getData")
    public void tester(Hashtable<String, String> table) {
        try{
            if(table.get(Constants.RUN_MODE_COL).equals(Constants.DATA_YES)) {
                driverScript.executeKeywords(testName, xlsReader, table);
                System.out.println(table);
            }  // else skip the test
        } catch (Exception exception) {
            System.out.println("Exception: " + exception.getMessage());
        }
    }

    @DataProvider
    public Object[][] getData() {
        return DataUtil.getTestData(testName, xlsReader);
    }
}
