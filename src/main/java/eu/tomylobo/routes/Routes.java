package eu.tomylobo.routes;

import eu.tomylobo.abstraction.plugin.AbstractMetaPlugin;
import eu.tomylobo.routes.commands.RoutesCommands;
import eu.tomylobo.routes.commands.TestCommands;
import eu.tomylobo.routes.commands.TravelCommands;
import eu.tomylobo.routes.config.RoutesConfig;
import eu.tomylobo.routes.infrastructure.TransportSystem;
import eu.tomylobo.routes.infrastructure.editor.RouteEditor;
import eu.tomylobo.routes.sign.SignHandler;
import eu.tomylobo.routes.travel.TravelAgency;

public class Routes extends AbstractMetaPlugin {
	protected static Routes instance;
	{
		instance = this;
	}

	public static Routes getInstance() {
		return instance;
	}

	public RoutesConfig config = new RoutesConfig(this);

	public TravelAgency travelAgency;
	public final TransportSystem transportSystem = new TransportSystem(this);
	public SignHandler signHandler;
	public RouteEditor routeEditor;

	@Override
	public void onEnable() {
		travelAgency = new TravelAgency(this);
		signHandler = new SignHandler(this);
		routeEditor = new RouteEditor(this);
		load();

		getCommandSystem().register(
				new RoutesCommands(),
				new TravelCommands(),
				new TestCommands()
		);
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
}
