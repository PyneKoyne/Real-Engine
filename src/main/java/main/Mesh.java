// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the mesh data structure, which manages the 3d graphics portion of game objects

package main;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Mesh {
	// Variables
	public Point3D[] vertices;
	public Face[] faces;

	public int points; // number of points within the mesh
	public Point3D[] mesh;
	public float[] rawMesh;
	public int[] colour_mesh;

	// Defines the mesh based on the given parameters
	public Mesh(Point3D[] vertices, int[][] faceStructure) {
		this.vertices = vertices;
		Face[] faces = new Face[faceStructure.length];

		for (int i = 0; i < faceStructure.length; i++){
			faces[i] = new Face(faceStructure[i]);
		}
		this.faces = faces;

//		for (Point edge : edges) {
//			if (this.edges.get(edge.x) != null) {
//				this.edges.get(edge.x).add(edge.y);
//			} else {
//				this.edges.put(edge.x, new ArrayList<>());
//			}
//
//			if (this.edges.get(edge.y) != null) {
//				this.edges.get(edge.y).add(edge.x);
//			} else {
//				this.edges.put(edge.y, new ArrayList<>());
//			}
//		}
	}

	// Creates the total mesh by drawing each face
	public void createMesh() {
		ArrayList<Point3D> temp_mesh = new ArrayList<>();
		for (Face face : faces) {
            temp_mesh.addAll(face.drawFace(vertices));
		}
		System.out.println(temp_mesh.get(0));
		Point3D[] arr = new Point3D[temp_mesh.size()];
		mesh = temp_mesh.toArray(arr);
		points = mesh.length;
		setRawMesh(mesh);
		colour_mesh = new int[temp_mesh.size()];
	}

	// sets the raw mesh and points field of the object
	public void setRawMesh(Point3D[] point_mesh) {
		float[] rawMesh = Point3D.toFloat(point_mesh);
		setRawMesh(rawMesh);
	}

	// sets the raw mesh and points field of the object
	public void setRawMesh(float[] rawMesh) {
		this.rawMesh = rawMesh;
		this.points = rawMesh.length;
	}

	// returns the total list of points
	public Point3D[] getPoints() {
		return mesh;
	}
}
