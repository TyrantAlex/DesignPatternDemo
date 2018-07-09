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
 * 将android中英文有的字符串给泰文对应地方一份
 * @author : hongshen
 * @Date: 2018/6/28 0028
 */
public class CheckAndroidEnUnTh {

    /**
     * android项目对应位置
     */
    public static final String PROJECT_PATH = "D:/AndroidStudio/AndroidProject/Checkout3";

    /**
     * xml文件中标签key  标识此文件为何种values文件
     * color; style; dimen; declare-styleable; string
     */
    private static final String FILE_NODE_KEY = "string";

    /**
     * String文件类型目标文件夹
     */
//    private static final String DIRECT_FOLDER_VALUES = "values";
    private static final String DIRECT_FOLDER_VALUES_EN = "values-en";
//    private static final String DIRECT_FOLDER_VALUES_TW = "values-zh-rTW";
    private static final String DIRECT_FOLDER_VALUES_TH = "values-th-rTH";

    /**
     * 路径打印时候无用的路径部分 如需全路径则置空
     * D:\AndroidStudio\AndroidProject\Checkout3\
     */
    private String fileUnuseName = "";

    /**
     * 所有文件的对应类型的所有中文String字符串对象
     */
    private List<StringXmlBean> allList = new ArrayList<>();

    public static void main(String[] args) {
        CheckAndroidEnUnTh check = new CheckAndroidEnUnTh();
        check.start();
    }

    private void start() {
        //取所有英文
        //将android英文取出组成list
        checkOnMultiFolder(PROJECT_PATH, DIRECT_FOLDER_VALUES_EN);
        List<StringXmlBean> allEN= new ArrayList<>();
        allEN.addAll(allList);
        System.out.println("所有英文字符数量: " + allList.size());
        allList.clear();

        //取所有泰文
        checkOnMultiFolder(PROJECT_PATH, DIRECT_FOLDER_VALUES_TH);
        List<StringXmlBean> allTH= new ArrayList<>();
        allTH.addAll(allList);
        System.out.println("所有泰文字符数量: " + allList.size());
        allList.clear();

        //比较英文有泰文没有的
        List<StringXmlBean> beanList = compareAndroidCNAndEn(allEN, allTH);
        System.out.println("所有有英文有泰文没有的 字符数量: " + beanList.size());

        //去重
        for  ( int  i  =   0 ; i  <  beanList.size()  -   1 ; i ++ )  {
            for  ( int  j  =  beanList.size()  -   1 ; j  >  i; j -- )  {
                if  (beanList.get(j).getKey().equals(beanList.get(i).getKey()) && beanList.get(j).getFileName().equals(beanList.get(i).getFileName()))  {
//                    System.out.println("需要补充的英文字符重复的key且在同一份文件中 : " + beanList.get(i).getKey());
                    beanList.remove(j);
                }
            }
        }

        //比较英文有泰文没有的写入对应文件最下面
        write2CorrespondFile(beanList);
    }

    /**
     * 将list中的对象写入各自英文文件中
     * @param beanList
     */
    private void write2CorrespondFile(List<StringXmlBean> beanList) {
        for (StringXmlBean bean : beanList) {
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
            //替换为国际化目录
            String englishDir = grandparent + File.separator + DIRECT_FOLDER_VALUES_TH;

            File file1 = new File(englishDir);
            if (!file1.exists()) {
                file1.mkdirs();
                createForiegnFile(englishDir, fileName, key, value, formatted);
            } else {
                //列出当前目录的所有文件
                File[] files = file1.listFiles();
                if (files.length >= 1) {
                    //选values-en中第一个文件进行添加
                    boolean nodeKeyFile = isNodeKeyFile(files[0]);
                    //判断是否为string文件
                    if (nodeKeyFile) {
                        //dom解析读取文件并执行添加
                        addStringByDom(files[0], key, value, formatted);
                    }else{
                        System.out.println("当前英文目录存放的不是string文件: " + files.length + " ," + files[0].getPath());
                    }

                    if (files.length > 1) {
                        System.out.println("当前英文目录不止一个文件: " + files.length + " ," + file1.getPath());
                    }
                } else {
                    createForiegnFile(englishDir, fileName, key, value, formatted);
                }
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
    private void createForiegnFile(String englishDir, String fileName, String key, String value, String formatted) {
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
                createStringByDom(fileEn,key,value, formatted);
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
     * @param key
     * @param value
     */
    private void addStringByDom(File file, String key1, String value1, String formatted) {
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

            Element elementNew = dt.createElement("string");
            elementNew.setAttribute("name", key1);
            if (formatted != null && formatted.length() !=0) {
                elementNew.setAttribute("formatted", formatted);
            }
            elementNew.setTextContent(value1);
            element.appendChild(elementNew);

            //write the updated document to file or console
//            dt.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(dt);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
//            System.out.println("替换英文字符完毕...");
        } catch (Exception e) {
            System.out.println("替换ios英文字符异常...");
            e.printStackTrace();
        }
    }


    /**
     * 比较android中英文list key 取出有中文无英文的组成list
     * @param allCN
     * @param allEN
     */
    private List<StringXmlBean> compareAndroidCNAndEn(List<StringXmlBean> allCN, List<StringXmlBean> allEN) {
        //有中文无英文的
        List<StringXmlBean> cnNoEn = new ArrayList<>();
        for (StringXmlBean cnBean : allCN) {
            boolean isHaveEn = false;
            for (StringXmlBean enBean : allEN) {
                if (cnBean.getKey().equals(enBean.getKey())) {
                    isHaveEn = true;
                }
            }
            if (!isHaveEn) {
                cnNoEn.add(cnBean);
            }
        }
        return cnNoEn;
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
                boolean isBuild = parent.contains("\\build\\");
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
