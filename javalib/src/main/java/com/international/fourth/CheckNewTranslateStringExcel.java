package com.international.fourth;

import com.international.repeat.vo.StringXmlBean;
import com.international.util.InternationalDocUtils;
import com.international.util.InternationalFileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 翻译回来的中文与英文泰文按对应位置匹配到项目中
 * @author : hongshen
 * @Date: 2018/6/19 0019
 */
public class CheckNewTranslateStringExcel {
    /**
     * 翻译回来的文件路径
     */
    public static final String EXCEL_FILE_PATH = "/Users/shen/Documents/2dfire/international/11-26THAI.xls";

    /**
     * 项目对应位置
     */
    public static final String PROJECT_PATH = "/Users/shen/Develop/2dfire/project/checkout_1";

    /**
     * 所有文件的对应类型的所有String字符串对象
     */
    private List<StringXmlBean> allList = new ArrayList<>();

    /**
     * xml文件中标签key  标识此文件为何种values文件
     * color; style; dimen; declare-styleable; string
     */
    private static final String FILE_NODE_KEY = "string";

    /**
     * 路径打印时候无用的路径部分 如需全路径则置空
     * D:\AndroidStudio\AndroidProject\Checkout3\
     */
    private String fileUnuseName = "";

    /**
     * String文件类型目标文件夹
     */
    private static final String DIRECT_FOLDER_VALUES = "values";
//    private static final String DIRECT_FOLDER_VALUES_EN = "values-en";
//    private static final String DIRECT_FOLDER_VALUES_TW = "values-zh-rTW";
    private static final String DIRECT_FOLDER_VALUES_TH = "values-th-rTH";

    private Map<String, String> cnEnMap = new HashMap<>();

    public static void main(String[] args) {
        CheckNewTranslateStringExcel check = new CheckNewTranslateStringExcel();
        System.out.println("------------------------------------START------------------------------");
        check.start();
        System.out.println("------------------------------------END---------------------------------");
    }

    private void start() {
        //遍历翻译回来的excel文档 读取对应翻译到map中 通过修改column值改变读取的内容
        readExcel(EXCEL_FILE_PATH, 0, 2);

        //查询项目目录中所有的中文字符与key
        checkOnMultiFolder(PROJECT_PATH, DIRECT_FOLDER_VALUES);
        System.out.println("所有中文字符数量: " + allList.size());

        //比较所有中文与翻译回来的中文集合, 找出相符的key与文件对应位置放入集合
        List<StringXmlBean> checkBeanList = checkTranslate(cnEnMap);
        System.out.println("比较翻译后集合数量: " + checkBeanList.size());

        //是否有未匹配上的翻译后文字
        for (Map.Entry<String, String> entry : cnEnMap.entrySet()) {
            String str = entry.getValue();
            boolean isMatch = false;
            for (StringXmlBean bean : checkBeanList) {
                if (bean.getValue().equals(str)) {
                    isMatch = true;
                }
            }
            if (!isMatch) {
                System.out.println("翻译后文件未匹配上的字符串为: " + str);
            }
        }

        //查询集合中对应中文位置对应的英文文件, 遍历英文文件 如果有则替换无则添加
        replaceStringFilePath(checkBeanList);
    }

    /**
     * 读取excel指定行列
     * @param filePath
     * @param sheetNum
     */
    public void readExcel(String filePath, int sheetNum, int columnNum) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println(filePath + "路径下文件不存在");
            return;
        }
        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            System.out.println("Excel的页签数量 = " + sheet_size);
            Sheet sheet = wb.getSheet(sheetNum);
            // sheet.getRows()返回该页的总行数
            int rows = sheet.getRows();
            System.out.println("当前页签总行数 = " + rows);
            // sheet.getColumns()返回该页的总列数
            int columns = sheet.getColumns();
            System.out.println("当前页签总列数 = " + columns);
            for (int i = 0; i < rows; i++) {
                String cnInfo = sheet.getCell( 0, i).getContents();
                String thaiInfo = sheet.getCell( columnNum, i).getContents();
                //放入map
                cnEnMap.put(cnInfo, thaiInfo);
                System.out.println(cnInfo + " == " + thaiInfo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行替换
     * 此时bean中存放的应当是中文key 英文value 中文string文件地址
     * @param checkBeanList
     */
    private void replaceStringFilePath(List<StringXmlBean> checkBeanList) {
        for (StringXmlBean bean : checkBeanList) {
            String value = bean.getValue();
            String key = bean.getKey();
            String filePath = bean.getFileName();
            String formatted = bean.getFormatted();
            //中文string文件file
            File file = new File(filePath);
            String fileName = file.getName();
            //到values一层
            File parent = file.getParentFile();
            //values父一级 string文件祖父一级
            String grandparent = parent.getParent();
            //替换为外文目录
            String englishDir = grandparent + File.separator + DIRECT_FOLDER_VALUES_TH;

            File file1 = new File(englishDir);
            if (!file1.exists()) {
                file1.mkdirs();
                createForiegnFile(englishDir, fileName, key, value, formatted);
            } else {
                //列出当前目录的所有文件
                File[] files = file1.listFiles();
                int enFileNum = files.length;
                for (File fileSingle : files) {
                    //选values-en中的文件进行添加
                    boolean nodeKeyFile = isNodeKeyFile(fileSingle);
                    //判断是否为string文件
                    if (nodeKeyFile) {
                        //dom解析读取文件并执行添加
                        replaceStringByDom(fileSingle, key, value, formatted);
                        break;
                    }else{
                        System.out.println("当前英文目录存放的不是string文件: " + files.length + " ," + fileSingle.getPath());
                        enFileNum -- ;
                        if (enFileNum == 0) {
                            System.out.println("当前英文目录没有存放英文文件：" + " ," + fileSingle.getPath());
                            createForiegnFile(englishDir, fileName, key, value, formatted);
                        }
                    }
                }
//                //列出当前目录的所有文件
//                File[] files = file1.listFiles();
//                if (files.length >= 1) {
//                    //选values-en中第一个文件进行添加
//                    boolean nodeKeyFile = isNodeKeyFile(files[0]);
//                    //判断是否为string文件
//                    if (nodeKeyFile) {
//                        //dom解析读取文件并执行添加
//                        replaceStringByDom(files[0], key, value, formatted);
//                    }else{
//                        System.out.println("当前英文目录存放的不是string文件: " + files.length + " ," + files[0].getPath());
//                    }
//
//                    if (files.length > 1) {
//                        System.out.println("当前英文目录不止一个文件: " + files.length + " ," + file1.getPath());
//                    }
//                } else {
//                    createForiegnFile(englishDir, fileName, key, value, formatted);
//                }
            }
        }
    }

    /**
     * 有目录无文件的情况
     * @param englishDir
     * @param fileName
     * @param key
     * @param value
     */
    private void createForiegnFile(String englishDir, String fileName, String key, String value, String fomatted) {
        //建立英文目录文件
        String enStringFilePath = englishDir + File.separator + fileName;
        File fileEn = new File(enStringFilePath);
        //是否需要拦截
        if (interceptCreateFile(enStringFilePath)) {
            return;
        }
        System.out.println("需要创建英文目录的字符: " + value + ", path = " + enStringFilePath);
        try {
            boolean newFile = fileEn.createNewFile();
            if (newFile) {
                createStringByDom(fileEn,key,value, fomatted);
                System.out.println("创建英文目录文件成功: " + englishDir);
            } else {
                System.out.println("创建英文目录文件失败: " + englishDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拦截一些不需要创建国际化文件的仓库
     */
    private boolean interceptCreateFile(String path) {
        boolean isNeed2Intercept = false;
        //口碑排除
        if (path.toLowerCase().contains("koubei")) {
            isNeed2Intercept = true;
        }
        return isNeed2Intercept;
    }

    /**
     * 创建xml string文件
     * @param file
     * @param key
     * @param value
     */
    private void createStringByDom(File file, String key, String value, String formatted) {
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
            elementNew.setAttribute("name", key);
            if (formatted != null && formatted.length() !=0) {
                elementNew.setAttribute("formatted", formatted);
            }
            elementNew.setTextContent(value);
            element.appendChild(elementNew);
            //write the updated document to file or console
            document.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            System.out.println("创建string文件成功..." + file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("创建string文件失败..." + file.getPath());
        }
    }

    /**
     * 通过dom解析xml文件执行替换操作
     * @param file
     * @param key1 key
     * @param value1
     */
    private void replaceStringByDom(File file, String key1, String value1, String formatted) {
        if (!file.exists()) {
            return;
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
            //匹配次数
            int matchNum = 0;
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node1 = childNodes.item(i);
                if ("string".equals(node1.getNodeName())) {
                    // 键
                    String filekey = node1.getAttributes().getNamedItem("name")
                            .getNodeValue();
                    // 值
                    String filevalue = node1.getTextContent();

                    //取到xml文件键值开始匹配
                    if (filekey.equals(key1)) {
                        node1.setTextContent(value1);
                        if (formatted != null && formatted.length() != 0) {
                            Element element1 = (Element) node1;
                            element1.setAttribute("formatted", formatted);
                        }
                        matchNum++;
                    }
                }
            }
            if (matchNum == 0) {
                //直接添加
                //获取String元素
                Element elementNew = dt.createElement("string");
                elementNew.setAttribute("name", key1);
                if (formatted != null && formatted.length() !=0) {
                    elementNew.setAttribute("formatted", formatted);
                }
                elementNew.setTextContent(value1);
                element.appendChild(elementNew);
//                System.out.println("无匹配项直接添加元素"+ "key : " + key1 + ", value: " + value1 + ", path：" + file.getPath());
            }
            //write the updated document to file or console
            dt.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(dt);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
//            System.out.println("替换英文字符完毕...");
        } catch (Exception e) {
            System.out.println("替换英文字符异常！！！");
            e.printStackTrace();
        }
    }

    /**
     * 比较所有中文与翻译后中文相符的对象放入一个集合中
     * @param translateMap 翻译后中文外文集合
     * @return
     */
    private List<StringXmlBean> checkTranslate(Map<String,String> translateMap) {
        List<StringXmlBean> checkedList = new ArrayList<>();
        for (StringXmlBean bean : allList) {
            //检测重复字符 避免多次添加
            boolean isRepeat = false;
            //去标点再比较
//            String originBean = InternationalDocUtils.myFormatUtil(bean.getValue());
            for (Map.Entry<String, String> entry : translateMap.entrySet()) {
                String transedCNStr = entry.getKey();
                String transedENStr = entry.getValue();

                //去标点再比较
//                String originTrans = InternationalDocUtils.myFormatUtil(transedStr);
//                if (originTrans.equals(originBean)) {
                if (transedCNStr.equals(bean.getValue())) {
                    if (!isRepeat) {
                        /**
                         * 获取英文value 替换中文bean中的value
                         * 此时bean中存放的应当是中文key 英文value 中文string文件地址
                         */
//                        String androidForeignString = transedENStr.replace("%@", "%s");
//                        androidForeignString = androidForeignString.replace("'", "\\'");
                        bean.setValue(transedENStr);
                        checkedList.add(bean);
                    } else {
                        System.out.println("重复的翻译后字符串为: " + transedCNStr);
                    }
                    isRepeat = true;
                }
            }
        }
        return checkedList;
    }

    /**
     * 查找文件
     * 读取一个路径下多个文件夹的指定文件
     * return 当前文件夹下所有符合的数据list集合
     */
    private void checkOnMultiFolder(String baseDirName, String directFoldString) {
        File baseDir = new File(baseDirName);       // 创建一个File对象
        if (!baseDir.exists() || !baseDir.isDirectory()) {  // 判断目录是否存在
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        }
        String tempName = null;
        //判断目录是否存在
        File tempFile;
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            if (tempFile.isDirectory()) {
                checkOnMultiFolder(tempFile.getAbsolutePath(), directFoldString);
            } else if (tempFile.isFile()) {
                //只扫描values文件夹
                String parent = tempFile.getParent();
                boolean isBuild = parent.contains("/build/");
                if (isBuild) {
                    continue;
                }
                boolean isValues = parent.endsWith(directFoldString);
                if (isValues) {
                    boolean stringFile = isNodeKeyFile(tempFile);
                    if (stringFile) {
                        List<StringXmlBean> beanList = splitStringXml2Map(tempFile);
                        if (beanList != null) {
                            allList.addAll(beanList);
                        }
                    }
                }
            }
        }
    }

    /**
     * 标识对应文件身份
     *
     * @param file
     * @return
     */
    private boolean isNodeKeyFile(File file) {
        boolean isNodeKeyFile = false;
        if (!file.exists()) {
            return false;
        }
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
                if (FILE_NODE_KEY.equals(node1.getNodeName())) {
                    isNodeKeyFile = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isNodeKeyFile;
    }

    /**
     * android中文string.xml文件拆分
     *
     * @return List<StringXmlBean> xml文件转list集合
     */
    private List<StringXmlBean> splitStringXml2Map(File file) {
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

                    //formatted
                    String formatted = null;
                    if (node1.getAttributes().getNamedItem("formatted") != null) {
                        formatted = node1.getAttributes().getNamedItem("formatted").getNodeValue();
                    }

                    StringXmlBean stringXmlBean = new StringXmlBean();
                    String path = file.getPath();
                    String subPath = "";
                    if (path.contains(fileUnuseName)) {
                        subPath = path.substring(fileUnuseName.length(), path.length());
                    }
                    stringXmlBean.setFileName(subPath);
                    stringXmlBean.setKey(key);
                    stringXmlBean.setValue(value);
                    if (formatted != null && formatted.length() !=0) {
                        stringXmlBean.setFormatted(formatted);
                    }
                    list.add(stringXmlBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
