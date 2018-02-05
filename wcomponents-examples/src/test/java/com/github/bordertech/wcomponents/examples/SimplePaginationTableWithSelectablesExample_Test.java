package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.examples.table.SimplePaginationTableWithSelectablesExample;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTableWebElement;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

public class SimplePaginationTableWithSelectablesExample_Test extends WComponentExamplesTestCase {

	private final String SELECT_BUTTON = "WButton[0]";
	private final String DELETE_BUTTON = "WButton[1]";
	private final String EDIT_BUTTON = "WButton[2]";
	private final String REFRESH_BUTTON = "WButton[3]";

	public SimplePaginationTableWithSelectablesExample_Test() {
		super(new SimplePaginationTableWithSelectablesExample());
	}

	@Test
	public void testButtonsDisabledOnLoad() {
		SeleniumWComponentsWebDriver driver = getDriver();

//		List<SeleniumWComponentWebElement> buttons = driver.findElement(byWComponentPath("WButton"));
		Assert.assertFalse("Buttons should be disabled on initial load",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled()
				&& driver.findElement(byWComponentPath(DELETE_BUTTON)).isEnabled()
				&& driver.findElement(byWComponentPath(EDIT_BUTTON)).isEnabled()
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
					driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled()
						&& driver.findElement(byWComponentPath(DELETE_BUTTON)).isEnabled()
						&& driver.findElement(byWComponentPath(EDIT_BUTTON)).isEnabled()
				);
			}
			if (i == 1) {
				Assert.assertTrue("All buttons should be enabled with one item selected",
					driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled()
					&& driver.findElement(byWComponentPath(DELETE_BUTTON)).isEnabled()
					&& driver.findElement(byWComponentPath(EDIT_BUTTON)).isEnabled());
			}
			if (i > 1) {
				Assert.assertFalse("Edit button should be disabled with more than one item selected",
					driver.findElement(byWComponentPath(EDIT_BUTTON)).isEnabled());
				Assert.assertTrue("Delete button should be enabled with more than one item selected",
					driver.findElement(byWComponentPath(DELETE_BUTTON)).isEnabled());
			}
			if ((i > 0) && (i < 4)) {
				Assert.assertTrue("Select button should be enabled with 1 to 3 items selected",
					driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled());
			}
			if (i >= 4) {
				Assert.assertFalse("Select button should be disabled with 4 or more items selected",
					driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled());
			}
			wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='"+i+"']")).click();
		}
		driver.clearUserContext();
	}

	@Test
	public void testActionConstraintsAcrossPagesWithRefresh() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWTableWebElement wTableWebElement = getTable();

		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='0']")).click();
		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='1']")).click();
		wTableWebElement.getNextPageButton().click();
		driver.waitForPageReady();

		driver.findElement(byWComponentPath(REFRESH_BUTTON)).click();

		Assert.assertTrue("Select and delete buttons should be enabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled()
			&& driver.findElement(byWComponentPath(DELETE_BUTTON)).isEnabled());
		Assert.assertFalse("Edit button should be disabled",
			driver.findElement(byWComponentPath(EDIT_BUTTON)).isEnabled());

		wTableWebElement = getTable(); // Needs to be added after each refresh to prevent StaleElementReferenceException

		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='5']")).click();
		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='6']")).click();

		Assert.assertFalse("Select button should be enabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled());

		wTableWebElement.getPreviousPageButton().click();
		driver.waitForPageReady();
		driver.findElement(byWComponentPath(REFRESH_BUTTON)).click();

		Assert.assertFalse("Select button should be disabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled());

		wTableWebElement = getTable();
		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='0']")).click();

		Assert.assertTrue("Select button should be enabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled());

		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='0']")).click();

		wTableWebElement.getLastPageButton().click();
		driver.findElement(byWComponentPath(REFRESH_BUTTON)).click();

		Assert.assertFalse("Select and Edit Buttons should be disabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled()
			&& driver.findElement(byWComponentPath(EDIT_BUTTON)).isEnabled());
		Assert.assertTrue("Delete button should be enabled",
			driver.findElement(byWComponentPath(DELETE_BUTTON)).isEnabled());
		driver.clearUserContext();
	}

	@Test
	public void testActionConstraintsAcrossPagesWithoutRefresh() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWTableWebElement wTableWebElement = getTable();

		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='0']")).click();
		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='1']")).click();
		wTableWebElement.getNextPageButton().click();
		driver.waitForPageReady();

		Assert.assertTrue("Select and delete buttons should be enabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled()
				&& driver.findElement(byWComponentPath(DELETE_BUTTON)).isEnabled());
		Assert.assertFalse("Edit button should be disabled",
			driver.findElement(byWComponentPath(EDIT_BUTTON)).isEnabled());

		wTableWebElement = getTable(); // Needs to be added after ajax/refresh to prevent StaleElementReferenceException

		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='5']")).click();
		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='6']")).click();

		Assert.assertFalse("Select button should be enabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled());

		wTableWebElement.getPreviousPageButton().click();
		driver.waitForPageReady();

		Assert.assertFalse("Select button should be disabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled());

		wTableWebElement = getTable();
		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='0']")).click();

		Assert.assertTrue("Select button should be enabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled());

		wTableWebElement.getTable().findElement(By.cssSelector("tr[data-wc-rowindex='0']")).click();

		wTableWebElement.getLastPageButton().click();

		Assert.assertFalse("Select and Edit Buttons should be disabled",
			driver.findElement(byWComponentPath(SELECT_BUTTON)).isEnabled()
				&& driver.findElement(byWComponentPath(EDIT_BUTTON)).isEnabled());
		Assert.assertTrue("Delete button should be enabled",
			driver.findElement(byWComponentPath(DELETE_BUTTON)).isEnabled());
		driver.clearUserContext();
	}

	private SeleniumWTableWebElement getTable() {
		return getDriver().findWTable(byWComponentPath("WTable"));
	}
}
