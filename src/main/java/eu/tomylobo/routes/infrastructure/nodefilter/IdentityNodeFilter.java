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
import eu.tomylobo.routes.infrastructure.Node;

public class IdentityNodeFilter implements NodeFilter {
	@Override
	public List<Node> filter(World world, List<Node> nodes) {
		return new ArrayList<Node>(nodes); // TEMP, to test if it'S being called correctly
	}

	private static IdentityNodeFilter instance;
	public static NodeFilter getInstance() {
		if (instance == null)
			instance = new IdentityNodeFilter();

		return instance;
	}
}
