package com.gade.zaraproductchecker;

import com.gade.zaraproductchecker.model.ProductData;
import com.gade.zaraproductchecker.model.ProductStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class ProductJSONHelper {

    public static List<String> getSizesFromFromJSONString(final String JSONResponse) {
        final JSONObject productJSON = getJSONObjectFromString(JSONResponse);
        if (productJSON == null) {
            return null;
        }

        final JSONArray productSizes = getJSONArrayColorsFromFromJSONObject(productJSON).getJSONObject(0).getJSONArray("sizes");

        final List<String> sizes = new ArrayList<>();
        for (int i = 0; i < productSizes.length(); i++) {
            final String size = productSizes.getJSONObject(i).getString("name");
            if (size != null && !size.isEmpty()) {
                sizes.add(size);
            }
        }

        return sizes;
    }

    public static List<String> getColorsFromFromJSONString(final String JSONResponse) {
        final JSONObject productJSON = getJSONObjectFromString(JSONResponse);
        if (productJSON == null) {
            return null;
        }

        final JSONArray productColors = getJSONArrayColorsFromFromJSONObject(productJSON);

        final List<String> colors = new ArrayList<>();
        for (int i = 0; i < productColors.length(); i++) {
            final String color = productColors.getJSONObject(i).getString("name");
            if (color != null && !color.isEmpty()) {
                colors.add(color);
            }
        }

        return colors;
    }

    public static ProductData getProductDataFromJSONString(final String JSONResponse) {
        final JSONObject productJSON = getJSONObjectFromString(JSONResponse);
        if (productJSON == null) {
            return null;
        }

        final ProductData productData = new ProductData();
        productData.setAPIId(productJSON.getString("id")); // May contains different ID from URL ID

        final String name = productJSON.getString("name");
        if (name == null || name.isEmpty()) {
            productData.setName("No name founded");
        } else {
            productData.setName(name);
        }

        final JSONObject firstXMedia = productJSON.getJSONArray("xmedia").getJSONObject(0);
        productData.setImageURLFromString(APIHelper.buildProductImageURL(firstXMedia.getString("path"),
                                                                         firstXMedia.getString("name"),
                                                                         firstXMedia.getString("timestamp")));

        return productData;
    }

    public static List<ProductStatus> getProductStatuses(String JSONResponse) {
        if (JSONResponse == null || JSONResponse.isEmpty()) {
            return null;
        }

        JSONArray arrayResponse = (JSONArray) new JSONTokener(JSONResponse).nextValue();
        if (arrayResponse.length() < 1) {
            return null;
        }

        final List<ProductStatus> productsStatuses = new ArrayList<>();

        for (int i = 0; i < arrayResponse.length(); i++) {
            JSONObject productJSON = arrayResponse.getJSONObject(i);
            String productID = productJSON.getString("id");

            JSONArray productColors = productJSON.getJSONObject("detail").getJSONArray("colors");
            for (int j = 0; j < productColors.length(); j++) {
                String productColor = productColors.getJSONObject(j).getString("name");

                JSONArray productSizes = productColors.getJSONObject(j).getJSONArray("sizes");
                for (int k = 0; k < productSizes.length(); k++) {
                    final String productPrice = productSizes.getJSONObject(k).getString("price");
                    final String productSize = productSizes.getJSONObject(k).getString("name");
                    final String productAvailability = productSizes.getJSONObject(k).getString("availability");

                    final ProductStatus productStatus = new ProductStatus();
                    productStatus.setAPIId(productID);
                    productStatus.setColor(productColor);
                    productStatus.setSize(productSize);
                    productStatus.setPrice(productPrice);
                    productStatus.setAvailability(productAvailability);

                    productsStatuses.add(productStatus);
                }
            }
        }

        return productsStatuses;
    }

    private static JSONObject getJSONObjectFromString(final String JSONResponse) {
        if (JSONResponse == null || JSONResponse.isEmpty()) {
            return null;
        }

        final JSONArray arrayResponse;
        try {
            arrayResponse = (JSONArray) new JSONTokener(JSONResponse).nextValue();
        } catch (Exception e) {
            return null;
        }

        if (arrayResponse.length() != 1) {
            return null;
        }

        return arrayResponse.getJSONObject(0);
    }

    private static JSONArray getJSONArrayColorsFromFromJSONObject(final JSONObject productJSON) {
        return productJSON.getJSONObject("detail").getJSONArray("colors");
    }
}
