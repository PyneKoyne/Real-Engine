package main;

import java.util.ArrayList;

public class Triangle {
    public final Point3D A;
    public final Point3D B;
    public final Point3D C;
    private Point3D circumcenter;
    private boolean circumcenterCalculated = false;

    public Triangle(Point3D A, Point3D B, Point3D C) {
        this.A = A;
        this.B = B;
        this.C = C;
    }

    public Point3D getCircumcenter() {
        if (this.circumcenterCalculated == true) {
            return this.circumcenter;
        } else {
            calculateCircumcenter();
            return this.circumcenter;
        }
    }

    public void calculateCircumcenter() {
        circumcenterCalculated = true;

        double a = C.distanceSq(B);
        double b = A.distanceSq(C);
        double c = B.distanceSq(A);
        double d = a * (b + c - a) + b * (c + a - b) + c * (a + b - c);
        if (d > 1e-12 * Math.pow(a + b + c, 2)) {
            this.circumcenter = (A.mul(a * (b + c - a)).add(
                    B.mul(b * (c + a - b))).add(
                    C.mul(c * (a + b - c))).mul(1 / d));
        }
    }
}
