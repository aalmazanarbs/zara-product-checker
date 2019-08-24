package com.gade.zaraproductchecker.model;

import java.net.URI;
import java.net.URISyntaxException;

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

    @SuppressWarnings("unused")
    public String getApiId() {
        return apiId;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
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

        private Builder() { }

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
}
