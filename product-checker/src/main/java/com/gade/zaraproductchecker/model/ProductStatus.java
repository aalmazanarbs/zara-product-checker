package com.gade.zaraproductchecker.model;

public class ProductStatus {

    private String APIId;
    private String price;
    private String color;
    private String size;
    private String availability;

    public ProductStatus() {
    }

    public ProductStatus(String APIId, String price, String color, String size, String availability) {
        this.APIId = APIId;
        this.price = price;
        this.color = color;
        this.size = size;
        this.availability = availability;
    }

    public String getAPIId() {
        return APIId;
    }

    public void setAPIId(String APIId) {
        this.APIId = APIId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
