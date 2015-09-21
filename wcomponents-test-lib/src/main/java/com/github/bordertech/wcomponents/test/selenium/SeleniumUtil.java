package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.AbstractWSelectList;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.Util;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * General Selenium-related utility methods for the WComponent Selenium test classes.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class SeleniumUtil {

	/**
	 * Hide utility class constructor.
	 */
	private SeleniumUtil() {
	}

	/**
	 * Narrows down the search for a WebElement to find the appropriate value.
	 *
	 * @param current the current WebElement which was reached during a search.
	 * @param component the component corresponding to the given WebElement.
	 * @param context the context for the component.
	 * @param value the value to search for.
	 * @return the WebElement with the given value, or null if not found.
	 */
	public static WebElement findValue(final WebElement current, final WComponent component,
			final UIContext context, final Object value) {
		// If not narrowing down the search, just return the current element.
		if (value == null) {
			return current;
		}

		UIContextHolder.pushContext(context);

		try {
			if (component instanceof AbstractWSelectList) {
				AbstractWSelectList list = (AbstractWSelectList) component;

				List<?> options = list.getOptions();

				if (options != null) {
					for (int i = 0; i < options.size(); i++) {
						Object option = options.get(i);

						if (Util.equals(value, option) || Util.equals(value.toString(), list.getDesc(option, i))) {
							return current.findElement(By.xpath(".//*[@value='" + list.getCode(option, i) + "']"));
						}
					}
				}

				// Not found
				return null;
			} else if (component instanceof WRadioButton) {
				return value.equals(((WRadioButton) component).getValue()) ? current : null;
			} else {
				return current.findElement(By.xpath(".//*[@value='" + WebUtilities.encode(String.valueOf(value)) + "']"));
			}
		} finally {
			UIContextHolder.popContext();
		}
	}

	/**
	 * Narrows the results of a search using the given value. A search is performed under each search result for an
	 * element with the given value. Existing results are either replaced or removed, depending on whether a match was
	 * found.
	 *
	 * @param results the search results to modify.
	 * @param component the component.
	 * @param context the context for the component.
	 * @param value the value to search for.
	 */
	protected static void narrowResults(final List<WebElement> results, final WComponent component,
			final UIContext context, final Object value) {
		if (value != null) {
			for (int i = 0; i < results.size(); i++) {
				WebElement narrowed = findValue(results.get(i), component, context, value);

				if (narrowed == null) {
					// No match, remove the element from the current set of results
					results.remove(i--);
				} else {
					// Found a match, replace the old result
					results.set(i, narrowed);
				}
			}
		}
	}

}
