package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class Grayscale extends Filter {
    public Grayscale(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                int rgb = image.getRGB(j, i);
                int a = rgb & 0xFF000000;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                //int avg = (r + g + b) / 3;
                int avg = (77*r + 151*g + 28*b) >> 8;
                newImage.setRGB(j, i, a | (avg << 16) | (avg << 8) | avg);
            }
        }

        return newImage;
    }
}
