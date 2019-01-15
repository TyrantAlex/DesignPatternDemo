package com.international.fourth;

import com.international.util.InternationalFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 通过一个总的翻译文档去匹配现有泰文中的英文
 */
public class CheckAllStrToThaiStr {

    /**
     * 泰文中的英文 的文件路径
     */
    public static final String THAI_EN_PATH = "/Users/shen/Documents/2dfire/international/2019-01-03.txt";

    /**
     * 番茄给的总的翻译 文件路径
     */
    public static final String ALL_THRANSLATE_PATH = "/Users/shen/Documents/2dfire/international/火掌柜.xls";

    /**
     * key cn value thai
     */
    private Map<String, String> cnThaiMap = new HashMap<>();

    public static void main(String[] args) {
        CheckAllStrToThaiStr check = new CheckAllStrToThaiStr();
        System.out.println("------------------------------------START------------------------------");
        check.start();
        System.out.println("------------------------------------END---------------------------------");
    }

    private void start() {
        //遍历翻译回来的excel文档 读取对应翻译到map中 通过修改column值改变读取的内容
        try {
            readExcel(ALL_THRANSLATE_PATH, 0, 2);

            List<String> thaiEnList = InternationalFileUtils.readFileByLine(THAI_EN_PATH);
            System.out.println("泰文中的英文有: " + thaiEnList.size());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取excel指定行列
     * @param filePath
     * @param sheetNum
     */
    public void readExcel(String filePath, int sheetNum, int columnNum) {
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
                String thaiInfo = sheet.getCell( columnNum, i).getContents();
                //放入map
                cnThaiMap.put(cnInfo, thaiInfo);
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
}
