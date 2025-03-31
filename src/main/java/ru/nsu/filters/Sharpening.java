package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class Sharpening extends MatrixFilter {

    private static final double[] matrix = {
        0, -1, 0,
        -1, 5, -1,
        0, -1, 0
    };

    Sharpening(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        return apply(image, new FilterMatrix(matrix, 3));
    }
}
