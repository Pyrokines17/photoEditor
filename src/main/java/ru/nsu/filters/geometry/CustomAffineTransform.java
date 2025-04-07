package ru.nsu.filters.geometry;

public class CustomAffineTransform {
    private double[][] matrix = {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
    };

    public void rotate(double angleDegrees) {
        double radians = Math.toRadians(angleDegrees);

        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double[][] rotation = {
                {cos, -sin, 0},
                {sin, cos, 0},
                {0, 0, 1}
        };

        matrix = multiplyMatrices(matrix, rotation);
    }

    public void translate(double tx, double ty) {
        double[][] translation = {
                {1, 0, tx},
                {0, 1, ty},
                {0, 0, 1}
        };

        matrix = multiplyMatrices(matrix, translation);
    }

    private double[][] multiplyMatrices(double[][] a, double[][] b) {
        double[][] result = new double[3][3];

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                result[i][j] = 0;

                for (int k = 0; k < 3; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return result;
    }

    public Point transformPoint(Point p) {
        double x = p.getX() * matrix[0][0] + p.getY() * matrix[0][1] + matrix[0][2];
        double y = p.getX() * matrix[1][0] + p.getY() * matrix[1][1] + matrix[1][2];

        return new Point(x, y);
    }

    public Point inverseTransformPoint(Point p) {
        double det = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        if (det == 0) return p;

        double[][] inverse = new double[3][3];

        inverse[0][0] = matrix[1][1] / det;
        inverse[0][1] = -matrix[0][1] / det;
        inverse[1][0] = -matrix[1][0] / det;
        inverse[1][1] = matrix[0][0] / det;

        double tx = matrix[0][2];
        double ty = matrix[1][2];

        double x = p.getX() - tx;
        double y = p.getY() - ty;

        return new Point(
                x * inverse[0][0] + y * inverse[0][1],
                x * inverse[1][0] + y * inverse[1][1]
        );
    }
}
