package com.github.bordertech.wcomponents.test.selenium.driver;

import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * <p>
 * WebDriverType implementation for a WebDriver created at runtime via configuration.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class ParameterizedWebDriverType extends WebDriverType<WebDriver> {

	/**
	 * An optional String for the name of the current test.
	 */
	private final String testClassName;

	/**
	 * Default constructor to use standard global configuration.
	 */
	public ParameterizedWebDriverType() {
		testClassName = null;
	}

	/**
	 * Construct this instance to look for configuration for a specific test class.
	 *
	 * @param testClassName the name of the test class. Nullable.
	 */
	public ParameterizedWebDriverType(final String testClassName) {
		this.testClassName = testClassName;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getDriverTypeName() {
		return getDriverClassName();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public WebDriver getDriverImplementation() {

		setSystemProperty();
		return SeleniumWComponentsWebDriverFactory.createBackingDriver(getDriverClassName());
	}

	/**
	 * Used to set any necessary system properties prior to the driver's construction.
	 */
	protected void setSystemProperty() {

		// Properties may be empty, but will not be null.
		Properties props = ConfigurationProperties.getTestSeleniumParameterisedDriverSysProperties();
		System.getProperties().putAll(props);

	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public DesiredCapabilities getDefaultDriverCapabilities() {
		throw new SystemException("Capabilities not supported by ParameterizedWebDriverType.");
	}

	/**
	 * @return the class name for the driver implementation.
	 */
	private String getDriverClassName() {
		if (StringUtils.isNotBlank(testClassName)) {
			return ConfigurationProperties.getTestSeleniumParameterisedDriver(testClassName);
		}

		final String classname = ConfigurationProperties.getTestSeleniumParameterisedDriver();
		if (StringUtils.isBlank(classname)) {
			throw new SystemException("No parameter defined for " + getClass().getName() + " expected parameter: "
					+ ConfigurationProperties.TEST_SELENIUM_PARAMETERISED_DRIVER);
		}

		return classname;
	}

}
