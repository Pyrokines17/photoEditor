package ru.nsu.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Cycles extends Filter {
    public Cycles(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        newImage.setData(image.getData());

        int width = image.getWidth();
        int height = image.getHeight();

        float centerX = (float) x / width;
        float centerY = (float) y / height;

        float icenterX = width * centerX;
        float icenterY = height * centerY;

        double radius = parameters.getIntParam("radius");

        if (radius == 0) {
            radius = Math.min(icenterX, icenterY);
        }

        double radiusSquared = radius * radius;

        int[] inPixels = getRGB(image, 0, 0, width, height, null);

        Rectangle newSpace = new Rectangle(0, 0, width, height);

        int srcWidth = width;
        int srcHeight = height;
        int srcWidthMinus1 = srcWidth - 1;
        int srcHeightMinus1 = srcHeight - 1;
        int outWidth = newSpace.width;
        int outHeight = newSpace.height;
        int outX, outY;
        int[] outPixels = new int[outWidth];

        outX = newSpace.x;
        outY = newSpace.y;
        float[] out = new float[2];

        for (int i = 0; i < outHeight; ++i) {
            for (int j = 0; j < outWidth; ++j) {
                transformInverse(outX+j, outY+i, out, icenterX, icenterY, radius, radiusSquared);

                int srcX = (int) Math.floor(out[0]);
                int srcY = (int) Math.floor(out[1]);
                float xWeight = out[0] - srcX;
                float yWeight = out[1] - srcY;
                int nw, ne, sw, se;

                if (srcX >= 0 && srcX < srcWidthMinus1 && srcY >= 0 && srcY < srcHeightMinus1) {
                    int q = srcWidth*srcY + srcX;
                    nw = inPixels[q];
                    ne = inPixels[q + 1];
                    sw = inPixels[q + srcWidth];
                    se = inPixels[q + srcWidth + 1];
                } else {
                    nw = getPixel(inPixels, srcX, srcY, srcWidth, srcHeight);
                    ne = getPixel(inPixels, srcX + 1, srcY, srcWidth, srcHeight);
                    sw = getPixel(inPixels, srcX, srcY + 1, srcWidth, srcHeight);
                    se = getPixel(inPixels, srcX + 1, srcY + 1, srcWidth, srcHeight);
                }

                outPixels[j] = bilinearInterpolation(xWeight, yWeight, nw, ne, sw, se);
            }

            setRGB(newImage, 0, i, newSpace.width, 1, outPixels);
        }

        return newImage;
    }

    private int getPixel(int[] pixels, int x, int y, int width, int height) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return 0;
        }

        return pixels[y * width + x];
    }

    private void transformInverse(int x, int y, float[] out, float icenterX, float icenterY, double radius, double radius2) {
        float dx = x - icenterX;
        float dy = y - icenterY;
        float distance2 = dx * dx + dy * dy;

        double amplitude = parameters.getDoubleParam("amplitude");
        double wavelength = parameters.getDoubleParam("wavelength");
        double phase = parameters.getDoubleParam("phase");

        if (distance2 > radius2) {
            out[0] = x;
            out[1] = y;
        } else {
            float distance = (float) Math.sqrt(distance2);
            float amount = (float) (amplitude * (float)Math.sin(distance / wavelength * 2 * Math.PI + phase));
            amount *= (float) ((radius - distance) / radius);

            if (distance != 0) {
                amount *= (float) (wavelength / distance);
            }

            out[0] = x + dx * amount;
            out[1] = y + dy * amount;
        }
    }

    private int bilinearInterpolation(float x, float y, int nw, int ne, int sw, int se) {
        float m0, m1;

        int a0 = (nw >> 24) & 0xff;
        int a1 = (ne >> 24) & 0xff;
        int a2 = (sw >> 24) & 0xff;
        int a3 = (se >> 24) & 0xff;

        int r0 = (nw >> 16) & 0xff;
        int r1 = (ne >> 16) & 0xff;
        int r2 = (sw >> 16) & 0xff;
        int r3 = (se >> 16) & 0xff;

        int g0 = (nw >> 8) & 0xff;
        int g1 = (ne >> 8) & 0xff;
        int g2 = (sw >> 8) & 0xff;
        int g3 = (se >> 8) & 0xff;

        int b0 = nw & 0xff;
        int b1 = ne & 0xff;
        int b2 = sw & 0xff;
        int b3 = se & 0xff;

        float cx = 1.0f - x;
        float cy = 1.0f - y;

        m0 = cx * a0 + x * a1;
        m1 = cx * a2 + x * a3;
        int a = (int) (cy * m0 + y * m1);

        m0 = cx * r0 + x * r1;
        m1 = cx * r2 + x * r3;
        int r = (int) (cy * m0 + y * m1);

        m0 = cx * g0 + x * g1;
        m1 = cx * g2 + x * g3;
        int g = (int) (cy * m0 + y * m1);

        m0 = cx * b0 + x * b1;
        m1 = cx * b2 + x * b3;
        int b = (int) (cy * m0 + y * m1);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
