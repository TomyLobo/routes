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

package eu.tomylobo.routes.infrastructure.interpolation;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import eu.tomylobo.math.Vector;
import eu.tomylobo.routes.infrastructure.Node;

/**
 * Reparametrises another interpolation function by arc length.<br />
 * This is done so entities travel at roughly the same speed across
 * the whole route.
 *
 * @author TomyLobo
 *
 */
public class ReparametrisingInterpolation implements Interpolation {
	private final Interpolation baseInterpolation;
	private double totalArcLength;
	private final TreeMap<Double, Double> cache = new TreeMap<Double, Double>();

	public ReparametrisingInterpolation(Interpolation baseInterpolation) {
		this.baseInterpolation = baseInterpolation;
	}

	@Override
	public void setNodes(List<Node> nodes) {
		baseInterpolation.setNodes(nodes);
		cache.clear();
		cache.put(0.0, 0.0);
		cache.put(totalArcLength = baseInterpolation.arcLength(0.0, 1.0), 1.0);
	}

	public Interpolation getBaseInterpolation() {
		return baseInterpolation;
	}

	@Override
	public Vector getPosition(double position) {
		if (position > 1)
			return null;

		return baseInterpolation.getPosition(arcToParameter(position));
	}

	@Override
	public Vector get1stDerivative(double position) {
		if (position > 1)
			return null;

		return baseInterpolation.get1stDerivative(arcToParameter(position)).normalized().multiply(totalArcLength);
	}

	@Override
	public double arcLength(double positionA, double positionB) {
		return baseInterpolation.arcLength(arcToParameter(positionA), arcToParameter(positionB));
	}

	private double arcToParameter(double arc) {
		if (cache.isEmpty())
			throw new IllegalStateException("Must call setNodes first.");

		if (arc > 1) arc = 1;
		arc *= totalArcLength;

		Entry<Double, Double> floorEntry = cache.floorEntry(arc);
		final double leftArc = floorEntry.getKey();
		final double leftParameter = floorEntry.getValue();

		if (leftArc == arc) {
			return leftParameter;
		}

		Entry<Double, Double> ceilingEntry = cache.ceilingEntry(arc);
		if (ceilingEntry == null) {
			System.out.println("Error in arcToParameter: no ceiling entry for "+arc+" found!");
			return 0;
		}
		final double rightArc = ceilingEntry.getKey();
		final double rightParameter = ceilingEntry.getValue();

		if (rightArc == arc) {
			return rightParameter;
		}

		return evaluate(arc, leftArc, leftParameter, rightArc, rightParameter);
	}

	private double evaluate(double arc, double leftArc, double leftParameter, double rightArc, double rightParameter) {
		double midParameter = 0;
		for (int i = 0; i < 10; ++i) {
			midParameter = (leftParameter + rightParameter) * 0.5;
			//final double midArc = leftArc + baseInterpolation.arcLength(leftParameter, midParameter);
			final double midArc = baseInterpolation.arcLength(0, midParameter);
			cache.put(midArc, midParameter);

			if (midArc < leftArc) {
				return leftParameter;
			}

			if (midArc > rightArc) {
				return rightParameter;
			}
			
			if (Math.abs(midArc - arc) < 0.01) {
				return midParameter;
			}

			if (arc < midArc) {
				// search between left and mid
				rightArc = midArc;
				rightParameter = midParameter;
			}
			else {
				// search between mid and right
				leftArc = midArc;
				leftParameter = midParameter;
			}
		}
		return midParameter;
	}

	@Override
	public int getSegment(double position) {
		if (position > 1)
			return Integer.MAX_VALUE;

		return baseInterpolation.getSegment(arcToParameter(position));
	}
}
