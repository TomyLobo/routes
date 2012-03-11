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

package eu.tomylobo.abstraction.spout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.tomylobo.abstraction.World;
import eu.tomylobo.abstraction.block.BlockState;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.math.Vector;

public class SpoutWorld implements World {
	final org.spout.api.geo.World backend;

	SpoutWorld(org.spout.api.geo.World backend) {
		this.backend = backend;
	}

	@Override
	public String getName() {
		return backend.getName();
	}

	@Override
	public List<Player> getPlayers() {
		final Set<org.spout.api.player.Player> players = backend.getPlayers();
		final List<Player> ret = new ArrayList<Player>(players.size());
		for (org.spout.api.player.Player player : players) {
			ret.add(SpoutUtils.wrap(player));
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SpoutWorld))
			return false;

		return backend.equals(SpoutUtils.unwrap((SpoutWorld) obj));
	}

	@Override
	public int hashCode() {
		return backend.hashCode();
	}

	@Override
	public BlockState getBlockState(Vector position) {
		// TODO: spout
		final org.spout.api.material.BlockMaterial blockMaterial = backend.getBlockMaterial((int) position.getX(), (int) position.getY(), (int) position.getZ());

		final int type = blockMaterial.getId();
		final int data = blockMaterial.getData();

		switch (type) {
		/*case 63: // SIGN_POST
		case 68: // WALL_SIGN
			final String[] lines = ((org.bukkit.block.Sign) block.getState()).getLines();
			return new Sign(type, data, lines);*/

		default:
			return new BlockState(type, data);
		}
	}

	@Override
	public void setBlockState(Vector position, BlockState blockState) {
		// TODO: spout
		final org.spout.api.geo.cuboid.Block block = backend.getBlock((int) position.getX(), (int) position.getY(), (int) position.getZ());

		//BlockMaterial blockMaterial = block.getBlockMaterial();
		block.setBlockId((short) blockState.getType());
		//block.setTypeIdAndData(blockState.getType(), (byte) blockState.getData(), true);

		/*if (blockState instanceof Sign) {
			String[] lines = ((Sign) blockState).getLines();
			final org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();

			for (int i = 0; i < lines.length; ++i) {
				sign.setLine(i, lines[i]);
			}

			sign.update();
		}*/
	}

	@Override
	public int getBlockType(Vector position) {
		return backend.getBlockId((int) position.getX(), (int) position.getY(), (int) position.getZ());
	}

	@Override
	public int getBlockData(Vector position) {
		return backend.getBlockData((int) position.getX(), (int) position.getY(), (int) position.getZ());
	}
}
