// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the Face Data Structure, which comprises a mesh

package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Face implements Cloneable {

	// Vertices
	public int[] verts;
	public Vector norm;
	public Point3D centre;

	// Constructor
	public Face(int[] verts) {
		this.verts = verts;
	}

	// Get Vert methods
	public int[] getVerts() {
		return verts;
	}

	public Point3D getVert(Point3D[] vertices, int n) {
		return vertices[verts[n]];
	}
	
	public int getVert(int n) {
		return verts[n];
	}

	// Methods to add vertices
	public void addVert(int vert, int index) {
		int[] temp_verts = verts;
		verts = new int[temp_verts.length + 1];

		for (int i = 0, j = 0; i < temp_verts.length + 1; i++, j++) {
			if (i == index) {
				verts[j] = vert;
				i++;
			}
			verts[j] = temp_verts[i];
		}
	}

	public void addVert(int vert) {
		int[] temp_verts = verts;
		verts = new int[temp_verts.length + 1];

		System.arraycopy(temp_verts, 0, verts, 0, temp_verts.length);
		verts[temp_verts.length] = vert;
	}

	public void addVerts(int[] new_verts) {
		int[] temp_verts = verts;
		verts = new int[temp_verts.length + 1];

		System.arraycopy(temp_verts, 0, verts, 0, temp_verts.length);

		if (new_verts.length - 1 >= 0)
			System.arraycopy(new_verts, 0, verts, temp_verts.length - 1, new_verts.length - 1);
	}

	// Number of vertices
	public int length() {
		return verts.length;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + verts + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	// Draw all points within a face given vertices
	// Currently only draws Quats
	public ArrayList<Point3D> drawFace(Point3D[] vertices) {
		final double LENGTH = 0.2;
		ArrayList<Point3D> Points = new ArrayList<Point3D>();
		Vector x, y;

		// Draws the two diagonals
		Vector diagX, diagY;
		diagX = vertices[verts[2]].subtract(vertices[verts[0]]).mul(.01);
		diagY = vertices[verts[3]].subtract(vertices[verts[1]]).mul(.01);

		for (int i = 0; i <= 100; i += 1) {
			Vector line = vertices[verts[3]].add(diagY.mul(i)).subtract(vertices[verts[2]].add(diagX.mul(i)));
			for (double j = 0; j <= line.mag(); j += LENGTH) {
				Points.add(vertices[verts[3]].add(diagY.mul(i)).add(line.fastNormalize(j)));
			}
		}

		for (int i = 0; i <= 100; i += 1) {
			Vector line = vertices[verts[3]].add(diagY.mul(i)).subtract(vertices[verts[0]].add(diagX.mul(-i)));
			for (double j = 0; j < line.mag() + LENGTH / 2; j += LENGTH) {
				Points.add(vertices[verts[3]].add(diagY.mul(i)).add(line.fastNormalize(j)));
			}
		}

		x = vertices[verts[3]].subtract(vertices[verts[0]]).add(vertices[verts[2]].subtract(vertices[verts[1]]));
		y = vertices[verts[3]].subtract(vertices[verts[2]]).add(vertices[verts[0]].subtract(vertices[verts[1]]));

		this.norm = x.crossProd(y).normalize();
		this.centre = new Point3D(
				(vertices[verts[0]].x + vertices[verts[1]].x + vertices[verts[2]].x + vertices[verts[3]].x) / 4,
				(vertices[verts[0]].y + vertices[verts[1]].y + vertices[verts[2]].y + vertices[verts[3]].y) / 4,
				(vertices[verts[0]].z + vertices[verts[1]].z + vertices[verts[2]].z + vertices[verts[3]].z) / 4
		);
		return Points;
	}

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
		if (obj instanceof Face p) {
            return Arrays.equals(verts, p.getVerts());
		}
		return false;
	}
}
