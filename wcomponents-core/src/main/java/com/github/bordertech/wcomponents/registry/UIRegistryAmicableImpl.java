package com.github.bordertech.wcomponents.registry;

import com.github.bordertech.wcomponents.ErrorPage;
import com.github.bordertech.wcomponents.FatalErrorPageFactory;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.container.AbstractContainerHelper;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This UIRegistry implementation differs from the UIRegistryClassLoaderImpl in two ways.</p>
 *
 * <p>
 * 1. If the top level WComponent cannot be loaded, then an ErrorPage WComponent will be returned by
 * {@link #getUI(String)} instead of a null. The ErrorPage will be generated using the
 * {@link FatalErrorPageFactory}.</p>
 *
 * <p>
 * 2. The ErrorPage WComponent will not be cached so that subsequent calls to {@link #getUI(String)} will attempt to
 * re-load the top level component.</p>
 *
 * @author Darian Bridge
 * @since 1.0.0
 */
public class UIRegistryAmicableImpl extends UIRegistry {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(UIRegistryAmicableImpl.class);

	/**
	 * The UI registry map.
	 */
	private final Map<String, WComponent> registry = new HashMap<>();

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
	 * @param key The registration key.
	 * @return the UI for the given key. The UI may be newly created.
	 */
	@Override
	public synchronized WComponent getUI(final String key) {
		WComponent ui = registry.get(key);

		if (ui == null) {
			// Looks like we haven't tried loading this UI yet, so do it now.
			ui = loadUI(key);

			ui.setLocked(true);

			// Cache the result only if the UI was successfully loaded.
			if (ui instanceof ErrorPage) {
				LOG.debug("Returning non-cached ErrorPage WComponent. Key=" + key);
			} else {
				register(key, ui);

				LOG.debug("Returning cached WComponent. Key=" + key);
			}
		} else {
			LOG.debug("Returning cached WComponent. Key=" + key);
		}

		return ui;
	}

	/**
	 * Attempts to load a UI using the key as a class name.
	 *
	 * @param key The registration key.
	 * @return A WComponent if one could be loaded from the classpath, else an ErrorPage WComponent containing the
	 * problem.
	 */
	private static WComponent loadUI(final String key) {
		String classname = key.trim();

		try {
			Class<?> clas = Class.forName(classname);

			if (WComponent.class.isAssignableFrom(clas)) {
				WComponent instance = (WComponent) clas.newInstance();
				LOG.debug("WComponent successfully loaded with class name \"" + classname + "\".");
				return instance;
			} else {
				throw new SystemException(
						"The resource with the name \"" + classname + "\" is not a WComponent.");
			}
		} catch (Exception ex) {
			LOG.error("Unable to load a WComponent using the resource name \"" + classname + "\"",
					ex);

			// Are we in developer friendly error mode?
			boolean friendly = Config.getInstance().getBoolean(
					AbstractContainerHelper.DEVELOPER_MODE_ERROR_HANDLING, false);

			FatalErrorPageFactory factory = Factory.newInstance(FatalErrorPageFactory.class);
			WComponent errorPage = factory.createErrorPage(friendly, ex);

			return errorPage;
		}
	}
}
