package com.gade.zaraproductchecker.model;

public class URLConfiguration {

    private String aPIBase;
    private String aPIParam;
    private String aPISuffix;

    private String imageBase;
    private String imageSize;
    private String imageFormat;
    private String imageTs;

    public String getAPIBase() {
        return aPIBase;
    }

    public void setAPIBase(String aPIBase) {
        this.aPIBase = aPIBase;
    }

    public String getAPIParam() {
        return aPIParam;
    }

    public void setAPIParam(String aPIParam) {
        this.aPIParam = aPIParam;
    }

    public String getAPISuffix() {
        return aPISuffix;
    }

    public void setAPISuffix(String aPISuffix) {
        this.aPISuffix = aPISuffix;
    }

    public String getImageBase() {
        return imageBase;
    }

    public void setImageBase(String imageBase) {
        this.imageBase = imageBase;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getImageTs() {
        return imageTs;
    }

    public void setImageTs(String imageTs) {
        this.imageTs = imageTs;
    }
}
