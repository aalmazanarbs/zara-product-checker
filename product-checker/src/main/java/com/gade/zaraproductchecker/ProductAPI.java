package com.gade.zaraproductchecker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ProductAPI {

    public static String doCall(final String productURL) {

        if (APIHelper.getValidURL(productURL) == null) {
            return null;
        }

        final String productID = APIHelper.getProductIDFromURL(productURL);

        if (productID == null || productID.isEmpty()) {
            return null;
        }

        Set<String> productIDs = new HashSet<String>() {{ add(productID); }};

        return ProductAPI.doCall(productIDs);
    }

    public static String doCall(final Set<String> productIDs) {
        if (productIDs.size() < 1) {
            return null;
        }

        return ProductAPI.internalCall(APIHelper.getValidURL(APIHelper.buildProductInfoAPIURL(productIDs)));
    }

    private static String internalCall(final URL url) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();

                return response.toString();
            } finally{
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return null;
        }
    }


}
