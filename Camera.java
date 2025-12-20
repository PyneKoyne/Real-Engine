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

public class Camera extends gameObject {

    private final Handler handler;
    public double focal_length;
    public double size;
    public Window window;
    public double focal_vel;
    public boolean locked = true;
    public int cos = 0;
    public int tan = 0;
    private volatile float screenX, screenY;
    public Point3D focalPoint = Point3D.zero;
    private BufferedImage bufferedImg;     // image creation
    private volatile int[] pixelData;
    private volatile short[] pixelCount;
    private final GraphicsConfiguration CONFIG = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private ExecutorService executor = Executors.newFixedThreadPool(10); // threads


    public Camera(Point3D coords, double focal, ID id, Handler handler, Window window) {
        super(coords, new Vector(0, 0, 0), id);
        this.focal_length = focal;
        this.handler = handler;
        this.window = window;
        bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
        this.screenX = bufferedImg.getWidth() / 2.0f; // updates the dimension variables of the screen
        this.screenY = bufferedImg.getHeight() / 2.0f;
        pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
        pixelCount = new short[pixelData.length];
    }

    public double getFocalLength() {
        return focal_length;
    }

    // Sets Focal Length Change Rate
    public void setFocalVel(double vel) {
        focal_vel = vel;
    }

    public Point3D getFocalPoint() {
        return focalPoint;
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

    // Moves every tick
    public void tick() {
        coords = coords.add(vel.mul(1));
        focalPoint = this.coords.add(norm.mul(this.focal_length));

        // Changes the focal length based on the focal length velocity
        if (focal_vel < 0 && focal_length < 1) {
            focal_length += focal_length * focal_vel / 4;
        } else {
            focal_length += focal_vel / 4;
        }

        focal_vel /= 4;
    }

    public void switchLock() {
        locked = !locked;
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
        }

        Arrays.fill(pixelData, Color.lightGray.getRGB());
        Arrays.fill(pixelCount, (short) 0);

        // Loops through all objects
        for (int i = 0; i < handler.object.size(); i++) {
            gameObject tempObject = handler.object.get(i);

            // If the object is a cube, it renders it
            if (tempObject.getid() != ID.Camera) {

                renders.add((Future<String>) executor.submit(new Thread(() -> {

                    // Finds the mesh
                    Point3D[] mesh = ((gameObject) tempObject).getMesh().getPoints();

                    // Sets the colour to the colour of the object
                    Color color = ((gameObject) tempObject).getColor();
                    for (Point3D p : mesh) {

                        // Calculates where on screen the point should map to
                        Vector camPoint = p.screenOrthoCoordinates(this, cos, tan);
                        if (camPoint != null) {
                            int x = (int) (camPoint.getY() + screenX);
                            int y = (int) (camPoint.getZ() + screenY);
                            if (x > 0 && x < screenX * 2 - 2 && y > 0 && y < screenY * 2 - 2) {
                                fillRect(pixelData, x, y, color.getRGB());
                                fillRect(pixelData, x + 1, y, color.getRGB());
                                fillRect(pixelData, x , y + 1, color.getRGB());
                                fillRect(pixelData, x + 1, y + 1, color.getRGB());
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
                throw new RuntimeException(e);
            }
        }
        // draws the image onscreen
        g.drawImage(bufferedImg, 0, 0, null);
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
            setRot(getAngles().add(new Vector(0, (screenY - p.getY() + window.screenLoc().y) / 1000, (screenX - p.getX() + window.screenLoc().x) / 1000)));

            try {
                Robot robot = new Robot();
                robot.mouseMove((int) (screenX + window.screenLoc().x), (int) (screenY + window.screenLoc().y));

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    // fills a one by two rectangle on the image
    private void fillRect(int[] pixelData, int x, int y, int color) {
        float ratio = (float) ((pixelCount[x + y * this.window.getWidth()] + 1.0) / (pixelCount[x + y * this.window.getWidth()] + 2.0));
        pixelCount[x + y * this.window.getWidth()]++;
        pixelData[x + y * this.window.getWidth()] = blendColor(color, pixelData[x + y * this.window.getWidth()], ratio); // blends the new colour with the old colour so the order at which pixels are drawn on screen is irrelevant
    }

    // a method to blend two rgb colours together
    private int blendColor(int color1, int color2, double ratio) {
        int a1 = (color1 >> 24 & 0xff);
        int r1 = ((color1 & 0xff0000) >> 16);
        int g1 = ((color1 & 0xff00) >> 8);
        int b1 = (color1 & 0xff);

        int a2 = (color2 >> 24 & 0xff);
        int r2 = ((color2 & 0xff0000) >> 16);
        int g2 = ((color2 & 0xff00) >> 8);
        int b2 = (color2 & 0xff);

        int a = (int) ((a1 * (1 - ratio)) + (a2 * ratio));
        int r = (int) ((r1 * (1 - ratio)) + (r2 * ratio));
        int g = (int) ((g1 * (1 - ratio)) + (g2 * ratio));
        int b = (int) ((b1 * (1 - ratio)) + (b2 * ratio));
        return (a << 24 | r << 16 | g << 8 | b);
    }
}
