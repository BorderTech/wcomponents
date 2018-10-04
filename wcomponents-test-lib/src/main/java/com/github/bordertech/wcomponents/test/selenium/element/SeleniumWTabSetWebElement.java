package com.github.bordertech.wcomponents.test.selenium.element;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The Wcomponent WTabSet web element.
 */
public class SeleniumWTabSetWebElement extends SeleniumWComponentWebElement {
	/**
	 * Creates a WebElementWrapper.
	 *
	 * @param element the backing element.
	 * @param driver  the SeleniumWComponentsWebDriver.
	 */
	public SeleniumWTabSetWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

	/**
	 * Gets the currently selected tabs label.
	 *
	 * @return the label value.
	 */
	public String findSelectedTabLabel() {
		WebElement selectedTab = findSelectedTab();
		if (selectedTab != null) {
			return selectedTab.getText();
		}
		throw new IllegalArgumentException("There is no selected tab.");
	}

	/**
	 * Finds the currently selected tab's content.
	 *
	 * @return the content of the currently selected tab.
	 */
	public SeleniumWComponentWebElement findSelectedTabContent() {
		WebElement selectedTab = findSelectedTab();
		if (selectedTab == null) {
			throw new IllegalArgumentException("There is no selected tab.");
		}
		String selectedTabId = selectedTab.getAttribute("id");
		List<WebElement> elements = getDriver().findElements(By.className("wc-tabcontent"));
		for (WebElement webElement : elements) {
			if (StringUtils.equals(selectedTabId, webElement.getAttribute("data-wc-ajaxalias"))) {
				return new SeleniumWComponentWebElement(webElement, getDriver());
			}
		}
		throw new IllegalArgumentException("There is no tab content for " + selectedTabId);
	}

	/**
	 * Selects the tab with a defined label.
	 * @param tabLabel the tab to find with defined label
	 */
	public void clickTab(final String tabLabel) {
		List<WebElement> elements = getDriver().findElements(By.className("wc-tab"));
		for (WebElement webElement : elements) {
			if (StringUtils.equals(tabLabel, webElement.getText())) {
				webElement.click();
				return;
			}
		}
		throw new IllegalArgumentException("There is no tab with label " + tabLabel);
	}

	/**
	 * finds the currently selected tab label.
	 * @return the currently selected tab item.
	 */
	private WebElement findSelectedTab() {
		List<WebElement> elements = getDriver().findElements(By.className("wc-tab"));
		for (WebElement webElement : elements) {
			String ariaSelectedAttr = webElement.getAttribute("aria-selected");
			if (Boolean.TRUE.valueOf(ariaSelectedAttr)) {
				return webElement;
			}
		}
		throw new IllegalArgumentException("There is no selected tab.");
	}

}
