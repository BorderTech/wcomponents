package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;

/**
 * An incomplete Multi File Wdiegt Web element.
 */
public class SeleniumWMultiFileWidgetWebElement extends SeleniumWComponentInputWebElement {
	/**
	 * Construct an input element.
	 *
	 * @param element the web element.
	 * @param driver  the driver.
	 */
	public SeleniumWMultiFileWidgetWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

	/**
	 * Find the current count of files attached.
	 *
	 * @return the current count of files uploaded.
	 */
	public int findFilesAttachedCount() {
		final List<WebElement> li = getElement().findElements(By.tagName("li"));
		return li.size();
	}

	/**
	 *
	 * @param filePath of the file to upload
	 */
	public void attachFile(final String filePath) {
		SeleniumWComponentWebElement elem = (SeleniumWComponentWebElement) getElement().findElement(By.xpath("//input[@type='file']"));

		elem.sendKeys(true, filePath);
		WebDriver driver = ((SeleniumWComponentsWebDriver) getDriver()).getDriver();
		((RemoteWebDriver) driver).getKeyboard().pressKey(Keys.TAB);
//		elem.click();


	}
}
