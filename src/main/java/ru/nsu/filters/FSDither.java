package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class FSDither extends Filter {
    public FSDither(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int w = image.getWidth(); int h = image.getHeight();
        int errR, errG, errB, tmpR, tmpG, tmpB;

        int[] arrImg = new int[w * h];

        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                int rgb = image.getRGB(j, i);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                arrImg[i * w + j] = (r << 16) | (g << 8) | b;
            }
        }

        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                int rgb = arrImg[i * w + j];
                int newColor = findClosestColor(rgb);
                newImage.setRGB(j, i, newColor);

                errR = ((rgb >> 16) & 0xFF) - ((newColor >> 16) & 0xFF);
                errG = ((rgb >> 8) & 0xFF) - ((newColor >> 8) & 0xFF);
                errB = (rgb & 0xFF) - (newColor & 0xFF);

                if (j + 1 < w) {
                    tmpR = ((arrImg[i * w + j + 1] >> 16) & 0xFF) + (errR * 7 / 16);
                    tmpG = ((arrImg[i * w + j + 1] >> 8) & 0xFF) + (errG * 7 / 16);
                    tmpB = (arrImg[i * w + j + 1] & 0xFF) + (errB * 7 / 16);
                    arrImg[i * w + j + 1] = (tmpR << 16) | (tmpG << 8) | tmpB;
                }

                if (j - 1 >= 0 && i + 1 < h) {
                    tmpR = ((arrImg[(i + 1) * w + j - 1] >> 16) & 0xFF) + (errR * 3 / 16);
                    tmpG = ((arrImg[(i + 1) * w + j - 1] >> 8) & 0xFF) + (errG * 3 / 16);
                    tmpB = (arrImg[(i + 1) * w + j - 1] & 0xFF) + (errB * 3 / 16);
                    arrImg[(i + 1) * w + j - 1] = (tmpR << 16) | (tmpG << 8) | tmpB;
                }

                if (i + 1 < h) {
                    tmpR = ((arrImg[(i + 1) * w + j] >> 16) & 0xFF) + (errR * 5 / 16);
                    tmpG = ((arrImg[(i + 1) * w + j] >> 8) & 0xFF) + (errG * 5 / 16);
                    tmpB = (arrImg[(i + 1) * w + j] & 0xFF) + (errB * 5 / 16);
                    arrImg[(i + 1) * w + j] = (tmpR << 16) | (tmpG << 8) | tmpB;
                }

                if (j + 1 < w && i + 1 < h) {
                    tmpR = ((arrImg[(i + 1) * w + j + 1] >> 16) & 0xFF) + (errR / 16);
                    tmpG = ((arrImg[(i + 1) * w + j + 1] >> 8) & 0xFF) + (errG / 16);
                    tmpB = (arrImg[(i + 1) * w + j + 1] & 0xFF) + (errB / 16);
                    arrImg[(i + 1) * w + j + 1] = (tmpR << 16) | (tmpG << 8) | tmpB;
                }
            }
        }

        return newImage;
    }

    private int findClosestColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int newR = findClosestVal(r);
        int newG = findClosestVal(g);
        int newB = findClosestVal(b);

        return (newR << 16) | (newG << 8) | newB;
    }

    private int findClosestVal(int val) {
        int diff0 = Math.abs(val);
        int diff128 = Math.abs(val - 128);
        int diff255 = Math.abs(val - 255);

        if (diff0 <= diff128 && diff0 <= diff255) {
            return 0;
        } else if (diff128 <= diff0 && diff128 <= diff255) {
            return 128;
        } else {
            return 255;
        }
    }
}
