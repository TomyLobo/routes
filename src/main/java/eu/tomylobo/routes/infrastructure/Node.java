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

package eu.tomylobo.routes.infrastructure;


import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import eu.tomylobo.math.Vector;
import eu.tomylobo.routes.util.Ini;

/**
 * Represents a node on a route.<br />
 * The {@link #tension}, {@link #bias} and {@link #continuity} fields
 * are parameters for the Kochanek-Bartels interpolation algorithm.
 *
 * @author TomyLobo
 *
 */
public class Node extends AbstractDirtyable {
	private Vector position;

	private double tension;
	private double bias;
	private double continuity;

	public Node() {
		this(new Vector(0, 0, 0));
	}

	public Node(Vector position) {
		this.position = position;
	}

	public Node(Multimap<String, Multimap<String, String>> sections, String nodeName) {
		load(sections, nodeName);
	}


	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
		setDirty();
	}

	public double getTension() {
		return tension;
	}

	public void setTension(double tension) {
		this.tension = tension;
		setDirty();
	}

	public double getBias() {
		return bias;
	}

	public void setBias(double bias) {
		this.bias = bias;
		setDirty();
	}

	public double getContinuity() {
		return continuity;
	}

	public void setContinuity(double continuity) {
		this.continuity = continuity;
		setDirty();
	}


	public void save(Multimap<String, Multimap<String, String>> sections, String nodeName) {
		final String nodeSectionName = "node " + nodeName;
		final Multimap<String, String> nodeSection = LinkedListMultimap.create();

		Ini.saveVector(nodeSection, "position.%s", position);
		nodeSection.put("tension", String.valueOf(tension));
		nodeSection.put("bias", String.valueOf(bias));
		nodeSection.put("continuity", String.valueOf(continuity));

		sections.put(nodeSectionName, nodeSection);
	}

	public void load(Multimap<String, Multimap<String, String>> sections, String nodeName) {
		final String nodeSectionName = "node " + nodeName;
		final Multimap<String, String> nodeSection = Ini.getOnlyValue(sections.get(nodeSectionName));

		position = Ini.loadVector(nodeSection, "position.%s");
		tension = Ini.getOnlyDouble(nodeSection.get("tension"));
		bias = Ini.getOnlyDouble(nodeSection.get("bias"));
		continuity = Ini.getOnlyDouble(nodeSection.get("continuity"));
	}
}
