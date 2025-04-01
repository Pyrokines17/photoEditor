package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class Crystallize extends Filter {
    protected Crystallize(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        int crystalWidth = parameters.getIntParam("crystal width");
        int crystalHeight = parameters.getIntParam("crystal height");

        int layer = 0;
        for (int i = 0; i < image.getHeight() + crystalHeight; i += crystalHeight / 2) {
            for (int j = 0; j < image.getWidth() + crystalWidth; j += crystalWidth) {
                int actualJPosition = j;
                if (layer % 2 == 0) {
                    actualJPosition -= (crystalWidth / 2);
                }
                int averageColor = averageColorInCrystal(image, actualJPosition, i, crystalWidth, crystalHeight);
                paintCrystal(newImage, averageColor, actualJPosition, i, crystalWidth, crystalHeight);
            }
            layer += 1;
        }

        return newImage;

    }

    private int averageColorInCrystal(BufferedImage image, int pivotX, int pivotY, int crystalWidth, int crystalHeight) {
        int rSum = 0;
        int gSum = 0;
        int bSum = 0;

        int count = 0;

        for (int i = pivotY - crystalHeight / 2; i < pivotY + crystalHeight / 2; ++i) {
            if (i >= image.getHeight() || i < 0) {
                continue;
            }
            int lineWidth = (int) (((double) crystalHeight / 2) - (Math.abs(pivotY - i)) / ((double) crystalHeight / 2) * ((double) crystalWidth / 2));
            for (int j = pivotX - lineWidth; j < pivotX + lineWidth; ++j) {
                if (j >= image.getWidth() || j < 0) {
                    continue;
                }
                int rgb = image.getRGB(j, i);
                rSum += getRedComponent(rgb);
                gSum += getGreenComponent(rgb);
                bSum += getBlueComponent(rgb);
                count += 1;
            }
        }

        return convertBack((double) rSum / count, (double) gSum / count, (double) bSum / count);

    }

    private void paintCrystal(BufferedImage image, int color, int pivotX, int pivotY, int crystalWidth, int crystalHeight) {
        for (int i = pivotY - crystalHeight / 2; i < pivotY + crystalHeight / 2; ++i) {
            if (i >= image.getHeight() || i < 0) {
                continue;
            }
            int lineWidth = (int) (((double) crystalHeight / 2) - (Math.abs(pivotY - i)) / ((double) crystalHeight / 2) * ((double) crystalWidth / 2));
            for (int j = pivotX - lineWidth; j < pivotX + lineWidth; ++j) {
                if (j >= image.getWidth() || j < 0) {
                    continue;
                }
                image.setRGB(j, i, color);
            }
        }
    }

}
