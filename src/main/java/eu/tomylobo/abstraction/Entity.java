package eu.tomylobo.abstraction;

import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;

public interface Entity {
	int getEntityId();

	Location getLocation();
	void teleport(Location location);
	void teleport(Location location, boolean withAngles, boolean notify);

	void setVelocity(Vector multiply);

	Entity getPassenger();
	boolean setPassenger(Entity passenger);

	boolean isDead();
	void remove();
}
