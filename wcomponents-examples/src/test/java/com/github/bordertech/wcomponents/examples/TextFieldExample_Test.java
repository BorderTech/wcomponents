package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link TextFieldExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class TextFieldExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new TextFieldExample_Test.
	 */
	public TextFieldExample_Test() {
		super(new TextFieldExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();
		driver.findWTextField(byWComponentPath("WTextField[0]")).sendKeys("1234567890");
		Assert.assertEquals("First TextField should have a max length of 6",
				"123456",
				driver.findWTextField(byWComponentPath("WTextField[0]")).getValue());
		// test of value of a read only field
		Assert.assertEquals("Third TextField should be read only",
				"This is read only.",
				driver.findWTextField(byWComponentPath("WTextField[2]")).getText());
		// test of read-only-ness of a read-only field
		Assert.assertTrue("Third WTextField should be read only", driver.findWTextField(byWComponentPath("WTextField[2]")).isReadOnly());
		Assert.assertFalse("Fourth TextField should be disabled", driver.findWTextField(byWComponentPath("WTextField[3]")).isEnabled());
		driver.findElement(byWComponentPath("WButton")).click();
		Assert.assertTrue("Fourth TextField should be enabled", driver.findWTextField(byWComponentPath("WTextField[3]")).isEnabled());
	}
}
