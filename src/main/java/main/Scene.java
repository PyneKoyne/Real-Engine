package main;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Scene {

    public Handler handler;
    public Camera camera;

    public Scene(Handler handler, Camera camera) {
        this.handler = handler;
        this.camera = camera;
    }

    public Scene(Handler handler) {
        this.handler = handler;
    }

    public void testScene() {
        this.camera = new Camera(new Point3D(0, 0, 0), 0.2, ID.Camera, handler, null);
        //Places the Camera
        handler.addObject(this.camera);

        // Places cubes
        Cube c1 = (new Cube(new Point3D(84, -20, 32), Vector.zero, 32, ID.Cube, handler, Color.white));
        c1.setVel(new Vector(0, 0.1, 0));
        handler.addObject(c1);

        Cube c2 = (new Cube(new Point3D(92, -50, 24), Vector.zero, 16, ID.Cube, handler, Color.white));
        c2.setVel(new Vector(0, 0.1, 0));
        handler.addObject(c2);

        Cube c3 = (new Cube(new Point3D(92, -70, 20), Vector.zero, 8, ID.Cube, handler, Color.white));
        c3.setVel(new Vector(0, 0.2, 0));
        handler.addObject(c3);

        handler.addObject(new Plane(new Point3D(100, -24.5, 0.5), new Vector(0, Math.PI/2, 0), 1, ID.Plane, handler, Color.cyan));
        handler.addObject(new Plane(new Point3D(100, -23, 1), new Vector(0, Math.PI/2, 0), 2, ID.Plane, handler, Color.yellow));
        handler.addObject(new Plane(new Point3D(100, -20, 2), new Vector(0, Math.PI/2, 0), 4, ID.Plane, handler, Color.green));
        handler.addObject(new Plane(new Point3D(100, -20, 6), new Vector(0, Math.PI/2, 0), 4, ID.Plane, handler, Color.red));
        handler.addObject(new Plane(new Point3D(100, -20, 10), new Vector(0, Math.PI/2, 0), 4, ID.Plane, handler, Color.blue));
        handler.addObject(new Plane(new Point3D(100, -20, 14), new Vector(0, Math.PI/2, 0), 4, ID.Plane, handler, Color.black));
        handler.addObject(new Plane(new Point3D(100, -14, 4), new Vector(0, Math.PI/2, 0), 8, ID.Plane, handler, Color.blue));
        handler.addObject(new Plane(new Point3D(100, -14, 12), new Vector(0, Math.PI/2, 0), 8, ID.Plane, handler, Color.green));
        handler.addObject(new Plane(new Point3D(100, -2, 8), new Vector(0, Math.PI/2, 0), 16, ID.Plane, handler, Color.darkGray));
        handler.addObject(new Plane(new Point3D(100, 22, 16), new Vector(0, Math.PI/2, 0), 32, ID.Plane, handler, Color.orange));
        handler.addObject(new Plane(new Point3D(100, 70, 32), new Vector(0, Math.PI/2, 0), 64, ID.Plane, handler, Color.green));
        handler.addObject(new Plane(new Point3D(100, 166, 64), new Vector(0, Math.PI/2, 0), 128, ID.Plane, handler, Color.red));
        handler.addObject(new Plane(new Point3D(100, 358, 128), new Vector(0, Math.PI/2, 0), 256, ID.Plane, handler, Color.blue));
        handler.addObject(new Plane(new Point3D(100, 742, 256), new Vector(0, Math.PI/2, 0), 512, ID.Plane, handler, Color.black));
    }

    public void renderObjScene() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        String timestampStr = now.format(formatter);
        Rendering renderer = new Rendering(handler.window.getWidth(), handler.window.getHeight(), timestampStr + ".mp4", 30);
        this.camera = new Camera(new Point3D(0, 0, 0), 0.2, ID.Camera, handler, renderer);
        //Places the Camera
        handler.addObject(this.camera);
        OBJLoader cube = new OBJLoader("src/main/java/main/snekfinal1_corrected.obj", new Point3D(84, -20, 32), Vector.zero, 16, ID.Cube, handler);
        handler.addObject(cube);
    }
}
