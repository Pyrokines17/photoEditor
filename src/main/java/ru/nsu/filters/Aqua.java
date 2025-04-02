package ru.nsu.filters;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Aqua extends Filter {
    private final static int[] matrix = {
            0, -1, 0,
            -1, 5, -1,
            0, -1, 0
    };

    public Aqua(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth(); int height = image.getHeight();

        int[] inPixels = getRGB(image, 0, 0, width, height, null);
        inPixels = filterPixels(width, height, inPixels);
        inPixels = filterPixelsSharpen(width, height, inPixels);
        setRGB(newImage, 0, 0, width, height, inPixels);

        return newImage;
    }

    private int[] filterPixelsSharpen(int width, int height, int[] inPixels) {
        int[] outPixels = new int[width * height];
        int index = 0;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                outPixels[index++] = applyMatrix(width, height, inPixels, x, y);
            }
        }

        return outPixels;
    }

    private int applyMatrix(int width, int height, int[] inPixels, int i, int j) {
        double newR = 0, newG = 0, newB = 0;
        int rgb, r, g, b;

        for (int mi = -1; mi <= 1; ++mi) {
            for (int mj = -1; mj <= 1; ++mj) {
                int x = i + mi; int y = j + mj;

                if (x < 0 || x >= width || y < 0 || y >= height) {
                    continue;
                }

                rgb = inPixels[y * width + x];

                r = getRedComponent(rgb);
                g = getGreenComponent(rgb);
                b = getBlueComponent(rgb);

                newR += r * matrix[mj + 1 + (mi + 1) * 3];
                newG += g * matrix[mj + 1 + (mi + 1) * 3];
                newB += b * matrix[mj + 1 + (mi + 1) * 3];
            }
        }

        return convertBack(newR, newG, newB);
    }

    private int[] filterPixels(int width, int height, int[] inPixels) {
        int[] outPixels = new int[width * height];
        int index = 0;

        int[] argb = new int[25];
        int[] r = new int[25];
        int[] g = new int[25];
        int[] b = new int[25];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int k = 0;

                for (int dy = -2; dy <= 2; ++dy) {
                    int iy = y + dy;

                    if (0 <= iy && iy < height) {
                        int ioffset = iy * width;

                        for (int dx = -2; dx <= 2; ++dx) {
                            int ix = x + dx;

                            if (0 <= ix && ix < width) {
                                int rgb = inPixels[ioffset + ix];

                                argb[k] = rgb;
                                r[k] = (rgb >> 16) & 0xff;
                                g[k] = (rgb >> 8) & 0xff;
                                b[k] = rgb & 0xff;
                                k++;
                            }
                        }
                    }
                }

                while (k < 25) {
                    argb[k] = 0xff000000;
                    r[k] = g[k] = b[k] = 0;
                    k++;
                }

                outPixels[index++] = argb[rgbMedian(r, g, b)];

                /*
                Arrays.sort(r);
                Arrays.sort(g);
                Arrays.sort(b);

                outPixels[index++] = convertBack(r[12], g[12], b[12]);
                */
            }
        }

        return outPixels;
    }

    private int rgbMedian(int[] r, int[] g, int[] b) {
        int sum, index = 0, min = Integer.MAX_VALUE;

        for (int i = 0; i < 25; ++i) {
            sum = 0;

            for (int j = 0; j < 25; ++j) {
                sum += Math.abs(r[i]-r[j]);
                sum += Math.abs(g[i]-g[j]);
                sum += Math.abs(b[i]-b[j]);
            }

            if (sum < min) {
                min = sum;
                index = i;
            }
        }

        return index;
    }
}
