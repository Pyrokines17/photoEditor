package ru.nsu.filters;

public class FilterSwitch {
    public static Parameters getParameters(FilterList filter) {
        switch (filter) {
            case NEGATIVE:
                return new Parameters(null);
            case GRAYSCALE:
                return new Parameters(null);
            default:
                return new Parameters(null);
        }
    }

    public static Filter getFilter(FilterList filter, Parameters parameters) {
        switch (filter) {
            case NEGATIVE:
                return new Negative(parameters);
            case GRAYSCALE:
                return new Grayscale(parameters);
            default:
                return null;
        }
    }
}
