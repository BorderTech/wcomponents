package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.test.components.WTabSetUI;
import com.github.bordertech.wcomponents.test.selenium.ByWTabSet;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTabSetWebElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

@RunWith(MultiBrowserRunner.class)
public class WTabSetWebElementTest extends SeleniumJettyTestCase {

	/**
	 * Constructor...
	 *
	 */
	public WTabSetWebElementTest() {
		super(new WTabSetUI());
	}

	@Test
	public void testTabFeatures() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWTabSetWebElement wTabSet = driver.findWTabSet(new ByWTabSet());
		assertEquals(wTabSet.findSelectedTabLabel(), "First Tab");
		SeleniumWComponentWebElement selectedTabContent = wTabSet.findSelectedTabContent();
		assertEquals(selectedTabContent.getText(), "Tab: One");
		wTabSet.clickTab("Second Tab");

		assertEquals(wTabSet.findSelectedTabLabel(), "Second Tab");
		selectedTabContent = wTabSet.findSelectedTabContent();
		assertEquals(selectedTabContent.getText(), "Tab: Two");


	}
}
