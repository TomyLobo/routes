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

package eu.tomylobo.routes.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Can be used to keep statistics on certain values.
 *
 * @author TomyLobo
 *
 */
public class Statistics {
	private final List<Double> distances = new ArrayList<Double>();
	private double sum = 0;
	private double min = Double.MAX_VALUE;
	private double max = Double.MIN_VALUE;

	public void stat(double value) {
		distances.add(value);
		sum += value;

		if (value > max)
			max = value;

		if (value < min)
			min = value;
	}

	public double getSum() {
		return sum;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getMean() {
		return sum / distances.size();
	}

	public double sqrtSumSqErrors() {
		final double mean = getMean();
		double sumSqErrors = 0;

		for (double distance : distances) {
			distance -= mean;
			sumSqErrors += distance * distance;
		}

		return Math.sqrt(sumSqErrors / distances.size());
	}

	public String format() {
		return String.format("min/mean/max/rmse: %f/%f/%f/%f\n", min, getMean(), max, sqrtSumSqErrors());
	}
}
