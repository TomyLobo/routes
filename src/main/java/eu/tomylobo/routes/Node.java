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

package eu.tomylobo.routes;

import org.bukkit.util.Vector;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import eu.tomylobo.routes.util.Ini;

public class Node {
	private Vector position;

	public Node(Vector position) {
		this.position = position;
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public void save(Multimap<String, Multimap<String, String>> sections, String nodeName) {
		final String nodeSectionName = "node " + nodeName;
		final Multimap<String, String> nodeSection = LinkedListMultimap.create();

		Ini.saveVector(nodeSection, "position.%s", position);

		sections.put(nodeSectionName, nodeSection);
	}
}
