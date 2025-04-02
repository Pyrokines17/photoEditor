package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class OrderedDither extends Filter {
    private static final int[] DITHER_MATRIX_2 = {
            0, 2,
            3, 1
    };
    private static final float SCALE_2 = 4.0f;

    private static final int[] DITHER_MATRIX_4 = {
            0, 8, 2, 10,
            12, 4, 14, 6,
            3, 11, 1, 9,
            15, 7, 13, 5
    };
    private static final float SCALE_4 = 16.0f;

    private static final int[] DITHER_MATRIX_8 = {
            0, 32, 8, 40, 2, 34, 10, 42,
            48, 16, 56, 24, 50, 18, 58, 26,
            12, 44, 4, 36, 14, 46, 6, 38,
            60, 28, 52, 20, 62, 30, 54, 22,
            3, 35, 11, 43, 1, 33, 9, 41,
            51, 19, 59, 27, 49, 17, 57, 25,
            15, 47, 7, 39, 13, 45, 5, 37,
            63, 31, 55, 23, 61, 29, 53, 21
    };
    private static final float SCALE_8 = 64.0f;

    public OrderedDither(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int newR, newG, newB;

        int redQuants = parameters.getIntParam("red quants");
        int greenQuants = parameters.getIntParam("green quants");
        int blueQuants = parameters.getIntParam("blue quants");

        int[] redMatrix = getBestMatrix(redQuants);
        int[] greenMatrix = getBestMatrix(greenQuants);
        int[] blueMatrix = getBestMatrix(blueQuants);

        int redMatrixSize = getBestMatrixSize(redQuants);
        int greenMatrixSize = getBestMatrixSize(greenQuants);
        int blueMatrixSize = getBestMatrixSize(blueQuants);

        float redScale = getBestScale(redQuants);
        float greenScale = getBestScale(greenQuants);
        float blueScale = getBestScale(blueQuants);

        float redStep = 255.0f / (redQuants - 1);
        float greenStep = 255.0f / (greenQuants - 1);
        float blueStep = 255.0f / (blueQuants - 1);

        for (int i = 0; i < newImage.getHeight(); ++i) {
            for (int j = 0; j < newImage.getWidth(); ++j) {
                int rgb = image.getRGB(j, i);

                int r = getRedComponent(rgb);
                int g = getGreenComponent(rgb);
                int b = getBlueComponent(rgb);

                int redMatrixX = j % redMatrixSize;
                int redMatrixY = i % redMatrixSize;

                int greenMatrixX = j % greenMatrixSize;
                int greenMatrixY = i % greenMatrixSize;

                int blueMatrixX = j % blueMatrixSize;
                int blueMatrixY = i % blueMatrixSize;

                float redVal = r + ((float)redMatrix[redMatrixX + redMatrixY * redMatrixSize] / redScale - 0.5f) * redStep;
                float greenVal = g + ((float)greenMatrix[greenMatrixX + greenMatrixY * greenMatrixSize] / greenScale - 0.5f) * greenStep;
                float blueVal = b + ((float)blueMatrix[blueMatrixX + blueMatrixY * blueMatrixSize] / blueScale - 0.5f) * blueStep;

                newR = findClosestColor(Math.min(255, Math.max(0, redVal)), redStep);
                newG = findClosestColor(Math.min(255, Math.max(0, greenVal)), greenStep);
                newB = findClosestColor(Math.min(255, Math.max(0, blueVal)), blueStep);

                newImage.setRGB(j, i, convertBack(newR, newG, newB));
            }
        }

        return newImage;
    }

    private int findClosestColor(float value, float step) {
        return Math.round(value/step) * (int)step;
    }

    private int[] getBestMatrix(int quants) {
        int k = 256 / quants;
        if (k >= 4) {
            return DITHER_MATRIX_8;
        } else if (k >= 2) {
            return DITHER_MATRIX_4;
        } else {
            return DITHER_MATRIX_2;
        }
    }

    private int getBestMatrixSize(int quants) {
        int k = 256 / quants;
        if (k >= 4) {
            return 8;
        } else if (k >= 2) {
            return 4;
        } else {
            return 2;
        }
    }

    private float getBestScale(int quants) {
        int k = 256 / quants;
        if (k >= 4) {
            return SCALE_8;
        } else if (k >= 2) {
            return SCALE_4;
        } else {
            return SCALE_2;
        }
    }
}
