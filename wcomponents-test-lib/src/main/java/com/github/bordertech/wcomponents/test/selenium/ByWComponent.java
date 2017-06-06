package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.AbstractWSelectList;
import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.Util;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByXPath;

/**
 * <p>
 * This By implementation will only work if the servlet is running in the same JVM as the test. While this is the
 * easiest way to write tests, those tests will not be reusable for verifying environments.</p>
 * <p>
 * An implementation of By which can find HTML elements which correspond to (most) WComponents. Only WComponents which
 * emit elements with ids can be searched on. This means that components such as WText and "WComponent" itself can not
 * be searched for.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ByWComponent extends By {

	/**
	 * The component to search for.
	 */
	private final WComponent component;

	/**
	 * The context to search in.
	 */
	private UIContext context;

	/**
	 * The value to search for, for lists, radio button groups, etc.
	 */
	private final Object value;

	/**
	 * Creates a ByWComponent which searches for a component instance in the given context. If searching for a repeated
	 * component, the <code>context</code> should be the row context for the row you want to return.
	 *
	 * @param componentWithContext the component to search for.
	 */
	public ByWComponent(final ComponentWithContext componentWithContext) {
		this(componentWithContext.getComponent(), componentWithContext.getContext(), null);
	}

	/**
	 * Creates a ByWComponent which searches for a component instance.
	 *
	 * @param component the component instance to search for.
	 */
	public ByWComponent(final WComponent component) {
		this(component, null, null);
	}

	/**
	 * Creates a ByWComponent which searches for a component instance in the given context. If searching for a repeated
	 * component, the <code>context</code> should be the row context for the row you want to return.
	 *
	 * @param component the component instance to search for.
	 * @param context the context to search in, use null for the default context.
	 */
	public ByWComponent(final WComponent component, final UIContext context) {
		this(component, context, null);
	}

	/**
	 * Creates a ByWComponent which searches for a component instance in the given context. If searching for a repeated
	 * component, the <code>context</code> should be the row context for the row you want to return.
	 *
	 * @param component the component instance to search for.
	 * @param context the context to search in, use null for the default context.
	 * @param value If not null, narrow the search by value for e.g. list or drop-down entries.
	 */
	public ByWComponent(final WComponent component, final UIContext context, final Object value) {
		this.component = component;
		this.context = context;
		this.value = value;
	}

	/**
	 * Set the context.
	 *
	 * @param context the context to set.
	 */
	public void setContext(final UIContext context) {
		this.context = context;
	}

	/**
	 *
	 * @return the UIContext.
	 */
	protected UIContext getContext() {
		return context;
	}

	/**
	 *
	 * @return the component.
	 */
	protected WComponent getComponent() {
		return component;
	}

	/**
	 *
	 * @return the value.
	 */
	protected Object getValue() {
		return value;
	}

	/**
	 * Perform the driver search for the given component.
	 *
	 * @param searchContext the SearchContext to search within.
	 * @param uiContext the UIContext to retrieve the id/name from.
	 * @param component the component to find.
	 * @param compValue the component value to match.
	 * @return a list of matching elements.
	 */
	protected List<WebElement> findElement(final SearchContext searchContext, final UIContext uiContext,
			final WComponent component, final Object compValue) {
		List<WebElement> result = new ArrayList<>();
		UIContextHolder.pushContext(uiContext);
		try {
			if (searchContext instanceof FindsById) {
				String componentId = component.getId();
//				try {
//					WebElement element = ((FindsById) searchContext).findElementById(componentId);
//					if (element != null) {
//						result.add(element);
//					}
//				} catch (Exception e) {
//					System.out.println("Could not find element by ID [" + componentId + "]. " + e.getMessage());
//				}
				result = ((FindsById) searchContext).findElementsById(componentId);
			} else if (searchContext instanceof FindsByName) {
				String name = component.getId();
				result = ((FindsByName) searchContext).findElementsByName(name);
			} else {
				String componentId = component.getId();
				result = ((FindsByXPath) searchContext).findElementsByXPath("*[@id = '"
						+ componentId + "']");
			}
		} finally {
			UIContextHolder.popContext();
		}

		// Narrow the results, if applicable
		if (result != null) {
			narrowResults(result, component, uiContext, compValue);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<WebElement> findElements(final SearchContext searchContext) {

		return findElement(searchContext, context, component, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ByWComponent:" + component.getId()
				+ (value == null ? "" : (" with value \"" + value + '"'));
	}

	/**
	 * @return the class of the target WComponent
	 */
	public Class<? extends WComponent> getTargetWComponentClass() {
		return component.getClass();
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
	public static void narrowResults(final List<WebElement> results, final WComponent component,
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
