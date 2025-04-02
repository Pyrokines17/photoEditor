package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class FSDither extends Filter {
    public FSDither(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int w = image.getWidth(); int h = image.getHeight();

        float[] red = new float[w*h];
        float[] green = new float[w*h];
        float[] blue = new float[w*h];

        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                int rgb = image.getRGB(j, i);
                red[i*w + j] = getRedComponent(rgb);
                green[i*w + j] = getGreenComponent(rgb);
                blue[i*w + j] = getBlueComponent(rgb);
            }
        }

        float redStep = 255.0f / (parameters.getIntParam("red quants") - 1);
        float greenStep = 255.0f / (parameters.getIntParam("green quants") - 1);
        float blueStep = 255.0f / (parameters.getIntParam("blue quants") - 1);

        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                int index = i*w + j;

                float oldRed = red[index];
                float oldGreen = green[index];
                float oldBlue = blue[index];

                int newRed = findClosestColor(oldRed, redStep);
                int newGreen = findClosestColor(oldGreen, greenStep);
                int newBlue = findClosestColor(oldBlue, blueStep);

                newRed = Math.min(255, Math.max(0, newRed));
                newGreen = Math.min(255, Math.max(0, newGreen));
                newBlue = Math.min(255, Math.max(0, newBlue));

                newImage.setRGB(j, i, convertBack(newRed, newGreen, newBlue));

                float errorRed = oldRed - newRed;
                float errorGreen = oldGreen - newGreen;
                float errorBlue = oldBlue - newBlue;

                if (j + 1 < w) {
                    red[index + 1] += errorRed * 7 / 16;
                    green[index + 1] += errorGreen * 7 / 16;
                    blue[index + 1] += errorBlue * 7 / 16;
                }

                if (i + 1 < h) {
                    if (j - 1 >= 0) {
                        red[index + w - 1] += errorRed * 3 / 16;
                        green[index + w - 1] += errorGreen * 3 / 16;
                        blue[index + w - 1] += errorBlue * 3 / 16;
                    }

                    red[index + w] += errorRed * 5 / 16;
                    green[index + w] += errorGreen * 5 / 16;
                    blue[index + w] += errorBlue * 5 / 16;

                    if (j + 1 < w) {
                        red[index + w + 1] += errorRed * 1 / 16;
                        green[index + w + 1] += errorGreen * 1 / 16;
                        blue[index + w + 1] += errorBlue * 1 / 16;
                    }
                }
            }
        }

        return newImage;
    }

    private int findClosestColor(float color, float step) {
        return Math.round(color/step) * (int)step;
    }
}
