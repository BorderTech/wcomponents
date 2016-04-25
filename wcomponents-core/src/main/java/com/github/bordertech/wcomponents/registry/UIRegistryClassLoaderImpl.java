package com.github.bordertech.wcomponents.registry;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A UIRegistry implementation that can load UIs using the key, where the keys are the fully qualified class names.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class UIRegistryClassLoaderImpl extends UIRegistry {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(UIRegistryClassLoaderImpl.class);

	/**
	 * The UI registry map.
	 */
	private final Map<String, Object> registry = new HashMap<>();

	/**
	 * Registers the given user interface with the given key.
	 *
	 * @param key the registration key.
	 * @param ui the user interface to register.
	 */
	@Override
	public synchronized void register(final String key, final WComponent ui) {
		if (isRegistered(key)) {
			throw new SystemException("Cannot re-register a component. Key = " + key);
		}

		registry.put(key, ui);
	}

	/**
	 * Is there a user interface registered under the given key.
	 *
	 * @param key the registration key.
	 * @return true if there is a UI registered with the given key.
	 */
	@Override
	public synchronized boolean isRegistered(final String key) {
		return registry.containsKey(key);
	}

	/**
	 * Retrieves the user interface that was registered with the given key. If the UI has not been registered, this
	 * attempts to load the UI using the key as a class name.
	 *
	 * @param key the registration key
	 * @return the UI for the given key. The UI may be newly created.
	 */
	@Override
	public synchronized WComponent getUI(final String key) {
		Object obj = registry.get(key);

		if (obj == null) {
			// Looks like we haven't tried loading this UI yet, so do it now.
			obj = loadUI(key);

			// Cache the result.
			// Note that the result could be a flag indicating that no UI could be load for the given key.
			registry.put(key, obj);

			if (obj instanceof WComponent) {
				((WComponent) obj).setLocked(true);
			}
		}

		if (obj instanceof WComponent) {
			LOG.debug("Returning cached WComponent. Key=" + key);
			return (WComponent) obj;
		} else {
			return null;
		}
	}

	/**
	 * Attemps to load a ui using the key as a class name.
	 *
	 * @param key the registration key
	 * @return A WComponent if one could be loaded from the classpath, else it returns Boolean.FALSE.
	 */
	private static Object loadUI(final String key) {
		String classname = key.trim();

		try {
			Class<?> clas = Class.forName(classname);

			if (WComponent.class.isAssignableFrom(clas)) {
				Object instance = clas.newInstance();
				LOG.debug("WComponent successfully loaded with class name \"" + classname + "\".");
				return instance;
			} else {
				LOG.error("The resource with the name \"" + classname + "\" is not a WComponent.");
			}
		} catch (Exception ex) {
			LOG.error("Unable to load a WComponent using the resource name \"" + classname + "\"",
					ex);
		}

		return Boolean.FALSE;
	}
}
