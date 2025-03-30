package ru.nsu.filters.iliaDither;

import ru.nsu.filters.Filter;
import ru.nsu.filters.Parameters;

import java.awt.image.BufferedImage;

public class OrderedDither extends Filter {

    private static final double[] matrix2 = {
        0 / 4.0, 2 / 4.0,
        3 / 4.0, 1 / 4.0
    };

    private static final double[] matrix4 = {
            0 / 16.0, 8 / 16.0, 2 / 16.0, 10 / 16.0,
            12 / 16.0, 4 / 16.0, 14 / 16.0, 6 / 16.0,
            3 / 16.0, 11 / 16.0, 1 / 16.0, 9 / 16.0,
            15 / 16.0, 7 / 16.0, 13 / 16.0, 5 / 16.0,
    };

    private static final double[] matrix8 = {
            0 / 64.0, 32 / 64.0, 8 / 64.0, 40 / 64.0, 2 / 64.0, 34 / 64.0, 10 / 64.0, 42 / 64.0,
            48 / 64.0, 16 / 64.0, 56 / 64.0, 24 / 64.0, 50 / 64.0, 18 / 64.0, 58 / 64.0, 26 / 64.0,
            12 / 64.0, 44 / 64.0, 4 / 64.0, 36 / 64.0, 14 / 64.0, 46 / 64.0, 6 / 64.0, 38 / 64.0,
            60 / 64.0, 28 / 64.0, 52 / 64.0, 20 / 64.0, 62 / 64.0, 30 / 64.0, 54 / 64.0, 22 / 64.0,
            3 / 64.0, 35 / 64.0, 11 / 64.0, 43 / 64.0, 1 / 64.0, 33 / 64.0, 9 / 64.0, 41 / 64.0,
            51 / 64.0, 19 / 64.0, 59 / 64.0, 27 / 64.0, 49 / 64.0, 17 / 64.0, 57 / 64.0, 25 / 64.0,
            15 / 64.0, 47 / 64.0, 7 / 64.0, 39 / 64.0, 13 / 64.0, 45 / 64.0, 5 / 64.0, 37 / 64.0,
            63 / 64.0, 31 / 64.0, 55 / 64.0, 23 / 64.0, 61 / 64.0, 29 / 64.0, 53 / 64.0, 21 / 64.0
    };

    public OrderedDither(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        newImage.setData(image.getData());
        int r, g, b;

        int quants = parameters.getIntParam("quants");
        int matrixSize = 8;
        int threshold = 255 / quants;
        for (int i = 0; i < newImage.getHeight(); ++i) {
            for (int j = 0; j < newImage.getWidth(); ++j) {
                int rgb = image.getRGB(j, i);

                r = (int) (getRedComponent(rgb) + threshold * applyMatrix(j, i, matrixSize));
                g = (int) (getGreenComponent(rgb) + threshold * applyMatrix(j, i, matrixSize));
                b = (int) (getBlueComponent(rgb) + threshold * applyMatrix(j, i, matrixSize));

                newImage.setRGB(j, i, convertBack(nearestColor(r, quants), nearestColor(g, quants), nearestColor(b, quants)));

            }
        }

        return newImage;
    }

    private double applyMatrix(int j, int i, int matrixSize) {
        switch (matrixSize) {
            case 2 -> {
                return matrix2[j % 2 + (i % 2) * 2] - 0.5;
            }
            case 4 -> {
                return matrix4[j % 4 + (i % 4) * 4] - 0.5;
            }
            case 8 -> {
                return matrix8[j % 8 + (i % 8) * 8] - 0.5;
            }
            default -> throw new IllegalArgumentException("Illegal matrix size");
        }
    }

    private int nearestColor(int val, int quants) {
        int result = Math.max(0, val);
        result = Math.min(255, result);
        int threshold = 255 / quants;
        return threshold * (int) Math.round((double)result / threshold);
    }

}
