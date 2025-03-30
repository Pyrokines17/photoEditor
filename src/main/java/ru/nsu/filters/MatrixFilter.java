package ru.nsu.filters;

import java.awt.image.BufferedImage;

public abstract class MatrixFilter extends Filter {
    protected record FilterMatrix(double[] matrix, int size) {}

    MatrixFilter(Parameters parameters) {
        super(parameters);
    }

    protected BufferedImage apply(BufferedImage image, FilterMatrix filterMatrix) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                int applied = applyMatrixToPixel(image, filterMatrix, j, i);
                newImage.setRGB(j, i, applied);
            }
        }
        return newImage;
    }

    protected int applyMatrixToPixel(BufferedImage image, FilterMatrix filterMatrix, int i, int j) {
        int rgb, r, g, b;
        int halfMatrixSize = filterMatrix.size / 2;
        double newR = 0, newG = 0, newB = 0;

        for (int mi = 0; mi < filterMatrix.size; ++mi) {
            for (int mj = 0; mj < filterMatrix.size; ++mj) {
                rgb = getImageRGB(image, i - halfMatrixSize + mi, j - halfMatrixSize + mj);

                r = getRedComponent(rgb);
                g = getGreenComponent(rgb);
                b = getBlueComponent(rgb);

                newR += r * filterMatrix.matrix[mj + mi * filterMatrix.size];
                newG += g * filterMatrix.matrix[mj + mi * filterMatrix.size];
                newB += b * filterMatrix.matrix[mj + mi * filterMatrix.size];

            }
        }

        return convertBack(newR, newG, newB);
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
