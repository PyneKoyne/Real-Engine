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
    
    
    public Cube(Point3D p, float scale, ID id, Handler handler, Color color){
        super(p, new Vector(0, 0, 0), id);
        this.handler = handler;

        Point3D[] verts = {
                p,
                p.add(new Vector(scale, 0, 0)),
                p.add(new Vector(0, scale, 0)),
                p.add(new Vector(scale, scale, 0)),

                p.add(new Vector(0, 0, scale)),
                p.add(new Vector(scale, 0, scale)),
                p.add(new Vector(0, scale, scale)),
                p.add(new Vector(scale, scale, scale))
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

        this.mesh = new Mesh(verts, faceVerts);
        this.color = color;
        mesh.createMesh();
        
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        coords.add(vel);
    }

    public void render(Graphics g) {

    }

    // returns the color of the shape
    public Color getColor(){
        return color;
    }

}