package com.gade.zaraproductchecker.model;

public final class ProductStatus {

    private String apiId;
    private String price;
    private String color;
    private String size;
    private String availability;

    @SuppressWarnings("unused")
    public String getApiId() {
        return apiId;
    }

    public void setAPIId(String APIId) {
        this.apiId = APIId;
    }

    @SuppressWarnings("unused")
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @SuppressWarnings("unused")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @SuppressWarnings("unused")
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @SuppressWarnings("unused")
    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
