// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the cube class, creating a game object which is really just a plane currently

package main;

import java.awt.*;
import java.util.Arrays;

public class Cube extends gameObject{
    private final Handler handler;
    private final Color color;

    public Cube(Point3D p, Vector rot, float scale, ID id, Handler handler, Color color){
        super(p, rot, scale, id);
        this.handler = handler;

        Point3D[] verts = {
                new Point3D((double) -1 /2, (double)-1/2, (double) -1/2),
                new Point3D((double) 1/2, (double) -1/2, (double) -1/2),
                new Point3D((double) -1/2, (double) 1/2, (double) -1/2),
                new Point3D((double) 1/2, (double) 1/2, (double) -1/2),

                new Point3D((double) -1/2, (double) -1/2, (double) 1/2),
                new Point3D((double) 1/2, (double) -1/2, (double) 1/2),
                new Point3D((double) -1/2, (double) 1/2, (double) 1/2),
                new Point3D((double) 1/2, (double) 1/2, (double) 1/2)
        };
        System.out.println(Arrays.toString(verts));
        int[][] faceVerts = new int[][]
                {
                        {0, 1, 3, 2},
                        {0, 4, 6, 2},
                        {0, 1, 5, 4},
                        {2, 6, 7, 3},
                        {4, 5, 7, 6},
                        {5, 7, 3, 1}
                };

        this.bounds = scale / 1.732;
        this.mesh = new Mesh(verts, faceVerts);
        this.color = color;
        mesh.createMesh(0.01);

        mesh.colour_mesh = new int[mesh.points];
        Arrays.fill(mesh.colour_mesh, color.getRGB());
        handler.regenerateObject(this);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        coords = coords.add(vel);
        addRot(new Vector(0.02, 0, 0));
        handler.sceneChanged = true;
    }

    public void render(Graphics g) {
    }

    public void render(Graphics g, ArrayGPU[] gpu) {
    }
}