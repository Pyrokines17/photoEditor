package ru.nsu.filters;

import java.awt.image.BufferedImage;

public abstract class Filter {
    public Parameters parameters;

    Filter(Parameters parameters) {
        this.parameters = parameters;
    }

    public abstract BufferedImage apply();
}
