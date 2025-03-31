package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class GaussianFilter extends MatrixFilter {

    public GaussianFilter(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        return apply(image, generate(parameters.getIntParam("window size")));
    }

    private FilterMatrix generate(int size) {
        if (size % 2 == 0) {
            throw new IllegalArgumentException("Size must be odd.");
        }

        double sigma = 1.0;
        double[] kernel = new double[size * size];
        int mean = size / 2;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                kernel[j + i * size] = (1 / (2 * Math.PI * sigma * sigma)) * Math.exp(-(Math.pow(i - mean, 2) + Math.pow(j - mean, 2)) / (2 * sigma * sigma));
            }
        }

        normalize(kernel, size);

        return new FilterMatrix(kernel, size);
    }

    private static void normalize(double[] kernel, int size) {
        double sum = 0;
        for (int i = 0; i < size * size; ++i) {
            sum += kernel[i];
        }

        for (int i = 0; i < size * size; ++i) {
            kernel[i] = kernel[i] / sum;
        }
    }
}
