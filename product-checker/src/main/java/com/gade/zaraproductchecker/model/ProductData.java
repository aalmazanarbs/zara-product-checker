package com.gade.zaraproductchecker.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.gade.zaraproductchecker.util.StringUtil.isNotEmpty;

public final class ProductData {

    private final String apiId;
    private final String name;
    private final List<ProductImage> productImages;

    private ProductData(final Builder builder) {
        apiId = builder.apiId;
        name = builder.name;
        productImages = builder.productImages;
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
    public String findUrlByColor(final String color) {
        return productImages.stream().filter(productImage -> color.equals(productImage.getColor())).findFirst().get().getUrl();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String apiId;
        private String name = "No name founded";
        private final List<ProductImage> productImages = new ArrayList<>();

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

        public void addImage(final String color, final String imageUrl) {
            try {
                productImages.add(new ProductImage(color, new URI(imageUrl).toString()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        public ProductData build() {
            return new ProductData(this);
        }
    }
}
