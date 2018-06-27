package com.international.thailand.vo;

/**
 * 占位符的类
 * @author : hongshen
 * @Date: 2018/6/25 0025
 */
public class PlaceHolder implements Comparable<PlaceHolder>{

    /**
     * 占位符
     */
    private String placeHolder;

    /**
     * 占位符所在下标
     */
    private int placeHolderIndex;

    public PlaceHolder(String placeHolder, int placeHolderIndex) {
        this.placeHolder = placeHolder;
        this.placeHolderIndex = placeHolderIndex;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public int getPlaceHolderIndex() {
        return placeHolderIndex;
    }

    public void setPlaceHolderIndex(int placeHolderIndex) {
        this.placeHolderIndex = placeHolderIndex;
    }

    @Override
    public int compareTo(PlaceHolder placeHolder) {
        return this.placeHolderIndex - placeHolder.getPlaceHolderIndex();
    }
}
