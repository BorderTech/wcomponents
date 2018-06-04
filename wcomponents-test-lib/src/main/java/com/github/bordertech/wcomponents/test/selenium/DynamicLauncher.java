package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.registry.UIRegistry;
import org.apache.commons.lang3.StringUtils;

/**
 * This class extends SeleniumLauncher to allow the launched UI to be determined (and reconfigured) at runtime via the
 * setComponentToLaunch method.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class DynamicLauncher extends SeleniumLauncher {

	/**
	 * The name of the class to launch for the UI.
	 */
	private String uiRegistryKey = null;

	/**
	 * <p>
	 * Set the instance for the UI to launch.</p>
	 * <p>
	 * A null value will revert to the Default PlainLauncher parameter behavior.
	 * </p>
	 *
	 * @param uniqueId the uniqueId to register the component.
	 * @param componentToLaunch the WComponent to launch.
	 * @return the registered component as a singleton.
	 */
	public WApplication setComponentToLaunch(final String uniqueId, final WApplication componentToLaunch) {
		setCurrentKey(uniqueId);
		// Check if already registered
		WApplication appl = getRegisteredComponent(uniqueId);
		if (appl != null) {
			return appl;
		}
		// Register the componenent
		registerComponent(uniqueId, componentToLaunch);
		return componentToLaunch;
	}

	/**
	 * Register the component.
	 *
	 * @param uniqueId the uniqueId to register the component.
	 * @param componentToLaunch the WComponent to launch.
	 */
	public void registerComponent(final String uniqueId, final WApplication componentToLaunch) {
		// Must be locked
		if (!componentToLaunch.isLocked()) {
			componentToLaunch.setLocked(true);
		}
		UIRegistry.getInstance().register(uniqueId, componentToLaunch);
	}

	/**
	 * Retrieve a registered component.
	 *
	 * @param key the key of the registered component
	 * @return the registers component or null if not registered
	 */
	public WApplication getRegisteredComponent(final String key) {
		UIRegistry reg = UIRegistry.getInstance();
		if (reg.isRegistered(key)) {
			return (WApplication) reg.getUI(key);
		}
		return null;
	}

	/**
	 * @param key the current UI to be run by the launcher.
	 */
	public void setCurrentKey(final String key) {
		this.uiRegistryKey = key;

	}

	/**
	 * @return the current UI being run by the launcher
	 */
	public String getCurrentKey() {
		return uiRegistryKey;
	}

	/**
	 * Override to return the UI Registry Key - the class name is not needed because the instance has been added to the
	 * UIRegistry in advance.
	 *
	 * @return the UI Registry Key, or the super class value if the key is blank.
	 */
	@Override
	protected String getComponentToLaunchClassName() {
		String key = getCurrentKey();
		if (StringUtils.isBlank(key)) {
			return super.getComponentToLaunchClassName();
		} else {
			return key;
		}
	}
}
