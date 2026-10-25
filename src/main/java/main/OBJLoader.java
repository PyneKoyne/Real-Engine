package main;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class OBJLoader extends gameObject {
    private final Handler handler;

    public OBJLoader(String file_path, Point3D p, Vector rot, float scale, ID id, Handler handler) {
        super(p, rot, scale, id);
        this.handler = handler;

        ArrayList<Point3D> verts = new ArrayList<>();
        ArrayList<Face> faces = new ArrayList<>();
        HashMap<String, Integer> materialMap = new HashMap<>();
        int colour = Color.black.getRGB();

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
                    faces.add(new Face(new int[]{v1, v2, v3}, colour));

                } else if (line.startsWith("usemtl ")) {
                    String materialName = line.split(" ")[1];
                    if (!materialMap.containsKey(materialName)) {
                        materialMap.put(materialName, 0);
                    }
                    colour = materialMap.get(materialName);
                } else if (line.startsWith("mtllib ")) {
                    String mtlFilePath = line.split(" ")[1];
                    materialMap = loadMaterials(mtlFilePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (verts.isEmpty() || faces.isEmpty()) {
            throw new IllegalArgumentException("OBJ file is empty or invalid.");
        }
        System.out.println("Loaded " + verts.size() + " vertices and " + faces.size() + " faces from " + file_path);
        this.mesh = new Mesh(verts.toArray(new Point3D[0]), faces.toArray(new Face[0]));
        mesh.createMesh(500, handler.executor);
        System.out.println("Mesh created with " + mesh.points + " points.");

        handler.regenerateObject(this);
    }

    private HashMap<String, Integer> loadMaterials(String mtlFilePath) {
        HashMap<String, Integer> materialMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(mtlFilePath))) {
            String line, name = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("newmtl ")) {
                    name = line.substring("newmtl ".length()).trim();
                } else if (line.startsWith("Kd ") && name != null) {
                    String[] parts = line.split(" ");
                    double r = Math.clamp(Double.parseDouble(parts[1]), 0, 1);
                    double g = Math.clamp(Double.parseDouble(parts[2]), 0, 1);
                    double b = Math.clamp(Double.parseDouble(parts[3]), 0, 1);
                    materialMap.put(name, new Color((float) r, (float) g, (float) b).getRGB());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return materialMap;
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
