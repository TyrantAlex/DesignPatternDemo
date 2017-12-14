package com.international.fileoperator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 提取String成中文
 * 
 * @author Administrator
 * 
 */
public class XMLTest2 {

	private static final String txt_path = "C:/Users/Administrator/Desktop/temp/strings.txt";
	private static final String txt_path_new = "C:/Users/Administrator/Desktop/temp/string_zh.txt";

	public static void main(String[] args) {
		List<String> xmlStringValueList = catchXMLContent();
		writeList2TXT(xmlStringValueList);
	}

	// String文件中抽取值
	private static List<String> catchXMLContent() {
		List<String> xmlStringList = new ArrayList<String>();

		File f = new File(txt_path);
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
					xmlStringList.add(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlStringList;
	}

	// 将值写入普通txt文件中
	private static void writeList2TXT(List<String> contentList) {
		Iterator iterator = contentList.iterator();
		File file = new File(txt_path_new);
		FileWriter fw = null;
		BufferedWriter writer = null;
		try {
			fw = new FileWriter(file);
			writer = new BufferedWriter(fw);
			int i = 1;
			while (iterator.hasNext()) {
				writer.write(i+". " + iterator.next().toString());
				writer.newLine();// 换行
				i++;
			}
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
