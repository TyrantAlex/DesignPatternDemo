package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 批量替换git仓库remote地址
 */
public class UpdateGitRemoteUrl {

    /**
     * 项目对应位置
     */
    public static final String PROJECT_PATH = "/Users/shen/Develop/2dfire/project/checkout_1";

    /**
     * 待查找的文件夹名称
     */
    public static final String CHECKED_DIRECTORY_NAME = ".git";

    /**
     * 待查找的文件名称
     */
    public static final String CHECKED_FILE_NAME = "config";

    /**
     * 待匹配的字符
     */
    public static final String SPECAIL_STR = "url = git@git.2dfire-inc.com";

    /**
     * 待替换的字符
     */
    public static final String REPLACE_STR = "url = git@git.2dfire.net";

    public static void main(String[] args) {
        UpdateGitRemoteUrl remoteUrl = new UpdateGitRemoteUrl();
        remoteUrl.start();
    }

    private void start() {
        //当前项目文件夹目录
        File file = new File(PROJECT_PATH);
        //列出当前目录的所有文件
        File[] files = file.listFiles();
        for (File fileSon : files) {
            String fileSonName = fileSon.getName();
//            System.out.println(fileSonName);
            if (fileSon.isDirectory()) {
                //进入子一级目录找到.git文件夹
                File[] fileSons = fileSon.listFiles();
                boolean isCheckGitDirect = false;
                for (File fileGrandSon : fileSons) {
                    String fileGrandSonName = fileGrandSon.getName();
                    if (CHECKED_DIRECTORY_NAME.equals(fileGrandSonName)) {
                        isCheckGitDirect = true;
                        //列出.git文件夹下所有文件
                        File[] fileGits = fileGrandSon.listFiles();
                        boolean isCheckGitFile = false;
                        for (File fileGit : fileGits) {
                            String fileGitName = fileGit.getName();
                            if (CHECKED_FILE_NAME.equals(fileGitName)) {
                                isCheckGitFile = true;
                                //开始读取文件内容并进行匹配
                                try {
                                    updateFile(fileGit);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (!isCheckGitFile) {
                            System.out.println(fileSonName + "该文件夹下的git文件夹下无config文件");
                        }
                    }
                }
                if (!isCheckGitDirect) {
                    System.out.println(fileSonName + "该文件夹下无.git文件");
                }
            }
        }
    }

    private void updateFile(File file) throws IOException {
        String read = read(file);
        if (read == null || read.length() == 0) {
            System.out.println(file.getAbsolutePath() + ": 读取config文件失败...");
            return;
        }
        if (read.contains(SPECAIL_STR)) {
            String replace = read.replace(SPECAIL_STR, REPLACE_STR);
            write(file.getPath(), replace);
        } else {
            System.out.println(file.getAbsolutePath() + ": 该路径的config文件不包含specail str");
        }
    }

    private String read(File file)throws IOException {
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
     * 将内容回写到文件中
     *
     * @param filePath
     * @param content
     */
    public void write(String filePath, String content) {
        BufferedWriter bw = null;

        try {
            // 根据文件路径创建缓冲输出流
            bw = new BufferedWriter(new FileWriter(filePath));
            // 将内容写入文件中
            bw.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
    }
}
