package eu.tomylobo.abstraction;

import eu.tomylobo.math.Location;

public interface Player extends Entity, CommandSender {
	int getItemTypeInHand();

	boolean getAllowFlight();
	void setAllowFlight(boolean b);

	Location getEyeLocation();
}
