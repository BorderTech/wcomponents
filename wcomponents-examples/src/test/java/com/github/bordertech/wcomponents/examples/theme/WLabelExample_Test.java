package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.examples.SeleniumTests;
import com.github.bordertech.wcomponents.examples.WComponentExamplesTestCase;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWLabelWebElement;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Tests of WLabelExample. This is primarily used to test the WLabel Selenium WebElement extension.
 * @author Mark Reeves
 * @since 1.4.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WLabelExample_Test extends WComponentExamplesTestCase {

	private SeleniumWComponentsWebDriver driver;
	/**
	 * Create a test instance.
	 */
	public WLabelExample_Test() {
		super(new WLabelExample());
	}

	@Before
	public void doBefore() {
		driver = getDriver();
	}

	@Test
	public void testLabelCreated() {
		Assert.assertNotNull(driver.findWLabel(byWComponentPath("WLabel[0]")));
	}

	@Test
	public void testGetByText() {
		Assert.assertNotNull(driver.findWLabelWithPartialText("Normal input component"));
	}

	@Test
	public void testGetByTextComplexComponent() {
		Assert.assertNotNull(driver.findWLabelWithPartialText("Select one or more options"));
	}

	@Test
	public void testGetByTextWFieldSet() {
		Assert.assertNotNull(driver.findWLabelWithPartialText("Enter the dates of entry and exit"));
	}

	@Test
	public void testIsHidden() {
		SeleniumWLabelWebElement label = driver.findWLabel(byWComponentPath("WLabel[1]"));
		Assert.assertTrue(label.isHidden());
	}

	@Test
	public void testGetByTextHidden() {
		SeleniumWLabelWebElement label = driver.findWLabel(byWComponentPath("WLabel[1]"));
		Assert.assertTrue(label.isHidden());
		String text = label.getText();
		Assert.assertNotNull(driver.findWLabelWithPartialText(text));
	}

	@Test
	public void testIsReadOnly() {
		SeleniumWLabelWebElement label = driver.findWLabel(byWComponentPath("WLabel[2]"));
		Assert.assertTrue(label.isReadOnly());
	}

	@Test
	public void testGetComponentSimple() {
		SeleniumWLabelWebElement label = driver.findWLabel(byWComponentPath("WLabel[0]"));
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WTextField[0]"));
		Assert.assertEquals(expected.getAttribute("id"), label.getLabelledComponent().getAttribute("id"));
	}

	@Test
	public void testGetComponentComplex() {
		SeleniumWLabelWebElement label = driver.findWLabel(byWComponentPath("WLabel[6]"));
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WCheckBoxSelect[0]"));
		Assert.assertEquals(expected.getAttribute("id"), label.getLabelledComponent().getAttribute("id"));
	}

	@Test
	public void testGetComponentReadOnly() {
		SeleniumWLabelWebElement label = driver.findWLabel(byWComponentPath("WLabel[2]"));
		Assert.assertTrue(label.isReadOnly());
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WTextField[2]"));
		Assert.assertEquals(expected.getAttribute("id"), label.getLabelledComponent().getAttribute("id"));
	}


}
