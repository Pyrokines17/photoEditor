package ru.nsu.filters;

public class FilterSwitch {
    public static Parameters getParameters(FilterList filter) {
        switch (filter) {
            case NEGATIVE:
                return new Parameters(null);
            default:
                return new Parameters(null);
        }
    }

    public static Filter getFilter(FilterList filter, Parameters parameters) {
        switch (filter) {
            case NEGATIVE:
                return new Negative(parameters);
            default:
                return null;
        }
    }
}
