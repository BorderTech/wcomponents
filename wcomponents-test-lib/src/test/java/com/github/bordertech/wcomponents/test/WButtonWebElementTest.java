package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.test.components.WButtonUI;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.by.ByButtonImage;
import com.github.bordertech.wcomponents.test.selenium.by.ByButtonText;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWButtonWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWMessageBoxWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWMessagesWebElement;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import static org.junit.Assert.assertEquals;

@RunWith(MultiBrowserRunner.class)
public class WButtonWebElementTest extends SeleniumJettyTestCase<WButtonUI> {

	/**
	 * Constructor...
	 *
	 */
	public WButtonWebElementTest() {
		super(new WButtonUI());
	}

	@Test
	public void testTextButton() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWButtonWebElement textButton = driver.findWButton(new ByButtonText("Text Button"));
		textButton.click();

		SeleniumWMessagesWebElement messages = driver.findWMessages(By.id("messages"));
		List<SeleniumWMessageBoxWebElement> messageBoxes = messages.getMessageBoxes();
		assertEquals(1, messageBoxes.size());
		assertEquals("text button clicked", messageBoxes.get(0).getMessage().getText());

	}

	@Test
	public void testImageButton() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWButtonWebElement textButton = driver.findWButton(new ByButtonImage());
		textButton.click();

		SeleniumWMessagesWebElement messages = driver.findWMessages(By.id("messages"));
		List<SeleniumWMessageBoxWebElement> messageBoxes = messages.getMessageBoxes();
		assertEquals(1, messageBoxes.size());
		assertEquals("image button clicked", messageBoxes.get(0).getMessage().getText());

	}
}
