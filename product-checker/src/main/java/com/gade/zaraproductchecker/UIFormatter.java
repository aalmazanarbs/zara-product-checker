package com.gade.zaraproductchecker;

import java.util.Locale;

public final class UIFormatter {

    private static final String OUT_OF_STOCK = "out_of_stock";
    private static final String OUT_OF_STOCK_DISPLAY = "Out of Stock";
    private static final String BACK_SOON = "back_soon";
    private static final String BACK_SOON_DISPLAY = "Back Soon";
    private static final String IN_STOCK = "in_stock";
    private static final String IN_STOCK_DISPLAY = "In Stock";
    private static final String COMING_SOON = "coming_soon";
    private static final String COMING_SOON_DISPLAY = "Coming Soon";
    private static final String UNKNOWN_DISPLAY = "Unknown";

    @SuppressWarnings("unused")
    public static String productPrice(final String price) {
        final double decimalPrice = Double.parseDouble(price) / 100;

        if (decimalPrice == (long) decimalPrice) {
            return String.format(Locale.getDefault(),"%d €", (long) decimalPrice);
        } else {
            return String.format("%s €", decimalPrice);
        }
    }

    @SuppressWarnings("unused")
    public static String productAvailability(final String size) {
        switch (size) {
            case OUT_OF_STOCK:
                return OUT_OF_STOCK_DISPLAY;
            case BACK_SOON:
                return BACK_SOON_DISPLAY;
            case IN_STOCK:
                return IN_STOCK_DISPLAY;
            case COMING_SOON:
                return COMING_SOON_DISPLAY;
            default:
                return UNKNOWN_DISPLAY;
        }
    }
}
