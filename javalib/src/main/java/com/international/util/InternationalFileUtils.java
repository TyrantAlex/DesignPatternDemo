package com.international.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 国际化文件工具类
 * @author : hongshen
 * @Date: 2018/4/27 0027
 */
public class InternationalFileUtils {
    /**
     * 读取File 到string
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readFileToString(String filePath) throws IOException {
        FileInputStream fin = null;
        BufferedReader reader = null;
        try {
            File fl = new File(filePath);
            fin = new FileInputStream(fl);
            reader = new BufferedReader(new InputStreamReader(fin));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        } finally {
            if (reader != null) reader.close();
            if (fin != null) fin.close();
        }
    }

    /**
     * 读取File 到string
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFileToString(File file) throws IOException {
        FileInputStream fin = null;
        BufferedReader reader = null;
        try {
            if (file.exists()) {
                fin = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(fin));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } else {
                return null;
            }
        } finally {
            if (reader != null) reader.close();
            if (fin != null) fin.close();
        }
    }

    /**
     * 通配符匹配
     * @param pattern    通配符模式
     * @param str    待匹配的字符串
     * @return    匹配成功则返回true，否则返回false
     */
    public static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                //通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                //通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    //表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }

    /**
     * 按行读取
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> readFileByLine(String path) throws IOException {
        List<String> list = new ArrayList<String>();
        FileInputStream fis = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line = br.readLine()) != null) {
            if (line.lastIndexOf("---") < 0) {
                list.add(line);
            }
        }
        br.close();
        isr.close();
        fis.close();
        return list;
    }

    /**
     * 按行写入
     * @param fileName
     * @param clist
     * @throws IOException
     */
    public static void fileWriter(String fileName,List<String> clist) throws IOException{
        //创建一个FileWriter对象
        FileWriter fw = new FileWriter(fileName);
        //遍历clist集合写入到fileName中
        for (String str: clist){
            fw.write(str);
            fw.write("\n");
        }
        //刷新缓冲区
        fw.flush();
        //关闭文件流对象
        fw.close();
    }

    /**
     * 按行写入
     * @param file
     * @param clist
     * @throws IOException
     */
    public static void fileWriter(File file,List<String> clist) throws IOException{
        //创建一个FileWriter对象
        FileWriter fw = new FileWriter(file);
        //遍历clist集合写入到fileName中
        for (String str: clist){
            fw.write(str);
            fw.write("\n");
        }
        //刷新缓冲区
        fw.flush();
        //关闭文件流对象
        fw.close();
    }
}
