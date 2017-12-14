package com.international.fileoperator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 对比两个xml文件中的key,如果有独立的,提取出来
 * @author Administrator
 *
 */
public class XMLTest5 {
	//String中文路径
	private final static String STRING_XML_ZH = "D:\\translatetest/string_zh.xml";
	//String英文路径
	private final static String STRING_XML_EN = "D:\\translatetest/string_en.xml";
	//中文中有英文中没有的新文件路径
	private final static String STRING_NEW = "D:\\translatetest/string_new.txt";
	//英文中有中文中没有的新文件路径
	private final static String STRINGS = "D:\\translatetest/strings.txt";
	
	public static void main(String[] args) {
		//提取xml.zh中的String项Map
		Map<String,String> zhMap = getMap(STRING_XML_ZH);
		Map<String,String> enMap = getMap(STRING_XML_EN);
		//开始比较
		compareStringMap(zhMap,enMap);
//		compareStringMap(enMap,zhMap);
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
	
	/**
	 * 进行比较并进行写出操作
	 * @param zhMap
	 * @param enMap
	 */
	private static void compareStringMap(Map<String,String> zhMap,Map<String,String> enMap){
		boolean flag;
		//开始比较,先遍历两个map找到不一样的key
		for(String zhKey : zhMap.keySet()){
			flag = true;
			for(String enKey : enMap.keySet()){
				//如果找到了相同的key则开始新的循环
				if(zhKey.equals(enKey)){
					flag = false;
					break;
				}
			}
			//没有匹配的键值,则写入文件
			if(flag == true) {
				write2ZHNewContent(zhKey, zhMap.get(zhKey));
			}
		}
	}
	
	/**
	 * 写入文件
	 * @param content
	 */
	private static void write2ZHNewContent(String key, String content) {
		File file = new File(STRING_NEW);
		FileWriter fw = null;
		BufferedWriter writer = null;
		try {
			fw = new FileWriter(file,true);
			writer = new BufferedWriter(fw);
//			writer.write(key + " === " + content);
			writer.write(content);
			writer.newLine();// 换行
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
