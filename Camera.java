// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the camera  class, creating a game object of which points can be displayed on screen according to its location and rotation

package main;

import java.awt.*;

public class Camera extends gameObject {

    private final Handler handler;
    public double focal_length;
    public double size;
    public Window window;
    public double focal_vel;
    public boolean locked = true;
    public int cos = 0;
    public int tan = 0;
    public Point3D focalPoint = Point3D.zero;

    public Camera(Point3D coords, double focal, ID id, Handler handler, Window window) {
        super(coords, new Vector(0, 0, 0), id);
        this.focal_length = focal;
        this.handler = handler;
        this.window = window;
    }
    
    public double getFocalLength() {
    	return focal_length;
    }

    // Sets Focal Length Change Rate
    public void setFocalVel(double vel) {
        focal_vel = vel;
    }

    public Point3D getFocalPoint() {return focalPoint;}

    // Sets the number of cosines applied in the projection
    public void setCos(int cos) {
        if (cos < 0){
            cos = 0;
        }
        this.cos = cos;
    }

    // Sets the number of tangents applied in the projection
    public void setTan(int tan) {
        if (tan < 0){
            tan = 0;
        }
        this.tan = tan;
    }

    // Moves every tick
    public void tick() {
        coords = coords.add(vel.mul(1));
        focalPoint = this.coords.add(norm.mul(this.focal_length));

        // Changes the focal length based on the focal length velocity
        if (focal_vel < 0 && focal_length < 1) {
            focal_length += focal_length * focal_vel / 4;
        }
        else{
            focal_length += focal_vel/4;
        }

        focal_vel /= 4;
    }

    public void switchLock(){
        locked = !locked;
    }

    // Renders the screen
    public void render(Graphics g) {

    	int screenX = window.getWidth() / 2;
    	int screenY = window.getHeight() / 2;
        // Loops through all objects
        for(int i = 0; i < handler.object.size(); i ++) {
            gameObject tempObject = handler.object.get(i);

            // If the object is a cube, it renders it
            if (tempObject.getid() == ID.Cube) {
                Cube cube = (Cube) tempObject;

                // Finds the mesh
            	Point3D[] mesh = cube.getMesh().getPoints();

                // Sets the colour to the colour of the object
                g.setColor(cube.getColor());
            	for (Point3D p: mesh) {

                    // Calculates where on screen the point should map to
            		Vector camPoint = p.screenOrthoCoordinates(this, cos, tan);
                    if (camPoint !=  null){
                        g.fillRect((int) (camPoint.getY() + screenX), (int) (camPoint.getZ() + screenY), 2, 2);
                    }
            	}
            }
        }
        g.setColor(Color.black);

        // Prints the focal-length on screen and number of cosines and tangents applied
        g.drawString("Focal Length: " + focal_length, 600, 600);
        g.drawString("Coordinates: " + coords, 600, 625);
        g.drawString("# of Cos Applied: " + cos, 600, 650);
        g.drawString("# of Tan Applied: " + tan, 600, 675);

        // Moves the mouse to the centre of the screen if not shift locked
        if (locked) {
            // Finds the difference in mouse coordinates
            Point p = MouseInfo.getPointerInfo().getLocation();
            setRot(getAngles().add(new Vector(0, (screenY - p.getY() + window.screenLoc().y)/1000, (screenX - p.getX() + window.screenLoc().x)/1000)));

            try {
                Robot robot = new Robot();
                robot.mouseMove((int) (screenX + window.screenLoc().x), (int) (screenY + window.screenLoc().y));

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

}
