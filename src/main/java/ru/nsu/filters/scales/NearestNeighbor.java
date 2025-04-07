package ru.nsu.filters.scales;

import java.awt.image.BufferedImage;

public class NearestNeighbor {
    public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());

        float xRatio = ((float) image.getWidth()) / newWidth;
        float yRatio = ((float) image.getHeight()) / newHeight;

        for (int y = 0; y < newHeight; ++y) {
            for (int x = 0; x < newWidth; ++x) {
                int nearestX = Math.round(x * xRatio);
                int nearestY = Math.round(y * yRatio);

                nearestX = Math.min(nearestX, image.getWidth() - 1);
                nearestY = Math.min(nearestY, image.getHeight() - 1);

                resizedImage.setRGB(x, y, image.getRGB(nearestX, nearestY));
            }
        }

        return resizedImage;
    }
}
