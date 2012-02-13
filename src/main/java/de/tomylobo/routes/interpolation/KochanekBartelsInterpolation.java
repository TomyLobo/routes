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

package de.tomylobo.routes.interpolation;

import java.util.Collections;
import java.util.List;

import org.bukkit.util.Vector;

import de.tomylobo.routes.Node;

public class KochanekBartelsInterpolation implements Interpolation {
	private double tension;
	private double bias;
	private double continuity;
	private List<Node> nodes;
	private Vector[] coeffA;
	private Vector[] coeffB;
	private Vector[] coeffC;
	private Vector[] coeffD;
	private double scaling;

	public KochanekBartelsInterpolation(double tension, double bias, double continuity) {
		this.tension = tension;
		this.bias = bias;
		this.continuity = continuity;
		setNodes(Collections.<Node>emptyList());
	}

	@Override
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
		recalc();
	}

	private void recalc() {
		// Kochanek-Bartels tangent coefficients
		final double ta = (1-tension)*(1+bias)*(1+continuity)/2; // Factor for lhs of d[i]
		final double tb = (1-tension)*(1-bias)*(1-continuity)/2; // Factor for rhs of d[i]
		final double tc = (1-tension)*(1+bias)*(1-continuity)/2; // Factor for lhs of d[i+1]
		final double td = (1-tension)*(1-bias)*(1+continuity)/2; // Factor for rhs of d[i+1]

		final int nNodes = nodes.size();
		coeffA = new Vector[nNodes];
		coeffB = new Vector[nNodes];
		coeffC = new Vector[nNodes];
		coeffD = new Vector[nNodes];

		for (int i = 0; i < nNodes; ++i) {
			coeffA[i] = linearCombination(i,  -ta,    ta-  tb-tc+2,    tb+tc-td-2,  td);
			coeffB[i] = linearCombination(i, 2*ta, -2*ta+2*tb+tc-3, -2*tb-tc+td+3, -td);
			coeffC[i] = linearCombination(i,  -ta,    ta-  tb     ,    tb        ,   0);
			//coeffD[i] = linearCombination(i,    0,               1,             0,   0);
			coeffD[i] = retrieve(i); // this is an optimization
		}

		scaling = nodes.size() - 1;
	}

	/**
	 * Returns the linear combination of the given coefficients with the nodes adjacent to baseIndex.
	 *
	 * @param baseIndex node index
	 * @param f1 coefficient for baseIndex-1
	 * @param f2 coefficient for baseIndex
	 * @param f3 coefficient for baseIndex+1
	 * @param f4 coefficient for baseIndex+2
	 * @return linear combination of nodes[n-1..n+2] with f1..4
	 */
	private Vector linearCombination(int baseIndex, double f1, double f2, double f3, double f4) {
		final Vector r1 = retrieve(baseIndex - 1).clone().multiply(f1);
		final Vector r2 = retrieve(baseIndex    ).clone().multiply(f2);
		final Vector r3 = retrieve(baseIndex + 1).clone().multiply(f3);
		final Vector r4 = retrieve(baseIndex + 2).clone().multiply(f4);

		return r1.add(r2).add(r3).add(r4);
	}

	/**
	 * Retrieves a node. Indexes are clamped to the valid range. 
	 * 
	 * @param index node index to retrieve
	 * @return nodes[clamp(0, nodes.length-1)]
	 */
	private Vector retrieve(int index) {
		if (index < 0)
			return fastRetrieve(0);

		if (index >= nodes.size())
			return fastRetrieve(nodes.size()-1);

		return fastRetrieve(index);
	}

	public Vector fastRetrieve(int index) {
		return nodes.get(index).getPosition();
	}

	@Override
	public Vector getPosition(double position) {
		if (position > 1)
			return null;

		position *= scaling;

		final int index = (int) Math.floor(position);
		final double remainder = position - index;

		final Vector a = coeffA[index];
		final Vector b = coeffB[index];
		final Vector c = coeffC[index];
		final Vector d = coeffD[index];

		return a.clone().multiply(remainder).add(b).multiply(remainder).add(c).multiply(remainder).add(d);
	}

	@Override
	public Vector get1stDerivative(double position) {
		if (position > 1)
			return null;

		position *= scaling;

		final int index = (int) Math.floor(position);
		//final double remainder = position - index;

		final Vector a = coeffA[index];
		final Vector b = coeffB[index];
		final Vector c = coeffC[index];

		return a.clone().multiply(1.5*position - 3.0*index).add(b).multiply(2.0*position).add(a.clone().multiply(1.5*index).subtract(b).multiply(2.0*index)).add(c).multiply(scaling);
	}

	@Override
	public double arcLength(double positionA, double positionB) {
		if (positionA > positionB)
			return arcLength(positionB, positionA);

		positionA *= scaling;
		positionB *= scaling;

		final int indexA = (int) Math.floor(positionA);
		final double remainderA = positionA - indexA;

		final int indexB = (int) Math.floor(positionB);
		final double remainderB = positionB - indexB;

		return arcLengthRecursive(indexA, remainderA, indexB, remainderB);
	}

	/**
	 * Assumes a < b
	 * 
	 * @param indexLeft
	 * @param remainderLeft
	 * @param indexRight
	 * @param remainderRight
	 * @return
	 */
	private double arcLengthRecursive(int indexLeft, double remainderLeft, int indexRight, double remainderRight) {
		switch (indexRight - indexLeft) {
		case 0:
			return arcLengthRecursive(indexLeft, remainderLeft, remainderRight);

		case 1:
			// This case is merely a speed-up for a very common case
			return
					arcLengthRecursive(indexLeft, remainderLeft, 1.0) + 
					arcLengthRecursive(indexRight, 0.0, remainderRight);

		default:
			return
					arcLengthRecursive(indexLeft, remainderLeft, indexRight - 1, 1.0) + 
					arcLengthRecursive(indexRight, 0.0, remainderRight);
		}
	}

	private double arcLengthRecursive(int index, double remainderLeft, double remainderRight) {
		final Vector a = coeffA[index].clone().multiply(3.0);
		final Vector b = coeffB[index].clone().multiply(2.0);
		final Vector c = coeffC[index];

		final int nPoints = 8;

		double accum = a.clone().multiply(remainderLeft).add(b).multiply(remainderLeft).add(c).length() / 2.0;
		for (int i = 1; i < nPoints-1; ++i) {
			double t = ((double) i) / nPoints;
			t = (remainderRight-remainderLeft)*t + remainderLeft;
			accum += a.clone().multiply(t).add(b).multiply(t).add(c).length();
		}

		accum += a.clone().multiply(remainderRight).add(b).multiply(remainderRight).add(c).length() / 2.0;
		return accum * (remainderRight - remainderLeft) / nPoints;
	}
}
