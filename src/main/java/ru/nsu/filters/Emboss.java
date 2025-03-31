package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class Emboss extends MatrixFilter {

    private static final double[] matrix = {
        0, 1, 0,
        -1, 0, 1,
        0, -1, 0
    };

    Emboss(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        return apply(image, new FilterMatrix(matrix, 3));
    }

    @Override
    protected int applyMatrixToPixel(BufferedImage image, FilterMatrix filterMatrix, int i, int j) {
        int applied = super.applyMatrixToPixel(image, filterMatrix, i, j);
        int r = cutToBorders(getRedComponent(applied) + 128, 0, 255);
        int g = cutToBorders(getGreenComponent(applied) + 128, 0, 255);
        int b = cutToBorders(getBlueComponent(applied) + 128, 0, 255);
        return convertBack(r, g, b);
    }
}
