// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the Plane class, creating a game object which is really just a plane currently

package main;

import java.awt.*;
import java.util.Arrays;

public class Plane extends gameObject{
    private final Handler handler;
    private final Color color;


    public Plane(Point3D p, Vector rot, float scale, ID id, Handler handler, Color color){
        super(p, rot, scale, id);
        this.handler = handler;
        Point3D[] verts = {
                new Point3D((double) -1 /2, (double) -1/2, 0),
                new Point3D((double) 1/2, (double) -1/2, 0),
                new Point3D((double) -1/2, (double) 1/2, 0),
                new Point3D((double) 1/2, (double) 1/2, 0),
        };
        System.out.println(Arrays.toString(verts));
        int[][] faceVerts = new int[][]
                {
                        {0, 1, 3, 2},
                };

        this.bounds = scale/1.414;
        this.mesh = new Mesh(verts, faceVerts);
        this.color = color;
        mesh.createMesh(0.001);

        mesh.colour_mesh = new int[mesh.points];
        Arrays.fill(mesh.colour_mesh, color.getRGB());
        handler.regenerateObject(this);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        coords.add(vel);
    }

    public void render(Graphics g) {
    }

    public void render(Graphics g, ArrayGPU[] gpu) {
    }

    // returns the color of the shape
    public Color getColor(){
        return color;
    }

}