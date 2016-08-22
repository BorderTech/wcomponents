package com.github.bordertech.wcomponents.util;

import org.apache.commons.configuration.Configuration;

/**
 * SPI interface for classes that can load a custom configuration for WComponents.
 *
 * @author Joshua Barclay
 * @since 1.2.5
 */
public interface ConfigurationLoader {

	/**
	 * <p>
	 * Provides the configuration for this loader. The result of this configuration will be added to a composite
	 * configuration along with WComponents and the configuration from other ConfigurationLoaders.</p>
	 *
	 * @return The custom configuration for this loader.
	 */
	Configuration getConfiguration();

}
