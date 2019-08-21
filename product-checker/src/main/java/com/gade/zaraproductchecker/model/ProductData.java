package com.gade.zaraproductchecker.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.gade.zaraproductchecker.util.StringUtil.isNotEmpty;

public final class ProductData {

    private final String apiId;
    private final String name;
    private final String imageUrl;

    private ProductData(final Builder builder) {
        apiId = builder.apiId;
        name = builder.name;
        imageUrl = builder.imageUrl;
    }

    public String getApiId() {
        return apiId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String apiId;
        private String name = "No name founded";
        private String imageUrl;

        private Builder() {
        }

        public Builder withApiId(final String apiId) {
            this.apiId = apiId;
            return this;
        }

        public Builder withName(final String name) {
            if (isNotEmpty(name)) {
                this.name = name;
            }
            return this;
        }

        public Builder withImageUrl(final String imageUrl) {
            try {
                this.imageUrl = new URI(imageUrl).toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return this;
        }

        public ProductData build() {
            return new ProductData(this);
        }
    }

    /*@SuppressWarnings("unused")
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
    }*/


}
