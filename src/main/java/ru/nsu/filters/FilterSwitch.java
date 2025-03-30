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
            case GAUSSIAN_FILTER: {
                HashMap<String, String> types = new HashMap<>();
                types.put("window size", "int");
                HashMap<String, String> borders = new HashMap<>();
                borders.put("window size", "3|5");
                return new Parameters(types, borders);
            }
            case SOBEL_HIGHLIGHTING, ROBERTS_HIGHLIGHTING: {
                HashMap<String, String> types = new HashMap<>();
                types.put("sensitivity", "int");
                HashMap<String, String> borders = new HashMap<>();
                borders.put("sensitivity", "0|255");
                return new Parameters(types, borders);
            }
            case NEGATIVE, GRAYSCALE, ORDERED_DITHERING, FSDITHERING, SHARPENING, EMBOSS:
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
            case GAUSSIAN_FILTER -> new GaussianFilter(parameters);
            case SHARPENING -> new Sharpening(parameters);
            case EMBOSS -> new Emboss(parameters);
            case SOBEL_HIGHLIGHTING -> new SobelBorderHighlight(parameters);
            case ROBERTS_HIGHLIGHTING -> new RobertsBorderHighlight(parameters);
        };
    }
}
