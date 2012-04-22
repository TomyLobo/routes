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

package eu.tomylobo.routes.infrastructure.nodefilter;

import java.util.ArrayList;
import java.util.List;

import eu.tomylobo.abstraction.World;
import eu.tomylobo.math.Vector;
import eu.tomylobo.routes.infrastructure.Node;

public class DropToFloorNodeFilter implements NodeFilter {
	private static final double yOffset = 0;

	@Override
	public List<Node> filter(World world, List<Node> nodes) {
		List<Node> newNodes = new ArrayList<Node>(nodes.size());

		for (Node node : nodes) {
			final Node newNode = new Node(node);
			final Vector floorPosition = node.getPosition().floor();
			final Vector offset = node.getPosition().subtract(floorPosition).setY(yOffset);
			final Vector newPosition;
			if (isSolid(world, floorPosition)) {
				newPosition = findUp(world, floorPosition);
			}
			else {
				newPosition = findDown(world, floorPosition);
			}

			newNode.setPosition(newPosition.add(offset));
			newNodes.add(newNode);
		}

		return newNodes;
	}

	private static Vector findDown(World world, Vector position) {
		double y = position.getY();
		while (y > 0) {
			final Vector below = position.setY(--y);
			if (isSolid(world, below))
				break;

			position = below;
		}

		return position;
	}

	private Vector findUp(World world, Vector position) {
		double y = position.getY();
		while (y < world.getMaxY() && isSolid(world, position)) {
			position = position.setY(++y);
		}

		return position;
	}

	private static boolean isSolid(World world, Vector position) {
		return world.getBlockType(position) != 0;
	}
}
