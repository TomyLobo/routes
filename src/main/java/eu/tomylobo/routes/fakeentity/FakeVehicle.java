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

package eu.tomylobo.routes.fakeentity;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import net.minecraft.server.Packet23VehicleSpawn;

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.entity.VehicleType;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;

/**
 * A client-side-only entity spawned through {@link Packet23VehicleSpawn}.
 *
 * @author TomyLobo
 *
 */
public class FakeVehicle extends FakeEntity {
	private final int vehicleTypeId;
	public FakeVehicle(Location location, VehicleType vehicleType) {
		super(location, vehicleType);

		this.vehicleTypeId = vehicleType.getId();
	}

	@Override
	public void sendImplementation(Player player) {
		Vector position = location.getPosition();
		Environment.network().sendSpawnOther(
				player, entityId,
				position.getX(), position.getY(), position.getZ(),
				vehicleTypeId, 0
		);

		setOrientation(location);
	}

	private static final ArrayListMultimap<Integer, FakeVehicle> pool = ArrayListMultimap.create();

	public static FakeVehicle poolCreate(Location location, VehicleType vehicleType) {
		final List<FakeVehicle> list = pool.get(vehicleType.getId());
		if (!list.isEmpty()) {
			final FakeVehicle removed = list.remove(0);

			if (removed != null) {
				if (!removed.getLocation().equals(location)) {
					removed.teleport(location);
				}

				return removed;
			}
		}

		final FakeVehicle entity = new FakeVehicle(location, vehicleType);
		return entity;
	}

	public void poolFree() {
		pool.put(vehicleTypeId, this);
	}

	public static void poolClear(VehicleType vehicleType) {
		for (FakeEntity entity : pool.removeAll(vehicleType.getId())) {
			entity.remove();
		}
	}
/*
	@Override
	public EntityType getType() {
		return OtherType.fromId(vehicleTypeId).toEntityType();
	}
*/
}
