package com.github.bordertech.wcomponents.test.selenium.by;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;

/**
 * Extension of the By helper to find WTabSets.
 */
public class ByWTabSet extends By {

	/**
	 * Css class to find the WTabSet.
	 */
	public static final String CSS_SELECTOR_TABSET = "wc-tabset";


	@Override
	public List<WebElement> findElements(final SearchContext context) {
		return ((FindsByClassName) context).findElementsByClassName(CSS_SELECTOR_TABSET);
	}
}
