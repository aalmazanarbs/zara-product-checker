package com.gade.zaraproductchecker;

import java.util.Locale;

public class UIFormatter {

    public static String productPrice (String price) {
        double decimalPrice = Double.parseDouble(price) / 100;

        if (decimalPrice == (long) decimalPrice) {
            return String.format(Locale.getDefault(),"%d €", (long) decimalPrice);
        } else {
            return String.format("%s €", decimalPrice);
        }
    }

    public static String productAvailability (String size) {
        String humanReadableSize;
        switch (size) {
            case "out_of_stock":
                humanReadableSize = "Out of Stock";
                break;
            case "back_soon":
                humanReadableSize = "Back Soon";
                break;
            case "in_stock":
                humanReadableSize = "In Stock";
                break;
            case "coming_soon":
                humanReadableSize = "Coming Soon";
                break;
            default:
                humanReadableSize = "Unknown";
                break;
        }

        return humanReadableSize;
    }
}
