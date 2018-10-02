package com.github.bordertech.wcomponents.test.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsById;

import java.util.List;

/**
 * Extension of the By Helper to find tabsets.
 */
public class ByTabSet extends By {

	/**
	 * Css class to find the tabset.
	 */
	public static final String CSS_SELECTOR_TABSET = "wc-tabset";

	private final String tabsetId;

	/**
	 * ByTabSet using the tabset's ID.
	 *
	 * @param buttonId the ID of the label.
	 */
	public ByTabSet(final String buttonId) {

		this.tabsetId = buttonId;
	}

	/**
	 * use the standard.
	 */
	public ByTabSet() {
		this.tabsetId = null;
	}

	@Override
	public List<WebElement> findElements(final SearchContext context) {
		List<WebElement> labels;
		if (tabsetId != null) {
			labels = ((FindsById) context).findElementsById(tabsetId);
		} else {
			labels = ((FindsByClassName) context).findElementsByClassName(CSS_SELECTOR_TABSET);
		}
		return labels;
	}
}
