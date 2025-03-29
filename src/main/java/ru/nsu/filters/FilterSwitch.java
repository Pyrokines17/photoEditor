package ru.nsu.filters;

import java.util.HashMap;

public class FilterSwitch {
    public static Parameters getParameters(FilterList filter) {
        switch (filter) {
            case GAMMA: {
                HashMap<String, String> types = new HashMap<>();
                types.put("gamma", "double");
                HashMap<String, String> borders = new HashMap<>();
                borders.put("gamma", "0.1|10.0");
                return new Parameters(types, borders);
            }
            case NEGATIVE, GRAYSCALE, ORDERED_DITHERING:
            default:
                return new Parameters(null, null);
        }
    }

    public static Filter getFilter(FilterList filter, Parameters parameters) {
        return switch (filter) {
            case NEGATIVE -> new Negative(parameters);
            case GRAYSCALE -> new Grayscale(parameters);
            case GAMMA -> new Gamma(parameters);
            case ORDERED_DITHERING -> new OrderedDither(parameters);
        };
    }
}
