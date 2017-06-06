package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link InfoDump}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class InfoDump_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new InfoDump_Test.
	 */
	public InfoDump_Test() {
		super(new InfoDump());
	}

	@Test
	public void testExample() {

		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		Assert.assertEquals("Incorrect default text", "", driver.findWTextArea(byWComponentPath("WTextArea")).getText());
		driver.findElement(byWComponentPath("WButton[1]")).click();

		String text = driver.findWTextArea(byWComponentPath("WTextArea")).getText();
		Assert.assertTrue("Text should contain dump info", text.contains("WEnvironment"));

		UIContext uic = getUserContextForSession();
		UIContextHolder.pushContext(uic);
		try {
			Environment env = getUi().getEnvironment();
			Assert.assertTrue("Incorrect AppId", text.contains("AppId: " + env.getAppId()));
		} finally {
			UIContextHolder.popContext();
		}

		driver.findElement(byWComponentPath("WButton[0]")).click();
		Assert.assertEquals("Text should have been cleared", "", driver.findWTextArea(byWComponentPath("WTextArea")).getText());
	}
}
