package com.international.listener;

import java.io.File;
import java.util.List;

public interface SpecialKeyInXmlListener<T> {
    /**
     * 通过特殊的key过滤数据
     * 具体的过滤规则看具体的实现
     * @param data 待过滤的数据
     * @return 过滤结果 true 需要的data， false 不需要的data
     */
//    boolean neededData(String... data);

    /**
     * 单个xml文件过滤规则
     * @param file 单个xml string文件
     * @return 返回指定泛型的list
     */
    List<T> filterData(File file);
}
