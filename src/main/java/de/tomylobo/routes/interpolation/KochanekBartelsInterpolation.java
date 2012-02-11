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

		/*
		Kochanek-Bartels tangents (from http://en.wikipedia.org/wiki/Kochanek%E2%80%93Bartels_spline):
		d[i  ] = ta*(p[i  ]-p[i-1]) + tb*(p[i+1]-p[i  ])
		d[i+1] = tc*(p[i+1]-p[i  ]) + td*(p[i+2]-p[i+1])

		Cubic Hermite spline (from http://en.wikipedia.org/wiki/Cubic_Hermite_spline#Unit_interval_.280.2C_1.29):
		p(t) =
			h_00(t)*p[i  ] + h_10(t)*d[i  ] +
			h_01(t)*p[i+1] + h_11(t)*d[i+1]

		Inserting the tangents:
		p(t) =
			h_00(t)*p[i  ] + h_10(t)*(ta*(p[i  ]-p[i-1]) + tb*(p[i+1]-p[i  ])) +
			h_01(t)*p[i+1] + h_11(t)*(tc*(p[i+1]-p[i  ]) + td*(p[i+2]-p[i+1]))

		Inserting the Hermite basis functions:
		p(t) =
			( 2*t^3 - 3t^2 + 1)*p[i  ] + (t^3-2*t^2+t)*(ta*(p[i  ]-p[i-1]) + tb*(p[i+1]-p[i  ])) +
			(-2*t^3 + 3*t^2   )*p[i+1] + (t^3-  t^2  )*(tc*(p[i+1]-p[i  ]) + td*(p[i+2]-p[i+1]))

		Sorting the formula for node indexes:
		p(t) =
			(t^3-2*t^2+t)*ta*(-p[i-1]) +
			( 2*t^3 - 3t^2 + 1)*p[i  ] + (t^3-2*t^2+t)*(ta*p[i  ] - tb*p[i  ]) - (t^3-t^2)*tc*p[i  ] +
			(t^3-2*t^2+t)*tb*p[i+1] + (-2*t^3 + 3*t^2   )*p[i+1] + (t^3-  t^2  )*(tc*p[i+1] - td*p[i+1]) +
			(t^3-  t^2  )*td*p[i+2]

		Factoring out the nodes p[i-1..i+2]:
		p(t) =
			p[i-1]*((-ta           )*t^3 + ( 2*ta             )*t^2 + (-ta   )*t      ) +
			p[i  ]*(( ta-tb-tc   +2)*t^3 + (-2*ta+2*tb+tc   -3)*t^2 + ( ta-tb)*t + (1)) +
			p[i+1]*((    tb+tc-td-2)*t^3 + (     -2*tb-tc+td+3)*t^2 + (    tb)*t      ) +
			p[i+2]*((          td  )*t^3 + (             -td  )*t^2                   )

		matrix representation:
			    | p[i-1] |      p[i  ]     | p[i+1]        | p[i+2] |
			----+--------+-----------------+---------------+--------+
			t^3 |    -ta |    ta-  tb-tc+2 |    tb+tc-td-2 |     td |
			t^2 |   2*ta | -2*ta+2*tb+tc-3 | -2*tb-tc+td+3 |    -td |
			t   |    -ta |    ta-  tb      |    tb         |      0 |
			1   |      0 |               1 |             0 |      0 |
		*/

		final int nNodes = nodes.size();
		coeffA = new Vector[nNodes];
		coeffB = new Vector[nNodes];
		coeffC = new Vector[nNodes];
		coeffD = new Vector[nNodes];

		for (int i = 0; i < nNodes; ++i) {
			coeffA[i] = linearCombination(i,  -ta,    ta-  tb-tc+2,    tb+tc-td-2,  td);
			coeffB[i] = linearCombination(i, 2*ta, -2*ta+2*tb+tc-3, -2*tb-tc+td+3, -td);
			coeffC[i] = linearCombination(i,  -ta,    ta-  tb     ,    tb        ,   0);
			coeffD[i] = retrieve(i); // linearCombination(i,    0,               1,             0,   0);

		}
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

		position *= nodes.size() - 1;

		final int index = (int) Math.floor(position);
		final double remainder = position - index;

		final Vector a = coeffA[index];
		final Vector b = coeffB[index];
		final Vector c = coeffC[index];
		final Vector d = coeffD[index];

		return a.clone().multiply(remainder).add(b).multiply(remainder).add(c).multiply(remainder).add(d);
	}

	/*
	Formula for position in monomial form:
		a*t^3 + b*t^2 + c*t + d
	1st derivative in monomial form:
		a*3*t^2 + 2*b*t + c
	1st derivative in (modified) Horner form:
		(a*1.5*t + b)*2*t + c
	*/
	@Override
	public Vector getEye(double position) {
		if (position > 1)
			return null;

		position *= nodes.size() - 1;

		final int index = (int) Math.floor(position);
		final double remainder = position - index;

		final Vector a = coeffA[index];
		final Vector b = coeffB[index];
		final Vector c = coeffC[index];

		return a.clone().multiply(1.5*remainder).add(b).multiply(2*remainder).add(c);
	}
}
