package com.bat.driver;

import com.bat.keywords.ApplicationKeyword;
import com.bat.util.Constants;
import com.bat.util.ExcelFileReader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;

public class DriverScript {
    private Properties prop;
    private ApplicationKeyword applicationKeyword;

    public Properties getProp() {
        return prop;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }

    public void executeKeywords(String testName, ExcelFileReader xlsReader, Hashtable<String, String> data) {
        Method method;
        applicationKeyword = new ApplicationKeyword();
        applicationKeyword.setProp(this.getProp());

        int rowNumber = xlsReader.getRowCount(Constants.KEYWORDS_SHEET);
        for(int rNum = 1; rNum < rowNumber; rNum++) {
            if(xlsReader.getCellData(rNum, Constants.TCID_COL).equals(testName)) {
                // read the required data from the xls file
                String keyword = xlsReader.getCellData(rNum, Constants.KEYWORD_COL);
                String objectKey = xlsReader.getCellData(rNum, Constants.OBJECT_KEY_COL);
                String dataKey = xlsReader.getCellData(rNum, Constants.DATA_KEY_COL);
                String dataValue = data.get(dataKey);
//                System.out.println(keyword + " ---- " + prop.getProperty(objectKey) + " ---- " + dataKey + " : " + dataValue);

                // set the object key and data value in the application key word class
                applicationKeyword.setObjectKey(objectKey);
                applicationKeyword.setData(dataValue);

                // call the keyword methods dynamically
                try {
                    method = applicationKeyword.getClass().getMethod(keyword);
                    method.invoke(applicationKeyword);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void quit() {
        if(applicationKeyword != null) applicationKeyword.closeBrowser();
    }
}
