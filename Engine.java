// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the main class of the engine, running the game loop and calling all game object methods, as well as listeners

package main;

//Modules

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Engine extends Canvas implements Runnable{
    //Dimensions
    private static final long serialVersionUID = 1L;
    public static int WIDTH = 1366, HEIGHT = 768;

    //Variables
    private Thread thread;
    private boolean running = false;
    private final Handler handler;
    private final Window window;

    //To start and stop the loop
    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Main Class
    public Engine(){
        handler = new Handler();
        
        // Adds KeyInputs
        this.addKeyListener(new KeyInput(handler));
        
        // Mouse Wheel Listener
        this.addMouseWheelListener(e -> {
        	int notches = e.getWheelRotation();
        	
            // Changes the focal length of the camera to zoom in and out
            for(int i = 0; i < handler.object.size(); i ++){
            	gameObject tempObject = handler.object.get(i);
            	if (tempObject.id == ID.Camera) {
            		Camera camera = (Camera) tempObject;

                    // Sets focal length velocity change to make it more seamless
            		camera.setFocalVel(-0.2 * notches);
            	}
            }
        });
        
        //Starts the Window
        window = new Window(WIDTH, HEIGHT, "Real Engine", this);

        //Places the Camera
        handler.addObject(new Camera(new Point3D(0, 0, 0), 0.2, ID.Camera, handler, window));

        // Places cubes
        handler.addObject(new Cube(new Point3D(-50, -50, -8), 50, ID.Cube, handler, Color.white));
    }

    //Game Loop
    public void run(){

        // Time variables to control the game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        // Game Loop
        while(running){

            // Adds to Delta Time
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            // Ticks all game objects
            while(delta >=1)
            {
                tick();
                delta--;
            }
            if(running) {
                render();
            }
            frames++;

            // Prints every second
            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;

                //Prints the FPS into the console
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }

        // Stops the whole engine
        stop();
    }

    private void tick(){
		// Finds the width and height of the screen every tick
    	WIDTH = window.getWidth();
        HEIGHT = window.getHeight();
        handler.tick();
    }
    
    private void render(){
        // To render the Screen every frame
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        
        //Sets the background for the game
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, WIDTH, HEIGHT);
		
        //Renders the Game
        handler.render(g);
        g.dispose();
        bs.show();
    }

    public static void main(){
        new Engine();

    }

}