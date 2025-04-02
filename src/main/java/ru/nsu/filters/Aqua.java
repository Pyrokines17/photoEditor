package ru.nsu.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Aqua extends Filter {
    public Aqua(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        newImage.setData(image.getData());

        int width = image.getWidth();
        int height = image.getHeight();

        Rectangle transform = new Rectangle(0, 0, width, height);

        int[] inPixels = getRGB(image, 0, 0, width, height, null);
        inPixels = filterPixels(width, height, inPixels);
        setRGB(newImage, 0, 0, transform.width, transform.height, inPixels);

        return newImage;
    }

    private int[] filterPixels(int width, int height, int[] inPixels) {
        int index = 0;

        int levels = 256;
        int range = 5;

        int[] rHistogram = new int[levels];
        int[] gHistogram = new int[levels];
        int[] bHistogram = new int[levels];

        int[] rTotal = new int[levels];
        int[] gTotal = new int[levels];
        int[] bTotal = new int[levels];

        int[] outPixels = new int[width * height];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                for (int i = 0; i < levels; ++i)
                    rHistogram[i] = gHistogram[i] = bHistogram[i] = rTotal[i] = gTotal[i] = bTotal[i] = 0;

                for (int row = -range; row <= range; ++row) {
                    int iy = y+row;
                    int ioffset;

                    if (0 <= iy && iy < height) {
                        ioffset = iy*width;

                        for (int col = -range; col <= range; ++col) {
                            int ix = x+col;

                            if (0 <= ix && ix < width) {
                                int rgb = inPixels[ioffset+ix];

                                int r = (rgb >> 16) & 0xff;
                                int g = (rgb >> 8) & 0xff;
                                int b = rgb & 0xff;

                                int ri = r*levels/256;
                                int gi = g*levels/256;
                                int bi = b*levels/256;

                                rTotal[ri] += r;
                                gTotal[gi] += g;
                                bTotal[bi] += b;

                                rHistogram[ri]++;
                                gHistogram[gi]++;
                                bHistogram[bi]++;
                            }
                        }
                    }
                }

                int r = 0, g = 0, b = 0;

                for (int i = 1; i < levels; ++i) {
                    if (rHistogram[i] > rHistogram[r])
                        r = i;
                    if (gHistogram[i] > gHistogram[g])
                        g = i;
                    if (bHistogram[i] > bHistogram[b])
                        b = i;
                }

                r = rTotal[r] / rHistogram[r];
                g = gTotal[g] / gHistogram[g];
                b = bTotal[b] / bHistogram[b];

                outPixels[index] = (inPixels[index] & 0xff000000) | ( r << 16 ) | ( g << 8 ) | b;
                index++;
            }
        }

        return outPixels;
    }
}
