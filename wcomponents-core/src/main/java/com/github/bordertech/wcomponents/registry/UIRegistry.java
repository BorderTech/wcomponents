package com.github.bordertech.wcomponents.registry;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.Factory;

/**
 * The UIRegistry enables sharing of UIs between multiple sessions.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public abstract class UIRegistry {

	/**
	 * The UIRegistry singleton instance.
	 */
	private static UIRegistry instance = null;

	/**
	 * @return the singleton instance of the UIRegistry.
	 */
	public static synchronized UIRegistry getInstance() {
		if (instance == null) {
			instance = Factory.newInstance(UIRegistry.class);
		}

		return instance;
	}

	/**
	 * Registers the given user interface with the given key.
	 *
	 * @param key the registration key.
	 * @param ui the user interface to register.
	 */
	public abstract void register(String key, WComponent ui);

	/**
	 * Is there a user interface registered under the given key.
	 *
	 * @param key the registration key.
	 * @return true if there is a UI registered with the given key.
	 */
	public abstract boolean isRegistered(String key);

	/**
	 * Retrieves the user interface which was registered with the given key.
	 *
	 * @param key the registration key
	 * @return the user interface which was registered with the given key.
	 */
	public abstract WComponent getUI(String key);
}
