package eu.tomylobo.math;

public class Vector {
	private final double x;
	private final double y;
	private final double z;

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Vector add(Vector other) {
		return new Vector(x + other.x, y + other.y, z + other.z);
	}

	public Vector add(double x, double y, double z) {
		return new Vector(this.x + x, this.y + y, this.z + z);
	}

	public Vector multiply(double f) {
		return new Vector(f * x, f * y, f * z);
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vector subtract(Vector other) {
		return new Vector(x - other.x, y - other.y, z - other.z);
	}

	public double distance(Vector other) {
		return subtract(other).length();
	}

	public Vector normalized() {
		return divide(length());
	}

	private Vector divide(double f) {
		return multiply(1 / f);
	}

	public double dot(Vector other) {
		return x * other.x + y * other.y + z * other.z;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector))
			return false;

		Vector vector = (Vector) obj;
		if (x != vector.x)
			return false;

		if (y != vector.y)
			return false;

		if (z != vector.z)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return doubleHashCode(x) + 19 * (doubleHashCode(y) + 19 * (doubleHashCode(z)));
	}

	private int doubleHashCode(double value) {
		long bits = Double.doubleToLongBits(value);
		return (int)(bits ^ (bits >>> 32));
	}
}
