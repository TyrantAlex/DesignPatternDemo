package com.international.repeat;

import com.international.repeat.vo.StringXmlBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 查找项目中String文件中key相同的字符串
 * @author : hongshen
 * @Date: 2018/4/27 0027
 */
public class CheckAndroidRepeatString {

    private static final String path = "D:/translateTemp/replace2018.04.27";

    /**
     * 所有文件的所有String字符串对象
     */
    private List<StringXmlBean> allList = new ArrayList<>();

    public static void main(String[] args){
        CheckAndroidRepeatString checkAndroidRepeatString = new CheckAndroidRepeatString();
        checkAndroidRepeatString.checkFolder();
        checkAndroidRepeatString.printRepeatValue();
    }

    /**
     * 查找文件
     * 读取一个文件夹内的所有文件
     */
    private void checkFolder(){
        File file = new File(path);
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
                if (keyi.equals(keyj)) {
                    if (set.add(beani)) {
                        repeatList.add(beani);
                    }
                    if (set.add(beanj)) {
                        repeatList.add(beanj);
                    }
                }
            }
        }
        for (StringXmlBean bean : repeatList) {
            System.out.println("重复的 keyi = " + bean.getKey() + "\n 值 = " + bean.getValue() + "\n 文件名 = " + bean.getFileName() + "\n");
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
