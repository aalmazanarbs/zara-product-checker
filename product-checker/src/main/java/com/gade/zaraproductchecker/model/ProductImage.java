package com.gade.zaraproductchecker.model;

public class ProductImage {

    private final String color;
    private final String url;

    public ProductImage(final String color, final String url) {
        this.color = color;
        this.url = url;
    }

    public String getColor() {
        return color;
    }

    public String getUrl() {
        return url;
    }
}
