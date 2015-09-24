package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link TextFieldExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class TextFieldExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new TextFieldExample_Test.
	 */
	public TextFieldExample_Test() {
		super(new TextFieldExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		driver.findElement(byWComponentPath("WTextField[0]")).sendKeys("1234567890");
		Assert.assertEquals("First TextField should have a max length of 6",
				"123456",
				driver.findElement(byWComponentPath("WTextField[0]")).getAttribute("value"));

		Assert.assertEquals("Third TextField should be read only",
				"This is read only.",
				driver.findElement(byWComponentPath("WTextField[2]")).getText());

		Assert.assertFalse("Fourth TextField should be disabled",
				driver.findElement(byWComponentPath("WTextField[3]")).isEnabled());

		driver.findElement(byWComponentPath("WButton")).click();

		Assert.assertTrue("Fourth TextField should be enabled",
				driver.findElement(byWComponentPath("WTextField[3]")).isEnabled());
	}
}
