package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.examples.SeleniumTests;
import com.github.bordertech.wcomponents.examples.WComponentExamplesTestCase;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWMessageBoxWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWMessagesWebElement;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Tests of WMessagesExample.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WMessagesExample_Test extends WComponentExamplesTestCase {

	/**
	 * Instantiate the tests.
	 */
	public WMessagesExample_Test() {
		super(new WMessagesExample());
	}

	@Test
	public void testMessageBoxes() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWMessagesWebElement messages = driver.findWMessages(byWComponentPath("WMessages[0]"));
		List<SeleniumWMessageBoxWebElement> messageBoxes = messages.getMessageBoxes();
		Assert.assertEquals(4, messageBoxes.size());
	}

	@Test
	public void testMessageBoxTypes() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWMessagesWebElement messages = driver.findWMessages(byWComponentPath("WMessages[0]"));
		List<SeleniumWMessageBoxWebElement> messageBoxes = messages.getMessageBoxes();
		Assert.assertEquals(4, messageBoxes.size());
		// first one is expected to be an error box
		Assert.assertTrue(messageBoxes.get(0).getAttribute("type").equals(SeleniumWMessageBoxWebElement.TYPE_ERROR));
		Assert.assertTrue(messageBoxes.get(1).getAttribute("type").equals(SeleniumWMessageBoxWebElement.TYPE_WARNING));
		Assert.assertTrue(messageBoxes.get(2).getAttribute("type").equals(SeleniumWMessageBoxWebElement.TYPE_INFO));
		Assert.assertTrue(messageBoxes.get(3).getAttribute("type").equals(SeleniumWMessageBoxWebElement.TYPE_SUCCESS));
	}

	@Test
	public void testHiddenWMessages() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWMessagesWebElement messages = driver.findWMessages(byWComponentPath("WMessages[1]"));
		Assert.assertTrue(messages.isHidden());
		Assert.assertFalse(messages.isDisplayed());
	}
}
