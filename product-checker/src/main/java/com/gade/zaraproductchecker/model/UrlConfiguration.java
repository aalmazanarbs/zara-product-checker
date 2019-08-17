package com.gade.zaraproductchecker.model;

public final class UrlConfiguration {

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

    @SuppressWarnings("unused")
    public void setAPIBase(String aPIBase) {
        this.aPIBase = aPIBase;
    }

    public String getAPIParam() {
        return aPIParam;
    }

    @SuppressWarnings("unused")
    public void setAPIParam(String aPIParam) {
        this.aPIParam = aPIParam;
    }

    public String getAPISuffix() {
        return aPISuffix;
    }

    @SuppressWarnings("unused")
    public void setAPISuffix(String aPISuffix) {
        this.aPISuffix = aPISuffix;
    }

    public String getImageBase() {
        return imageBase;
    }

    @SuppressWarnings("unused")
    public void setImageBase(String imageBase) {
        this.imageBase = imageBase;
    }

    public String getImageSize() {
        return imageSize;
    }

    @SuppressWarnings("unused")
    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    @SuppressWarnings("unused")
    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getImageTs() {
        return imageTs;
    }

    @SuppressWarnings("unused")
    public void setImageTs(String imageTs) {
        this.imageTs = imageTs;
    }
}
