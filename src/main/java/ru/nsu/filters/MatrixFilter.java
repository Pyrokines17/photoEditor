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

}
