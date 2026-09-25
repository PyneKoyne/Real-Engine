// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the Quaternion Data Structure, which is really just for rotations

package main;

public class Quaternion implements Cloneable {
	public double w, x, y, z;

	// Constructs a normal Quaternion
	public Quaternion(double theta, double x, double y, double z) {
		this.w = Math.cos(theta/2.0);
		this.x = x * Math.sin(theta/2.0);
		this.y = y * Math.sin(theta/2.0);
		this.z = z * Math.sin(theta/2.0);
	}

	// Constructs a Quaternion from an angle and a Vector
	public Quaternion(double theta, Vector v){
		this.w = Math.cos(theta/2.0);
		this.x = v.x * Math.sin(theta/2.0);
		this.y = v.y * Math.sin(theta/2.0);
		this.z = v.z * Math.sin(theta/2.0);
	}

	// Constructs a Quaternion from Euler Angles
	public Quaternion(double roll, double pitch, double yaw) {
		this.w = Math.cos(roll/2) * Math.cos(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2);
		this.x = Math.sin(roll/2) * Math.cos(pitch/2) * Math.cos(yaw/2) - Math.cos(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2);
		this.y = Math.cos(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2);
		this.z = Math.cos(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2) - Math.sin(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2);
	}

	// Rotates a Vector given Euler Angles
	public static Vector rotateVectorByEuclid(Vector v, Vector angles, boolean inv)
	{
		Quaternion q = new Quaternion(angles.getX(), angles.getY(), angles.getZ());
		if (inv){
			q.inv();
		}

		return rotateVector(q, v);
	}

	public void setLocation(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[w=" + w + ",x=" + x + ",y=" + y + "z=" + z + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	// Finds the magnitude of a quaternion
	public double mag(double w, double x, double y, double z) {
		return Math.sqrt(w * w + x * x + y * y + z * z);
	}

	// Finds the magnitude of the quaternion
	public double mag() {
		return Math.sqrt(w * w + x * x + y * y + z * z);
	}

	// Prints whether the magnitude of the Quaternion is 1
	public boolean isUnit() {
		return mag() == 1;
	}

	// Quaternion to angle
	public double quat2angle() {
		return 2 * Math.acos(w);
	}

	// returns a Quaternion with magnitude 1
	public Quaternion normalize() {
		double hyp = mag();

		return new Quaternion(w / hyp, x / hyp, y / hyp, z / hyp);
	}

	// returns the inverse of the Quaternion
	public Quaternion inv() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	// Rotates a point based on a Quaternion
	public Point3D rotatePoint(Quaternion rot, Point3D point) {
		Quaternion quatPoint = new Quaternion(0, point.getX(), point.getY(), point.getZ());
		Quaternion pPrime = rot.inv().mul(quatPoint).mul(rot);
		return new Point3D(pPrime.x, pPrime.y, pPrime.z);
	}

	public static Vector rotateVector(Quaternion q, Vector v) {
		// Extract the vector part of the quaternion
		Vector unitVector = new Vector(q.x, q.y, q.z);

		// Extract the scalar part of the quaternion
		double s = q.w;

		// Do the math
		return(
				unitVector.mul(2.0 * unitVector.dotProd(v)).add(
						v.mul(s*s - unitVector.dotProd(unitVector))).add(
						unitVector.crossProd(v).mul(2.0 * s))
		);
	}

	public Vector rotateVector(Vector v, boolean inv) {
		// Extract the vector part of the quaternion
		Vector unitVector = new Vector(this.x, this.y, this.z);

		if (inv) {
			unitVector = unitVector.mul(-1);
		}

		// Extract the scalar part of the quaternion
		double s = this.w;

		// Do the math
		return(
				unitVector.mul(2.0 * unitVector.dotProd(v)).add(
						v.mul(s*s - unitVector.dotProd(unitVector))).add(
						unitVector.crossProd(v).mul(2.0 * s))
		);
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}


	@Override
	public int hashCode() {
		long l = java.lang.Double.doubleToLongBits(w);
		l = l * 31 ^ java.lang.Double.doubleToLongBits(x);
		l = l * 31 ^ java.lang.Double.doubleToLongBits(y);
		l = l * 31 ^ java.lang.Double.doubleToLongBits(z);
		return (int) ((l >> 31) ^ l);
	}

	// Defines the equal function for a Quaternion
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Quaternion q) {
            return w == q.w && x == q.x && y == q.y && z == q.z;
		}
		return false;
	}

	// Multiples a Quaternion with another Quaternion
	public Quaternion mul(Quaternion q) {
		double p0 = w * q.w - x * q.x - y * q.y - z * q.z;
		double p1 = w * q.x + x * q.w - y * q.z + z * q.y;
		double p2 = w * q.y + x * q.z + y * q.w - z * q.x;
		double p3 = w * q.z - x * q.y + y * q.x + z * q.w;
		return new Quaternion(p0, p1, p2, p3);
	}
}
