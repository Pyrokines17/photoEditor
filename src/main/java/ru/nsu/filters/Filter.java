package ru.nsu.filters;

import java.awt.image.BufferedImage;

public abstract class Filter {
    public Parameters parameters;

    Filter(Parameters parameters) {
        this.parameters = parameters;
    }

    public abstract BufferedImage apply(BufferedImage image, int x, int y);

    protected int getRedComponent(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    protected int getGreenComponent(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    protected int getBlueComponent(int rgb) {
        return rgb & 0xFF;
    }

    protected int convertBack(int newRed, int newGreen, int newBlue) {
        return (newRed << 16) | (newGreen << 8) | newBlue;
    }

}
