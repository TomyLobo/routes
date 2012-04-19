/*
 * Copyright (C) 2012 TomyLobo
 *
 * This file is part of Routes.
 *
 * Routes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
	public double lengthSq() {
		return x * x + y * y + z * z;
	}

	public Vector subtract(Vector other) {
		return new Vector(x - other.x, y - other.y, z - other.z);
	}

	public double distance(Vector other) {
		return subtract(other).length();
	}

	public double distanceSq(Vector other) {
		return subtract(other).lengthSq();
	}

	public Vector normalized() {
		return divide(length());
	}

	public Vector divide(double f) {
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

	@Override
	public String toString() {
		return String.format("(%.2f, %.2f, %.2f)", x, y, z);
	}

	private static int doubleHashCode(double value) {
		long bits = Double.doubleToLongBits(value);
		return (int)(bits ^ (bits >>> 32));
	}

	public Vector floor() {
		return new Vector(Math.floor(x), Math.floor(y), Math.floor(z));
	}

	public Vector setX(double x) {
		return new Vector(x, y, z);
	}

	public Vector setY(double y) {
		return new Vector(x, y, z);
	}

	public Vector setZ(double z) {
		return new Vector(x, y, z);
	}
}
