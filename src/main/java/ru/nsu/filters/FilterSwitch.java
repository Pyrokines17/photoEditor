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
            case TEST: {
                HashMap<String, String> types = new HashMap<>();
                types.put("test1", "int");
                types.put("test2", "double");
                types.put("enum1", "int");
                types.put("enum2", "double");
                HashMap<String, String> borders = new HashMap<>();
                borders.put("test1", "1|10");
                borders.put("test2", "1.0|10.0");
                borders.put("enum1", "1,3,7,14");
                borders.put("enum2", "1.5,33.01,0.0");
                return new Parameters(types, borders);
            }
            case NEGATIVE, GRAYSCALE, ORDERED_DITHERING, FSDITHERING:
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
            case FSDITHERING -> new FSDither(parameters);
            case TEST -> new TestFilter(parameters);
        };
    }
}
