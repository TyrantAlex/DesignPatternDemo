package com.international.fileoperator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * txt抽取到string文件
 * 并完成英文到英文的转换
 * @author Administrator
 *
 */
public class XMLTest4 {
	private static final String STRINGXMLCN = "D:\\translatetest/strings.xml";
	private static final String STRINGXMLEN = "D:\\translatetest/strings_en.xml";
	private static final String FILE_TRANSLATE = "D:\\translatetest/translate.txt";
	private static final String FILE_NEW = "D:\\translatetest/strings_en1.xml";
	//原文
	private static List<String> translateList = null;
	//英文
	private static List<String> translateListEn = null;
	//中文
	private static List<String> translateListZh = null;
	
	public static void main(String[] args) {
		translateList = new ArrayList<String>();
		translateListEn = new ArrayList<String>();
		translateListZh = new ArrayList<String>();
		getTranslate2Collection();
		split2List();
		startAdd2String();
	}
	
	//依据行来读取翻译文件
	private static void getTranslate2Collection(){
		File file = new File(FILE_TRANSLATE);
		FileReader fr = null;
		BufferedReader br = null;
		String line = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((line = br.readLine())!= null){
				translateList.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//分离List中英文翻译和中文原文到两个list中
	private static void split2List(){
		for(int i=0;i<translateList.size();i++){
			String listStr = translateList.get(i);
			String substring = null;
			if(listStr != null) {
				int indexOf = listStr.indexOf(".");
				substring = listStr.substring(indexOf+2, listStr.length());
			}
			if (i%2 == 0) translateListZh.add(substring);
			else if(i%2 == 1) translateListEn.add(substring);
		}
	}
	
	//匹配String.xml文件中的中文原文,并将List中的英文翻译填到String文件节点下 
	private static void startAdd2String(){
		//中文的XML map
		Map<String,String> cn_Map = getDataFromXml(STRINGXMLCN);
		//英文的XML map
		Map<String,String> en_Map = getDataFromXml(STRINGXMLEN);
		
		//干净的key value 纯英文的 只有改动的部分
//		Map<String,String> androidMapClean = new HashMap<String,String>();
		for(String key:cn_Map.keySet()){
			   //执行去除标点再匹配
			   String cn_mapValue = myFormatUtil(cn_Map.get(key));
			   for(int i=0;i<translateListZh.size();i++){
				   String translateListZhValue = myFormatUtil(translateListZh.get(i));
				   if(cn_mapValue.equals(translateListZhValue)){
					   //如果中文MAP的值匹配到了翻译中文的值 就把英文的map对应中文mapkey的值替换成翻译英文的值
					   en_Map.put(key, translateListEn.get(i));
					   System.out.println("替键:" + key +"替值:"+ translateListEn.get(i));
//					   androidMapClean.put(key, translateListEn.get(i));
				   }
			   }
		}
		setDataForXml(en_Map);
	}
	
	//android string文件拆分键值
	private static Map<String,String> getDataFromXml(String FileName) {
		Map<String, String> stringMap = new HashMap<String, String>();

		File f = new File(FileName);

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
					// 加入map集合  键是String节点的键  值是中文值
					stringMap.put(key, value);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return stringMap;
	}
	
	//android string文件完成替换
	private static void setDataForXml(Map<String,String> androidDoneMap) {
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
					String englishValue = androidDoneMap.get(key);
//					System.out.println("替换的key是:" + key + "替换的值是:" + englishValue);
					//完成替换
					node1.setNodeValue(englishValue);
					node1.setTextContent(englishValue);
					
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
	
	//字符串去标点和数字
	public static String myFormatUtil(String s){   
		if(s != null){
			String str=s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");   
			//去数字
			str = str.replaceAll("\\d+","");
			return str;   
		}
		return null;
    }  
}
