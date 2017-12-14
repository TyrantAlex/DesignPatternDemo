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
 * @author Administrator
 *
 */
public class XMLTest3 {

	private static final String ANDROID_TRANSLATE_FILE = "D:\\translate1.txt";
	private static final String ANDROID_XML_FILE = "D:\\strings_en.xml";
	private static final String ANDROID_XML_FILE_ENGLISH = "D:\\strings_en1.xml";
	//原文
	private static List<String> translateList = new ArrayList<String>();
	//英文
	private static List<String> translateListEn = new ArrayList<String>();
	//中文
	private static List<String> translateListZh = new ArrayList<String>();
	
	public static void main(String[] args) {
		getTranslate2Collection();
		split2List();
		Map<String, String> dataFromXml = getDataFromXml();
		startAdd2String(dataFromXml);
	}
	
	//依据行来读取翻译文件
	private static void getTranslate2Collection(){
		File file = new File(ANDROID_TRANSLATE_FILE);
		FileReader fr = null;
		BufferedReader br = null;
		String line = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			line = br.readLine();
			while(line != null){
				line = br.readLine();
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
			if (i%2 == 0)translateListEn.add(translateList.get(i));
			else if(i%2 == 1) translateListZh.add(translateList.get(i));
		}
	}
	
	//匹配String.xml文件中的中文原文,并将List中的英文翻译填到String文件节点下 
	private static void startAdd2String(Map<String,String>androidMap){
		for(String key:androidMap.keySet()){
			   //执行去除标点再匹配
			   String myFormatUtilValue = myFormatUtil(androidMap.get(key));
			   for(int i=0;i<translateListZh.size();i++){
				   if(myFormatUtilValue.equals(translateListZh.get(i))){
					   androidMap.put(key, translateListEn.get(i));
				   }
			   }
		}
		
		setDataForXml(androidMap);
	}
	
	//android string文件拆分键值
	private static Map<String,String> getDataFromXml() {
		Map<String, String> stringMap = new HashMap<String, String>();

		File f = new File(ANDROID_XML_FILE);

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
					// 加入map集合  键是String节点的键 值是中文值
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
		File f = new File(ANDROID_XML_FILE_ENGLISH);
		
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
					System.out.println("替换的key是:" + key + "替换的值是:" + englishValue);
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
            StreamResult result = new StreamResult(new File(ANDROID_XML_FILE_ENGLISH));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            System.out.println("XML file updated successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//字符串去标点
	public static String myFormatUtil(String s){   
        String str=s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");   
        return str;   
    }  
}
