package com.gade.zaraproductchecker;

import com.gade.zaraproductchecker.model.ProductStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIHelper {

    static String getProductIDFromURL(String url) {
        String productID = null;

        // Check url like https://www.zara.com/es/es/blusa-bordado-perforado-p07521067.html?v1=5671069&v2=805003
        Pattern p = Pattern.compile("\\w*\\\\?v1=([0-9]{7})");
        Matcher m = p.matcher(url);

        if (m.find() && m.groupCount() == 1) {
            productID = m.group(1);
        }

        return productID;
    }

    static String buildProductInfoAPIURL(final Set<String> productIDs) {
        StringBuilder url = new StringBuilder(URLConfigurationLoader.get().getAPIBase());
        Iterator productIDsIterator = productIDs.iterator();
        if (productIDsIterator.hasNext()) {
            url.append(URLConfigurationLoader.get().getAPIParam()).append(productIDsIterator.next());
        }

        while (productIDsIterator.hasNext()) {
            url.append("&").append(URLConfigurationLoader.get().getAPIParam()).append(productIDsIterator.next());
        }

        url.append(URLConfigurationLoader.get().getAPISuffix());

        return url.toString();
    }

    static String buildProductImageURL(String path, String name, String timestamp) {
        return URLConfigurationLoader.get().getImageBase() +
               path +
               URLConfigurationLoader.get().getImageSize() +
               name +
               URLConfigurationLoader.get().getImageFormat() +
               URLConfigurationLoader.get().getImageTs() +
               timestamp;
    }

    static URL getValidURL(final String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static ProductStatus searchProductStatus(final List<ProductStatus> productStatuses, final String APIId, final String size, final String color) {
        for (final ProductStatus productStatus : productStatuses) {
            if (productStatus.getAPIId().equals(APIId) && productStatus.getSize().equals(size) && productStatus.getColor().equals(color)) {
                return productStatus;
            }
        }

        return null;
    }
}
