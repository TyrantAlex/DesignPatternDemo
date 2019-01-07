package com.international.fourth;

import com.international.listener.SpecialKeyInXmlListener;
import com.international.repeat.vo.StringXmlBean;
import com.international.util.InternationalFileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 检出项目中泰文文件中的英文字符
 * 通过预留的xml标签检出中间的内容
 * <string name="dfire_temporary_translation_cannot_delete">
 * <string name="dfire_temporary_translation_cannot_delete_end">
 */
public class CheckThaiStrByXmlRoot {
    /**
     * 项目对应位置
     */
    public static final String PROJECT_PATH = "/Users/shen/Develop/2dfire/project/checkout_1";

    public static final String OUTPUT_PATH = "/Users/shen/Documents/2dfire/international/2019-01-03.txt";

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

    /**
     * 待过滤出来的的特殊字符标记
     */
    private static final String SPECIAL_KEY_START = "dfire_temporary_translation_cannot_delete";
    private static final String SPECIAL_KEY_END = "dfire_temporary_translation_cannot_delete_end";

    public static void main(String[] args) {
        CheckThaiStrByXmlRoot check = new CheckThaiStrByXmlRoot();
        System.out.println("------------------------------------START------------------------------");
        check.start();
        System.out.println("------------------------------------END---------------------------------");
    }

    private void start() {
        //将android中文取出组成list
        checkOnMultiFolder(PROJECT_PATH, DIRECT_FOLDER_VALUES, null);
        System.out.println("所有中文字符数量: " + allList.size());
        List<StringXmlBean> cnList = new ArrayList<>();
        cnList.addAll(allList);
        allList.clear();

        //查询项目目录中所有的泰文字符与key
        checkOnMultiFolder(PROJECT_PATH, DIRECT_FOLDER_VALUES_TH, specialKeyInXmlListener);
        System.out.println("所有泰文字符数量: " + allList.size());
        List<StringXmlBean> thList = new ArrayList<>();
        thList.addAll(allList);
        allList.clear();

        compareAndOutPut(cnList, thList);
//        for (StringXmlBean bean: allList) {
//            System.out.println("path: " + bean.getFileName() + ", key ：" + bean.getKey() + ", value: " + bean.getValue());
//        }
    }

    /**
     * 比较并输出
     * @param cnList
     * @param thList
     */
    private void compareAndOutPut(List<StringXmlBean> cnList, List<StringXmlBean> thList) {
        List<StringXmlBean> tempList = new ArrayList<>();
        for (StringXmlBean thBean : thList) {
            String thkey = thBean.getKey();
            boolean isUnUse = true;
            for (StringXmlBean cnBean : cnList) {
                String cnkey = cnBean.getKey();
                String cnValue = cnBean.getValue();
                if (thkey.equals(cnkey)) {
                    isUnUse = false;
                    thBean.setValue(cnValue);
                    break;
                }
            }
            if (!isUnUse) {
                tempList.add(thBean);
            }
        }
        //输出
        List<String> outputlist = new ArrayList<>();

        List<StringXmlBean> noRepeatList = deRepeat(tempList);

        for (StringXmlBean bean : noRepeatList) {
            outputlist.add(bean.getValue());
        }

        //写入文件
        File file = new File(OUTPUT_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            InternationalFileUtils.fileWriter(file, outputlist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * list去重
     * @param beanList
     * @return
     */
    private List<StringXmlBean> deRepeat(List<StringXmlBean> beanList) {
        //去重
        for (int i = 0; i < beanList.size() - 1; i++) {
            for (int j = beanList.size() - 1; j > i; j--) {
                if ((beanList.get(j).getKey().equals(beanList.get(i).getKey()) && beanList.get(j).getFileName().equals(beanList.get(i).getFileName())) || beanList.get(j).getValue().equals(beanList.get(i).getValue())) {
                    System.out.println("需要翻译的泰文现在是英文且重复的 : " + beanList.get(i).getKey());
                    beanList.remove(j);
                }
            }
        }
        System.out.println("去重后的 需要补充的国际化字符数量: " + beanList.size());
        return beanList;
    }

    private SpecialKeyInXmlListener<StringXmlBean> specialKeyInXmlListener = new SpecialKeyInXmlListener<StringXmlBean>() {
        @Override
        public List<StringXmlBean> filterData(File file) {
            System.out.println("输入一下当前正在解析的xml: " + file.getPath());
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
                //待记录的position
                int numNeededStart = 0;
                int numNeededEnd = 0;
                //找出范围
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node1 = childNodes.item(i);
                    if ("string".equals(node1.getNodeName())) {
                        // 键
                        String key = node1.getAttributes().getNamedItem("name")
                                .getNodeValue();

                        if (SPECIAL_KEY_START.equals(key)) {
                            numNeededStart = i;
                            System.out.println("SPECIAL_KEY_START: " + i);
                        }
                        if (SPECIAL_KEY_END.equals(key)) {
                            numNeededEnd = i;
                            System.out.println("SPECIAL_KEY_END: " + i);
                        }
                    }
                }
                for (int i = numNeededStart + 1; i < numNeededEnd; i++) {
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
    };

    /**
     * 查找文件
     * 读取一个路径下多个文件夹的指定文件
     * return 当前文件夹下所有符合的数据list集合
     */
    private void checkOnMultiFolder(String baseDirName, String directFoldString, SpecialKeyInXmlListener<StringXmlBean> listener) {
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
                checkOnMultiFolder(tempFile.getAbsolutePath(), directFoldString, listener);
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
                        List<StringXmlBean> beanList = splitStringXml2Map(tempFile, listener);
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
     * @param listener 具体实现的接口
     * @return List<StringXmlBean> xml文件转list集合
     */
    private List<StringXmlBean> splitStringXml2Map(File file, SpecialKeyInXmlListener<StringXmlBean> listener) {
        if (!file.exists()) {
            System.out.println("splitStringXml2Map 时 文件不存在 " + file.getPath());
            return null;
        }
        /**
         * 加入过滤规则
         */
        if (listener != null) {
            return listener.filterData(file);
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
