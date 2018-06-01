package com.international.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 国际化文档工具类
 * Created by hongshen on 2017/11/23 0023.
 */

public class InternationalDocUtils {
    /**
     * android中文string.xml文件拆分
     *
     * @return map key = string.xml的key; map value = string.xml value
     */
    public static ConcurrentHashMap<String, String> splitStringXml2Map(String filePath) {
        ConcurrentHashMap<String, String> stringMap = new ConcurrentHashMap<String, String>();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringMap;
    }


    /**
     * 拆分iOS文档到map集合中
     * 文档内容以行的形式读取  “中文” = “英文”
     *
     * @param filePath iOS文档路径
     * @return map key = iOS 文件的 key; map value = iOS 文件的 value
     */
    public static Map<String, String> splitFileStrByLine2Map(String filePath) {
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
                    String[] split = line.split("=");
                    if (split.length != 2) {
                        System.out.println("ios文件中的异常line = " + line);
                        line = br.readLine();
                        continue;
                    }
                    split[0] = split[0].trim();
                    split[1] = split[1].trim();
                    if (!split[0].startsWith("\"")) {
                        System.out.println("ios文件中的异常line 中文部分不是已 “ 开头 = " + line);
                    }
                    if (!split[1].startsWith("\"")) {
                        System.out.println("ios文件中的异常line 英文部分不是已 “ 开头 = " + line);
                    }
                    split[0] = split[0].substring(1, split[0].lastIndexOf("\""));
                    split[1] = split[1].substring(1, split[1].lastIndexOf("\""));

                    //有问题
//                    split[0] = split[0].substring(split[0].indexOf("\"") + 1, split[0].lastIndexOf("\""));
//                    split[1] = split[1].substring(split[1].indexOf("\"") + 1, split[1].lastIndexOf("\""));

                    //使用下面注释的提取方式有问题, 第一个双引号前面可能有长条空格
//                    split[0] = split[0].replace("\"", "");
//                    split[1] =split[1].replace("\"", "");
//                    split[1] =split[1].substring(2, split[1].length());

//                    System.out.println("split 0 = " + split[0]);
//                    System.out.println("split 1 = " + split[1]);
                    //中文,英文以键值对的形式存入map集合中
                    iOSstringMap.put(split[0], split[1]);
                } else {
                    if (line != null && !"".equals(line) && line.length() != 0) {
                        System.out.println("直接打印不包含=的line = " + line);
                    }
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
     * 字符串去标点
     * 这里最好全部替换成通用字符, 不然语义有可能发生变化
     * 通用字符 HAHA  标点
     */
    public static String myFormatUtil(String s) {
        String str1 = s.replaceAll("(?:%s|%@|%d|%x|%o|%f|%a|%e|%g|%n|%%|%1$|%3$|%2$|%4$|%1d|%2d|%3d|%4d|%1$f|%2$f|%3$f|%4f)","HAHA");
//        String str1 = s.replaceAll("%s","HAHA");


        String str = str1.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "标点");
        return str;
    }
}
