package eu.tomylobo.abstraction.bukkit;

import org.bukkit.Bukkit;

import eu.tomylobo.abstraction.Factory;

public class BukkitFactory extends Factory {
	@Override
	protected BukkitWorld worldImpl(String worldName) {
		return BukkitUtils.wrap(Bukkit.getServer().getWorld(worldName));
	}

	private final BukkitNetwork networkInstance = new BukkitNetwork();

	@Override
	protected BukkitNetwork networkImpl() {
		return networkInstance;
	}
}
