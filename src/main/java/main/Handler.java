// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates a data structure which handles all game objects

package main;

import java.awt.*;
import java.util.LinkedList;

// Handler Class
public class Handler {
    // A list of all the gameObjects
    LinkedList<gameObject> object = new LinkedList<gameObject>();
    public ArrayGPU[] gpu = new ArrayGPU[3]; // gpu rendering accessible for the game objects
    public boolean useGPU = false;
    public Window window;

    // Ticks and renders every game object
    public void tick(){
        for (int i = 0; i < object.size(); i ++) {
            gameObject tempObject = object.get(i);
            tempObject.tick();
        }
    }
    public void render(Graphics g){
        if (this.useGPU) {
            for (int i = 0; i < object.size(); i ++) {
                gameObject tempObject = object.get(i);
                tempObject.render(g, this.gpu);
            }
        }
        else {
            for (int i = 0; i < object.size(); i ++) {
                gameObject tempObject = object.get(i);
                tempObject.render(g);
            }
        }
    }
    //Adds a gameObject to the list
    public void addObject(gameObject object){
        this.object.add(object);
    }

    // regenerates an objects gpu stored mesh in case it is updated but has not been removed
    public void regenerateObject(gameObject object){
        // adds the mesh of the game object into the gpu memory
        Mesh tempMesh = object.getMesh();
        if (tempMesh != null && useGPU) {
            gpu[0].unallocateMemory(object.getHash());
            gpu[0].allocateMemory(tempMesh.points, tempMesh.rawMesh, tempMesh.colour_mesh, object.getHash());
        }
        changeScene();
    }

    //Removes a gameObject from the list
    public void removeObject(gameObject object){
        this.object.remove(object);
        gpu[0].unallocateMemory(object.getHash());
        changeScene();
    }

    // Tells any camera objects that the scene has changed
    private void changeScene(){
        for(int i = 0; i < object.size(); i ++) {
            gameObject tempObject = object.get(i);
            if (tempObject.getId() == ID.Camera){
                Camera c = (Camera) tempObject;
                c.sceneChanged = true;
            }
        }
    }
}
