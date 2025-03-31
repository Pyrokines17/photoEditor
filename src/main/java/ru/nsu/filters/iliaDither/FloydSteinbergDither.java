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
        newImage.setData(image.getData());
        int r, g, b;
        int re, ge, be;

        int quants = parameters.getIntParam("quants");
        for (int i = 0; i < newImage.getHeight(); ++i) {
            for (int j = 0; j < newImage.getWidth(); ++j) {
                int rgb = image.getRGB(j, i);

                r = getRedComponent(rgb);
                g = getGreenComponent(rgb);
                b = getBlueComponent(rgb);

                re = r - (int)(r + 0.5);
                ge = g - (int)(g + 0.5);
                be = b - (int)(b + 0.5);

                newImage.setRGB(j, i, convertBack(nearestColor(r, quants), nearestColor(g, quants), nearestColor(b, quants)));

                propagateError(newImage, j + 1, i, 7 * re / 16, 7 * ge / 16, 7 * be / 16);
                propagateError(newImage, j - 1, i + 1, 3 * re / 16, 3 * ge / 16, 3 * be / 16);
                propagateError(newImage, j, i + 1, 5 * re / 16, 5 * ge / 16, 5 * be / 16);
                propagateError(newImage, j + 1, i + 1, re / 16, ge / 16, be / 16);

            }
        }

        return newImage;
    }

    private int nearestColor(int val, int quants) {
        int result = Math.max(0, val);
        result = Math.min(255, result);
        int threshold = 255 / quants;
        return threshold * (int) Math.round((double)result / threshold);
    }

    private void propagateError(BufferedImage newImage, int j, int i, int re, int ge, int be) {
        if (j >= newImage.getWidth() || j < 0 || i >= newImage.getHeight() || i < 0) {
            return;
        }

        int r = getImageRGB(newImage, j, i);
        int g = getImageRGB(newImage, j, i);
        int b = getImageRGB(newImage, j, i);

        newImage.setRGB(j, i, convertBack(r + re, g + ge, b + be));

    }
}
