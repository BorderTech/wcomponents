package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.examples.SeleniumTests;
import com.github.bordertech.wcomponents.examples.WComponentExamplesTestCase;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWLabelWebElement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Tests of WLabelExample. This is primarily used to test the WLabel Selenium WebElement extension.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WLabelExample_Test extends WComponentExamplesTestCase {

	/**
	 * Create a test instance.
	 */
	public WLabelExample_Test() {
		super(new WLabelExample());
	}

	@Test
	public void testLabelCreated() {
		Assert.assertNotNull(getDriver().findWLabel(byWComponentPath("WLabel[0]")));
	}

	@Test
	public void testGetByText() {
		Assert.assertNotNull(getDriver().findWLabelWithPartialText("Normal input component"));
	}

	@Test
	public void testGetByTextComplexComponent() {
		Assert.assertNotNull(getDriver().findWLabelWithPartialText("Select one or more options"));
	}

	@Test
	public void testGetByTextWFieldSet() {
		Assert.assertNotNull(getDriver().findWLabelWithPartialText("Enter the dates of entry and exit"));
	}

	@Test
	public void testIsHidden() {
		SeleniumWLabelWebElement label = getDriver().findWLabel(byWComponentPath("WLabel[1]"));
		Assert.assertTrue(label.isHidden());
	}

	@Test
	public void testGetByTextHidden() {
		SeleniumWLabelWebElement label = getDriver().findWLabel(byWComponentPath("WLabel[1]"));
		Assert.assertTrue(label.isHidden());
		String text = label.getText();
		Assert.assertNotNull(getDriver().findWLabelWithPartialText(text));
	}

	@Test
	public void testIsReadOnly() {
		SeleniumWLabelWebElement label = getDriver().findWLabel(byWComponentPath("WLabel[2]"));
		Assert.assertTrue(label.isReadOnly());
	}

	@Test
	public void testGetComponentSimple() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWLabelWebElement label = driver.findWLabel(byWComponentPath("WLabel[0]"));
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WTextField[0]"));
		Assert.assertEquals(expected.getAttribute("id"), label.getLabelledComponent().getAttribute("id"));
	}

	@Test
	public void testGetComponentComplex() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWLabelWebElement label = driver.findWLabel(byWComponentPath("WLabel[7]"));
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WCheckBoxSelect[1]"));
		Assert.assertEquals(expected.getAttribute("id"), label.getLabelledComponent().getAttribute("id"));
	}

	@Test
	public void testGetComponentReadOnly() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWLabelWebElement label = driver.findWLabel(byWComponentPath("WLabel[2]"));
		Assert.assertTrue(label.isReadOnly());
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WTextField[2]"));
		Assert.assertEquals(expected.getAttribute("id"), label.getLabelledComponent().getAttribute("id"));
	}

	// Use ByLabel to get a component by label text
	@Test
	public void testGetByLabelSimple() {
		SeleniumWComponentsWebDriver driver = getDriver();
		String labelText = "Normal input component";
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WTextField[0]"));
		SeleniumWComponentWebElement actual = driver.findElement(new ByLabel(labelText, false));
		Assert.assertEquals(expected.getAttribute("id"), actual.getAttribute("id"));
	}

	@Test
	public void testGetByLabelSimplePartial() {
		String labelText = "Normal input";
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WTextField[0]"));
		SeleniumWComponentWebElement actual = driver.findElement(new ByLabel(labelText, true));
		Assert.assertEquals(expected.getAttribute("id"), actual.getAttribute("id"));
	}

	@Test
	public void testGetByLabelComplex() {
		String labelText = "Select one or more options";
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WCheckBoxSelect[1]"));
		SeleniumWComponentWebElement actual = driver.findElement(new ByLabel(labelText, false));
		Assert.assertEquals(expected.getAttribute("id"), actual.getAttribute("id"));
	}

	@Test
	public void testGetByLabelComplexPartial() {
		String labelText = "one or more";
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WCheckBoxSelect[1]"));
		SeleniumWComponentWebElement actual = driver.findElement(new ByLabel(labelText, true));
		Assert.assertEquals(expected.getAttribute("id"), actual.getAttribute("id"));
	}

	@Test
	public void testGetByLabelReadOnly() {
		String labelText = "A hidden label for a read only field";
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WTextField[2]"));
		SeleniumWComponentWebElement actual = driver.findElement(new ByLabel(labelText, false));
		Assert.assertEquals(expected.getAttribute("id"), actual.getAttribute("id"));
	}

	@Test
	public void testGetByLabelReadOnlyPartial() {
		String labelText = "for a read only";
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWComponentWebElement expected = driver.findElement(byWComponentPath("WTextField[2]"));
		SeleniumWComponentWebElement actual = driver.findElement(new ByLabel(labelText, true));
		Assert.assertEquals(expected.getAttribute("id"), actual.getAttribute("id"));
	}

}
