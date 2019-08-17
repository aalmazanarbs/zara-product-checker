package com.gade.zaraproductchecker.model;

import java.net.MalformedURLException;
import java.net.URL;

public final class ProductData {

    private String APIId;
    private String name;
    private URL imageURL;

    @SuppressWarnings("unused")
    public String getAPIId() {
        return APIId;
    }

    public void setAPIId(String APIId) {
        this.APIId = APIId;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
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
