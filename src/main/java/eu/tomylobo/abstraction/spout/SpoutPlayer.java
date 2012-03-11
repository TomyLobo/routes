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

import org.spout.api.entity.PlayerController;
import org.spout.api.inventory.ItemStack;

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.math.Location;

public class SpoutPlayer extends SpoutEntity implements Player {
	final org.spout.api.player.Player backend;
	public SpoutPlayer(org.spout.api.player.Player backend) {
		super(backend.getEntity());

		this.backend = backend;
	}

	@Override
	public void teleport(Location location, boolean withAngles, boolean notify) {
		super.teleport(location, withAngles, notify);
		if (notify) {
			Environment.network().sendPlayerPosition(this, location.getPosition());
		}
	}

	@Override
	public int getItemTypeInHand() {
		final ItemStack currentItem = backend.getEntity().getInventory().getCurrentItem();
		if (currentItem == null)
			return 0;

		return currentItem.getMaterial().getId();
	}

	@Override
	public boolean getAllowFlight() {
		// TODO: spout
		return true;
	}

	@Override
	public void setAllowFlight(boolean flight) {
		// TODO: spout
	}

	@Override
	public void sendMessage(String message) {
		backend.sendMessage(message);
	}

	@Override
	public String getName() {
		return backend.getName();
	}

	@Override
	public boolean hasPermission(String permission) {
		return backend.hasPermission(permission);
	}

	@Override
	public Location getEyeLocation() {
		// TODO: spout
		org.spout.api.entity.Entity entity = backend.getEntity();
		PlayerController controller = (PlayerController) entity.getController();
		boolean isSneaking = controller == null;
		final double eyeHeight = isSneaking ? 1.54 : 1.62;
		return getLocation().add(0, eyeHeight, 0);
		//return SpoutUtils.wrap(backend.getEyeLocation());
	}
}
