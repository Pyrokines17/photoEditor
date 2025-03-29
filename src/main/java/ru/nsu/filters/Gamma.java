package ru.nsu.filters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Gamma extends Filter {
    public Gamma(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        double gamma = parameters.getDoubleParam("gamma");
        double invGamma = 1.0 / gamma;

        List<Integer> changedPixels = new ArrayList<>();

        for (int i = 0; i < 256; ++i) {
            changedPixels.add((int) (255 * Math.pow(i / 255.0, invGamma) + 0.5));
        }

        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                int rgb = image.getRGB(j, i);
                int r = changedPixels.get((rgb >> 16) & 0xFF);
                int g = changedPixels.get((rgb >> 8) & 0xFF);
                int b = changedPixels.get(rgb & 0xFF);
                newImage.setRGB(j, i, (r << 16) | (g << 8) | b);
            }
        }

        return newImage;
    }
}
