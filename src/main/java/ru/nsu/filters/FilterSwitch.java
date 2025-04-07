package ru.nsu.filters;

import ru.nsu.filters.iliaDither.FloydSteinbergDither;

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
                borders.put("window size", "3,5,7,9,11");
                return new Parameters(types, borders);
            }
            case SOBEL_HIGHLIGHTING, ROBERTS_HIGHLIGHTING: {
                HashMap<String, String> types = new HashMap<>();
                types.put("sensitivity", "int");
                HashMap<String, String> borders = new HashMap<>();
                borders.put("sensitivity", "0|255");
                return new Parameters(types, borders);
            }
            case FSDITHERING_I, ORDERED_DITHER_I, FSDITHERING, ORDERED_DITHERING: {
                HashMap<String, String> types = new HashMap<>();
                types.put("red quants", "int");
                types.put("green quants", "int");
                types.put("blue quants", "int");
                HashMap<String, String> borders = new HashMap<>();
                borders.put("red quants", "2|128");
                borders.put("green quants", "2|128");
                borders.put("blue quants", "2|128");
                return new Parameters(types, borders);
            }
            case CRYSTALLIZE: {
                HashMap<String, String> types = new HashMap<>();
                types.put("crystal width", "int");
                types.put("crystal height", "int");
                HashMap<String, String> borders = new HashMap<>();
                borders.put("crystal width", "10|100");
                borders.put("crystal height", "10|100");
                return new Parameters(types, borders);
            }
            case CYCLES: {
                HashMap<String, String> types = new HashMap<>();
                types.put("radius", "int");
                types.put("amplitude", "double");
                types.put("wavelength", "double");
                types.put("phase", "double");
                HashMap<String, String> borders = new HashMap<>();
                borders.put("radius", "1|300");
                borders.put("amplitude", "0.1|100.0");
                borders.put("wavelength", "0.1|100.0");
                borders.put("phase", "0.1|100.0");
                return new Parameters(types, borders);
            }
            case ROTATE: {
                HashMap<String, String> types = new HashMap<>();
                types.put("angle", "double");
                HashMap<String, String> borders = new HashMap<>();
                borders.put("angle", "-180.0|180.0");
                return new Parameters(types, borders);
            }
            case NEGATIVE, GRAYSCALE, SHARPENING, EMBOSS, AQUA:
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
            case FSDITHERING_I -> new FloydSteinbergDither(parameters);
            case ORDERED_DITHER_I -> new ru.nsu.filters.iliaDither.OrderedDither(parameters);
            case CRYSTALLIZE -> new Crystallize(parameters);
            case CYCLES -> new Cycles(parameters);
            case AQUA -> new Aqua(parameters);
            case ROTATE -> new Rotate(parameters);
        };
    }
}
