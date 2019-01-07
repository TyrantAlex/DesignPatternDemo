package com.international.repeat;

import com.international.repeat.vo.StringXmlBean;
import com.international.util.InternationalFileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 检测项目所有String文件中默认values文件中不存在却在其他语言文件中存在的String
 * @author : hongshen
 * @Date: 2018/5/3 0027
 */
public class CheckAndroidNullDefaultString {

//    private static final String singlePath = "D:/translateTemp/replace2018.04.27";

    private static final String multiPath = "/Users/shen/Develop/2dfire/project/checkout_1";

    /**
     * 路径打印时候无用的路径部分 如需全路径则置空
     * D:\AndroidStudio\AndroidProject\Checkout3\
     */
    private String fileUnuseName = "";

    /**
     * xml文件中标签key  标识此文件为何种values文件
     * color; style; dimen; declare-styleable; string
     */
    private static final String FILE_NODE_KEY = "string";

    /**
     * String文件类型目标文件夹
     */
    private static final String DIRECT_FOLDER_VALUES = "values";
    private static final String DIRECT_FOLDER_VALUES_EN = "values-en";
    private static final String DIRECT_FOLDER_VALUES_TW = "values-zh-rTW";
    private static final String DIRECT_FOLDER_VALUES_TH = "values-th-rTH";

    /**
     * 所有文件的对应类型的所有中文String字符串对象
     */
    private List<StringXmlBean> allList = new ArrayList<>();

    public static int num = 0;

    public static void main(String[] args){
        CheckAndroidNullDefaultString checkAndroidNullDefaultString = new CheckAndroidNullDefaultString();
        System.out.println("----------------------------------start-----------------------------------------");
        checkAndroidNullDefaultString.start();
        System.out.println("----------------------------------end-----------------------------------------");
    }

    private void start() {
        //values 目录下所有String字符串对象
        List<StringXmlBean> valuesList = new ArrayList<>();
        checkOnMultiFolder(multiPath, DIRECT_FOLDER_VALUES);
        valuesList.addAll(allList);
        allList.clear();

        //values-en 目录下所有String字符串对象
        List<StringXmlBean> enList = new ArrayList<>();
        checkOnMultiFolder(multiPath, DIRECT_FOLDER_VALUES_EN);
        enList.addAll(allList);
        allList.clear();

        System.out.println("开始打印缺省values的en.....");
        //打印缺省values的en
        printNullDefaultString(valuesList, enList);
        enList.clear();

        //values-zh-rTW 目录下所有String字符串对象
        List<StringXmlBean> twList = new ArrayList<>();
        checkOnMultiFolder(multiPath, DIRECT_FOLDER_VALUES_TW);
        twList.addAll(allList);
        allList.clear();

        System.out.println("开始打印缺省values的tw.....");
        //打印缺省values的tw
        printNullDefaultString(valuesList, twList);
        twList.clear();

        //values-th-rTH 目录下所有String字符串对象
        List<StringXmlBean> thList = new ArrayList<>();
        checkOnMultiFolder(multiPath, DIRECT_FOLDER_VALUES_TH);
        thList.addAll(allList);
        allList.clear();

        System.out.println("开始打印缺省values的th.....");
        //打印缺省values的th
        printNullDefaultString(valuesList, thList);
        thList.clear();
        System.out.println("******** VALUES NUM = " + num);
    }

    /**
     * 打印出缺省默认值的String信息
     * @param valuesList 默认values
     * @param enList 待比较的国际化values
     */
    private void printNullDefaultString(List<StringXmlBean> valuesList, List<StringXmlBean> enList) {
        //set去重
        List<StringXmlBean> allList = new ArrayList<>();
        Set<String> keySet = new HashSet<>();

        for (int i = 0; i < enList.size(); i++) {
            boolean isExsit = false;
            StringXmlBean enBean = enList.get(i);
            for (int j = 0; j < valuesList.size(); j++) {
                StringXmlBean values = valuesList.get(j);
                if (enBean.getKey().equals(values.getKey())) {
                    isExsit = true;
                }
            }
            if (!isExsit) {
                num++;
                if (keySet.add(enBean.getKey())) {
                    allList.add(enBean);
                }
            }
        }

        for (int i = 0; i < allList.size(); i++) {
            StringXmlBean bean = allList.get(i);
//            System.out.println("| " + bean.getFileName()
//                    + "| " + bean.getKey()
////                    + "| " + bean.getValue()
//                    + "| | 未完成|");
            System.out.println("key = " + bean.getKey() + "\n value = " + bean.getValue() + "\n filePath = " + bean.getFileName());
        }
        System.out.println("******** SET NUM = " + allList.size());
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
            return;
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
                boolean isBuild = parent.contains("/build/");
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

                    //formatted
                    String formatted = null;
                    if (node1.getAttributes().getNamedItem("formatted") != null) {
                        formatted = node1.getAttributes().getNamedItem("formatted").getNodeValue();
                    }

                    StringXmlBean stringXmlBean = new StringXmlBean();
                    String path = file.getPath();
                    String subPath = "";
                    if (path.contains(fileUnuseName)) {
                        subPath = path.substring(fileUnuseName.length(), path.length());
                    }
                    stringXmlBean.setFileName(subPath);
                    stringXmlBean.setKey(key);
                    stringXmlBean.setValue(value);
                    if (formatted != null && formatted.length() !=0) {
                        stringXmlBean.setFormatted(formatted);
                    }
                    list.add(stringXmlBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
