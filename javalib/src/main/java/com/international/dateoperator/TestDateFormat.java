package com.international.dateoperator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 测试转换日期格式
 * Created by hongshen on 2017/12/1 0001.
 */

public class TestDateFormat {

    public static void main(String[] args) {
        new TestDateFormat().parseCNDateFormat("yyyy-MM-dd");
        new TestDateFormat().parseCNDateFormat("yyyy.MM.dd");
        new TestDateFormat().parseCNDateFormat("yyyyMMdd");
    }

    private String parseCNDateFormat(String cn) {
        String link = "";
        String year = "";
        String month = "";
        String day = "";
        String temp = "";

        if (cn != null && cn.length() != 0) {
            if (cn.contains("yyyy") || cn.contains("MM") || cn.contains("dd")) {
                if (cn.contains("yyyy") && cn.contains("MM") && cn.indexOf("MM") > cn.indexOf("yyyy")) {
                    int mm = cn.indexOf("MM");
                    int yy = cn.indexOf("yyyy");
                    StringBuilder sb = new StringBuilder(cn);
                    sb.replace(mm, mm + 2, "yyyy");
                    sb.replace(yy, yy + 4, "MM");
                    cn = sb.toString();
                    System.out.println(cn);
                }
                if (cn.contains("MM") && cn.contains("dd") && cn.indexOf("dd") > cn.indexOf("MM")) {
                    int mm = cn.indexOf("MM");
                    int dd = cn.indexOf("dd");
                    StringBuilder sb = new StringBuilder(cn);
                    sb.replace(mm, mm + 2, "dd");
                    sb.replace(dd, dd + 2, "MM");
                    cn = sb.toString();
                    System.out.println(cn);
                }
                if (cn.contains("yyyy") && cn.contains("dd") && cn.indexOf("dd") > cn.indexOf("yyyy")) {
                    int yy = cn.indexOf("yyyy");
                    int dd = cn.indexOf("dd");
                    StringBuilder sb = new StringBuilder(cn);
                    sb.replace(yy, yy + 4, "dd");
                    sb.replace(dd, dd + 2, "yyyy");
                    cn = sb.toString();
                    System.out.println(cn);
                }
            }
        }
        return cn;
    }

//    private void commonReplace(String String early, String later) {
//
//    }

    /**
     * 字符串是否包含中文
     * @param str
     * @return true:包含中文; false:不包含中文
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
