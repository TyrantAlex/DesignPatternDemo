package com.international.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
}
