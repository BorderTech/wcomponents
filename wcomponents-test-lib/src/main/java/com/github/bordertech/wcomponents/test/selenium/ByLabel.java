package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentInputWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentWebProperties;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByXPath;

/**
 * <p>
 * An implementation of By which can find HTML elements by the element's label.</p>
 *
 * @author Joshua Barclay
 * @since 1.3.0
 */
public class ByLabel extends By {

	/**
	 * The XPATH for matching a label with exact text.
	 */
	public static final String XPATH_LABEL_TEXT_EXACT_ROOT = "//span[text()='%1$s']|//label[text()='%1$s']|//legend[text()='%1$s']";
	/**
	 * The XPATH for matching a label that contains the text.
	 */
	public static final String XPATH_LABEL_TEXT_CONTAINS_ROOT =
			"//span[contains(text(),'%1$s')]|//label[contains(text(),'%1$s')]|//legend[contains(text(),'%1$s')]";
	/**
	 * The XPATH for matching a label with exact text.
	 */
	public static final String XPATH_LABEL_TEXT_EXACT_RELATIVE = ".//span[text()='%1$s']|.//label[text()='%1$s']|.//legend[text()='%1$s']";
	/**
	 * The XPATH for matching a label that contains the text.
	 */
	public static final String XPATH_LABEL_TEXT_CONTAINS_RELATIVE =
			".//span[contains(text(),'%1$s')]|.//label[contains(text(),'%1$s')]|.//legend[contains(text(),'%1$s')]";

	/**
	 * The ID of the label.
	 */
	private final String labelId;
	/**
	 * The text of the label.
	 */
	private final String labelText;
	/**
	 * Whether the search is a partial match.
	 */
	private final boolean partialMatch;

	private static final String OPTION_INNER_SPAN_CLASSNAME = "wc-labeltext";

	/**
	 * Whether the xpath lookup is relative to the current element ({@code true}) or not.
	 */
	private final boolean relative;

	/**
	 * ByLabel using the label's ID.
	 *
	 * @param labelId the ID of the label.
	 */
	public ByLabel(final String labelId) {

		this.labelId = labelId;
		this.labelText = null;
		this.partialMatch = false;
		this.relative = false;
	}

	/**
	 * ByLabel using the label's text, either as an exact or partial match and using a relative XPATh to explicitly look 'inside' the current element.
	 *
	 * @param labelText the text used to find the label
	 * @param partialMatch whether it can be a partial text match
	 * @param relative whether to use a relative xpath lookup
	 */
	public ByLabel(final String labelText, final boolean partialMatch, final boolean relative) {
		this.labelText = labelText;
		this.labelId = null;
		this.partialMatch = partialMatch;
		this.relative = relative;
	}

	/**
	 * ByLabel using the label's text, either as an exact or partial match.
	 *
	 * @param labelText the text used to find the label.
	 * @param partialMatch whether it can be a partial text match.
	 */
	public ByLabel(final String labelText, final boolean partialMatch) {
		this(labelText, partialMatch, false);
	}

	/**
	 * Find the elements by label.
	 *
	 * @param context the search context.
	 * @return the matching elements.
	 */
	@Override
	public List<WebElement> findElements(final SearchContext context) {

		List<WebElement> labels;
		if (labelId != null) {
			labels = ((FindsById) context).findElementsById(labelId);
		} else {
			String xpath;
			if (partialMatch) {
				xpath = String.format((relative ? XPATH_LABEL_TEXT_CONTAINS_RELATIVE : XPATH_LABEL_TEXT_CONTAINS_ROOT), labelText);
			} else {
				xpath = String.format((relative ? XPATH_LABEL_TEXT_EXACT_RELATIVE : XPATH_LABEL_TEXT_EXACT_ROOT), labelText);
			}
			labels = ((FindsByXPath) context).findElementsByXPath(xpath);
		}

		if (CollectionUtils.isEmpty(labels)) {
			return labels;
		}

		List<WebElement> results = new ArrayList<>();
		for (WebElement label : labels) {
			WebElement nestedElement;
			if ("legend".equalsIgnoreCase(label.getTagName())) {
				// the labelled element is the label's parent element
				results.add(label.findElement(By.xpath("..")));
				continue;
			}

			String elementId = label.getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_LABEL_FOR.toString());
			if (StringUtils.isEmpty(elementId)) {
				elementId = label.getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_LABEL_FAUX_FOR.toString());
			}
			if (StringUtils.isEmpty(elementId)) {
				elementId = label.getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_LABEL_FOR_READ_ONLY.toString());
			}

			if ("span".equalsIgnoreCase(label.getTagName()) && OPTION_INNER_SPAN_CLASSNAME.equals(label.getAttribute("class"))) {
				label = label.findElement(By.xpath(".."));
			}

			if (StringUtils.isEmpty(elementId)) {
				if ("label".equalsIgnoreCase(label.getTagName())) {
					nestedElement = label.findElement(By.tagName("input"));
					if (nestedElement == null) {
						nestedElement = label.findElement(By.tagName("select"));
					}
					if (nestedElement == null) {
						nestedElement = label.findElement(By.tagName("textarea"));
					}
					if (nestedElement != null) {
						results.add(nestedElement);
					}
				}
				// otherwise the search has probably picked up a non-label span element.
				continue;
			}

			WebElement element = ((FindsById) context).findElementById(elementId);
			if (elementId.endsWith(SeleniumWComponentWebProperties.ID_SUFFIX.toString())) {
				SeleniumWComponentInputWebElement wrapped = SeleniumWComponentsUtil.wrapInputElementWithTypedWebElement((WebDriver) context, element);
				results.add(wrapped);
			} else {
				results.add(element);
			}
		}

		return results;
	}

}
