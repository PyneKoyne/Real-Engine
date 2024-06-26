// Author: Kenny Z
// Date: June 16th
// Program Name: Craft Me In
// Description: This class creates the Face Data Structure, which a mesh is made out of

package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

// Face object which is a set of vertices which are connected together to form a surface
public class Face implements Cloneable {

    // Vertices
    private short[] verts;
    private ArrayList<Point3D> vertPointers;
    public Vector norm;
    public Point3D centre;
    private static final double LENGTH = 0.03;// constant Length

    // Constructor
    public Face(short[] verts) {
        this.verts = verts;
    }

    // To String method for debugging
    @Override
    public String toString() {
        return getClass().getName() + "[" + Arrays.toString(verts) + "]";
    }

    // clone method if necessary
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    // Sets the normal and center of the face for analysis
    public void setFace(ArrayList<Point3D> vertices) {
        Vector x, y;

        // finds the normal and centre of the face
        x = vertices.get(verts[3]).subtract(vertices.get(verts[0])).add(vertices.get(verts[2]).subtract(vertices.get(verts[1])));
        y = vertices.get(verts[3]).subtract(vertices.get(verts[2])).add(vertices.get(verts[0]).subtract(vertices.get(verts[1])));

        this.norm = x.crossProd(y).normalize();

        // finds the centre of the shape
        this.centre = new Point3D(
                (vertices.get(verts[0]).x + vertices.get(verts[1]).x + vertices.get(verts[2]).x + vertices.get(verts[3]).x) / 4,
                (vertices.get(verts[0]).y + vertices.get(verts[1]).y + vertices.get(verts[2]).y + vertices.get(verts[3]).y) / 4,
                (vertices.get(verts[0]).z + vertices.get(verts[1]).z + vertices.get(verts[2]).z + vertices.get(verts[3]).z) / 4
        );

        // sets the given vertices as the global variable
        vertPointers = vertices;
    }

    // Draw all points within a face given vertices
    public ArrayList<Point3D> drawFace(ArrayList<Integer> colors, Color color) {
        ArrayList<Point3D> Points = new ArrayList<>();

        // Draws the two diagonals
        Vector diagX, diagY;
        diagX = vertPointers.get(verts[2]).subtract(vertPointers.get(verts[0])).mul(.05);
        diagY = vertPointers.get(verts[3]).subtract(vertPointers.get(verts[1])).mul(.05);

        // draws all the points between both diagonals
        for (int i = 0; i <= 20; i += 1) {
            Vector line = vertPointers.get(verts[3]).add(diagY.mul(i)).subtract(vertPointers.get(verts[2]).add(diagX.mul(i)));
            for (double j = 0; j <= line.mag(); j += LENGTH) {
                Points.add(vertPointers.get(verts[3]).add(diagY.mul(i)).add(line.fastNormalize(j)));
                colors.add(color.getRGB()); // adds to the mesh colours too
            }
        }

        // draws all the points between the two diagonals in the other way
        for (int i = 0; i <= 20; i += 1) {
            Vector line = vertPointers.get(verts[3]).add(diagY.mul(i)).subtract(vertPointers.get(verts[0]).add(diagX.mul(-i)));
            for (double j = 0; j < line.mag() + LENGTH / 4; j += LENGTH) {
                Points.add(vertPointers.get(verts[3]).add(diagY.mul(i)).add(line.fastNormalize(j)));
                colors.add(color.getRGB()); // adds to the mesh colours too
            }
        }
        return Points;
    }

    // a method which checks if a given ray passes through a face of a game object, and returns the normal force
    public Vector intersects(Point3D tail, Vector ray, Point3D loc) {
        double distance = norm.dotProd(tail.subtract(loc.add(this.centre)));
        double ratio = distance / norm.dotProd(ray);
        // if the ratio is not between 0 and 1, or the ray is shorter than the distance by twice, then we stop
        if (ratio <= 0 || ratio >= 1 || Double.isNaN(ratio) || ray.mag() * 2 < distance) {
            return null;
        }

        // checks if the intersection point lands on the shape
        Vector intersection = loc.add(centre).subtract(tail.add(ray.mul(ratio)));
        if (Math.abs(intersection.x) <= 0.5 && Math.abs(intersection.y) <= 0.5 && Math.abs(intersection.z) <= 0.5) {
            // checks on both sides of the face
            if (norm.dotProd(ray) < 0) {
                return (norm.mul(-norm.dotProd(ray) / (norm.mag())));
            }
            if (norm.dotProd(ray) > 0) {
                return (norm.mul(norm.mul(-1).dotProd(ray) / (norm.mag())));
            }
        }
        return null;
    }

    // allows for hashing of the face
    @Override
    public int hashCode() {
        long l = verts[0];
        for (int i = 1; i < verts.length; i++) {
            l = l * 31 ^ verts[i];
        }
        return (int) ((l >> 31) ^ l);
    }

    // Defines the equal function for Faces
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Face) {
            Face p = (Face) obj;
            return p.norm.equals(this.norm) && p.centre.equals(this.centre) || p.norm.equals(this.norm.mul(-1)) && p.centre.equals(this.centre);
        }
        return false;
    }
}