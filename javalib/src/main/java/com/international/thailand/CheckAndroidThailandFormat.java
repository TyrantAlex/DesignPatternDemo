package com.international.thailand;

import com.international.repeat.vo.StringXmlBean;
import com.international.thailand.vo.PlaceHolder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 检测有问题的format字段
 * 未完成
 * @author : hongshen
 * @Date: 2018/7/5 0005
 */
public class CheckAndroidThailandFormat {

    /**
     * 项目对应位置
     */
    public static final String PROJECT_PATH = "D:/AndroidStudio/AndroidProject/Checkout3";

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

    public static void main(String[] args) {
        CheckAndroidThailandFormat check = new CheckAndroidThailandFormat();
        System.out.println("------------------------------------START------------------------------");
        check.start();
        System.out.println("------------------------------------END---------------------------------");
    }

    private void start() {
        //取出所有有两个或两个以上format字段的字符

        //查询项目目录中所有的泰文字符与key
        checkOnMultiFolder(PROJECT_PATH, DIRECT_FOLDER_VALUES_TH);

        scanFormatStr();
    }

    private void scanFormatStr() {
        String[] palceHolderAllArray = {"%s","%@","%d","%x","%o","%f","%a","%e","%g","%n","%%","%ld","%lu","%zd","%1$s","%2$s","%3$s"
                ,"%4$s","%1d","%2d","%3d","%4d","%.1f","%.2f","%.3f","%.4f","%1s","%2s","%3s","%4s"};

        for (StringXmlBean bean : allList) {
            String androidTH = bean.getValue();
            List<PlaceHolder> androidTHPlaceHolderList = new ArrayList<>();
            for (int i = 0; i < palceHolderAllArray.length; i++) {
                androidTHPlaceHolderList = indexOfPlaceHolder(androidTHPlaceHolderList, androidTH, palceHolderAllArray[i]);
            }
            if (androidTHPlaceHolderList.size() >= 2) {
                System.out.println("th key = " + bean.getKey());
                System.out.println("th value = " + bean.getValue());
                System.out.println("th path = " + bean.getFileName());
                System.out.println("");
            }
        }

    }

    /**
     * 将原始字符串对应占位符信息导入list中
     * @param list
     * @param originStr
     * @param placeHolder
     * @return
     */
    private List<PlaceHolder> indexOfPlaceHolder(List<PlaceHolder> list, String originStr, String placeHolder) {
        int i1 = originStr.indexOf(placeHolder);
        if (i1 != -1) {
            list.add(new PlaceHolder(placeHolder, i1));
        }
        while (i1 != -1){
            i1 = originStr.indexOf(placeHolder, i1 + placeHolder.length());
            if (i1 != -1) {
                list.add(new PlaceHolder(placeHolder, i1));
            }
        }
        return list;
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
