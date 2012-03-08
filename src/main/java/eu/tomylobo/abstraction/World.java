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

package eu.tomylobo.abstraction;

import java.util.List;

import eu.tomylobo.abstraction.block.BlockState;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.math.Vector;

public interface World {
	String getName();
	List<Player> getPlayers();
	BlockState getBlockState(Vector position);

	int getBlockType(Vector position);
	int getBlockData(Vector position);

	void setBlockState(Vector position, BlockState blockState);
}
