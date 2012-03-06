package eu.tomylobo.abstraction;

import eu.tomylobo.abstraction.bukkit.BukkitFactory;

public abstract class Factory {
	private static final Factory instance = new BukkitFactory();

	protected abstract World worldImpl(String worldName);
	public static World world(String worldName) {
		return instance.worldImpl(worldName);
	}

	protected abstract Network networkImpl();
	public static Network network() {
		return instance.networkImpl();
	}
}
