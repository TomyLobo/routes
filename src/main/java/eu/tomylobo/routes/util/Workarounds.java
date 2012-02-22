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

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public class Workarounds {
	public static Location getEyeLocation(final Player player) {
		final Location eyeLocation = player.getLocation();
		double eyeHeight = player.getEyeHeight(true);
		if (player.isSneaking()) {
			eyeHeight -= 0.08;
		}
		eyeLocation.add(0, eyeHeight, 0);
		return eyeLocation;
	}

	public static Location getLocation(BlockState state) {
		return state.getBlock().getLocation();
	}
}
