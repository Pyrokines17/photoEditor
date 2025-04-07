package ru.nsu.filters;

import ru.nsu.filters.geometry.CustomAffineTransform;
import ru.nsu.filters.geometry.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rotate extends Filter {
    public Rotate(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth(); int height = image.getHeight();
        Color white = new Color(255, 255, 255);
        int whiteRGB = white.getRGB();

        CustomAffineTransform affineTransform = new CustomAffineTransform();

        double angle = parameters.getDoubleParam("angle");
        double centerX = width / 2.0; double centerY = height / 2.0;

        affineTransform.translate(centerX, centerY);
        affineTransform.rotate(angle);
        affineTransform.translate(-centerX, -centerY);

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                Point newPoint = affineTransform.inverseTransformPoint(new Point(j, i));

                int srcX = Math.round((float)newPoint.getX());
                int srcY = Math.round((float)newPoint.getY());

                if (srcX < 0 || srcX >= width || srcY < 0 || srcY >= height) {
                    newImage.setRGB(j, i, whiteRGB);
                } else {
                    newImage.setRGB(j, i, image.getRGB(srcX, srcY));
                }
            }
        }

        return newImage;
    }
}
