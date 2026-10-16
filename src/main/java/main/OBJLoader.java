package main;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class OBJLoader extends gameObject {
    private final Handler handler;

    public OBJLoader(String file_path, Point3D p, Vector rot, float scale, ID id, Handler handler) {
        super(p, rot, scale, id);
        this.handler = handler;

        ArrayList<Point3D> verts = new ArrayList<>();
        ArrayList<int[]> faceVerts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] parts = line.split(" ");
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    verts.add(new Point3D(x, y, z));
                } else if (line.startsWith("f ")) {
                    String[] parts = line.split(" ");
                    int v1 = Integer.parseInt(parts[1].split("/")[0]) - 1;
                    int v2 = Integer.parseInt(parts[2].split("/")[0]) - 1;
                    int v3 = Integer.parseInt(parts[3].split("/")[0]) - 1;
                    faceVerts.add(new int[]{v1, v2, v3});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (verts.isEmpty() || faceVerts.isEmpty()) {
            throw new IllegalArgumentException("OBJ file is empty or invalid.");
        }
        this.mesh = new Mesh(verts.toArray(new Point3D[0]), faceVerts.toArray(new int[0][]));
        mesh.createMesh(500, handler.executor);

        mesh.colour_mesh = new int[mesh.points];
        Arrays.fill(mesh.colour_mesh, Color.black.getRGB());
        handler.regenerateObject(this);
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void render(Graphics g, ArrayGPU[] gpu) {

    }
}
