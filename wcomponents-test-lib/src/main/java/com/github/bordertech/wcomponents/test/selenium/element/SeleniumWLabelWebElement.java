package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement specific to WLabel.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
public class SeleniumWLabelWebElement extends SeleniumWComponentWebElement {

	/**
	 * HTML tag which defines a real label.
	 */
	private static final String LABEL_ELEMENT = "label";

	/**
	 * HTML tag which defines a faux-label.
	 */
	private static final String FAUX_LABEL_ELEMENT = "span";

	/**
	 * HTML class attribute value common to all labels.
	 */
	private static final String CLASS_NAME = "wc-label";

	/**
	 * HTML attribute which holds the reference to a simple input.
	 */
	private static final String LABEL_FOR_ATTRIB = SeleniumWComponentWebProperties.ATTRIBUTE_LABEL_FOR.toString();

	/**
	 * HTML attribute used to hold a reference to the "labelled" component for a faux label.
	 */
	private static final String FAUX_FOR_ATTRIBUTE = SeleniumWComponentWebProperties.ATTRIBUTE_LABEL_FAUX_FOR.toString();

	/**
	 * HTML attribute used to hold a reference to the labelled component when that component is in a read-only state.
	 */
	private static final String RO_FOR_ATTRIBUTE = SeleniumWComponentWebProperties.ATTRIBUTE_LABEL_FOR_READ_ONLY.toString();

	/**
	 * Create a SeleniumWLabelWebElement from a generic WebElement.
	 *
	 * @param element a generic WebElement
	 * @param driver the current WebDriver instance
	 */
	public SeleniumWLabelWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
		String tagName = element.getTagName();
		if (!(LABEL_ELEMENT.equalsIgnoreCase(tagName) || FAUX_LABEL_ELEMENT.equalsIgnoreCase(tagName))) {
			throw new SystemException("Incorrect element selected for SeleniumWLabelWebElement. Found: " + tagName);
		}

		if (FAUX_LABEL_ELEMENT.equalsIgnoreCase(tagName)) {
			String className = element.getAttribute("class");
			if (!className.contains(CLASS_NAME)) {
				throw new SystemException("Incorrect element selected for SeleniumWLabelWebElement. Expected className to include `wc-label` found: "
						+ className);
			}
		}
	}

	/**
	 * @return the WComponent labelled by the WLabel
	 */
	public SeleniumWComponentWebElement getLabelledComponent() {
		String tagName = getTagName();
		String attribName = LABEL_ELEMENT.equalsIgnoreCase(tagName) ? LABEL_FOR_ATTRIB : FAUX_FOR_ATTRIBUTE;
		String forId = getAttribute(attribName);
		if (Util.empty(forId)) {
			// could be in a read-only state
			if (FAUX_LABEL_ELEMENT.equalsIgnoreCase(tagName)) {
				forId = getAttribute(RO_FOR_ATTRIBUTE);
			}
		}
		if (Util.empty(forId)) {
			throw new SystemException("No labelled component found, expected id " + forId);
		}
		// TODO Maybe should not be immediate!!
		SeleniumWComponentWebElement element = findElementImmediate(By.xpath("//*[@id='" + forId + "']"));
		if (attribName.equals(LABEL_FOR_ATTRIB)) {
			return SeleniumWComponentsUtil.wrapInputElementWithTypedWebElement(getDriver(), element);
		}
		return element;
	}

	/**
	 * @return the hint child element of a WLabel
	 */
	public WebElement getHint() {
		return findElementImmediate(By.cssSelector(".wc-label-hint"));
	}

	/**
	 * Is the label "for" a component in a read-only state?
	 *
	 * @return {@code true} if the label should be for a WComponent in a read-only state.
	 */
	public boolean isReadOnly() {
		String forId = getAttribute(RO_FOR_ATTRIBUTE);
		return !Util.empty(forId);
	}

	/**
	 * Indicates if the WLabel is "hidden". Note that hidden for a WLabel means rendered out-of-viewport.
	 *
	 * @return {@code true} if the label is 'hidden'.
	 */
	public boolean isHidden() {
		return getAttribute("class").contains("wc-off");
	}

}
