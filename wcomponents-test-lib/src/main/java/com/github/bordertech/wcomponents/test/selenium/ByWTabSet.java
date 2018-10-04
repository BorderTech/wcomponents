package com.github.bordertech.wcomponents.test.selenium;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsById;

/**
 * Extension of the By helper to find WTabSets.
 */
public class ByWTabSet extends By {

	/**
	 * Css class to find the tabset.
	 */
	public static final String CSS_SELECTOR_TABSET = "wc-tabset";

	private final String tabsetId;

	/**
	 * ByWTabSet using the WTabSet's ID.
	 *
	 * @param tabsetId the ID of the WTabSet.
	 */
	public ByWTabSet(final String tabsetId) {

		this.tabsetId = tabsetId;
	}

	/**
	 * use the standard.
	 */
	public ByWTabSet() {
		this.tabsetId = null;
	}

	@Override
	public List<WebElement> findElements(final SearchContext context) {
		List<WebElement> wTabSets;
		if (tabsetId != null) {
			wTabSets = ((FindsById) context).findElementsById(tabsetId);
		} else {
			wTabSets = ((FindsByClassName) context).findElementsByClassName(CSS_SELECTOR_TABSET);
		}
		return wTabSets;
	}
}
