package com.bat.util;

import java.util.Hashtable;

public class DataUtil {
    private static int rowStartNumber = 0;
    private static int colStartNumber = 0;
    private static int totalRowNumber = 0;
    private static int totalColNumber = 0;
    private static ExcelFileReader xlsReader = null;

    /**
     * Check if the run mode of the test case is Y or no
     * If the run mode is Y - return true
     * If the run mode is N - return false
     * If the test case is not found, return false
     * */
    public static boolean isRunTetCase(String testName, ExcelFileReader xlsReader) {
        int rows = xlsReader.getRowCount(Constants.TEST_CASES_SHEET);
        for(int rNum = 1; rNum < rows; rNum++) {
            if (xlsReader.getCellData(rNum, Constants.TCID_COL).equals(testName)) {
                return xlsReader.getCellData(rNum, Constants.RUN_MODE_COL).equals(Constants.RUN_MODE_YES);
            }
        }
        return false;
    }

    /**
     * Read data from the sheet with the same name of @param testName
     * Put each data set in a Hash map
     * Put all Hash maps in a two dimensional object array and return it
     * */
    public static Object[][] getTestData(String testName, ExcelFileReader excelFileReader) {
        Object[][] data = null;
        xlsReader = excelFileReader;
        try {
            xlsReader.setWorkSheet(testName);
            setDataStartRowAndCol();
            setTotalRowAndCol();

//            System.out.println("Row start Number - " + rowStartNumber);
//            System.out.println("Col start Number - " + colStartNumber);
//            System.out.println("Total data rows - " + totalRowNumber);
//            System.out.println("Total data cols - " + totalColNumber);
//            System.out.println("First cell data : "+ xlsReader.getCellData(rowStartNumber, colStartNumber));

            int arrayRowSize = totalRowNumber == 0 ? 0 : totalRowNumber - 1;
            data = new Object[arrayRowSize][1];
            Hashtable<String, String> table = null;

            int testDataStartRow = rowStartNumber + 1;
            int indexForObjectArray = 0;
            for(int rNum = testDataStartRow; rNum < rowStartNumber + totalRowNumber; rNum++) {
                table = new Hashtable<String, String>();
                for(int cNum = colStartNumber; cNum < colStartNumber + totalColNumber; cNum++) {
                    //System.out.println(xlsReader.getCellData(rowStartNumber, cNum) + " : " +xlsReader.getCellData(rNum, cNum));
                    table.put(xlsReader.getCellData(rowStartNumber, cNum), xlsReader.getCellData(rNum, cNum));
                }
                data[indexForObjectArray][0] = table;
                indexForObjectArray++;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return data;
    }

    /**
     * Set the data staring row and col number in the sheet
     * */
    private static void setDataStartRowAndCol() {
        // Set the staring of the data row in the xls sheet
        while(xlsReader.getCellData(rowStartNumber, colStartNumber).equals("")) {
//            System.out.println("Row start Number in loop - " + rowStartNumber);
            // Set the staring of the data row in the xls sheet
            while(xlsReader.getCellData(rowStartNumber, colStartNumber).equals("")) {
                if(colStartNumber >= 10) {
                    colStartNumber = 0;
                    break;
                } else {
                    colStartNumber++;
                }

            }
            rowStartNumber++;
        }
        // The rowStartNumber is increased by one at the end of the loop.
        // That's why is decremented 1 here to shift it back to the start of data
        // except for row number = 1
        rowStartNumber = rowStartNumber > 1 ? rowStartNumber - 1 : rowStartNumber;
    }

    /**
     * Set the data staring row and col number in the sheet
     * */
    private static void setTotalRowAndCol() {
        // Set the staring of the data row in the xls sheet
        while(!xlsReader.getCellData(rowStartNumber + totalRowNumber, colStartNumber).equals("")) {
            totalRowNumber++;
        }

        // Set the staring of the data row in the xls sheet
        while(!xlsReader.getCellData(rowStartNumber, colStartNumber + totalColNumber).equals("")) {
            totalColNumber++;
        }
    }
}
