package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class Negative extends Filter {
    public Negative(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply() {
        BufferedImage image = parameters.image();
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                int r = 255 - ((rgb >> 16) & 0xFF);
                int g = 255 - ((rgb >> 8) & 0xFF);
                int b = 255 - (rgb & 0xFF);
                newImage.setRGB(i, j, (r << 16) | (g << 8) | b);
            }
        }

        return newImage;
    }
}
