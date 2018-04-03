package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link TextAreaExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class TextAreaExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new TextAreaExample_Test.
	 */
	public TextAreaExample_Test() {
		super(new TextAreaExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		Assert.assertTrue("First TextArea should be enabled",
				driver.findWTextArea(byWComponentPath("WTextArea[0]")).isEnabled());

		Assert.assertTrue("Second TextArea should be read-only",
				driver.findWTextArea(byWComponentPath("WTextArea[1]")).isReadOnly());

		Assert.assertTrue("Third TextArea should be editable",
				driver.findWTextArea(byWComponentPath("WTextArea[2]")).isEnabled());

		Assert.assertTrue("Fourth TextArea should be read only",
				driver.findWTextArea(byWComponentPath("WTextArea[3]")).isReadOnly());

		Assert.assertEquals("Fourth TextArea should be read only",
				"This is read only.",
				driver.findWTextArea(byWComponentPath("WTextArea[3]")).getText());

		Assert.assertFalse("Fifth TextArea should be disabled",
				driver.findWTextArea(byWComponentPath("WTextArea[4]")).isEnabled());

		driver.findWTextArea(byWComponentPath("WTextArea[5]")).clearContent();
		driver.findWTextArea(byWComponentPath("WTextArea[8]")).sendKeys("ABC");
		driver.findElement(byWComponentPath("WButton[1]")).click();

		Assert.assertTrue("Fifth TextArea should be enabled",
				driver.findWTextArea(byWComponentPath("WTextArea[4]")).isEnabled());
	}
}
