package com.international.fileoperator;

import com.international.util.InternationalDocUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * 替换脚本 精英版
 * 根据android中文string.xml文件和英文string_en.xml文件, 通过匹配iOS国际化文档, 将中文string.xml文件中未翻译的添加到 string_en文件末尾。
 *
 * @author : hongshen
 * @Date: 2018/1/15 0015
 */

public class ReplaceAndroidCnCompareEnByiOSFile {

    //android中文string.xml文件路径
    private static final String ANDROID_CHINESE_STRING_NAME = "D:\\translateTemp/replace2018.01.18/strings.xml";
    //android英文string.xml文件路径
    private static final String ANDROID_ENGLISH_STRING_NAME = "D:\\translateTemp/replace2018.01.18/strings_en.xml";
    //ios国际化文件路径
    private static final String IOS_STRING_NAME = "D:\\translateTemp/replace2018.01.18/ios.txt";

    public static void main(String[] args) {
        ConcurrentHashMap<String, String> androidCnStrMap = InternationalDocUtils.splitStringXml2Map(ANDROID_CHINESE_STRING_NAME);
        ConcurrentHashMap<String, String> androidEnStrMap = InternationalDocUtils.splitStringXml2Map(ANDROID_ENGLISH_STRING_NAME);
        Map<String, String> iOSfileStrByLine = InternationalDocUtils.splitFileStrByLine2Map(IOS_STRING_NAME);
        ReplaceAndroidCnCompareEnByiOSFile.replaceIOS2Android(iOSfileStrByLine, androidCnStrMap, androidEnStrMap);
    }

    /**
     * map集合替换
     *
     * @param iosMap
     * @param androidCnMap
     */
    public static void replaceIOS2Android(Map<String, String> iosMap, ConcurrentHashMap<String, String> androidCnMap, ConcurrentHashMap<String, String> androidEnMap) {
        //这个map存放匹配不上的 中文string
        Map<String, String> tempMap = new HashMap<>();
        //遍历中文英文string文件去除已翻译的内容
        for (String key : androidCnMap.keySet()) {
            for (String keyEn : androidEnMap.keySet()) {
                if (key.equals(keyEn)) {
                    androidCnMap.remove(key);
                }
            }
        }
        //遍历剩下的中文string文件内容去匹配iOS中的文件内容
        for (String keyCn : androidCnMap.keySet()) {
            boolean isMatch = false;
            for (String keyiOS : iosMap.keySet()) {
                //执行去除标点再匹配
                String myFormatUtilKey = InternationalDocUtils.myFormatUtil(keyiOS);
                //strings.xml文件value值去标点
                String myFormatUtilValue = InternationalDocUtils.myFormatUtil(androidCnMap.get(keyCn));
                //ios的中文匹配了android的中文
                if (myFormatUtilKey.equals(myFormatUtilValue)) {
                    isMatch = true;
                    //执行ios英文替换android中文的操作
                    androidCnMap.put(keyCn, iosMap.get(keyiOS));
                    break;
                }
            }
            if (!isMatch) {
                //此处可以做操作 如: 取出未能匹配的中文。
                tempMap.put(keyCn, androidCnMap.get(keyCn));
                androidCnMap.remove(keyCn);
            }
        }
        //此时 android map中的值应该都过滤了一遍,开始替换XML操作
        setDataForXml(androidCnMap, tempMap);
    }

    //android string文件完成替换
    public static void setDataForXml(Map<String, String> androidDoneMap, Map<String, String> tempCnMap) {
        File f = new File(ANDROID_ENGLISH_STRING_NAME);
        Element element = null;
        DocumentBuilder db = null;
        DocumentBuilderFactory dbf = null;
        try {
            // 返回documentBuilderFactory对象
            dbf = DocumentBuilderFactory.newInstance();
            // 返回db对象用documentBuilderFatory对象获得返回documentBuildr对象
            db = dbf.newDocumentBuilder();
            // 得到一个DOM并返回给document对象
            Document dt = db.parse(f);
            dt.getDocumentElement().normalize();
            // 得到一个elment根元素
            element = dt.getDocumentElement();
            // 获得根节点
//			System.out.println("根元素：" + element.getNodeName());
            // 获得根元素下的子节点
            NodeList childNodes = element.getChildNodes();

            Element elementTest = dt.createElement("test");
            elementTest.setTextContent("---------------------------------------------分割线-已翻译----------------------------------------------------");
            element.appendChild(elementTest);

            for (String key : androidDoneMap.keySet()) {
                Element elementNew = dt.createElement("string");
                elementNew.setAttribute("name", key);
                elementNew.setTextContent(androidDoneMap.get(key));
                element.appendChild(elementNew);
            }

            Element elementTest1 = dt.createElement("test");
            elementTest1.setTextContent("----------------------------------------------分割线-未翻译---------------------------------------------------");
            element.appendChild(elementTest1);
            for (String key : tempCnMap.keySet()) {
                Element elementNew = dt.createElement("string");
                elementNew.setAttribute("name", key);
                elementNew.setTextContent(tempCnMap.get(key));
                element.appendChild(elementNew);
            }

            //write the updated document to file or console
            dt.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(dt);
            StreamResult result = new StreamResult(new File(ANDROID_ENGLISH_STRING_NAME));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            System.out.println("XML file updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
