package com.international.fileoperator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
 * 替换脚本
 * 根据android中文string.xml文件生成英文string.xml文件, 通过匹配iOS国际化文档
 */
public class ReplaceAndroidCn2EnByiOSDoc {
	//android中文string.xml文件路径
	private static final String ANDROID_CHINESE_STRING_NAME = "D:\\translateTemp/string.xml";
	//android英文string.xml文件路径
	private static final String ANDROID_ENGLISH_STRING_NAME = "D:\\translateTemp/strings_en.xml";
	//ios国际化文件路径
	private static final String IOS_STRING_NAME = "D:\\translateTemp/ios_L.txt";

	public static void main(String[] args) {
		ReplaceAndroidCn2EnByiOSDoc x = new ReplaceAndroidCn2EnByiOSDoc();
		Map<String, String> androidDataFromXml = x.splitStringXml2Map(ANDROID_CHINESE_STRING_NAME);
		Map<String, String> iOSfileStrByLine = x.splitFileStrByLine2Map(IOS_STRING_NAME);
		
		x.replaceIOS2Android(iOSfileStrByLine,androidDataFromXml);
	}

	/**
	 * android中文string.xml文件拆分
	 * @param filePath android 中文string.xml文件路径
	 * @return map key = string.xml的key; map value = string.xml value
	 */
	private Map<String,String> splitStringXml2Map(String filePath) {
		Map<String, String> stringMap = new HashMap<String, String>();
		File f = new File(filePath);
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

	/**
	 * 拆分iOS文档到map集合中
	 * 文档内容以行的形式读取  “中文” = “英文”
	 * @param filePath iOS文档路径
	 * @return map key = iOS 文件的 key; map value = iOS 文件的 value
	 */
	private Map<String,String> splitFileStrByLine2Map(String filePath) {
		Map<String, String> iOSstringMap = new HashMap<String, String>();
		File file = new File(filePath);
		String result = null;
		FileReader fr = null;
		BufferedReader br = null;
		String line = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			line = br.readLine();
			while (line != null) {
//				System.out.println("已经读到 line = " + line);
				if (line.contains("=")) {
					line.replace("%@", "%s");
					line.replace("%@", "%s");
					String[] split = line.split("=");
					split[0] = split[0].replace("\"", "");
//					split[0] = split[0].replace(";", "");
					split[1] =split[1].replace("\"", "");
					split[1] =split[1].replace(";", "");
					split[1] =split[1].substring(2, split[1].length());
//					System.out.println("打印中文:" + split[0]);
//					System.out.println("打印英文:" + split[1]);
					//中文,英文以键值对的形式存入map集合中
					iOSstringMap.put(split[0], split[1]);
					line = br.readLine();
				} else {
					System.out.println("直接打印不包含的line = " + line);
					line = br.readLine();
				}
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
		return iOSstringMap;
	}

	/**
	 * map集合替换
	 * @param iosMap
	 * @param androidMap
	 */
	private void replaceIOS2Android(Map<String,String> iosMap,Map<String,String> androidMap){
		  for (String key : iosMap.keySet()) {
//			   System.out.println("key= "+ key + " and value= " + iosMap.get(key));
			   for(String key2 : androidMap.keySet()){
				   //执行去除标点再匹配
				   String myFormatUtilKey = myFormatUtil(key);
				   String myFormatUtilValue = myFormatUtil(androidMap.get(key2));
				   //ios的中文匹配了android的中文
				   if(myFormatUtilKey.equals(myFormatUtilValue)){
					  //执行ios英文替换android中文的操作
					  androidMap.put(key2,  iosMap.get(key));
//					  System.out.println("匹配上了androidkey= "+ key2 + " iosvalue= " + iosMap.get(key));
				   }else{
//					  System.out.println("未匹配上 ioskey= "+ key + " androidvalue= " + androidMap.get(key2));
				   }
			   }
		  }
		  
		  //此时 android map中的值应该都过滤了一遍,开始替换XML操作
		  setDataForXml(androidMap);
	}
	
	//android string文件完成替换
	private void setDataForXml(Map<String,String> androidDoneMap) {
		File f = new File(ANDROID_CHINESE_STRING_NAME);
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
            StreamResult result = new StreamResult(new File(ANDROID_ENGLISH_STRING_NAME));
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
