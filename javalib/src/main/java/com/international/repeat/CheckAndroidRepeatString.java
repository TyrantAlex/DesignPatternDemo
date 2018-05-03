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
 * 检测项目所有String文件中key相同的字符串
 * @author : hongshen
 * @Date: 2018/4/27 0027
 */
public class CheckAndroidRepeatString {

    private static final String singlePath = "D:/translateTemp/replace2018.04.27";

    private static final String multiPath = "D:/AndroidStudio/AndroidProject/Checkout4";

    /**
     * 所有文件的所有String字符串对象
     */
    private List<StringXmlBean> allList = new ArrayList<>();

    public static void main(String[] args){
        CheckAndroidRepeatString checkAndroidRepeatString = new CheckAndroidRepeatString();
        checkAndroidRepeatString.checkOnMultiFolder(multiPath, "strings.xml");
//        checkAndroidRepeatString.checkOnSingleFolder();
        checkAndroidRepeatString.printRepeatValue();
    }

    /**
     * 查找文件
     * 读取一个文件夹内的所有文件
     */
    private void checkOnSingleFolder(){
        File file = new File(singlePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f: files) {
                    List<StringXmlBean> beanList = splitStringXml2Map(f);
                    if (beanList != null) {
                        allList.addAll(beanList);
                    }
                }
            }
        }
    }

    /**
     * 查找文件
     * 读取一个路径下多个文件夹的指定文件
     */
    private void checkOnMultiFolder(String baseDirName, String targetFileName){
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
                checkOnMultiFolder(tempFile.getAbsolutePath(), targetFileName);
            }else if(tempFile.isFile()){
                //只扫描values文件夹
                String parent = tempFile.getParent();
                boolean isValues = parent.endsWith("values");
                if (isValues) {
                    tempName = tempFile.getName();
                    if(InternationalFileUtils.wildcardMatch(targetFileName, tempName)){
                        // 匹配成功，将文件名添加到结果集
//                    fileList.add(tempFile.getAbsoluteFile());
//                        System.out.println("fileName = " + tempFile.getName() + "\nAbsolutePath = " + tempFile.getAbsolutePath() + "\n");
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
     * 打印出重复项
     */
    private void printRepeatValue(){
        Set<StringXmlBean> set = new HashSet<>();
        List<StringXmlBean> repeatList=new ArrayList<>();
        for (int i = 0; i < allList.size()-1; i++) {
            for (int j = i + 1; j < allList.size(); j++) {
                StringXmlBean beani = allList.get(i);
                StringXmlBean beanj = allList.get(j);
                String keyi = beani.getKey();
                String keyj = beanj.getKey();
                String valuei = beani.getValue();
                String valuej = beanj.getValue();
                if (keyi.equals(keyj) && !valuei.equals(valuej)) {
                    if (set.add(beani)) {
                        repeatList.add(beani);
                    }
                    if (set.add(beanj)) {
                        repeatList.add(beanj);
                    }
                }
            }
        }
//        for (StringXmlBean bean : repeatList) {
//            System.out.println("key = " + bean.getKey() + "\nvalue = " + bean.getValue() + "\nfilePath = " + bean.getFileName() + "\n");
//        }
        printRepeatKey(repeatList);
    }

    /**
     * 打印出有多少个重复的key
     */
    private void printRepeatKey(List<StringXmlBean> repeatList){
        Set<String> keySet = new HashSet<>();
        for (int i = 0; i < repeatList.size()-1; i++) {
            String keyi = repeatList.get(i).getKey();
            keySet.add(keyi);
        }
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            System.out.println("key = " + key);
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
                    stringXmlBean.setFileName(file.getPath());
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
