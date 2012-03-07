package eu.tomylobo.routes;

import eu.tomylobo.routes.commands.system.CommandSystem;
import eu.tomylobo.routes.config.RoutesConfig;
import eu.tomylobo.routes.infrastructure.TransportSystem;
import eu.tomylobo.routes.infrastructure.editor.RouteEditor;
import eu.tomylobo.routes.sign.SignHandler;
import eu.tomylobo.routes.travel.TravelAgency;

public class Routes {
	private static Routes instance;
	{
		instance = this;
	}

	public static Routes getInstance() {
		return instance;
	}

	public RoutesConfig config = new RoutesConfig();
	
	public CommandSystem commandSystem;
	public TravelAgency travelAgency;
	public final TransportSystem transportSystem = new TransportSystem(this);
	public SignHandler signHandler;
	public RouteEditor routeEditor;

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
		return plugin.getDataFolder() + "/" + baseFileName;
	}

	public final BukkitRoutes plugin; // temporary
	public Routes(BukkitRoutes plugin) {
		this.plugin = plugin;
	}
}
