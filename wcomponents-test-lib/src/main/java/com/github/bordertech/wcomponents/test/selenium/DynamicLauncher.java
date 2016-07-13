package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.registry.UIRegistry;
import org.apache.commons.lang.StringUtils;

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
	public WComponent setComponentToLaunch(final String uniqueId, final WComponent componentToLaunch) {
		uiRegistryKey = uniqueId;

		//Only register the component if it is not already registered.
		if (!UIRegistry.getInstance().isRegistered(uiRegistryKey)) {
			UIRegistry.getInstance().register(uiRegistryKey, componentToLaunch);
			return componentToLaunch;
		} else {
			return UIRegistry.getInstance().getUI(uiRegistryKey);
		}
	}

	/**
	 * Override to return the UI Registry Key - the class name is not needed because the instance has been added to the
	 * UIRegistry in advance.
	 *
	 * @return the UI Registry Key, or the super class value if the key is blank.
	 */
	@Override
	protected String getComponentToLaunchClassName() {
		if (StringUtils.isBlank(uiRegistryKey)) {
			return super.getComponentToLaunchClassName();
		} else {
			return uiRegistryKey;
		}
	}
}
