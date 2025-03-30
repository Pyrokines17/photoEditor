package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class GaussianFilter extends MatrixFilter {

    private static final double[] window3 = {
            1.0 / 16, 2.0 / 16, 1.0 / 16,
            2.0 / 16, 4.0 / 16, 2.0 / 16,
            1.0 / 16, 2.0 / 16, 1.0 / 16
    };

    private static final FilterMatrix matrix3 = new FilterMatrix(window3, 3);

    private static final double[] window5 = {
        1.0 / 75, 2.0 / 75, 3.0 / 75, 2.0 / 75, 1.0 / 75,
        2.0 / 75, 4.0 / 75, 5.0 / 75, 4.0 / 75, 2.0 / 75,
        3.0 / 75, 5.0 / 75, 6.0 / 75, 5.0 / 75, 3.0 / 75,
        2.0 / 75, 4.0 / 75, 5.0 / 75, 4.0 / 75, 2.0 / 75,
        1.0 / 75, 2.0 / 75, 3.0 / 75, 2.0 / 75, 1.0 / 75
    };

    public GaussianFilter(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        return apply(image, generate(parameters.getIntParam("window size")));
    }

    private FilterMatrix generate(int size) {
        return switch (size) {
            case 3 -> matrix3;
            case 5 -> new FilterMatrix(window5, 5);
            default -> throw new IllegalArgumentException("Illegal value(or not implemented).");
        };
    }
}
