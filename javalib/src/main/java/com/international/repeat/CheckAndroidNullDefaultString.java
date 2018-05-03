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

    private static final String singlePath = "D:/translateTemp/replace2018.04.27";

    private static final String multiPath = "D:/AndroidStudio/AndroidProject/Checkout4";

    /**
     * values 目录下所有String字符串对象
     */
    private List<StringXmlBean> valuesList = new ArrayList<>();

    /**
     * values-en 目录下所有String字符串对象
     */
    private List<StringXmlBean> enList = new ArrayList<>();

    /**
     * values-zh-rTW 目录下所有String字符串对象
     */
    private List<StringXmlBean> twList = new ArrayList<>();

    public static int num = 0;


    public static void main(String[] args){
        CheckAndroidNullDefaultString checkAndroidRepeatString = new CheckAndroidNullDefaultString();
        System.out.println("----------------------------------start-----------------------------------------");
        checkAndroidRepeatString.checkOnMultiFolder(multiPath);

        checkAndroidRepeatString.printNullDefaultString();
        System.out.println("******** VALUES NUM = " + num);
        System.out.println("----------------------------------end-----------------------------------------");
    }

    /**
     * 打印出缺省默认值的String信息
     */
    private void printNullDefaultString() {
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

        for (int i = 0; i < twList.size(); i++) {
            boolean isExsit = false;
            StringXmlBean twBean = twList.get(i);
            for (int j = 0; j < valuesList.size(); j++) {
                StringXmlBean values = valuesList.get(j);
                if (twBean.getKey().equals(values.getKey())) {
                    isExsit = true;
                }
            }
            if (!isExsit) {
                num++;
                if (keySet.add(twBean.getKey())) {
                    allList.add(twBean);
                }
            }
        }

        for (int i = 0; i < allList.size(); i++) {
            StringXmlBean bean = allList.get(i);
            System.out.println("| " + bean.getFileName()
                    + "| " + bean.getKey()
//                    + "| " + bean.getValue()
                    + "| | 未完成|");
        }
        System.out.println("******** SET NUM = " + allList.size());

//        Iterator<StringXmlBean> iterator = set.iterator();
//        while (iterator.hasNext()){
//            StringXmlBean bean = iterator.next();
//            System.out.println("| " + bean.getFileName()
//                    + "| " + bean.getKey()
//                    + "| " + bean.getValue()
//                    + "| | |");
//            System.out.println("");
//        }
//        System.out.println("******** SET NUM = " + set.size());
    }

    /**
     * 查找文件
     * 读取一个路径下多个文件夹的指定文件
     */
    private void checkOnMultiFolder(String baseDirName){
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
                checkOnMultiFolder(tempFile.getAbsolutePath());
            }else if(tempFile.isFile()){
                //只扫描values文件夹
                String parent = tempFile.getParent();
                boolean isBuild = parent.contains("\\build\\");
                if (isBuild) {
                    continue;
                }
                boolean isValues = parent.endsWith("values");
                boolean isEn = parent.endsWith("values-en");
                boolean isTW = parent.endsWith("values-zh-rTW");
                if (isValues) {
                    tempName = tempFile.getName();
                    if (tempName.contains("tring")) {
//                        String path = tempFile.getPath();
//                        String absolutePath = tempFile.getAbsolutePath();
//                        System.out.println("******** VALUES PATH = " + path);
                        // 匹配成功，将文件名添加到结果集
                        List<StringXmlBean> beanList = splitStringXml2Map(tempFile);
                        if (beanList != null) {
                            valuesList.addAll(beanList);
                        }
                    }
                }
                if (isEn) {
                    tempName = tempFile.getName();
                    if (tempName.contains("tring")) {
//                        valuesFolad++;
//                        String path = tempFile.getPath();
//                        String absolutePath = tempFile.getAbsolutePath();
//                        System.out.println("----------- EN PATH = " + path);
                        // 匹配成功，将文件名添加到结果集
                        List<StringXmlBean> beanList = splitStringXml2Map(tempFile);
                        if (beanList != null) {
                            enList.addAll(beanList);
                        }
                    }
                }
                if (isTW) {
                    tempName = tempFile.getName();
                    if (tempName.contains("tring")) {
                        // 匹配成功，将文件名添加到结果集
                        List<StringXmlBean> beanList = splitStringXml2Map(tempFile);
                        if (beanList != null) {
                            twList.addAll(beanList);
                        }
                    }
                }
            }
        }
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

}
