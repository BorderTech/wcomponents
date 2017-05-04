package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * <p>
 * WebDriverType implementation for PhantomJS.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class PhantomJSWebDriverType extends WebDriverType<PhantomJSDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getDriverTypeName() {
		return "phantomjs";
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public PhantomJSDriver getDriverImplementation() {
		return new PhantomJSDriver(getCapabilities());
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public DesiredCapabilities getDefaultDriverCapabilities() {
		return DesiredCapabilities.phantomjs();
	}

//	@Override
//	public DesiredCapabilities getCapabilities() {
//		DesiredCapabilities capabilities = super.getCapabilities();
////
//		ArrayList<String> cliArgsCap = new ArrayList<String>();
//		cliArgsCap.add("--webdriver-loglevel=DEBUG");
//		cliArgsCap.add("--debug=true");
////		cliArgsCap.add("--disk-cache=true");
////		cliArgsCap.add("--disk-cache-path=");
////		cliArgsCap.add("--web-security=false");
////		cliArgsCap.add("--ssl-protocol=any");
////		cliArgsCap.add("--ignore-ssl-errors=true");
////		capabilities.setCapability("takesScreenshot", true);
//		capabilities.setCapability(
//				PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
//		capabilities.setCapability(
//				PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS,
//				new String[]{"--logLevel=2"});
//		return capabilities;
//	}
}
