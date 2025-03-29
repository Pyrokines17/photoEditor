package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class OrderedDither extends Filter {
    private static final int[] DITHER_MATRIX = {
            0, 32, 8, 40, 2, 34, 10, 42,
            48, 16, 56, 24, 50, 18, 58, 26,
            12, 44, 4, 36, 14, 46, 6, 38,
            60, 28, 52, 20, 62, 30, 54, 22,
            3, 35, 11, 43, 1, 33, 9, 41,
            51, 19, 59, 27, 49, 17, 57, 25,
            15, 47, 7, 39, 13, 45, 5, 37,
            63, 31, 55, 23, 61, 29, 53, 21
    };

    public OrderedDither(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int rgb, r, g, b, x1, y1, newR, newG, newB;
        double arg, border = 127.5;

        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                rgb = image.getRGB(j, i);
                r = (rgb >> 16) & 0xFF;
                g = (rgb >> 8) & 0xFF;
                b = rgb & 0xFF;
                x1 = j % 8;
                y1 = i % 8;
                arg = (DITHER_MATRIX[x1 + y1 * 8] / 64.0 - 0.5) * 255;
                newR = (r + arg > border) ? 255 : 0;
                newG = (g + arg > border) ? 255 : 0;
                newB = (b + arg > border) ? 255 : 0;
                newImage.setRGB(j, i, (newR << 16) | (newG << 8) | newB);
            }
        }

        return newImage;
    }
}
