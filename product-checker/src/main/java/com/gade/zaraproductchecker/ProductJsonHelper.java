package com.gade.zaraproductchecker;

import com.gade.zaraproductchecker.model.ProductData;
import com.gade.zaraproductchecker.model.ProductStatus;
import com.gade.zaraproductchecker.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gade.zaraproductchecker.ApiHelper.buildProductImageURL;

public final class ProductJsonHelper {

    @SuppressWarnings("unused")
    public static List<String> getSizesFromFromJSONString(final String jsonResponse) {
        return getJSONObjectFromString(jsonResponse)
                .map(productJSON -> {
                    final JSONArray productSizes = getJSONArrayColorsFromFromJSONObject(productJSON).getJSONObject(0).getJSONArray("sizes");
                    return jsonArrayToNameList(productSizes);
                })
                .orElseGet(Collections::emptyList);
    }

    @SuppressWarnings("unused")
    public static List<String> getColorsFromFromJSONString(final String jsonResponse) {
        return getJSONObjectFromString(jsonResponse)
                .map(productJSON -> jsonArrayToNameList(getJSONArrayColorsFromFromJSONObject(productJSON)))
                .orElseGet(Collections::emptyList);
    }

    @SuppressWarnings("unused")
    public static Optional<ProductData> getProductDataFromJSONString(final String jsonResponse) {
        return getJSONObjectFromString(jsonResponse)
                .map(productJSON -> {
                    final ProductData.Builder productDataBuilder = ProductData.builder()
                            .withApiId(productJSON.getString("id"))
                            .withName(productJSON.getString("name"));
                    addProductImageUrlsFromFromJSONObject(productJSON, productDataBuilder);
                    return productDataBuilder.build();
                });
    }

    @SuppressWarnings("unused")
    public static List<ProductStatus> getProductStatuses(final String jsonResponse) {
        return getJSONArrayFromString(jsonResponse)
                .filter(jsonArray -> jsonArray.length() > 0)
                .map(ProductJsonHelper::getProductStatusesFromProductJsonArray)
                .orElseGet(Collections::emptyList);
    }

    private static List<ProductStatus> getProductStatusesFromProductJsonArray(final JSONArray productJsonArray) {
        return convertJSONArrayToJSONObjectList(productJsonArray).stream().flatMap(productJSONObject -> {
            final String productID = productJSONObject.getString("id");
            final JSONArray productColorsJSONArray = getJSONArrayColorsFromFromJSONObject(productJSONObject);

            return convertJSONArrayToJSONObjectList(productColorsJSONArray).stream().flatMap(productColorJSONObject -> {
                final String productColor = productColorJSONObject.getString("name");
                final JSONArray productSizesJSONArray = productColorJSONObject.getJSONArray("sizes");

                return convertJSONArrayToJSONObjectList(productSizesJSONArray).stream().map(productSizeJSONObject -> {
                    final String productPrice = productSizeJSONObject.getString("price");
                    final String productSize = productSizeJSONObject.getString("name");
                    final String productAvailability = productSizeJSONObject.getString("availability");

                    final ProductStatus productStatus = new ProductStatus();
                    productStatus.setAPIId(productID);
                    productStatus.setColor(productColor);
                    productStatus.setSize(productSize);
                    productStatus.setPrice(productPrice);
                    productStatus.setAvailability(productAvailability);
                    return productStatus;
                });
            });
        }).collect(Collectors.toList());
    }

    private static List<String> jsonArrayToNameList(final JSONArray jsonArray) {
        return convertJSONArrayToJSONObjectList(jsonArray).stream()
                .map(jsonObject -> jsonObject.getString("name"))
                .filter(StringUtil::isNotEmpty)
                .collect(Collectors.toList());
    }

    private static List<JSONObject> convertJSONArrayToJSONObjectList(final JSONArray jsonArray) {
        final List<JSONObject> jsonObjects = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObjects.add(jsonArray.getJSONObject(i));
        }
        return jsonObjects;
    }

    private static Optional<JSONObject> getJSONObjectFromString(final String jsonResponse) {
        return getJSONArrayFromString(jsonResponse)
                .filter(jsonArray -> jsonArray.length() == 1)
                .map(jsonArray -> jsonArray.getJSONObject(0));
    }

    private static Optional<JSONArray> getJSONArrayFromString(final String jsonResponse) {
        try {
            return Optional.of((JSONArray) new JSONTokener(jsonResponse).nextValue());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static JSONArray getJSONArrayColorsFromFromJSONObject(final JSONObject productJSON) {
        return productJSON.getJSONObject("detail").getJSONArray("colors");
    }

    private static void addProductImageUrlsFromFromJSONObject(final JSONObject productJSON, final ProductData.Builder productDataBuilder) {
        convertJSONArrayToJSONObjectList(getJSONArrayColorsFromFromJSONObject(productJSON)).forEach(productColorJSONObject -> {
            final JSONObject firstXMedia = productColorJSONObject.getJSONArray("xmedia").getJSONObject(0);
            productDataBuilder.addImage(
                    productColorJSONObject.getString("name"),
                    buildProductImageURL(firstXMedia.getString("path"),
                                         firstXMedia.getString("name"),
                                         firstXMedia.getString("timestamp")));
        });
    }
}
