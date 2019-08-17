package com.gade.zaraproductchecker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public final class ProductApi {

    @SuppressWarnings("unused")
    public static Optional<String> doCall(final String productURL) {
        return ApiHelper.getProductIDFromURL(productURL).flatMap(productID -> doCall(Stream.of(productID).collect(toSet())));
    }

    @SuppressWarnings("unused")
    public static Optional<String> doCall(final Set<String> productIDs) {
        if (productIDs.size() < 1) {
            return Optional.empty();
        }

        return ApiHelper.getValidURL(ApiHelper.buildProductInfoAPIURL(productIDs)).flatMap(ProductApi::internalCall);
    }

    private static Optional<String> internalCall(final URL url) {
        try {
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            try {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                final StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();

                return Optional.of(response.toString());
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
