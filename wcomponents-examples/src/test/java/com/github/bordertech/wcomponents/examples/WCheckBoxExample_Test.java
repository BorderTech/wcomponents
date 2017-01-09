package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.examples.theme.WCheckBoxExample;
import com.github.bordertech.wcomponents.examples.theme.WCheckBoxSelectExample;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium unit tests for {@link WCheckBoxSelectExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WCheckBoxExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new WCheckBoxSelectExample_Test.
	 */
	public WCheckBoxExample_Test() {
		super(new WCheckBoxExample());
	}

	/**
	 * Test that ByLabel works for CheckBoxes by label id.
	 */
	@Test
	public void testFindByLabelId() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		WContainer container = (WContainer) getUi();
		WFieldLayout layout = (WFieldLayout) container.getChildAt(0);
		WField field = (WField) layout.getChildAt(0);

		String labelId = field.getLabel().getId();
		String componentId = field.getField().getId();

		WebElement checkBox = driver.findElement(new ByLabel(labelId));
		Assert.assertNotNull("Unable to find checkbox by labelId", checkBox);
		Assert.assertEquals("Checkbox element ID does not match expected", componentId, checkBox.getAttribute("id"));

	}

	/**
	 * Test that ByLabel works for CheckBoxes by label text exact match.
	 */
	@Test
	public void testFindByLabelTextExact() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		WContainer container = (WContainer) getUi();
		WFieldLayout layout = (WFieldLayout) container.getChildAt(0);
		WField field = (WField) layout.getChildAt(0);

		String labelText = field.getLabel().getText();
		String componentId = field.getField().getId();

		WebElement checkBox = driver.findElement(new ByLabel(labelText, false));
		Assert.assertNotNull("Unable to find checkbox by label text", checkBox);
		Assert.assertEquals("Checkbox element ID does not match expected", componentId, checkBox.getAttribute("id"));

	}

	/**
	 * Test that ByLabel works for CheckBoxes by label text partial match.
	 */
	@Test
	public void testFindByLabelTextPartial() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// Case sensitive match on partial text.
		List<WebElement> checkBoxes = driver.findElements(new ByLabel("check box", true));
		Assert.assertNotNull("Unable to find checkboxes by label text partial match", checkBoxes);
		Assert.assertEquals("Incorrect number of check boxes found by partial text match", 5, checkBoxes.size());
	}
}
