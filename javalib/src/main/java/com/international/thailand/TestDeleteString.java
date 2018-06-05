package com.international.thailand;

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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * 测试删除对应String
 * @author : hongshen
 * @Date: 2018/6/4 0004
 */
public class TestDeleteString {

    public static final String path = "D:/translateTemp/testDeleteString.xml";

    public static void main(String[] args) {
        TestDeleteString test = new TestDeleteString();
        File file = new File(path);
        test.deleteString(file, "aaa", "haha");
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
                        System.out.println("正在进行删除..." + "key: " + key + "value: " + value);
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
            System.out.println("删除完毕...");
        } catch (Exception e) {
            System.out.println("删除异常...");
            e.printStackTrace();
        }
        return list;
    }
}
