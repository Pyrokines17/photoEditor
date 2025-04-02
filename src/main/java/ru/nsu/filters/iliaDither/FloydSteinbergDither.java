package ru.nsu.filters.iliaDither;

import ru.nsu.filters.Filter;
import ru.nsu.filters.Parameters;

import java.awt.image.BufferedImage;

public class FloydSteinbergDither extends Filter {

    public FloydSteinbergDither(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        double r, g, b;
        double re, ge, be;

        int imageSize = image.getWidth() * image.getHeight();
        double[] redError = new double[imageSize];
        double[] greenError = new double[imageSize];
        double[] blueError = new double[imageSize];

        int redQuants = parameters.getIntParam("red quants");
        int greenQuants = parameters.getIntParam("green quants");
        int blueQuants = parameters.getIntParam("blue quants");
        for (int i = 0; i < newImage.getHeight(); ++i) {
            for (int j = 0; j < newImage.getWidth(); ++j) {
                int rgb;

                rgb = getImageRGB(image, j, i);

                r = redError[j + i * image.getWidth()] + getRedComponent(rgb);
                g = greenError[j + i * image.getWidth()] + getGreenComponent(rgb);
                b = blueError[j + i * image.getWidth()] + getBlueComponent(rgb);

                re = r - (int)(r + 0.5);
                ge = g - (int)(g + 0.5);
                be = b - (int)(b + 0.5);

                newImage.setRGB(j, i, convertBack(nearestColor((int) Math.round(r), redQuants),
                        nearestColor((int) Math.round(g), greenQuants), nearestColor((int) Math.round(b), blueQuants)));

                propagateError(redError, greenError, blueError, j + 1, i, 7 * re / 16, 7 * ge / 16, 7 * be / 16, image.getWidth(), image.getHeight());
                propagateError(redError, greenError, blueError, j - 1, i + 1, 3 * re / 16, 3 * ge / 16, 3 * be / 16, image.getWidth(), image.getHeight());
                propagateError(redError, greenError, blueError, j, i + 1, 5 * re / 16, 5 * ge / 16, 5 * be / 16, image.getWidth(), image.getHeight());
                propagateError(redError, greenError, blueError,  j + 1, i + 1, re / 16, ge / 16, be / 16, image.getWidth(), image.getHeight());

            }
        }

        return newImage;
    }

    private void propagateError(double[] newRed, double[] newGreen, double[] newBlue, int j, int i, double redError,
                                double greenError, double blueError, int width, int height) {
        if (j >= width || j < 0 || i >= height || i < 0) {
            return;
        }
        newRed[j + i * width] += redError;
        newGreen[j + i * width] += greenError;
        newBlue[j + i * width] += blueError;
    }

    private int nearestColor(int val, int quants) {
        int result = Math.max(0, val);
        result = Math.min(255, result);
        int threshold = 255 / quants;
        return threshold * (int) Math.round((double)result / threshold);
    }

}
