package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WTextExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WTextExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new WTextExample_Test.
	 */
	public WTextExample_Test() {
		super(new WTextExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		final String staticText = "Example shared text";
		final String dynamicText = "The current date is ";
		final String beanText = "(beanAttribute) for bean loaded at ";
		final String beanProviderText = "(innerBean.innerAttribute) for bean with id 123456";

		// Test initial state
		Assert.assertTrue("Dynamic model text should be present", driver.getPageSource().indexOf(
				dynamicText) != -1);
		Assert.assertTrue("Bean text should not be present", driver.getPageSource().
				indexOf(beanText) == -1);
		Assert.assertTrue("Bean provider text should not be present", driver.getPageSource().
				indexOf(beanProviderText) == -1);

		// Load bean bound bean
		driver.findElement(byWComponentPath("WButton[0]")).click();
		Assert.assertTrue("Dynamic model text should be present", driver.getPageSource().indexOf(
				dynamicText) != -1);
		Assert.assertTrue("Bean text should be present",
				driver.getPageSource().indexOf(beanText) != -1);
		Assert.assertTrue("Bean provider text should not be present", driver.getPageSource().
				indexOf(beanProviderText) == -1);

		// Load bean provider bound bean
		driver.findElement(byWComponentPath("WButton[1]")).click();
		Assert.assertTrue("Dynamic model text should be present", driver.getPageSource().indexOf(
				dynamicText) != -1);
		Assert.assertTrue("Bean text should be present",
				driver.getPageSource().indexOf(beanText) != -1);
		Assert.assertTrue("Bean provider text should be present", driver.getPageSource().indexOf(
				beanProviderText) != -1);
		Assert.assertTrue("Static model text should be present",
				driver.getPageSource().lastIndexOf(staticText) < driver.getPageSource().indexOf(
				"The following line of text is from the dynamic model."));
	}
}
