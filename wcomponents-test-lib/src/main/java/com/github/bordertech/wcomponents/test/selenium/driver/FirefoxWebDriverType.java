package com.github.bordertech.wcomponents.test.selenium.driver;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * <p>
 * WebDriverType implementation for Firefox.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the
 * implementation.</p>
 *
 * @author Joshua Barclay
 * @author Rick Brown
 * @since 1.2.0
 */
public class FirefoxWebDriverType extends WebDriverType<FirefoxDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getDriverTypeName() {
		return "firefox";
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public FirefoxDriver getDriverImplementation() {
		WebDriverManager.getInstance(DriverManagerType.FIREFOX).setup();
		FirefoxOptions options = new FirefoxOptions(getCapabilities());
		FirefoxBinary binary = getFirefoxBinary();
		FirefoxProfile profile = getFirefoxProfile();
		if (profile != null) {
			// it gets angry if you give it a null profile
			options.setProfile(profile);
		}
		// options.setHeadless(true);
		options.setBinary(binary);
		return new FirefoxDriver(options);
	}

	/**
	 * Get the FirefoxBinary to use. Exists to allow subclasses to override.
	 *
	 * @return the default Firefox binary.
	 */
	public FirefoxBinary getFirefoxBinary() {
		return new FirefoxBinary();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public DesiredCapabilities getDefaultDriverCapabilities() {
		return DesiredCapabilities.firefox();
	}

	/**
	 * Get the Firefox Profile to use. Exists to allow subclasses to override.
	 *
	 * @return the default Firefox profile (null).
	 */
	public FirefoxProfile getFirefoxProfile() {
		return null;
	}
}
