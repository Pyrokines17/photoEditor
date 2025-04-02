package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class FSDither extends Filter {
    public FSDither(Parameters parameters) {
        super(parameters);
    }

    private final static int[] matrix = {
            0, 0, 0,
            0, 0, 7,
            3, 5, 1
    };

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth(); int height = image.getHeight();

        int[] inPixels = getRGB(image, 0, 0, width, height, null);
        inPixels = filterPixels(width, height, inPixels);
        setRGB(newImage, 0, 0, width, height, inPixels);

        return newImage;
    }

    private int[] filterPixels(int width, int height, int[] inPixels) {
        int[] outPixels = new int[width * height];
        int index;

        int redLevels = parameters.getIntParam("red quants");
        int greenLevels = parameters.getIntParam("green quants");
        int blueLevels = parameters.getIntParam("blue quants");

        int[] redMap = getColorMap(redLevels);
        int[] greenMap = getColorMap(greenLevels);
        int[] blueMap = getColorMap(blueLevels);

        int[] redDiv = getColorDiv(redLevels);
        int[] greenDiv = getColorDiv(greenLevels);
        int[] blueDiv = getColorDiv(blueLevels);

        for (int y = 0; y < height; ++y) {
            int direction = 1;
            index = y*width;

            for (int x = 0; x < width; ++x) {
                int rgb1 = inPixels[index];

                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;

                int r2 = redMap[redDiv[r1]];
                int g2 = greenMap[greenDiv[g1]];
                int b2 = blueMap[blueDiv[b1]];

                outPixels[index] = (rgb1 & 0xff000000) | (r2 << 16) | (g2 << 8) | b2;

                int er = r1-r2;
                int eg = g1-g2;
                int eb = b1-b2;

                for (int i = -1; i <= 1; ++i) {
                    int iy = i+y;

                    if (0 <= iy && iy < height) {
                        for (int j = -1; j <= 1; ++j) {
                            int jx = j+x;

                            if (0 <= jx && jx < width) {
                                int w = matrix[(i+1)*3+j+1];

                                if (w != 0) {
                                    int k = index + j;

                                    rgb1 = inPixels[k];

                                    r1 = (rgb1 >> 16) & 0xff;
                                    g1 = (rgb1 >> 8) & 0xff;
                                    b1 = rgb1 & 0xff;

                                    r1 += er * w/16;
                                    g1 += eg * w/16;
                                    b1 += eb * w/16;

                                    inPixels[k] = (inPixels[k] & 0xff000000) | (clamp(r1) << 16) | (clamp(g1) << 8) | clamp(b1);
                                }
                            }
                        }
                    }
                }

                index += direction;
            }
        }

        return outPixels;
    }

    private int[] getColorMap(int levels) {
        int[] map = new int[levels];

        for (int i = 0; i < levels; ++i) {
            int v = 255 * i / (levels - 1);
            map[i] = v;
        }

        return map;
    }

    private int[] getColorDiv(int levels) {
        int[] map = new int[256];

        for (int i = 0; i < 256; ++i) {
            map[i] = levels * i / 256;
        }

        return map;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(value, 255));
    }
}
