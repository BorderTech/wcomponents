package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link TextAreaExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class TextAreaExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new TextAreaExample_Test.
	 */
	public TextAreaExample_Test() {
		super(new TextAreaExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		Assert.assertTrue("First TextArea should be enabled",
				driver.findElement(byWComponentPath("WTextArea[0]")).isEnabled());

		Assert.assertTrue("Second TextArea should be enabled",
				driver.findElement(byWComponentPath("WTextArea[1]")).isEnabled());

		Assert.assertEquals("Third TextArea should be read only",
				"This is read only.",
				driver.findElement(byWComponentPath("WTextArea[2]")).getText());

		Assert.assertFalse("Fourth TextArea should be disabled",
				driver.findElement(byWComponentPath("WTextArea[3]")).isEnabled());

		driver.findElement(byWComponentPath("WButton")).click();

		Assert.assertTrue("Fourth TextArea should be enabled",
				driver.findElement(byWComponentPath("WTextArea[3]")).isEnabled());
	}
}
