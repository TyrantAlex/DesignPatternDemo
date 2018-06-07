package com.international.thailand;

import com.international.repeat.vo.StringXmlBean;
import com.international.util.InternationalDocUtils;
import com.international.util.InternationalFileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 检查android中文与iOS中文不一致的
 *
 * @author : hongshen
 * @Date: 2018/6/1 0001
 */
public class CheckAndroidChineseForiOsChinese {

    /**
     * iOS文件路径
     */
    public static final String iOSPath = "D:/translateTemp/replace2018.05.28/ios/iosString0531";

    /**
     * 项目位置 65
     */
    public static final String FILE_PATH = "D:/AndroidStudio/AndroidProject/Checkout3";

    /**
     * 最终写入文件路径
     */
    public static final String AFTER_COMPARE_FILE_PATH = "D:/networkProje/DesignPatternDemo/javalib/src/main/java/com/international/thailand/CompareStringFile";

    /**
     * 需要过滤的关键字
     */
    public static final String KEY_WORD_FILE_PATH = "D:/networkProje/DesignPatternDemo/javalib/src/main/java/com/international/thailand/NeedBeDelete";

    /**
     * xml文件中标签key  标识此文件为何种values文件
     * color; style; dimen; declare-styleable; string
     */
    private static final String FILE_NODE_KEY = "string";

    /**
     * 目标文件夹
     */
    private static final String DIRECT_FOLDER_VALUES = "values";
    private static final String DIRECT_FOLDER_VALUES_EN = "values-en";
    private static final String DIRECT_FOLDER_VALUES_TW = "values-zh-rTW";

    /**
     * 所有文件的所有String字符串对象
     */
    private List<StringXmlBean> allList = new ArrayList<>();



    public static void main(String[] args) {
        System.out.println("Start match............................................................................");
        CheckAndroidChineseForiOsChinese check = new CheckAndroidChineseForiOsChinese();
        check.checkOnMultiFolder(FILE_PATH, DIRECT_FOLDER_VALUES);
        check.catchInconsistentChineseStr();
        System.out.println("End match............................................................................");
    }

    /**
     * 去重并输出
     */
    private void printUnrepeatStr(List<String> resultList) {
        Set<String> set = new LinkedHashSet<>();
        set.addAll(resultList);
        for (String str : set) {
            System.out.println(str);
        }
    }

    /**
     * 写入文件
     * @param resultList
     * @throws IOException
     */
    private void writeToFile(List<String> resultList){
        //去重
        System.out.println("去重前 数组长度为: " + resultList.size());
        //去重后数组
        List<String> listClear = new ArrayList<>();
        Set<String> set = new LinkedHashSet<>();
        set.addAll(resultList);
        for (String str : set) {
            listClear.add(str);
        }
        System.out.println("去重后 数组长度为: " + listClear.size());
        //过滤关键字
        System.out.println("删除关键词前数组长度为: " + listClear.size());
        List<String>  list = interceptionFilter(listClear);
        System.out.println("删除关键词后数组长度为: " + list.size());
        try {
            InternationalFileUtils.fileWriter(AFTER_COMPARE_FILE_PATH, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> interceptionFilter(List<String> resultList){
        List<String> afterDeleteList = new ArrayList<>();
        try {
            List<String> keywords = InternationalFileUtils.readFileByLine(KEY_WORD_FILE_PATH);
            for (String fileStr : resultList) {
                boolean isExit = false;
                for (String keyword : keywords) {
                    if (fileStr.contains(keyword)) {
                        isExit = true;
                    }
                }
                if (!isExit) {
                    //每一句话加上标识
                    afterDeleteList.add("<T>"+fileStr+"</T>");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return afterDeleteList;
    }


    /**
     * 匹配不一致的中文字符
     */
    private void catchInconsistentChineseStr() {
        System.out.println("android string文件字符串个数为: " + allList.size());
        /**
         * 最终输出的String list
         */
        List<StringXmlBean> resultList = new ArrayList<>();

        List<String> resultStringList = new ArrayList<>();

        //取iOS字符到list中
        List<String> iOSStrList = splitFileStrByLine2List(iOSPath);

        //打印不一致的字符
        for (StringXmlBean bean : allList) {
            boolean isMatch = false;
            for (String iOSStr : iOSStrList) {
                //去标点再比较
                String originiOs = InternationalDocUtils.myFormatUtil(iOSStr);
                String originAndroid = InternationalDocUtils.myFormatUtil(bean.getValue());
                if (originiOs.equals(originAndroid)) {
                    isMatch = true;
                }
            }
            //未匹配 输出
            if (!isMatch) {
//                resultList.add(bean);
                resultStringList.add(bean.getValue());
//                System.out.println(bean.getValue());
            }
        }
        //最终去重并打印
//        printUnrepeatStr(resultStringList);
        //写入文件
        writeToFile(resultStringList);
    }


    /**
     * 查找文件
     * 读取一个路径下多个文件夹的指定文件
     * return 当前文件夹下所有符合的数据list集合
     */
    private void checkOnMultiFolder(String baseDirName, String directFoldString) {
        File baseDir = new File(baseDirName);       // 创建一个File对象
        if (!baseDir.exists() || !baseDir.isDirectory()) {  // 判断目录是否存在
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        }
        String tempName = null;
        //判断目录是否存在
        File tempFile;
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            if (tempFile.isDirectory()) {
                checkOnMultiFolder(tempFile.getAbsolutePath(), directFoldString);
            } else if (tempFile.isFile()) {
                //只扫描values文件夹
                String parent = tempFile.getParent();
                boolean isBuild = parent.contains("\\build\\");
                if (isBuild) {
                    continue;
                }
                boolean isValues = parent.endsWith(directFoldString);
                if (isValues) {
                    boolean stringFile = isNodeKeyFile(tempFile);
                    if (stringFile) {
                        List<StringXmlBean> beanList = splitStringXml2Map(tempFile);
                        if (beanList != null) {
                            allList.addAll(beanList);
                        }
                    }
                }
            }
        }
    }

    /**
     * 标识对应文件身份
     *
     * @param file
     * @return
     */
    private boolean isNodeKeyFile(File file) {
        boolean isNodeKeyFile = false;
        if (!file.exists()) {
            return false;
        }
        Element element;
        DocumentBuilder db;
        DocumentBuilderFactory dbf;
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            Document dt = db.parse(file);
            element = dt.getDocumentElement();
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node1 = childNodes.item(i);
                if (FILE_NODE_KEY.equals(node1.getNodeName())) {
                    isNodeKeyFile = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isNodeKeyFile;
    }

    /**
     * android中文string.xml文件拆分
     *
     * @return List<StringXmlBean> xml文件转list集合
     */
    private List<StringXmlBean> splitStringXml2Map(File file) {
        if (!file.exists()) {
            return null;
        }
        List<StringXmlBean> list = new ArrayList<>();
        Element element;
        DocumentBuilder db;
        DocumentBuilderFactory dbf;
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            Document dt = db.parse(file);
            element = dt.getDocumentElement();
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node1 = childNodes.item(i);
                if ("string".equals(node1.getNodeName())) {
                    // 键
                    String key = node1.getAttributes().getNamedItem("name")
                            .getNodeValue();
                    // 值
                    String value = node1.getTextContent();

                    StringXmlBean stringXmlBean = new StringXmlBean();
                    String fileUnuseName = "D:\\AndroidStudio\\AndroidProject\\Checkout4\\";
                    String path = file.getPath();
                    String subPath = "";
                    if (path.contains(fileUnuseName)) {
                        subPath = path.substring(fileUnuseName.length(), path.length());
                    }
                    stringXmlBean.setFileName(subPath);
                    stringXmlBean.setKey(key);
                    stringXmlBean.setValue(value);
                    list.add(stringXmlBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 拆分iOS文档到List<String>集合中
     * 文档内容以行的形式读取  “中文” = “英文”
     *
     * @param filePath iOS文档路径
     * @return List<String>
     */
    public static List<String> splitFileStrByLine2List(String filePath) {
        List<String> iOSList = new ArrayList<>();
        File file = new File(filePath);
        String result = null;
        FileReader fr = null;
        BufferedReader br = null;
        String line = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            line = br.readLine();
            while (line != null) {
//				System.out.println("已经读到 line = " + line);
                if (line.contains("=")) {
                    String[] split = line.split("=");
                    if (split.length != 2) {
                        System.out.println("ios文件中的异常line = " + line);
                        line = br.readLine();
                        continue;
                    }
                    split[0] = split[0].trim();
                    split[1] = split[1].trim();
                    if (!split[0].startsWith("\"")) {
                        System.out.println("ios文件中的异常line 中文部分不是已 “ 开头 = " + line);
                    }
                    if (!split[1].startsWith("\"")) {
                        System.out.println("ios文件中的异常line 英文部分不是已 “ 开头 = " + line);
                    }
                    split[0] = split[0].substring(1, split[0].lastIndexOf("\""));
                    split[1] = split[1].substring(1, split[1].lastIndexOf("\""));

                    //iOS文件key value 都是中文翻译
                    iOSList.add(split[1]);
                } else {
                    if (line != null && !"".equals(line) && line.length() != 0) {
                        System.out.println("直接打印不包含=的line = " + line);
                    }
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return iOSList;
    }
}
