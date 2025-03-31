package ru.nsu.filters;

import java.awt.image.BufferedImage;

public class SobelBorderHighlight extends MatrixFilter {

    private static final double[] gx = {
            -1, 0, 1,
            -2, 0, 2,
            -1, 0, 1
    };

    private static final double[] gy = {
            -1, -2, -1,
            0, 0, 0,
            1, 2, 1
    };

    SobelBorderHighlight(Parameters parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage image, int x, int y) {
        return apply(image, new FilterMatrix(null, 3));
    }

    @Override
    protected int applyMatrixToPixel(BufferedImage image, FilterMatrix filterMatrix, int i, int j) {
        int rgb, r, g, b;
        int halfMatrixSize = filterMatrix.size() / 2;
        double rdx = 0, rdy = 0;
        double gdx = 0, gdy = 0;
        double bdx = 0, bdy = 0;

        for (int mi = 0; mi < filterMatrix.size(); ++mi) {
            for (int mj = 0; mj < filterMatrix.size(); ++mj) {
                rgb = getImageRGB(image, i - halfMatrixSize + mi, j - halfMatrixSize + mj);

                r = getRedComponent(rgb);
                g = getGreenComponent(rgb);
                b = getBlueComponent(rgb);

                rdx += applyForComponent(r, gx, filterMatrix.size(), mi, mj);
                rdy += applyForComponent(r, gy, filterMatrix.size(), mi, mj);

                gdy += applyForComponent(g, gx, filterMatrix.size(), mi, mj);
                gdy += applyForComponent(g, gy, filterMatrix.size(), mi, mj);

                bdy += applyForComponent(b, gx, filterMatrix.size(), mi, mj);
                bdy += applyForComponent(b, gy, filterMatrix.size(), mi, mj);

            }
        }

        return convertBack(Math.sqrt(rdx * rdx + rdy * rdy), Math.sqrt(gdx * gdx + gdy * gdy), Math.sqrt(bdx * bdx + bdy * bdy));

    }

    private double applyForComponent(int value, double[] matrix, int size, int i, int j) {
        return value * matrix[j + size * i];
    }

    @Override
    protected int cutToBorders(int val, int leftBorder, int rightBorder) {
        return super.cutToBorders(val, leftBorder, rightBorder) > parameters.getIntParam("sensitivity") ? rightBorder : leftBorder;
    }

}
