package com.github.bordertech.wcomponents.util;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;

/**
 * <p>
 * The Config class is the central access point to the WComponent configuration mechanism, and is used to read or modify
 * the current configuration.</p>
 *
 * <p>
 * The WComponent library is configured using the Apache Configuration API. This allows developers to programmatically
 * integrate the WComponent configuration with whatever mechanism is used to configure their applications.
 * <b>Note:</b>To prevent against accidental modifications, the default WComponent configuration is read-only, and any
 * attempt to modify it will result in a runtime exception.</p>
 *
 * <p>
 * For more information, refer to the "Configuring the WComponent library" section in the WComponent user's guide.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class Config {

	/**
	 * Contains the complete set of property change listeners that have registered with this class.
	 */
	private static final Set<PropertyChangeListener> PROPERTY_CHANGE_LISTENERS = new HashSet<>();

	/**
	 * The current configuration.
	 */
	private static Configuration configuration = new DefaultInternalConfiguration();

	/**
	 * Prevent instantiation of this utility class.
	 */
	private Config() {
	}

	/**
	 * @return the current configuration.
	 */
	public static Configuration getInstance() {
		return configuration;
	}

	/**
	 * Resets the configuration back to the default internal configuration. All configuration changes which have been
	 * made will be lost. This method is primarily intended for unit testing.
	 */
	public static synchronized void reset() {
		configuration = new DefaultInternalConfiguration();
		notifyListeners();
	}

	/**
	 * Creates a deep-copy of the given configuration. This is useful for unit-testing.
	 *
	 * @param original the configuration to copy.
	 * @return a copy of the given configuration.
	 */
	public static Configuration copyConfiguration(final Configuration original) {
		Configuration copy = new MapConfiguration(new HashMap<String, Object>());

		for (Iterator<?> i = original.getKeys(); i.hasNext();) {
			String key = (String) i.next();
			Object value = original.getProperty(key);

			if (value instanceof List) {
				value = new ArrayList((List) value);
			}

			copy.setProperty(key, value);
		}

		return copy;
	}

	/**
	 * Sets the current configuration.
	 *
	 * @param configuration the configuration to set.
	 */
	public static synchronized void setConfiguration(final Configuration configuration) {
		Config.configuration = configuration;
		notifyListeners();
	}

	/**
	 * This method notifies all the {@link PropertyChangeListener}s that have registered with this object that a change
	 * has occurred.
	 */
	public static void notifyListeners() {
		// The trivial case is when there are no listeners.
		if (PROPERTY_CHANGE_LISTENERS.isEmpty()) {
			return;
		}

		// A possible improvement here would be to verify that one or more of the properties has actually changed due to
		// the refresh.
		//
		// If we get here, then there is at least one listener who wants to be notified when a refresh occurs. The first
		// step in this notification process is to create the PropertyChangeEvent object that will be sent to each of
		// the registered PropertyChangeListeners.
		//
		// PropertyChangeEvent event = new PropertyChangeEvent(this, "propertiesRefresh", null, null);
		//
		// Finally, iterate through the complete set of PropertyChangeListeners and notify them that a change has
		// occurred.
		for (PropertyChangeListener listener : PROPERTY_CHANGE_LISTENERS) {
			listener.propertyChange(null);
		}
	}

	/**
	 * Registers a property change listener to receive notifications of configuration changes. Note that the listener
	 * will only be notified when this class knows that the configuration has changed. No notification is sent if a
	 * {@link Configuration} changes its values internally. In this case, a manual call to {@link #notifyListeners()}
	 * must be made to notify the listeners.
	 *
	 * @param listener the listener to add.
	 */
	public static void addPropertyChangeListener(final PropertyChangeListener listener) {
		PROPERTY_CHANGE_LISTENERS.add(listener);
	}
}
