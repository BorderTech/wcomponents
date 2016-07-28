package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;

/**
 * Selenium unit tests for {@link ForwardExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
//@Category(SeleniumTests.class)
//@RunWith(MultiBrowserRunner.class)
public class ForwardExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new ForwardExample_Test.
	 */
	public ForwardExample_Test() {
		super(new ForwardExample());
	}

	// Joshua-Barclay: Deactivate this test as the requirement to access an external website is not ideal for repeatable testing.
	//	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		String url = "http://www.example.com/";
		driver.findWTextField(byWComponentPath("WTextField")).clear();
		driver.findWTextField(byWComponentPath("WTextField")).sendKeys(url);
		driver.findElement(byWComponentPath("WButton")).clickNoWait();

		//Need to invoke the base driver to ensure we don't wait for the WComponent ready state.
		Assert.assertTrue("Incorrect forward location", driver.getDriver().getCurrentUrl().startsWith(url));
	}
}
