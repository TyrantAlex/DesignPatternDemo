package com.international.thailand;

import com.international.util.InternationalFileUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author : hongshen
 * @Date: 2018/6/5 0005
 */
public class TestReadLine {

    public static final String KEY_WORD_FILE_PATH = "D:/networkProje/DesignPatternDemo/javalib/src/main/java/com/international/thailand/NeedBeDelete";

    public static void main(String[] args) {

        try {
            List<String> fileStrs = InternationalFileUtils.readFileByLine(KEY_WORD_FILE_PATH);
            System.out.println("haha ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
