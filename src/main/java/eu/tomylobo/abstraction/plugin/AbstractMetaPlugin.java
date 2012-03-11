package eu.tomylobo.abstraction.plugin;

import java.io.File;

import eu.tomylobo.abstraction.CommandSender;

public abstract class AbstractMetaPlugin implements MetaPlugin {
	protected FrameworkPlugin frameworkPlugin;

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
		return false;
	}

	public File getDataFolder() {
		return frameworkPlugin.getDataFolder();
	}
}