package com.gade.zaraproductchecker;

import com.gade.zaraproductchecker.model.ProductStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ApiHelper {

    // Check url like https://www.zara.com/es/es/blusa-bordado-perforado-p07521067.html?v1=5671069&v2=805003
    private static final Pattern ZARA_URL_PATTERN = Pattern.compile("\\w*\\\\?v1=([0-9]{1,20})");

    @SuppressWarnings("unused")
    public static Optional<ProductStatus> searchProductStatus(final List<ProductStatus> productStatuses, final String APIId, final String size, final String color) {
        return productStatuses.stream()
                .filter(productStatus -> productStatus.getApiId().equals(APIId) &&
                        productStatus.getSize().equals(size) &&
                        productStatus.getColor().equals(color))
                .findFirst();
    }

    static Optional<String> getProductIDFromURL(final String url) {
        return getValidURL(url).flatMap(__ -> {
            final Matcher zaraUrlMatcher = ZARA_URL_PATTERN.matcher(url);
            if (zaraUrlMatcher.find() && zaraUrlMatcher.groupCount() == 1 && !zaraUrlMatcher.group(1).isEmpty()) {
                return Optional.of(zaraUrlMatcher.group(1));
            }

            return Optional.empty();
        });
    }

    static String buildProductInfoAPIURL(final Set<String> productIDs) {
        final StringBuilder url = new StringBuilder(UrlConfigurationLoader.get().getAPIBase());
        final Iterator<String> productIDsIterator = productIDs.iterator();
        if (productIDsIterator.hasNext()) {
            url.append(UrlConfigurationLoader.get().getAPIParam()).append(productIDsIterator.next());
        }

        while (productIDsIterator.hasNext()) {
            url.append("&").append(UrlConfigurationLoader.get().getAPIParam()).append(productIDsIterator.next());
        }

        url.append(UrlConfigurationLoader.get().getAPISuffix());

        return url.toString();
    }

    static String buildProductImageURL(final String path, final String name, final String timestamp) {
        return UrlConfigurationLoader.get().getImageBase() +
               path +
               UrlConfigurationLoader.get().getImageSize() +
               name +
               UrlConfigurationLoader.get().getImageFormat() +
               UrlConfigurationLoader.get().getImageTs() +
               timestamp;
    }

    static Optional<URL> getValidURL(final String url) {
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }
}
