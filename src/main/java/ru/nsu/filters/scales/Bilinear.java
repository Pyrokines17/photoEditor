package ru.nsu.filters.scales;

import java.awt.image.BufferedImage;

public class Bilinear {
    public static BufferedImage resize(BufferedImage src, int width, int height) {
        BufferedImage dst = new BufferedImage(width, height, src.getType());

        float xRatio = ((float) src.getWidth()) / width;
        float yRatio = ((float) src.getHeight()) / height;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                float srcX = x * xRatio;
                float srcY = y * yRatio;

                int x1 = (int) srcX;
                int y1 = (int) srcY;
                int x2 = Math.min(x1 + 1, src.getWidth() - 1);
                int y2 = Math.min(y1 + 1, src.getHeight() - 1);

                float xDiff = srcX - x1;
                float yDiff = srcY - y1;

                int a = src.getRGB(x1, y1);
                int b = src.getRGB(x2, y1);
                int c = src.getRGB(x1, y2);
                int d = src.getRGB(x2, y2);

                int[] result = interpolateBilinear(
                        extractRGB(a), extractRGB(c),
                        extractRGB(b), extractRGB(d),
                        xDiff, yDiff
                );

                int finalRGB = (result[0] << 16) | (result[1] << 8) | result[2];
                dst.setRGB(x, y, finalRGB);
            }
        }

        return dst;
    }

    private static int[] extractRGB(int rgb) {
        return new int[] {
                (rgb >> 16) & 0xFF,
                (rgb >> 8) & 0xFF,
                rgb & 0xFF
        };
    }

    private static int[] interpolateBilinear(int[] rgb11, int[] rgb12, int[] rgb21, int[] rgb22, float dx, float dy) {
        int[] result = new int[3];

        for (int i = 0; i < 3; ++i) {
            float top = rgb11[i] * (1 - dx) + rgb21[i] * dx;
            float bottom = rgb12[i] * (1 - dx) + rgb22[i] * dx;
            float value = top * (1 - dy) + bottom * dy;
            result[i] = Math.round(value);
        }

        return result;
    }
}
