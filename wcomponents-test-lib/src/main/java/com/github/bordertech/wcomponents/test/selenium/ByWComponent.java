package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WComponent;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByXPath;

/**
 * An implementation of By which can find HTML elements which correspond to (most) WComponents. Only WComponents which
 * emit elements with ids can be searched on. This means that components such as WText and "WComponent" itself can not
 * be searched for.
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
	private final UIContext context;

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
		this.context = context == null ? new UIContextImpl() : context;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<WebElement> findElements(final SearchContext searchContext) {
		List<WebElement> result = new ArrayList<>();
		UIContextHolder.pushContext(context);

		try {
			if (searchContext instanceof FindsById) {
				String componentId = component.getId();
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
			SeleniumUtil.narrowResults(result, component, context, value);
		}

		return result;
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
}
