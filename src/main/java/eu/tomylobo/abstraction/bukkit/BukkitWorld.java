package eu.tomylobo.abstraction.bukkit;

import java.util.ArrayList;
import java.util.List;

import eu.tomylobo.abstraction.Player;
import eu.tomylobo.abstraction.World;

public class BukkitWorld implements World {
	final org.bukkit.World backend;

	BukkitWorld(org.bukkit.World backend) {
		this.backend = backend;
	}

	@Override
	public String getName() {
		return backend.getName();
	}

	@Override
	public List<Player> getPlayers() {
		final List<org.bukkit.entity.Player> players = backend.getPlayers();
		final List<Player> ret = new ArrayList<Player>(players.size());
		for (org.bukkit.entity.Player player : players) {
			ret.add(BukkitUtils.wrap(player));
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BukkitWorld))
			return false;

		return backend.equals(BukkitUtils.unwrap((BukkitWorld) obj));
	}

	@Override
	public int hashCode() {
		return backend.hashCode();
	}
}
