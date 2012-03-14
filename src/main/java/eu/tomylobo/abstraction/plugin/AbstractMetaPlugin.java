package eu.tomylobo.abstraction.plugin;

import java.io.File;

import eu.tomylobo.abstraction.CommandSender;
import eu.tomylobo.routes.commands.system.CommandSystem;

public abstract class AbstractMetaPlugin implements MetaPlugin {
	private FrameworkPlugin frameworkPlugin;
	private final CommandSystem commandSystem = new CommandSystem();

	@Override
	public void setFrameworkPlugin(FrameworkPlugin frameworkPlugin) {
		this.frameworkPlugin = frameworkPlugin;
	}

	@Override
	public FrameworkPlugin getFrameworkPlugin() {
		return frameworkPlugin;
	}

	@Override
	public void onEnable() { }

	@Override
	public void onLoad() { }

	@Override
	public void onDisable() { }

	@Override
	public boolean onCommand(CommandSender sender, String commandName, String label, String[] args) {
		return getCommandSystem().dispatch(sender, commandName, label, args);
	}

	public File getDataFolder() {
		return frameworkPlugin.getDataFolder();
	}

	public CommandSystem getCommandSystem() {
		return commandSystem;
	}
}