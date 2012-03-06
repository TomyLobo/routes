package eu.tomylobo.abstraction.bukkit;

import eu.tomylobo.abstraction.CommandSender;

public class BukkitCommandSender implements CommandSender {
	final org.bukkit.command.CommandSender backend;

	public BukkitCommandSender(org.bukkit.command.CommandSender backend) {
		this.backend = backend;
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
	public boolean equals(Object obj) {
		if (!(obj instanceof BukkitCommandSender))
			return false;

		return backend.equals(BukkitUtils.unwrap((BukkitCommandSender) obj));
	}

	@Override
	public int hashCode() {
		return backend.hashCode();
	}
}
