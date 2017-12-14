package com.international.fileoperator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * NOTUSE!!!
 * 一个文件的key替换为另一个文件的key
 * @author Administrator
 *
 */
public class XMLTest6 {
	
	//String中文路径
	private final static String STRING_XML_ZH = "D:\\translatetest/string.xml";
	//String英文路径
	private final static String STRING_XML_EN = "D:\\translatetest/strings_key_en.xml";
	//生成的新文件路径
	private static final String FILE_NEW = "D:\\translatetest/strings_key_en1.xml";
	
	public static List<String> zhKeyList = new ArrayList<String>();
	public static List<String> enValueList = new ArrayList<String>();

	public static void main(String[] args) {
		reverser();
	}
	
	/**
	 * 开始交换
	 */
	public static void reverser(){
		//中文string map
		Map<String, String> zhMap = getMap1(STRING_XML_ZH);
		//英文string map
		Map<String, String> enMap = getMap2(STRING_XML_EN);
		Map<String,String> doneMap = new HashMap<String,String>();
		for(int i=0; i<zhKeyList.size();i++){
			System.out.println("enValueList长度" + enValueList.size());
			System.out.println("zhKeyList长度" + zhKeyList.size());
			doneMap.put(enValueList.get(i),zhKeyList.get(i));
		}
		setDataForXml(doneMap);
	}
	
	/**
	 * 抽取String文件中的内容到Map集合中
	 * KEY是String文件中的各个key
	 * value是各个值
	 * @param fileName 根据传入的文件名决定是哪个文件
	 * @return
	 */
	private static Map<String, String> getMap(String fileName) {
		Map<String,String> contentMap = new HashMap<String,String>();
		File f = new File(fileName);
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
			// 得到一个elment根元素
			element = dt.getDocumentElement();
			// 获得根节点
			System.out.println("根元素：" + element.getNodeName());

			// 获得根元素下的子节点
			NodeList childNodes = element.getChildNodes();

			// 遍历这些子节点
			for (int i = 0; i < childNodes.getLength(); i++) {
				// 获得每个对应位置i的结点
				Node node1 = childNodes.item(i);
				if ("string".equals(node1.getNodeName())) {
					// 键
					String key = node1.getAttributes().getNamedItem("name")
							.getNodeValue();
					// System.out.println(node1.getAttributes().getNamedItem("name").getNodeValue());
					// 值
					String value = node1.getTextContent();
					// System.out.println(node1.getTextContent());
					// 键是String节点的键 值是中文值
					contentMap.put(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(contentMap.isEmpty()) System.out.println("contentMap 为 null~~!" + fileName);
		return contentMap;
	}
	
	//android string文件完成替换
	public static void setDataForXml(Map<String,String> androidDoneMap) {
		File f = new File(FILE_NEW);
		
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

			// 遍历这些子节点
			for (int i = 0; i < childNodes.getLength(); i++) {
				// 获得每个对应位置i的结点
				Node node1 = childNodes.item(i);
				if ("string".equals(node1.getNodeName())) {
					// 键
					String key = node1.getAttributes().getNamedItem("name")
							.getNodeValue();
					// System.out.println(node1.getAttributes().getNamedItem("name").getNodeValue());
					// 值
					String value = node1.getTextContent();
					// System.out.println(node1.getTextContent());
					
					//根据key查询传过来的英文map的值去替换
					String key_Str = androidDoneMap.get(value);
					System.out.println("待替换的中文key是:" + key + "替换的英文StringKey是:" + key_Str);
					node1.getAttributes().getNamedItem("name").setNodeValue(key_Str);
					
//					String englishValue = androidDoneMap.get(key);
//					System.out.println("替换的key是:" + key + "替换的值是:" + englishValue);
					//完成替换
					// node1.setNodeValue(englishValue);
					// node1.setTextContent(englishValue);
					
				}
			}
			
            //write the updated document to file or console
			dt.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(dt);
            StreamResult result = new StreamResult(new File(FILE_NEW));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            System.out.println("XML file updated successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, String> getMap1(String fileName) {
		Map<String,String> contentMap = new HashMap<String,String>();
		File f = new File(fileName);
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
			// 得到一个elment根元素
			element = dt.getDocumentElement();
			// 获得根节点
			System.out.println("根元素：" + element.getNodeName());

			// 获得根元素下的子节点
			NodeList childNodes = element.getChildNodes();

			// 遍历这些子节点
			for (int i = 0; i < childNodes.getLength(); i++) {
				// 获得每个对应位置i的结点
				Node node1 = childNodes.item(i);
				if ("string".equals(node1.getNodeName())) {
					// 键
					String key = node1.getAttributes().getNamedItem("name")
							.getNodeValue();
					// System.out.println(node1.getAttributes().getNamedItem("name").getNodeValue());
					// 值
					String value = node1.getTextContent();
					// System.out.println(node1.getTextContent());
					// 键是String节点的键 值是中文值
					contentMap.put(key, value);
					zhKeyList.add(key);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(contentMap.isEmpty()) System.out.println("contentMap 为 null~~!" + fileName);
		return contentMap;
	}
	
	public static Map<String, String> getMap2(String fileName) {
		Map<String,String> contentMap = new HashMap<String,String>();
		File f = new File(fileName);
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
			// 得到一个elment根元素
			element = dt.getDocumentElement();
			// 获得根节点
			System.out.println("根元素：" + element.getNodeName());

			// 获得根元素下的子节点
			NodeList childNodes = element.getChildNodes();

			// 遍历这些子节点
			for (int i = 0; i < childNodes.getLength(); i++) {
				// 获得每个对应位置i的结点
				Node node1 = childNodes.item(i);
				if ("string".equals(node1.getNodeName())) {
					// 键
					String key = node1.getAttributes().getNamedItem("name")
							.getNodeValue();
					// System.out.println(node1.getAttributes().getNamedItem("name").getNodeValue());
					// 值
					String value = node1.getTextContent();
					// System.out.println(node1.getTextContent());
					// 键是String节点的键 值是中文值
					contentMap.put(key, value);
					enValueList.add(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(contentMap.isEmpty()) System.out.println("contentMap 为 null~~!" + fileName);
		return contentMap;
	}
}
