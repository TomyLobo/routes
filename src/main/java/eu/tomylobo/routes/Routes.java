package eu.tomylobo.routes;

import eu.tomylobo.abstraction.CommandSender;
import eu.tomylobo.abstraction.plugin.AbstractMetaPlugin;
import eu.tomylobo.routes.commands.system.CommandSystem;
import eu.tomylobo.routes.config.RoutesConfig;
import eu.tomylobo.routes.infrastructure.TransportSystem;
import eu.tomylobo.routes.infrastructure.editor.RouteEditor;
import eu.tomylobo.routes.sign.SignHandler;
import eu.tomylobo.routes.travel.TravelAgency;

public class Routes extends AbstractMetaPlugin {
	public RoutesConfig config = new RoutesConfig();

	public CommandSystem commandSystem;
	public TravelAgency travelAgency;
	public final TransportSystem transportSystem = new TransportSystem(this);
	public SignHandler signHandler;
	public RouteEditor routeEditor;

	protected static AbstractMetaPlugin instance;
	{
		instance = this;
	}

	public static Routes getInstance() {
		return (Routes) instance;
	}

	@Override
	public void onEnable() {
		commandSystem = new CommandSystem();
		travelAgency = new TravelAgency(this);
		signHandler = new SignHandler(this);
		routeEditor = new RouteEditor(this);
		load();
	}

	public void save() {
		config.save();
		transportSystem.save();
		signHandler.save();
	}

	public void load() {
		config.load();
		transportSystem.load();
		signHandler.load();
	}

	public String getConfigFileName(String baseFileName) {
		return getDataFolder() + "/" + baseFileName;
	}

	@Override
	public boolean onCommand(CommandSender sender, String commandName, String label, String[] args) {
		return commandSystem.dispatch(sender, commandName, label, args);
	}
}
