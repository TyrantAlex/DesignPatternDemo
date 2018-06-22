package com.international.thailand;

import com.international.repeat.vo.StringXmlBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
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
 * 测试dom解析创建xml
 * @author : hongshen
 * @Date: 2018/6/20 0020
 */
public class TestCreateStringXml {

    public static final String CREATE_FILE_PATH = "D://Test_CreateFile.xml";

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        File file = new File(CREATE_FILE_PATH);
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            //获取根元素
            Element element = document.getDocumentElement();
            if (element == null) {
                element = document.createElement("resources");
            }
            document.appendChild(element);

            //获取String元素
            Element elementNew = document.createElement("string");
            elementNew.setAttribute("name", "haha1");
            elementNew.setTextContent("hahahahahahaha");
            element.appendChild(elementNew);
            //write the updated document to file or console
            document.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            System.out.println("创建string文件成功...");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("创建string文件失败...");
        }
    }
}
