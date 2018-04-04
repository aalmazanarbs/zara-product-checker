package com.gade.zaraproductchecker.model;

import java.net.MalformedURLException;
import java.net.URL;

public class ProductData {

    private String APIId;
    private String name;
    private URL imageURL;

    public String getAPIId() {
        return APIId;
    }

    public void setAPIId(String APIId) {
        this.APIId = APIId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public void setImageURLFromString(final String url) {
        try {
            this.imageURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
