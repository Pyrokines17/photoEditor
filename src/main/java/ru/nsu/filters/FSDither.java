package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class FSDither extends Filter {
    public FSDither(Parameters parameters) {
        super(parameters);
    }

    private final static int[] matrix = {
            0, 0, 0,
            0, 0, 7,
            3, 5, 1
    };

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth(); int height = image.getHeight();

        int[] inPixels = getRGB(image, 0, 0, width, height, null);
        inPixels = filterPixels(width, height, inPixels);
        setRGB(newImage, 0, 0, width, height, inPixels);

        return newImage;
    }

    private int[] filterPixels(int width, int height, int[] inPixels) {
        int[] outPixels = new int[width * height];
        int index;

        int redLevels = parameters.getIntParam("red quants");
        int greenLevels = parameters.getIntParam("green quants");
        int blueLevels = parameters.getIntParam("blue quants");

        double[] redError = new double[width * height];
        double[] greenError = new double[width * height];
        double[] blueError = new double[width * height];

        for (int y = 0; y < height; ++y) {
            int direction = 1;
            index = y*width;

            for (int x = 0; x < width; ++x) {
                int rgb1 = inPixels[index];

                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;

                int r2 = (int) Math.round(redError[index] + r1);
                int g2 = (int) Math.round(greenError[index] + g1);
                int b2 = (int) Math.round(blueError[index] + b1);

                int r_fin = nearestColor(r2, redLevels);
                int g_fin = nearestColor(g2, greenLevels);
                int b_fin = nearestColor(b2, blueLevels);

                outPixels[index] = (rgb1 & 0xff000000) | (r_fin << 16) | (g_fin << 8) | b_fin;

                double er = r2 - r_fin;
                double eg = g2 - g_fin;
                double eb = b2 - b_fin;

                for (int i = -1; i <= 1; ++i) {
                    int iy = i+y;

                    if (0 <= iy && iy < height) {
                        for (int j = -1; j <= 1; ++j) {
                            int jx = j+x;

                            if (0 <= jx && jx < width) {
                                double w = matrix[(i+1)*3+j+1];

                                if (w != 0) {
                                    int k = index + j + i*width;

                                    redError[k] += er * w/16;
                                    greenError[k] += eg * w/16;
                                    blueError[k] += eb * w/16;
                                }
                            }
                        }
                    }
                }

                index += direction;
            }
        }

        return outPixels;
    }

    private int nearestColor(int val, int quants) {
        int buf = Math.min(255, Math.max(0, val));
        int step = 255 / quants;
        return step * (int) Math.round((double) buf / step);
    }
}
