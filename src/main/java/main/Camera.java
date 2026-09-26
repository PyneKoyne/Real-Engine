// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the camera  class, creating a game object of which points can be displayed on screen according to its location and rotation

package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static main.Rendering.blendColor;

public class Camera extends gameObject {

    private final Handler handler;
    public Window window;
    public double focal_length;
    public double size;
    public double focal_vel;
    public boolean locked = true;
    public boolean useGPU;
    public int cos = 0;
    public int tan = 0;
    private volatile float screenX, screenY;
    public Point3D focalPoint = Point3D.zero;
    private BufferedImage bufferedImg;     // image creation
    private volatile int[] pixelData;
    private volatile short[] pixelCount;
    private final GraphicsConfiguration CONFIG = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private ExecutorService executor = Executors.newFixedThreadPool(10); // threads

    private float[] cameraMemory;
    private Robot robot;
    private double yaw, pitch;

    public Camera(Point3D coords, double focal, ID id, Handler handler) {
        super(coords, new Vector(0, 0, 0), id);
        this.yaw = 0;
        this.pitch = 0;
        this.focal_length = focal;
        this.handler = handler;
        this.useGPU = handler.useGPU;
        this.window = handler.window;
        bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
        this.screenX = bufferedImg.getWidth() / 2.0f; // updates the dimension variables of the screen
        this.screenY = bufferedImg.getHeight() / 2.0f;
        cameraMemory = new float[]{
                (float) this.focal_length,
                screenX,
                screenY
        };
        pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
        pixelCount = new short[pixelData.length];
        try {
            this.robot = new Robot();
        }
         catch (AWTException e) {
            e.printStackTrace();
        }
        if (useGPU) {
            handler.gpu[0].setPixelData(window.getWidth(), window.getHeight());
        }
    }

    // Moves every tick
    public void tick() {
        coords = coords.add(vel.mul(1));
        // Moves the mouse to the centre of the screen if not shift locked
        if (locked) {
            // Finds the difference in mouse coordinates
            Point p = MouseInfo.getPointerInfo().getLocation();
            addRot(new Vector(0, (screenY - p.getY() + window.screenLoc().y) / 1000, (screenX - p.getX() + window.screenLoc().x) / 1000));
            robot.mouseMove((int) (screenX + window.screenLoc().x), (int) (screenY + window.screenLoc().y));
        }

        float[] tempMemory;
        Point3D tempFocal;
        tempMemory = new float[]{
                (float) this.focal_length,
                screenX,
                screenY
        };

        handler.sceneChanged = !Arrays.equals(tempMemory, cameraMemory);
        cameraMemory = tempMemory;
        tempFocal = this.coords.add(norm.mul(this.focal_length)); // sets the focal point as the coordinates of the camera plus the normal multiplied by the length
        if (!tempFocal.equals(this.focalPoint)) {
            handler.sceneChanged = true;
            this.focalPoint = tempFocal;
        }

        // Changes the focal length based on the focal length velocity
        if (focal_vel < 0 && focal_length < 1) {
            focal_length += focal_length * focal_vel / 4;
        } else {
            focal_length += focal_vel / 4;
        }

        focal_vel /= 4;
    }

    // Renders the screen
    public void render(Graphics gParent, ArrayGPU[] gpu) {
        // if the screen size has changed, creates a new canvas
        if (bufferedImg.getHeight() != window.getHeight() || bufferedImg.getWidth() != window.getWidth()) {
            bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
            pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
            pixelCount = new short[pixelData.length];
            this.screenX = bufferedImg.getWidth() / 2.0f; // updates the dimension variables of the screen
            this.screenY = bufferedImg.getHeight() / 2.0f;
            handler.sceneChanged = false; // waits until next tick
            gpu[0].setPixelData(window.getWidth(), window.getHeight());
        }

        if (handler.sceneChanged) { // only renders if the scene has changed
            gpu[0].clearScreen(window.getHeight() * window.getWidth());
            gpu[0].setCamMem(cameraMemory); // sets variables required for computing the screen location of the point in the GPU

            // Loops through all objects
            for (int i = 0; i < handler.object.size(); i++) {
                gameObject tempObject = handler.object.get(i);

                // If the object is a cube, it renders it
                if (tempObject.getId() != ID.Camera) {

                    Vector tempFocal = tempObject.coords.subtract(this.focalPoint);
                    if (tempFocal.dotProd(norm) > 0.2) { // if the object is behind the user, it doesn't render
                        continue;
                    }

                    Vector temp_norm = tempObject.rot.rotateVector(this.norm, true);
                    float[] focal = tempObject.rot.rotateVector(tempFocal, true).toFloat(); // finds the focal point of the object in the object's local coordinates
                    Quaternion new_rot = tempObject.rot.inv().mul(this.rot);
                    float[] rot_mem = new float[]{
                            (float) ((-1) * new_rot.x),
                            (float) ((-1) * new_rot.y),
                            (float) ((-1) * new_rot.z),
                            (float) new_rot.w,
                            (float) (new_rot.w * new_rot.w - (new_rot.x * new_rot.x + new_rot.y * new_rot.y + new_rot.z * new_rot.z)),
                    };
                    gpu[0].projectVectors(focal, temp_norm.toFloat(), rot_mem, tempObject.getMesh().points / 3, tempObject.getHash()); // grabs the screen locations of all the points by sending a script to the GPU
                }
            }
            try {
                gpu[0].collapseColours(window.getHeight() * window.getWidth(), Color.lightGray.getRGB(), pixelData); // collapses the colours of all points onto the screen
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        // draws the image onscreen
        gParent.drawImage(bufferedImg, 0, 0, null);
        gParent.setColor(Color.white);

        // Prints the focal-length on screen and number of cosines and tangents applied
        gParent.drawString("Focal Point: " + focalPoint, 600, 575);
        gParent.drawString("Focal Length: " + focal_length, 600, 600);
        gParent.drawString("Coordinates: " + coords, 600, 625);
        gParent.drawString("Norm: " + this.norm, 600, 700);
        gParent.drawString("# of Cos Applied: " + cos, 600, 650);
        gParent.drawString("# of Tan Applied: " + tan, 600, 675);
    }

    // Renders the screen
    public void render(Graphics g) {

        ArrayList<Future<String>> renders = new ArrayList<>();

        // if the screen size has changed, creates a new canvas
        if (bufferedImg.getHeight() != window.getHeight() || bufferedImg.getWidth() != window.getWidth()) {
            bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
            pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
            pixelCount = new short[pixelData.length];
            this.screenX = bufferedImg.getWidth() / 2.0f; // updates the dimension variables of the screen
            this.screenY = bufferedImg.getHeight() / 2.0f;
            handler.sceneChanged = false; // waits until next tick
        }
        if (handler.sceneChanged) { // only renders if the scene has changed
            Arrays.fill(pixelData, Color.red.getRGB());
            Arrays.fill(pixelCount, (short) 0);

            // Loops through all objects
            for (int i = 0; i < handler.object.size(); i++) {
                gameObject tempObject = handler.object.get(i);

                // If the object is a cube, it renders it
                if (tempObject.getId() != ID.Camera) {

                    renders.add((Future<String>) executor.submit(new Thread(() -> {

                        // Finds the mesh
                        Point3D[] mesh = ((gameObject) tempObject).getMesh().getPoints();
                        int[] color_mesh = ((gameObject) tempObject).getMesh().colour_mesh;
                        Point3D temp_focal = tempObject.coords.subtract(this.focalPoint).toPoint();

                        // Sets the colour to the colour of the object
                        for (int p = 0; p < mesh.length; p++) {

                            // Calculates where on screen the point should map to
                            Vector camPoint = mesh[p].screenOrthoCoordinates(this, temp_focal, cos, tan);
                            if (camPoint != null) {
                                int x = (int) (camPoint.getY() + screenX);
                                int y = (int) (camPoint.getZ() + screenY);
                                if (x > 0 && x < screenX * 2 - 2 && y > 0 && y < screenY * 2 - 2) {
                                    int color = color_mesh[p];
                                    fillRect(pixelData, x, y, color);
                                    fillRect(pixelData, x + 1, y, color);
                                    fillRect(pixelData, x, y + 1, color);
                                    fillRect(pixelData, x + 1, y + 1, color);
                                }
                            }
                        }
                    })));
                }
            }
            // waits for all objects to be rendered
            for (Future<String> f : renders) {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        // draws the image onscreen
        g.drawImage(bufferedImg, 0, 0, null);
        g.setColor(Color.white);

        // Prints the focal-length on screen and number of cosines and tangents applied
//        g.drawString("Focal Length: " + focal_length, 600, 600);
//        g.drawString("Coordinates: " + coords, 600, 625);
//        g.drawString("# of Cos Applied: " + cos, 600, 650);
//        g.drawString("# of Tan Applied: " + tan, 600, 675);
    }

    @Override
    public void addRot(Vector rot) {
        this.yaw = (this.yaw + rot.z) % (2 * Math.PI);
        this.pitch = (this.pitch + rot.y) % (2 * Math.PI);
        this.rot = new Quaternion(0, this.pitch, this.yaw);
        this.updateRot();
    }

    // fills a one by two rectangle on the image
    private void fillRect(int[] pixelData, int x, int y, int color) {
        float ratio = (float) ((pixelCount[x + y * this.window.getWidth()] + 1.0) / (pixelCount[x + y * this.window.getWidth()] + 2.0));
        pixelCount[x + y * this.window.getWidth()]++;
        pixelData[x + y * this.window.getWidth()] = blendColor(color, pixelData[x + y * this.window.getWidth()], ratio); // blends the new colour with the old colour so the order at which pixels are drawn on screen is irrelevant
    }

    public double getFocalLength() {
        return focal_length;
    }

    // Sets Focal Length Change Rate
    public void setFocalVel(double vel) {
        focal_vel = vel;
    }

    // Sets the number of cosines applied in the projection
    public void setCos(int cos) {
        if (cos < 0) {
            cos = 0;
        }
        this.cos = cos;
    }

    // Sets the number of tangents applied in the projection
    public void setTan(int tan) {
        if (tan < 0) {
            tan = 0;
        }
        this.tan = tan;
    }

    public void switchLock() {
        locked = !locked;
    }
}
