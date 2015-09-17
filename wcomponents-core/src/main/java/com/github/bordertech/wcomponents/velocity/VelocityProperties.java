package com.github.bordertech.wcomponents.velocity;

import java.util.Map;

/**
 * Components which use the VelocityLayout may implement this interface, in which case the Map given in the
 * getVelocityMap function will be installed into the VelocityContext.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public interface VelocityProperties {

	/**
	 * Retrieves a map of key/value pairs to be installed into the VelocityContext.
	 *
	 * @return the map to be inserted into the velocity context.
	 */
	Map getVelocityMap();

	/**
	 * This method will be called after the VelocityLayout has painted the template. Implementations may clear the map
	 * at this point, since Velocity has done its thing.
	 */
	void mapUsed();
}
