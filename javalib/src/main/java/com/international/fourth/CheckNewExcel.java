package com.international.fourth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 读取Excel
 */
public class CheckNewExcel {

    public static final String EXCEL_FILE_PATH = "/Users/shen/Documents/2dfire/international/11-26THAI.xls";

    public static void main(String[] args) {
        new CheckNewExcel().start();
    }

    private void start() {
        readExcel(EXCEL_FILE_PATH, 0);
    }

    /**
     * 读取excel指定行列
     * @param filePath
     * @param sheetNum
     */
    public void readExcel(String filePath, int sheetNum) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println(filePath + "路径下文件不存在");
            return;
        }
        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            System.out.println("Excel的页签数量 = " + sheet_size);
            Sheet sheet = wb.getSheet(sheetNum);
            // sheet.getRows()返回该页的总行数
            int rows = sheet.getRows();
            System.out.println("当前页签总行数 = " + rows);
            // sheet.getColumns()返回该页的总列数
            int columns = sheet.getColumns();
            System.out.println("当前页签总列数 = " + columns);
            for (int i = 0; i < rows; i++) {
                String cnInfo = sheet.getCell( 0, i).getContents();
                String thaiInfo = sheet.getCell( 2, i).getContents();
                System.out.println(cnInfo + " == " + thaiInfo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    public void readExcel(String filePath) {
        File file = new File(filePath);
        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                // sheet.getRows()返回该页的总行数
                for (int i = 0; i < sheet.getRows(); i++) {
                    // sheet.getColumns()返回该页的总列数
                    for (int j = 0; j < sheet.getColumns(); j++) {
                        String cellinfo = sheet.getCell(j, i).getContents();
                        System.out.println(cellinfo);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
