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
                applyMatrixToPixel(image, filterMatrix, j, i, newImage);
            }
        }
        return newImage;
    }

    private void applyMatrixToPixel(BufferedImage image, FilterMatrix filterMatrix, int i, int j, BufferedImage newImage) {
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

        newImage.setRGB(i, j, convertBack(newR, newG, newB));
    }

    private int convertBack(double newR, double newG, double newB) {
        int newRed = intCastWithBorders(newR, 0, 255);
        int newGreen = intCastWithBorders(newG, 0, 255);
        int newBlue = intCastWithBorders(newB, 0, 255);
        return convertBack(newRed, newGreen, newBlue);
    }

    private int intCastWithBorders(double val, int leftBorder, int rightBorder) {
        int intVal = (int) val;
        if (intVal < leftBorder) {
            return leftBorder;
        }
        if (intVal > rightBorder) {
            return rightBorder;
        }
        return intVal;
    }

    private int getImageRGB(BufferedImage image, int x, int y) {
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            return 0;
        }
        return image.getRGB(x, y);
    }

}
