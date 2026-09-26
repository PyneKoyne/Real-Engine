// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the GameObject Data Structure, which creates a structure for all game objects

package main;

import java.awt.*;

// Abstract class to define game objects such as Camera and Cube
public abstract class gameObject {

    // Default Variables for a game object
    protected Point3D coords;
    protected Vector vel;
    protected Quaternion rot;
    protected Vector norm;
    protected Vector up;
    protected Vector left;
    protected ID id;
    protected int hash = System.identityHashCode(this);
    protected Mesh mesh;

	// Average game object constructor
	public gameObject(Point3D cos, Vector rot, ID id) {
		setRot(rot);
		coords = cos;
		this.vel = new Vector(0, 0, 0);
		this.id = id;
	}

	// requires a tick and render method in every game object
	public abstract void tick();

	public abstract void render(Graphics g);

	public abstract void render(Graphics g, ArrayGPU[] gpu);


	// Getters and Setters
	public void setX(double x) {
		this.coords.setX(x);
	}

	public void setY(double y) {
		this.coords.setY(y);
	}

	public void setZ(double z) {
		this.coords.setZ(z);
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public Point3D getLocation() {
		return coords;
	}

	public double getX() {
		return coords.getX();
	}

	public double getY() {
		return coords.getY();
	}

	public double getZ() {
		return coords.getZ();
	}

	public void setId(ID id) {
		this.id = id;
	}

	public ID getId() {
		return id;
	}

    // gets the hash of the game object
    public int getHash() {
        return hash;
    }

    // sets the velocity of the game object
    public void setVel(Vector vel) {
        this.vel.setX(vel.getX());
        this.vel.setY(vel.getY());
        this.vel.setZ(vel.getZ());
    }

	public void setVelX(double velX) {
		this.vel.setX(velX);
    }

	public void setVelY(double velY) {
		this.vel.setY(velY);
	}
	
	public void setVelZ(double velZ) {
		this.vel.setZ(velZ);
	}

	public double getVelX() {
		return vel.getX();
	}

	public double getVelY() {
		return vel.getY();
	}
	
	public double getVelZ() {
		return vel.getZ();
	}
	
	public Vector getNorm() {
		return norm;
	}
	
	public void setNorm(Vector norm) {
		this.norm = norm;
	}

	public Vector getUp() {
		return up;
	}

	public void setUp(Vector up){
		this.up = up;
	}

    // gets the left vector of the game object
    public Vector getLeft() {
        return left;
    }

    // sets the left vector of the game object
    public void setLeft(Vector left) {
        this.left = left;
    }

	public void setRot(Vector rot) {
		this.rot = new Quaternion(rot.x, rot.y, rot.z);
		updateRot();
	}

    // sets the rotation of the game object
    public void addRot(Vector rot) {
        // Rotations over 360 degrees are modul-ised
        Quaternion roll = new Quaternion(rot.getX(), this.norm);
        Quaternion pitch = new Quaternion(rot.getY(), this.left);
        Quaternion yaw = new Quaternion(rot.getZ(), this.up);

		this.rot = yaw.mul(this.rot.mul(pitch).mul(roll)).normalize();
        updateRot();
    }

    // updates the rotation of the game object
    protected void updateRot() {
		// Sets the norm when the rotation is set as well
		this.setNorm(this.rot.rotateVector(Vector.i, false));
		this.setUp(this.rot.rotateVector(Vector.k, false));
		this.setLeft(this.rot.rotateVector(Vector.j, false));
	}

	// adds acceleration
	public void addForce(Vector force) {
		setVelX(getVelX() + force.getX());
		setVelY(getVelY() + force.getY());
		setVelZ(getVelZ() + force.getZ());
	}

	public Quaternion getRot() {
		return rot;
	}
}