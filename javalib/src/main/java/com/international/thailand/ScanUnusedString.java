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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * 扫描默认values中未曾使用的String, 并进行删除
 *  优化: 搜寻其他语言包中的对应项进行删除
 * @author : hongshen
 * @Date: 2018/6/4 0001
 */
public class ScanUnusedString {

    /**
     * 项目位置 65
     */
    public static final String FILE_PATH = "D:/AndroidStudio/AndroidProject/Checkout3";

    /**
     * xml文件中标签key  标识此文件为何种values文件
     * color; style; dimen; declare-styleable; string
     */
    private static final String FILE_NODE_KEY = "string";

    /**
     * 目标文件夹
     */
//    private static final String DIRECT_FOLDER_VALUES = "values";
//    private static final String DIRECT_FOLDER_VALUES_EN = "values-en";
//    private static final String DIRECT_FOLDER_VALUES_TW = "values-zh-rTW";
    private static final String DIRECT_FOLDER_VALUES_TH = "values-th-rTH";

    /**
     * 所有文件的所有String字符串对象
     */
    private List<StringXmlBean> allList = new ArrayList<>();

    private List<String> allJavaDocList = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Start Scan............................................................................");
        ScanUnusedString check = new ScanUnusedString();
        check.checkOnMultiFolder(FILE_PATH, DIRECT_FOLDER_VALUES_TH);
        check.scanJavaDoc(FILE_PATH);
        check.excuteScan();
        System.out.println("End Scan............................................................................");
    }

    /**
     * 扫描项目中未使用的字符串
     */
    private void excuteScan() {
        System.out.println("项目java文件与xml文件扫描完成。一共 " + allJavaDocList.size() + "个文件");
        System.out.println("开始扫描未使用字符串...." + "字符串数目为: " + allList.size() + "java文件数目: " + allJavaDocList.size());
        for (StringXmlBean bean : allList) {
            String key = bean.getKey();
            boolean isUsed = false;
            try {
                //读取所有文件
                for (String path : allJavaDocList) {
                    List<String> strings = InternationalFileUtils.readFileByLine(path);
                    for (String text : strings) {
                        if (text.contains(key)) {
                            isUsed = true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!isUsed) {
//                System.out.println("value = " + bean.getValue());
//                System.out.println("key = " + bean.getKey());
//                System.out.println("path = " + bean.getFileName());
                String path = bean.getFileName();
                String strKey = bean.getKey();
                String strValue = bean.getValue();
                System.out.println("开始删除..." + "key: " + strKey + ", value: " + strValue + ", path: " + path);
                deleteString(new File(path), strKey, strValue);
            }
        }
    }

    /**
     * 扫描项目中的所有java文件和xml文件
     *
     * @param baseDirName
     */
    private void scanJavaDoc(String baseDirName) {
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
                scanJavaDoc(tempFile.getAbsolutePath());
            } else if (tempFile.isFile()) {
                //只扫描values文件夹
                String parent = tempFile.getParent();
                boolean isBuild = parent.contains("\\build\\");
                if (isBuild) {
                    continue;
                }
                //只匹配java文件
                if (parent.contains("\\src\\main\\java") || parent.contains("\\src\\main\\res\\layout")) {
                    if (tempFile.getName().endsWith(".java") || tempFile.getName().endsWith(".xml")) {
                        allJavaDocList.add(tempFile.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * 查找string文件
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
                //build排除
                boolean isBuild = parent.contains("\\build\\");
                if (isBuild) {
                    continue;
                }
                //友盟文件排除
                if ("umeng_socialize_strings.xml".equals(tempFile.getName())) {
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
                    if ("dfire_temporary_translation_cannot_delete".equals(key) || "dfire_temporary_translation_cannot_delete_end".equals(key)) {
                        continue;
                    }
                    // 值
                    String value = node1.getTextContent();

                    StringXmlBean stringXmlBean = new StringXmlBean();
                    String path = file.getPath();
//                    String fileUnuseName = "D:\\AndroidStudio\\AndroidProject\\Checkout4\\";
//                    String subPath = "";
//                    if (path.contains(fileUnuseName)) {
//                        subPath = path.substring(fileUnuseName.length(), path.length());
//                    }
                    stringXmlBean.setFileName(path);
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
     * 删除对应文件中的对应字符
     * @param file
     * @param deleteStrKey
     * @param deleteStrValue
     * @return
     */
    private List<StringXmlBean> deleteString(File file, String deleteStrKey, String deleteStrValue) {
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
                    if (deleteStrKey.equals(key) && deleteStrValue.equals(value)) {
                        System.out.println("正在进行删除..." + "key: " + key + ", value: " + value);
                        node1.getParentNode().removeChild(node1);
                    }
                }
            }
            //write the updated document to file or console
            dt.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(dt);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            System.out.println("删除该字符完毕...");
        } catch (Exception e) {
            System.out.println("删除异常...");
            e.printStackTrace();
        }
        return list;
    }
}
