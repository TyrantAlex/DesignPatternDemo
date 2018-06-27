package com.international.thailand;

import com.international.thailand.vo.PlaceHolder;
import com.international.util.InternationalDocUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 测试format后字符串是否正常显示
 * @author : hongshen
 * @Date: 2018/6/25 0025
 */
public class TestFormat {

    public static void main(String[] args) {
        TestFormat testFormat = new TestFormat();
//        String testStr = " 这是一段测试字符开始%s%.2fabc%.2f, %.3f  %s  %.2f测试字符结束。 ";
        String testStr = " 这是一段测试字符开始测试字符结束.";
        String s = InternationalDocUtils.myFormatUtil(testStr);
        System.out.println(s);


//        String iosCn = " 这是一段测试字符开始%@%.2fabc%.2f, %lu  %@  %.2f测试字符结束。 ";
//        String iosEn = " %luhahahahabcdef%.2fabc%.2f,   %@  %.2f helloworld。 %@";
        String iosCn = " 这是一段测试字符开始测试字符结束.";
        String iosEn = " abcdsadsadsadasdsadsadasdsadas haha";
        testFormat.dealWithString(testStr,iosCn,iosEn);
    }

    /**
     * 字符串处理 返回android对应ios正确的英文字符
     * @param androidCn
     * @param iOSCn
     * @param iOSEn
     * @return android EN
     */
    private String dealWithString(String androidCn, String iOSCn, String iOSEn) {
        String[] palceHolderAllArray = {"%s","%@","%d","%x","%o","%f","%a","%e","%g","%n","%%","%ld","%lu","%zd","%1$s","%2$s","%3$s"
                ,"%4$s","%1d","%2d","%3d","%4d","%.1f","%.2f","%.3f","%.4f","%1s","%2s","%3s","%4s"};
        String androidEn = iOSEn;
        List<PlaceHolder> androidCnPlaceHolderList = new ArrayList<>();
        List<PlaceHolder> iOSCnHolderList = new ArrayList<>();
        List<PlaceHolder> iOSEnHolderList = new ArrayList<>();
        for (int i = 0; i < palceHolderAllArray.length; i++) {
            androidCnPlaceHolderList = indexOfPlaceHolder(androidCnPlaceHolderList, androidCn, palceHolderAllArray[i]);
            iOSCnHolderList = indexOfPlaceHolder(iOSCnHolderList, iOSCn, palceHolderAllArray[i]);
            iOSEnHolderList = indexOfPlaceHolder(iOSEnHolderList, iOSEn, palceHolderAllArray[i]);
        }
        //排序
        Collections.sort(androidCnPlaceHolderList);
        Collections.sort(iOSCnHolderList);
        Collections.sort(iOSEnHolderList);
//        System.out.println(androidCnPlaceHolderList.size());

        //此时ios中英文list应当是相等的size
        if (iOSCnHolderList.size() != iOSEnHolderList.size()
                || androidCnPlaceHolderList.size() != iOSEnHolderList.size()
                || iOSCnHolderList.size() != androidCnPlaceHolderList.size()) {
            System.out.println("排序后不相等 , ios中文: " + iOSCnHolderList.size() + " : "+iOSCn
                    + ", ios英文: " + iOSEnHolderList.size() + " : "+iOSEn
                    + ", android 中文: " + androidCnPlaceHolderList.size() + " : "+androidCn);
            return androidEn;
        }
        List<PlaceHolder> androidEnPlaceHolder = new ArrayList<>();
        androidEnPlaceHolder.addAll(iOSEnHolderList);
        //开始比较
        for (int i = 0; i < iOSCnHolderList.size(); i++) {
            String androidCNPlaceHolder = androidCnPlaceHolderList.get(i).getPlaceHolder();
            String iosCNPlaceHolder = iOSCnHolderList.get(i).getPlaceHolder();
            for (int j = 0; j < iOSEnHolderList.size(); j++) {
                String enPlaceHolder = iOSEnHolderList.get(j).getPlaceHolder();
                int enIndex = iOSEnHolderList.get(j).getPlaceHolderIndex();
                //遍历时相等则认为可以替换, 替换android的占位符到ios英文
                if (iosCNPlaceHolder.equals(enPlaceHolder)) {

                    androidEn = androidEn.replaceAll(enPlaceHolder, androidCNPlaceHolder);

                    //替换为androidCN的holder iosEN的index下标位置
//                    PlaceHolder holder = new PlaceHolder(androidCNPlaceHolder, enIndex);
//                    androidEnPlaceHolder.set(j, holder);
                }
            }
        }
        System.out.println(androidEn);
//        List<String> iosEnNoPlaceHolderPartList = new ArrayList<>();
////        //头
////        String header = "";
////        header = iOSEn.substring(0, iOSEnHolderList.get(0).getPlaceHolderIndex());
////        if (header != null && header.length() != 0) {
////            iosEnNoPlaceHolderPartList.add(header);
////        }
////        //身体 从第一个占位符到最后一个占位符
////        for (int i = 1; i < iOSEnHolderList.size(); i++) {
////            PlaceHolder hold = iOSEnHolderList.get(i);
////            int placeHolderIndex = hold.getPlaceHolderIndex();
////            String placeHolderStr = hold.getPlaceHolder();
////            String substring = "";
////            //前一个占位符结束位置到后一个占位符开始位置
////            substring = iOSEn.substring(iOSEnHolderList.get(i - 1).getPlaceHolderIndex()
////                    + iOSEnHolderList.get(i - 1).getPlaceHolder().length()
////                    , iOSEnHolderList.get(i).getPlaceHolderIndex());
////            iosEnNoPlaceHolderPartList.add(substring);
////        }
////        //尾
////        String tail = "";
////        tail = iOSEn.substring(iOSEnHolderList.get(iOSEnHolderList.size()-1).getPlaceHolderIndex()
////                + iOSEnHolderList.get(iOSEnHolderList.size()-1).getPlaceHolder().length()
////                , iOSEn.length());
////        if (tail != null && tail.length() != 0) {
////            iosEnNoPlaceHolderPartList.add(tail);
////        }
////
////        androidEn = "";
////        for (int i = 1; i < iosEnNoPlaceHolderPartList.size()-1; i = i + 2) {
////            iosEnNoPlaceHolderPartList.add(i, androidEnPlaceHolder.get());
////        }

//        for (PlaceHolder androidEnHolder : androidEnPlaceHolder) {
//            String placeHolder = androidEnHolder.getPlaceHolder();
//            int placeHolderIndex = androidEnHolder.getPlaceHolderIndex();
//            androidEn = androidEn +
//        }

        return androidEn;
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
}
