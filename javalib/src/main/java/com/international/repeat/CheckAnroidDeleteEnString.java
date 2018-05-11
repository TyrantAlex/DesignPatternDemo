package com.international.repeat;

import com.international.repeat.vo.StringXmlBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 检测模块化后项目有无英文字符串被误删的情况
 * @author : hongshen
 * @Date: 2018/5/10 0010
 */
public class CheckAnroidDeleteEnString {

    public static String beforeFileUnuseName = "D:\\AndroidStudio\\AndroidProject\\Checkout3\\";

    public static String afterFileUnuseName = "D:\\AndroidStudio\\AndroidProject\\Checkout4\\";

    /**
     * 模块化之前的项目位置 61
     */
    public static final String BEFORE_FILE_PATH = "D:/AndroidStudio/AndroidProject/Checkout3";

    /**
     * 模块化之后项目位置 62
     */
    public static final String AFTER_FILE_PATH = "D:/AndroidStudio/AndroidProject/Checkout4";

    /**
     * xml文件中标签key  标识此文件为何种values文件
     * color; style; dimen; declare-styleable; string
     */
    private static final String FILE_NODE_KEY = "string";


//    List<StringXmlBean> allList = new ArrayList<>();


    public static void main(String[] args){
        /**
         * 目标文件夹名称
         *  boolean isValues = parent.endsWith("values");
         *  boolean isEn = parent.endsWith("values-en");
         *  boolean isTW = parent.endsWith("values-zh-rTW");
         */
        CheckAnroidDeleteEnString checkAnroidDeleteEnString = new CheckAnroidDeleteEnString();
        //模块化之前的项目英文字符list
        List<StringXmlBean> beforeEnList = new ArrayList<>();
        checkAnroidDeleteEnString.checkOnMultiFolder(BEFORE_FILE_PATH, beforeFileUnuseName, "values-en", beforeEnList);

        //模块化之后的项目中文字符list
        List<StringXmlBean> afterZhList =  new ArrayList<>();
        checkAnroidDeleteEnString.checkOnMultiFolder(AFTER_FILE_PATH, afterFileUnuseName, "values", afterZhList);

        //模块化之后的项目英文字符list
        List<StringXmlBean> afterEnList =  new ArrayList<>();
        checkAnroidDeleteEnString.checkOnMultiFolder(AFTER_FILE_PATH, afterFileUnuseName, "values-en", afterEnList);

        List<StringXmlBean> compareAfterZhList = checkAnroidDeleteEnString.compare(afterZhList, afterEnList);

        List<StringXmlBean> compareBeforeEnList = checkAnroidDeleteEnString.compare2(beforeEnList, compareAfterZhList);

        checkAnroidDeleteEnString.printLn(compareBeforeEnList);
    }

    private void printLn(List<StringXmlBean> compareBeforeEnList) {
        System.out.println("Start Output...  list size = " + compareBeforeEnList.size());
        for (int i = 0; i < compareBeforeEnList.size(); i++) {
            StringXmlBean bean = compareBeforeEnList.get(i);
//            System.out.println("| " + bean.getFileName()
//                    + "| " + bean.getKey()
//                    + "| " + bean.getValue()
//                    + "| | 未完成|");

//            System.out.println("FilePath = " + bean.getFileName());
//            System.out.println("String Key = " + bean.getKey());
//            System.out.println("String Value = " + bean.getValue());

            //<string name="shop_right">门店权限</string>
            System.out.println("FilePath = " + bean.getFileName());
            System.out.println("<String name = \"" + bean.getKey() + "\">" + bean.getValue() + "</string>");
            System.out.println("");
        }
        System.out.println("End Output...  list size = " + compareBeforeEnList.size());
    }

    private List<StringXmlBean> compare2(List<StringXmlBean> beforeEnList, List<StringXmlBean> compareAfterZhList) {
        List<StringXmlBean> allList = new ArrayList<>();
        for (StringXmlBean beforeEnBean : beforeEnList) {
            for (StringXmlBean afterZhBean : compareAfterZhList) {
                if (beforeEnBean.getKey().equals(afterZhBean.getKey())) {
                    allList.add(beforeEnBean);
                }
            }
        }
        return allList;
    }


    /**
     * 找zhlist中与enlist不相同元素
     * @param afterZhList
     * @param afterEnList
     * @return
     */
    private List<StringXmlBean> compare(List<StringXmlBean> afterZhList, List<StringXmlBean> afterEnList) {
        List<StringXmlBean> allList = new ArrayList<>();
        for (StringXmlBean afterZhBean : afterZhList) {
            boolean isExist = false;
            for (StringXmlBean afterEnBean : afterEnList) {
                if (afterZhBean.getKey().equals(afterEnBean.getKey())) {
                    isExist = true;
                }
            }
            if (!isExist) {
                allList.add(afterZhBean);
            }
        }
        return allList;
    }

    /**
     * 查找文件
     * 读取一个路径下多个文件夹的指定文件
     * return 当前文件夹下所有符合的数据list集合
     */
    private void checkOnMultiFolder(String baseDirName, String fileUnuseName, String directFoldString, List<StringXmlBean> list){
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
            if(tempFile.isDirectory()){
                checkOnMultiFolder(tempFile.getAbsolutePath(), fileUnuseName, directFoldString, list);
            }else if(tempFile.isFile()){
                //只扫描values文件夹
                String parent = tempFile.getParent();
                boolean isBuild = parent.contains("\\build\\");
                if (isBuild) {
                    continue;
                }
                boolean isValues = parent.endsWith(directFoldString);
                if (isValues) {
                    boolean stringFile = isNodeKeyFile(tempFile);
                    if (stringFile){
                        List<StringXmlBean> beanList = splitStringXml2Map(tempFile, fileUnuseName);
                        if (beanList != null) {
                            list.addAll(beanList);
                        }
                    }
                }
            }
        }
    }

    /**
     * 标识对应文件身份
     * @param file
     * @return
     */
    private boolean isNodeKeyFile(File file){
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
    private List<StringXmlBean> splitStringXml2Map(File file, String fileUnuseName) {
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
                if (FILE_NODE_KEY.equals(node1.getNodeName())) {
                    // 键
                    String key = node1.getAttributes().getNamedItem("name")
                            .getNodeValue();
                    // 值
                    String value = node1.getTextContent();

                    StringXmlBean stringXmlBean = new StringXmlBean();
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
}
