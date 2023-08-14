package com.android.bindreading.gson;

public class Result {
    private int origin_width;
    private int origin_height;
    private ImageInfo[] image_list;

    public int getOriginWidth() {
        return origin_width;
    }

    public int getOriginHeight() {
        return origin_height;
    }

    public ImageInfo[] getImageList() {
        return image_list;
    }
}
