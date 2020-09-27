package com.bat.util;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.Calendar;

public class ExcelFileReader {
    private String filePath;
    private FileInputStream fileInputStream;
    private XSSFWorkbook workbook;
    private XSSFSheet workSheet;
    private XSSFRow row;
    private XSSFCell cell;


    public ExcelFileReader(String filePath) {
        this.filePath = filePath;
        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fileInputStream);
            workSheet = workbook.getSheetAt(0);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ExcelFileReader(String filePath, String workSheetName) {
        this.filePath = filePath;
        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fileInputStream);
            setWorkSheet(workSheetName);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWorkSheet(String sheetName) throws Exception {
        if(workbook != null) {
            int sheetIndex = workbook.getSheetIndex(sheetName);
            workSheet = sheetIndex == -1 ? workbook.getSheetAt(0) : workbook.getSheetAt(sheetIndex);
        } else throw new Exception("File not set yet.");
    }

    public void setRow(int rowNum) {
        if(rowNum < 0)
            rowNum = 0;
        row = getWorkSheet().getRow(rowNum);
    }

    public XSSFSheet getWorkSheet() {
        return this.workSheet;
    }

    public int getRowCount() {
        return getWorkSheet().getLastRowNum()+1; // getLastRowNum returns the ZERO BASED ROW NUMBER
    }

    public int getRowCount(String sheetName) {
        try {
            setWorkSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getRowCount();
    }

    public int getCellCount() {
        setRow(0);
        return row == null ? 0 : row.getLastCellNum(); // getLastCellNum() returns the last cell num PLUS ONE
    }

    public int getCellCount(String sheetName) {
        try {
            setWorkSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getCellCount();
    }

    /**
     * returns cell data based on row and column index
     * */
    public String getCellData(int rowNum, int cellNum) {
        String cellData = "";
        try {
            if (rowNum < 0 || cellNum < 0)
                return cellData;

            setRow(rowNum);
            if (row == null)
                return cellData;

            cell = row.getCell(cellNum);
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                return cellData;
            } else if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            } else if(cell.getCellType()==CellType.NUMERIC || cell.getCellType()==CellType.FORMULA ){
                cellData  = String.valueOf(cell.getNumericCellValue());
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // format in form of M/D/YY
                    double d = cell.getNumericCellValue();

                    Calendar cal =Calendar.getInstance();
                    cal.setTime(HSSFDateUtil.getJavaDate(d));
                    cellData =
                            (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
                    cellData = cal.get(Calendar.DAY_OF_MONTH) + "/" +
                            cal.get(Calendar.MONTH)+1 + "/" +
                            cellData;
                }
            } else
                return String.valueOf(cell.getBooleanCellValue());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("row "+rowNum+" or column "+ cellNum +" does not exist in xls");
        }
        return cellData;
    }

    /**
     * returns cell data based on row index and column name
     * */
    public String getCellData(int rowNum, String cellName) {
        String cellData = "";
        try {
            if (rowNum <= 0)
                return cellData;

            int cellIndex = -1;
            for(int i = 0; i < getCellCount(); i++)
                if(row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(cellName))
                    cellIndex = i;

            if(cellIndex == -1)
                return cellData;
            else
                cellData = getCellData(rowNum, cellIndex);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("row "+rowNum+" or column "+ cellName +" does not exist in xls");
        }
        return cellData;
    }
}
