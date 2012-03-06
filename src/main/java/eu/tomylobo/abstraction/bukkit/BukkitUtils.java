package eu.tomylobo.abstraction.bukkit;

import eu.tomylobo.abstraction.CommandSender;
import eu.tomylobo.abstraction.Entity;
import eu.tomylobo.abstraction.Player;
import eu.tomylobo.abstraction.World;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;

public class BukkitUtils {
	public static BukkitWorld wrap(org.bukkit.World backend) {
		return new BukkitWorld(backend);
	}

	public static Vector wrap(org.bukkit.util.Vector vector) {
		return new Vector(vector.getX(), vector.getY(), vector.getZ());
	}

	public static org.bukkit.util.Vector unwrap(Vector vector) {
		return new org.bukkit.util.Vector(vector.getX(), vector.getY(), vector.getZ());
	}


	public static Location wrap(org.bukkit.Location location) {
		return new Location(
				wrap(location.getWorld()),
				wrap(location.toVector()),
				location.getYaw(), location.getPitch()
		);
	}

	public static org.bukkit.Location unwrap(Location location) {
		return unwrap(location.getPosition()).toLocation(
				unwrap(location.getWorld()),
				location.getYaw(), location.getPitch()
		);
	}


	public static BukkitPlayer wrap(org.bukkit.entity.Player player) {
		return new BukkitPlayer(player);
	}

	public static BukkitEntity wrap(org.bukkit.entity.Entity entity) {
		if (entity instanceof org.bukkit.entity.Player)
			return wrap((org.bukkit.entity.Player) entity);

		return new BukkitEntity(entity);
	}

	public static org.bukkit.entity.Entity unwrap(Entity entity) {
		return ((BukkitEntity) entity).backend;
	}
	public static org.bukkit.entity.Entity unwrap(Player player) {
		return unwrap((Entity) player);
	}

	public static CommandSender wrap(org.bukkit.command.CommandSender sender) {
		if (sender instanceof org.bukkit.entity.Player)
			return wrap((org.bukkit.entity.Player) sender);

		return new BukkitCommandSender(sender);
	}

	public static org.bukkit.World unwrap(World world) {
		return ((BukkitWorld) world).backend;
	}

	public static org.bukkit.command.CommandSender unwrap(CommandSender sender) {
		return ((BukkitCommandSender) sender).backend;
	}
}
