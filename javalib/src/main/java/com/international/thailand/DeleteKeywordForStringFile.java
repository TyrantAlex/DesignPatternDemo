package com.international.thailand;

import com.international.util.InternationalFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 为一份文件去除包含关键字的字符串
 * @author : hongshen
 * @Date: 2018/6/5 0005
 */
public class DeleteKeywordForStringFile {

    /**
     * 文件地址
     */
    public static final String path = "D:/networkProje/DesignPatternDemo/javalib/src/main/java/com/international/thailand/android与iOS不一致中文字符串0604";

    public static final String KEY_WORD_FILE_PATH = "D:/networkProje/DesignPatternDemo/javalib/src/main/java/com/international/thailand/NeedBeDelete";

    public static final String AFTER_DELETE_FILE_PATH = "D:/networkProje/DesignPatternDemo/javalib/src/main/java/com/international/thailand/AfterDelete";

    private List<String> afterDeleteList = new ArrayList<>();

//    private String[] keywords = {""};

    public static void main(String[] args) {
        DeleteKeywordForStringFile deleteKeywordForStringFile = new DeleteKeywordForStringFile();
        deleteKeywordForStringFile.execute();
    }

    private void execute() {
        try {
            List<String> fileStrs = InternationalFileUtils.readFileByLine(path);
            List<String> keywords = InternationalFileUtils.readFileByLine(KEY_WORD_FILE_PATH);
            for (String fileStr : fileStrs) {
                boolean isExit = false;
                for (String keyword : keywords) {
                    if (fileStr.contains(keyword)) {
                        isExit = true;
                    }
                }
                if (!isExit) {
                    afterDeleteList.add(fileStr);
                }
            }
            System.out.println("删除关键词后数组长度为: " + afterDeleteList.size());
            //写入文件
            File file = new File(AFTER_DELETE_FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            InternationalFileUtils.fileWriter(file, afterDeleteList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
