package com.international.thailand;

import com.international.repeat.vo.StringXmlBean;
import com.international.repeat.vo.StringiOSBean;
import com.international.util.InternationalDocUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
 * 匹配ios的国际化文件并实现与android国际化对比并替换
 * @author : hongshen
 * @Date: 2018/6/20 0020
 */
public class CheckAndroidInternationalByiOS {

    /**
     * android项目对应位置
     */
    public static final String PROJECT_PATH = "D:/AndroidStudio/AndroidProject/Checkout3";

    /**
     * iOS文件路径
     */
    private static final String IOS_FILE_PATH = "D:\\translateTemp\\replace2018.06.19\\translate_after\\0620iosen";

    /**
     * xml文件中标签key  标识此文件为何种values文件
     * color; style; dimen; declare-styleable; string
     */
    private static final String FILE_NODE_KEY = "string";

    /**
     * String文件类型目标文件夹
     */
    private static final String DIRECT_FOLDER_VALUES = "values";
    private static final String DIRECT_FOLDER_VALUES_EN = "values-en";
    private static final String DIRECT_FOLDER_VALUES_TW = "values-zh-rTW";

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
        CheckAndroidInternationalByiOS check = new CheckAndroidInternationalByiOS();
        check.start();
    }

    private void start() {
        //将android中文取出组成list
        checkOnMultiFolder(PROJECT_PATH, DIRECT_FOLDER_VALUES);
        List<StringXmlBean> allCN = new ArrayList<>();
        allCN.addAll(allList);
        System.out.println("所有中文字符数量: " + allList.size());
        allList.clear();

        //将android英文取出组成list
        checkOnMultiFolder(PROJECT_PATH, DIRECT_FOLDER_VALUES_EN);
        List<StringXmlBean> allEN= new ArrayList<>();
        allEN.addAll(allList);
        System.out.println("所有英文文字符数量: " + allList.size());
        allList.clear();

        //比较android中英文key 取出有中文无英文的组成list
        List<StringXmlBean> cnNoEn = compareAndroidCNAndEn(allCN, allEN);
        System.out.println("所有有中文无英文 字符数量: " + cnNoEn.size());

        //将ios文件取出并组成list
        List<StringiOSBean> stringiOSBeans = splitFileStrByLine2List(IOS_FILE_PATH);
        System.out.println("所有ios字符数量: " + stringiOSBeans.size());

        //比较android剩下的list和ios list比较 生成新的英文list
        List<StringXmlBean> beanList = compareAndroidAndiOS(cnNoEn, stringiOSBeans);
        System.out.println("需要补充的英文字符数量 : " + beanList.size());


        //去重
        for  ( int  i  =   0 ; i  <  beanList.size()  -   1 ; i ++ )  {
            for  ( int  j  =  beanList.size()  -   1 ; j  >  i; j -- )  {
                if  (beanList.get(j).getKey().equals(beanList.get(i).getKey()) && beanList.get(j).getFileName().equals(beanList.get(i).getFileName()))  {
//                    System.out.println("需要补充的英文字符重复的key且在同一份文件中 : " + beanList.get(i).getKey());
                    beanList.remove(j);
                }
            }
        }
        System.out.println("去重后的 需要补充的英文字符数量: " + beanList.size());

        //将英文写入各自文件，直接写在最后
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
            //中文string文件file
            File file = new File(filePath);
            String fileName = file.getName();
            //到values一层
            File parent = file.getParentFile();
            //values父一级 string文件祖父一级
            String grandparent = parent.getParent();
            //替换为英文目录
            String englishDir = grandparent + File.separator + DIRECT_FOLDER_VALUES_EN;

            File file1 = new File(englishDir);
            if (!file1.exists()) {
                file1.mkdirs();
                createForiegnFile(englishDir, fileName, key, value);
            } else {
                //列出当前目录的所有文件
                File[] files = file1.listFiles();
                if (files.length >= 1) {
                    //选values-en中第一个文件进行添加
                    boolean nodeKeyFile = isNodeKeyFile(files[0]);
                    //判断是否为string文件
                    if (nodeKeyFile) {
                        //dom解析读取文件并执行添加
                        addStringByDom(files[0], key, value);
                    }else{
                        System.out.println("当前英文目录存放的不是string文件: " + files.length + " ," + files[0].getPath());
                    }

                    if (files.length > 1) {
                        System.out.println("当前英文目录不止一个文件: " + files.length + " ," + file1.getPath());
                    }
                } else {
                    createForiegnFile(englishDir, fileName, key, value);
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
    private void createForiegnFile(String englishDir, String fileName, String key, String value) {
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
                createStringByDom(fileEn,key,value);
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
    private void createStringByDom(File file, String key, String value) {
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
    private void addStringByDom(File file, String key1, String value1) {
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
     * 比较android有中文无英文 list 与iOS list 中文匹配上 即替换为英文对象
     * @param cnNoEn
     * @param stringiOSBeans
     * @return List<StringXmlBean> 此时list中对象的value已为英文 此list为android英文对象list 而路径还是android中文路径
     */
    private List<StringXmlBean> compareAndroidAndiOS(List<StringXmlBean> cnNoEn, List<StringiOSBean> stringiOSBeans) {
        List<StringXmlBean> enList = new ArrayList<>();

        for (StringXmlBean androidBean : cnNoEn) {
            //去标点再匹配
            String androidOriginStr = InternationalDocUtils.myFormatUtil(androidBean.getValue());
            for (StringiOSBean iOSBean : stringiOSBeans) {
                //去标点再匹配
                String iOSOriginStr = InternationalDocUtils.myFormatUtil(iOSBean.getCnString());
                if (androidOriginStr.equals(iOSOriginStr)) {
                    //将对象value变为ios英文
                    String foreignString = iOSBean.getForeignString();
                    String androidForeignString = foreignString.replace("%@", "%s");
                    androidBean.setValue(androidForeignString);
                    enList.add(androidBean);
                }
            }
        }
        return enList;
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

                    StringXmlBean stringXmlBean = new StringXmlBean();
                    String path = file.getPath();
                    String subPath = "";
                    if (path.contains(fileUnuseName)) {
                        subPath = path.substring(fileUnuseName.length(), path.length());
                    }
                    stringXmlBean.setFileName(subPath);
                    stringXmlBean.setKey(key);
                    stringXmlBean.setValue(value);
                    list.add(stringXmlBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 拆分iOS文档到List<String>集合中
     * 文档内容以行的形式读取  “中文” = “英文”
     *
     * @param filePath iOS文档路径
     * @return List<StringiOSBean>
     */
    public static List<StringiOSBean> splitFileStrByLine2List(String filePath) {
        List<StringiOSBean> iOSList = new ArrayList<>();
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
                        System.out.println("ios文件中的异常line无等于号的行: " + line);
                        line = br.readLine();
                        continue;
                    }
                    split[0] = split[0].trim();
                    split[1] = split[1].trim();
                    if (!split[0].startsWith("\"")) {
                        System.out.println("ios文件中的异常line 中文部分不是已 “ 开头的中文行: " + line);
                    }
                    if (!split[1].startsWith("\"")) {
                        System.out.println("ios文件中的异常line 英文*****不是已 “ 开头的非中文行:" + line);
                    }
                    split[0] = split[0].substring(1, split[0].lastIndexOf("\""));
                    split[1] = split[1].substring(1, split[1].lastIndexOf("\""));

                    StringiOSBean bean = new StringiOSBean();
                    bean.setCnString(split[0]);
                    bean.setForeignString(split[1]);
                    iOSList.add(bean);
                } else {
                    if (line != null && !"".equals(line) && line.length() != 0) {
                        System.out.println("直接打印不包含=的line: " + line);
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
        return iOSList;
    }
}
