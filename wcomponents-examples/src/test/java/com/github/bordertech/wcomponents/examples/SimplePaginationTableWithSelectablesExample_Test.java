package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WSelectToggle;
import com.github.bordertech.wcomponents.examples.table.SimplePaginationTableWithSelectablesExample;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTableWebElement;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

public class SimplePaginationTableWithSelectablesExample_Test extends WComponentExamplesTestCase {

	public SimplePaginationTableWithSelectablesExample_Test() {
		super(new SimplePaginationTableWithSelectablesExample());
	}

	@Test
	public void testButtonsDisabledOnLoad() {
		SeleniumWComponentsWebDriver driver = getDriver();

//		List<SeleniumWComponentWebElement> buttons = driver.findElement(byWComponentPath("WButton"));
		Assert.assertFalse("Buttons should be disabled on initial load",
			driver.findElement(byWComponentPath("WButton[0]")).isEnabled()
				&& driver.findElement(byWComponentPath("WButton[1]")).isEnabled()
				&& driver.findElement(byWComponentPath("WButton[2]")).isEnabled()
			);
		SeleniumWTableWebElement wTableWebElement = getTable();

		wTableWebElement.getTable().findElement(By.cssSelector("tr[role='row']")).click();
		Assert.assertTrue("Buttons should be enabled", driver.findElement(byWComponentPath("WButton[1]"))
			.isEnabled());
		driver.clearUserContext();
	}

	@Test
	public void testActionConstraintsOnSinglePage() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWTableWebElement wTableWebElement = getTable();

		for (int i = 0; i < 5; i++) {
			if (i == 0) {
				Assert.assertFalse("Buttons should be disabled when nothing is selected",
					driver.findElement(byWComponentPath("WButton[0]")).isEnabled()
						&& driver.findElement(byWComponentPath("WButton[1]")).isEnabled()
						&& driver.findElement(byWComponentPath("WButton[2]")).isEnabled()
				);
			}
			if (i == 1) {
				Assert.assertTrue("All buttons should be enabled with one item selected",
					driver.findElement(byWComponentPath("WButton[0]")).isEnabled()
					&& driver.findElement(byWComponentPath("WButton[1]")).isEnabled()
					&& driver.findElement(byWComponentPath("WButton[2]")).isEnabled());
			}
			if (i > 1) {
				Assert.assertFalse("Edit button should be disabled with more than one item selected",
					driver.findElement(byWComponentPath("WButton[2]")).isEnabled());
				Assert.assertTrue("Delete button should be enabled with more than one item selected",
					driver.findElement(byWComponentPath("WButton[1]")).isEnabled());
			}
			if ((i > 0) && (i < 4)) {
				Assert.assertTrue("Select button should be enabled with 1 to 3 items selected",
					driver.findElement(byWComponentPath("WButton[0]")).isEnabled());
			}
			if (i >= 4) {
				Assert.assertFalse("Select button should be disabled with 4 or more items selected",
					driver.findElement(byWComponentPath("WButton[0]")).isEnabled());
			}
			wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='"+i+"']")).click();
		}
		driver.clearUserContext();
	}

	@Test
	public void testActionConstraintsAcrossPages() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWTableWebElement wTableWebElement = getTable();

		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='0']")).click();
		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='1']")).click();
		wTableWebElement.getNextPageButton().click();
		driver.waitForPageReady();

		Assert.assertTrue("Select and delete buttons should be enabled",
			driver.findElement(byWComponentPath("WButton[0]")).isEnabled()
			&& driver.findElement(byWComponentPath("WButton[1]")).isEnabled());
		Assert.assertFalse("Edit button should be disabled",
			driver.findElement(byWComponentPath("WButton[2]")).isEnabled());

		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='5']")).click();
		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='6']")).click();

		Assert.assertFalse("Select button should be enabled",
			driver.findElement(byWComponentPath("WButton[0]")).isEnabled());

		wTableWebElement.getPreviousPageButton().click();
		driver.waitForPageReady();
		Assert.assertFalse("Select button should be disabled",
			driver.findElement(byWComponentPath("WButton[0]")).isEnabled());
		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='0']")).click();

		Assert.assertTrue("Select button should be enabled",
			driver.findElement(byWComponentPath("WButton[0]")).isEnabled());

		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='0']")).click();

		wTableWebElement.getLastPageButton().click();
		Assert.assertFalse("Select and Edit Buttons should be disabled",
			driver.findElement(byWComponentPath("WButton[0]")).isEnabled()
			&& driver.findElement(byWComponentPath("WButton[2]")).isEnabled());
		Assert.assertTrue("Delete button should be enabled",
			driver.findElement(byWComponentPath("WButton[1]")).isEnabled());
		driver.clearUserContext();
	}

	private SeleniumWTableWebElement getTable() {
		return getDriver().findWTable(byWComponentPath("WTable"));
	}
}
