// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the Face Data Structure, which comprises a mesh

package main;

import java.awt.*;
import java.util.*;

public class Face implements Cloneable {

	// Vertices
	public int[] verts;
	public Vector norm;
	public Point3D centre;
	public int colour;

	// Constructor
	public Face(int[] verts) {
		this.verts = verts;
		this.colour = Color.black.getRGB();
	}
	public Face(int[] verts, int colour) {
		this.verts = verts;
		this.colour = colour;
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

	// Number of vertices
	public int length() {
		return verts.length;
	}

	// Draw all points within a face given vertices
	// Currently only draws Quats
	public ArrayList<Point3D> drawFace(Point3D[] vertices, double LENGTH) {
		ArrayList<Point3D> Points = new ArrayList<Point3D>();
		Vector x, y;

		// Draws the two diagonals
		Vector diagX, diagY;
		diagX = vertices[verts[0]].subtract(vertices[verts[2]]).mul(.01);
		diagY = vertices[verts[1]].subtract(vertices[verts[3]]).mul(.01);

		for (int i = 0; i <= 100; i += 1) {
			Vector line = vertices[verts[2]].add(diagX.mul(i)).subtract(vertices[verts[3]].add(diagY.mul(i)));
			for (double j = 0; j <= line.mag(); j += LENGTH) {
				Points.add(vertices[verts[3]].add(diagY.mul(i)).add(line.fastNormalize(j)));
			}
		}

		for (int i = 0; i <= 100; i += 1) {
			Vector line = vertices[verts[0]].add(diagX.mul(-i)).subtract(vertices[verts[3]].add(diagY.mul(i)));
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

	public ArrayList<Point3D> drawTriangleFace(Point3D[] vertices, double density) {
		// Implementation of Lloyd's algorithm
		ArrayList<Point3D> point_list = new ArrayList<Point3D>();
		Point3D centroid = Point3D.centroid(new Point3D[]{vertices[verts[0]], vertices[verts[1]], vertices[verts[2]]});
		Point3D[] v = new Point3D[]{vertices[verts[0]], vertices[verts[1]], vertices[verts[2]]};
		Vector u = v[1].subtract(v[0]);
		Vector w = v[2].subtract(v[0]);

		if (centroid == null) {
			return point_list;
		}

		for (int i = 0; i < Math.round(u.crossProd(w).mag() * 0.5 * density); i++) {
			double r1 = Math.random();
			double r2 = Math.random();
			if (r1 + r2 > 1) {
				r1 = 1 - r1;
				r2 = 1 - r2;
			}
            point_list.add(v[0].add(u.mul(r1)).add(w.mul(r2)));
		}

		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			HashMap<Point3D, HashSet<Point3D>> delaunayPoints = generateDelaunayFromTriangle(v, point_list.toArray(new Point3D[0]));
			ArrayList<Point3D> new_points = new ArrayList<>();
			for (Point3D p : delaunayPoints.keySet()) {
				HashSet<Point3D> delaunay = delaunayPoints.get(p);
				Point3D new_point = p;

				if (!delaunay.isEmpty()) {
					ArrayList<Point3D> closed = new ArrayList<>(Arrays.asList(v));
					for (Point3D point : delaunay) {
						closed = clipToTriangle(closed, p.add(point).mul(0.5), point.subtract(p));
						if (closed.isEmpty()) break;
					}
					if (closed.size() >= 3) {
						new_point = Point3D.centroid(closed.toArray(new Point3D[0]));
					}
				}
				new_points.add(new_point != null ? clampToTriangle(new_point, v, u, w) : p);
			}
			point_list = new_points;
		}

		return point_list;
	}

    public HashMap<Point3D, HashSet<Point3D>> generateDelaunayFromTriangle(Point3D[] vertices, Point3D[] point_list) {
		// Implementation of Bowyer-Watson algorithm for Delaunay diagram generation
		HashMap<Point3D, HashSet<Point3D>> delaunayPoints = new HashMap<>();
		for (Point3D p: point_list) {
			delaunayPoints.put(p, new HashSet<>());
		}

		HashSet<Triangle> triangles = new HashSet<>();
		Triangle superTriangle = new Triangle(vertices[0], vertices[1], vertices[2]);
		triangles.add(superTriangle);

		for (Point3D p: point_list) {
			HashSet<Triangle> badTriangles = new HashSet<>();

			for (Triangle triangle: triangles) {
				Point3D circumcenter = triangle.getCircumcenter();
				if (circumcenter != null && circumcenter.distanceSq(p) < circumcenter.distanceSq(triangle.A)) {
					badTriangles.add(triangle);
				}
			}

			HashSet<Edge> edges = new HashSet<>();
			HashSet<Edge> dup_edges = new HashSet<>();

			for (Triangle badTriangle: badTriangles) {
				Edge e1 = new Edge(badTriangle.A, badTriangle.B);
				Edge e2 = new Edge(badTriangle.B, badTriangle.C);
				Edge e3 = new Edge(badTriangle.C, badTriangle.A);
				if (edges.contains(e1)) {
					dup_edges.add(e1);
				} else {
					edges.add(e1);
				}
				if (edges.contains(e2)) {
					dup_edges.add(e2);
				} else {
					edges.add(e2);
				}
				if (edges.contains(e3)) {
					dup_edges.add(e3);
				} else {
					edges.add(e3);
				}
			}

			edges.removeAll(dup_edges);

			triangles.removeAll(badTriangles);

			for (Edge edge: edges) {
				Triangle newTriangle = new Triangle(edge.start, edge.end, p);
				triangles.add(newTriangle);
			}
		}

		triangles.removeIf(triangle -> triangle.A.equals(vertices[0]) || triangle.B.equals(vertices[0]) || triangle.C.equals(vertices[0]) ||
				triangle.A.equals(vertices[1]) || triangle.B.equals(vertices[1]) || triangle.C.equals(vertices[1]) ||
				triangle.A.equals(vertices[2]) || triangle.B.equals(vertices[2]) || triangle.C.equals(vertices[2]));

		for (Triangle t: triangles) {
			delaunayPoints.get(t.A).add(t.B);
			delaunayPoints.get(t.B).add(t.C);
			delaunayPoints.get(t.C).add(t.A);
			delaunayPoints.get(t.A).add(t.C);
			delaunayPoints.get(t.B).add(t.A);
			delaunayPoints.get(t.C).add(t.B);
		}

		return delaunayPoints;
	}

	private ArrayList<Point3D> clipToTriangle(ArrayList<Point3D> polygon, Point3D m, Vector n) {
		ArrayList<Point3D> clipped = new ArrayList<>();
		if (polygon.isEmpty()) {
			return clipped;
		}

		Point3D start = polygon.get(polygon.size() - 1);
		double startDot = start.subtract(m).dotProd(n);

		for (Point3D p : polygon) {
			double polyDot = p.subtract(m).dotProd(n);
			if (polyDot <= 0){
				if (startDot > 0) {
					double t = startDot / (startDot - polyDot);
					clipped.add(start.add(p.subtract(start).mul(t)));
				}
				clipped.add(p);
			}
			else if (startDot < 0) {
				double t = startDot / (startDot - polyDot);
				clipped.add(start.add(p.subtract(start).mul(t)));
			}
			start = p;
			startDot = polyDot;
		}

		return clipped;
	}

	private Point3D clampToTriangle(Point3D c, Point3D[] vertices, Vector u, Vector w) {
		Vector d = c.subtract(vertices[0]);
		double r1 = (d.dotProd(u) * w.dotProd(w) - d.dotProd(w) * u.dotProd(w)) / (u.dotProd(u) * w.dotProd(w) - u.dotProd(w) * u.dotProd(w));
		double r2 = (d.dotProd(w) * u.dotProd(u) - d.dotProd(u) * u.dotProd(w)) / (u.dotProd(u) * w.dotProd(w) - u.dotProd(w) * u.dotProd(w));
		if (r1 < 0) r1 = 0;
		if (r2 < 0) r2 = 0;
		if (r1 + r2 > 1) {
			double sum = r1 + r2;
			r1 /= sum;
			r2 /= sum;
		}
		return vertices[0].add(u.mul(r1)).add(w.mul(r2));
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
}
