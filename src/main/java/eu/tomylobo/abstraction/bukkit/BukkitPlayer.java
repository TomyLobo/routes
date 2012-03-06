package eu.tomylobo.abstraction.bukkit;

import eu.tomylobo.abstraction.Factory;
import eu.tomylobo.abstraction.Player;
import eu.tomylobo.math.Location;

public class BukkitPlayer extends BukkitEntity implements Player {
	public BukkitPlayer(org.bukkit.entity.Player backend) {
		super(backend);
	}

	@Override
	public void teleport(Location location, boolean withAngles, boolean notify) {
		super.teleport(location, withAngles, notify);
		if (notify) {
			Factory.network().sendPlayerPosition(this, location.getPosition());
		}
	}

	@Override
	public int getItemTypeInHand() {
		return ((org.bukkit.entity.Player) backend).getItemInHand().getTypeId();
	}

	@Override
	public boolean getAllowFlight() {
		return ((org.bukkit.entity.Player) backend).getAllowFlight();
	}

	@Override
	public void setAllowFlight(boolean flight) {
		((org.bukkit.entity.Player) backend).setAllowFlight(flight);
	}

	@Override
	public void sendMessage(String message) {
		((org.bukkit.entity.Player) backend).sendMessage(message);
	}

	@Override
	public String getName() {
		return ((org.bukkit.entity.Player) backend).getName();
	}

	@Override
	public boolean hasPermission(String permission) {
		return ((org.bukkit.entity.Player) backend).hasPermission(permission);
	}

	@Override
	public Location getEyeLocation() {
		return BukkitUtils.wrap(((org.bukkit.entity.Player) backend).getEyeLocation());
	}
}
