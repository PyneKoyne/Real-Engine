// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class handles all keyboard inputs

package main;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// KeyInput class
public class KeyInput extends KeyAdapter {
    private boolean uP = false;
    private boolean dP = false;
    private boolean lP = false;
    private boolean rP = false;
    private final Handler handler;

    public KeyInput(Handler handler){
        this.handler = handler;
    }

    // When a key is pressed on the keyboard
    public void keyPressed(KeyEvent e){

        // Grabs the key
        int key = e.getKeyCode();

        // Loops through every game object
        for(int i = 0; i < handler.object.size(); i ++) {
            gameObject tempObject = handler.object.get(i);
            if (tempObject.getid() == ID.Camera) {
                Camera cam = (Camera) tempObject;

                // Finds if there is any actions made
            	Vector force = Vector.zero;
                Vector norm = tempObject.getNorm();

                // Moves forward, right, backwards, and left respectively based on if the key is held down
                if(key == KeyEvent.VK_W ){
                    uP = true;
                    force = norm.mul(0.02);
                }
                if(key == KeyEvent.VK_D ){
                    rP = true;
                    force = norm.rotateByQuat(new Quaternion(Math.PI/2.0, tempObject.getUp())).mul(0.02);
                }
                if(key == KeyEvent.VK_S ){
                    dP = true;
                    force = norm.mul(-0.02);
                }
                if(key == KeyEvent.VK_A ){
                    lP = true;
                    force = norm.rotateByQuat(new Quaternion(-Math.PI/2.0, tempObject.getUp())).mul(0.02);
                }

                // If the shift key is pressed, it switches between the camera being locked or not
                if (key == KeyEvent.VK_SHIFT) {
                    cam.switchLock();
                }

                // If the e key is pressed, increases the number of cosines applied
                if (key == KeyEvent.VK_E) {
                    cam.setCos(cam.cos + 1);
                }

                // If the q key is pressed, decreases the number of cosines applied
                if (key == KeyEvent.VK_Q) {
                    cam.setCos(cam.cos - 1);
                }

                // If the c key is pressed, increases the number of tangents applied
                if (key == KeyEvent.VK_C) {
                    cam.setTan(cam.tan + 1);
                }

                // If the z key is pressed, decreases the number of tangents applied
                if (key == KeyEvent.VK_Z) {
                    cam.setTan(cam.tan - 1);
                }

                if (key == KeyEvent.VK_X){
                    handler.addObject(new Cube(cam.getLocation().add(cam.getNorm().mul(10)), 6, ID.Cube, handler, Color.yellow));
                }

                tempObject.addForce(force);

            }
        }
    }

    // If it detects a key is released, and it was a key that was pressed down and moving the camera, it stops moving the camera
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        for(int i = 0; i < handler.object.size(); i ++) {
            gameObject tempObject = handler.object.get(i);
            
            if (tempObject.getid() == ID.Camera) {

                //Key Release Events
                if (key == KeyEvent.VK_W) {
                    uP = false;
                    if (!dP) {
                    	tempObject.setVel(Vector.zero);
                    }
                }
                if (key == KeyEvent.VK_D) {
                    rP = false;
                    if (!lP) {
                    	tempObject.setVel(Vector.zero);
                    }
                }
                if (key == KeyEvent.VK_S) {
                    dP = false;
                    if (!uP) {
                    	tempObject.setVel(Vector.zero);
                    }
                }
                if (key == KeyEvent.VK_A) {
                    lP = false;
                    if (!rP) {
                    	tempObject.setVel(Vector.zero);
                    }
                }
            }
        }

    }
}
