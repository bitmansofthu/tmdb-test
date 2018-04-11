package io.supercharge.tmdb.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by aquajava on 2018. 04. 11..
 */

public class Utils {

    private static NumberFormat currencyFormat;

    static {
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormat.setCurrency(Currency.getInstance(Locale.US));
    }

    public static final String formatCurrency(double currency) {
        return currencyFormat.format(currency);
    }

    public static final String getFullPosterPath(String path, int width) {
        // supported sizes: "w92", "w154", "w185", "w342", "w500", "w780"
        return String.format("http://image.tmdb.org/t/p/w%d/%s", 185, path);
    }

}
