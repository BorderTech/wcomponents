package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Placeholderable;
import com.github.bordertech.wcomponents.WTextField;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link HtmlSanitizerUtil}.
 *
 * @author Rick Brown
 * @since 1.4
 */
public class HtmlRenderUtilTest extends AbstractWComponentTestCase {

	@Test
	public void testGetEffectivePlaceholder() {
		Placeholderable field = new WTextField();
		String actual = HtmlRenderUtil.getEffectivePlaceholder(field);
		Assert.assertNull("Placeholder should not be set for an optional field with no explicit placeholder text", actual);
	}

	@Test
	public void testGetEffectivePlaceholderWithExplicitValue() {
		Placeholderable field = new WTextField();
		String expected = "my placeholder is boring";
		field.setPlaceholder(expected);
		String actual = HtmlRenderUtil.getEffectivePlaceholder(field);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetEffectivePlaceholderWithMandatory() {
		WTextField field = new WTextField();
		field.setMandatory(true);
		String expected = I18nUtilities.format(null, InternalMessages.DEFAULT_REQUIRED_FIELD_PLACEHOLDER);
		String actual = HtmlRenderUtil.getEffectivePlaceholder(field);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetEffectivePlaceholderWithMandatoryAndExplicitValue() {
		WTextField field = new WTextField();
		field.setMandatory(true);
		String expected = "my placeholder is boring";
		field.setPlaceholder(expected);
		String actual = HtmlRenderUtil.getEffectivePlaceholder(field);
		Assert.assertEquals(expected, actual);
	}

}
