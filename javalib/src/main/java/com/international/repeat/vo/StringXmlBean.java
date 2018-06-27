package com.international.repeat.vo;

/**
 * String每一条数据信息vo
 * @author : hongshen
 * @Date: 2018/4/27 0027
 */
public class StringXmlBean{

    private String key;

    private String value;

    private String fileName;

    private String formatted;

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
