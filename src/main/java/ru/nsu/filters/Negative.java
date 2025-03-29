package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class Negative extends Filter {
    public Negative(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                int rgb = image.getRGB(j, i);
                int r = 255 - ((rgb >> 16) & 0xFF);
                int g = 255 - ((rgb >> 8) & 0xFF);
                int b = 255 - (rgb & 0xFF);
                newImage.setRGB(j, i, (r << 16) | (g << 8) | b);
            }
        }

        return newImage;
    }
}
