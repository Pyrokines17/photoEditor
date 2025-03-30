package ru.nsu.filters;

import java.awt.image.BufferedImage;

public abstract class Filter {
    public Parameters parameters;

    protected Filter(Parameters parameters) {
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

    protected int convertBack(double newR, double newG, double newB) {
        int newRed = intCastWithBorders(newR, 0, 255);
        int newGreen = intCastWithBorders(newG, 0, 255);
        int newBlue = intCastWithBorders(newB, 0, 255);
        return convertBack(newRed, newGreen, newBlue);
    }

    protected int intCastWithBorders(double val, int leftBorder, int rightBorder) {
        int intVal = (int) val;
        return cutToBorders(intVal, leftBorder, rightBorder);
    }

    protected int cutToBorders(int val, int leftBorder, int rightBorder) {
        if (val < leftBorder) {
            return leftBorder;
        }
        return Math.min(val, rightBorder);
    }

    protected int getImageRGB(BufferedImage image, int x, int y) {
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            return 0;
        }
        return image.getRGB(x, y);
    }

}
