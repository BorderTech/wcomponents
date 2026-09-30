package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.WSpan;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link HtmlIconUtil}.
 */
public class HtmlIconUtil_Test {

	@Test
	public void testCreateSpanIconWithHTMLProperties() {
		WSpan span = HtmlIconUtil.createSpanIcon(HtmlClassProperties.ICON_WARN);
		Assert.assertEquals("Incorrect HTML class returned", HtmlClassProperties.ICON_WARN.toString(), span.getHtmlClass());
		Assert.assertTrue("Should default to aria hidden", span.isAriaHidden());
	}

	@Test
	public void testCreateSpanIconWithNullHTMLProperties() {
		WSpan span = HtmlIconUtil.createSpanIcon((HtmlClassProperties) null);
		Assert.assertNull("Span should have null HTML class", span.getHtmlClass());
		Assert.assertTrue("Span should default to aria hidden", span.isAriaHidden());
	}

	@Test
	public void testCreateSpanIconWithClassString() {
		WSpan span = HtmlIconUtil.createSpanIcon("CLASS");
		Assert.assertEquals("Incorrect HTML class returned", "CLASS", span.getHtmlClass());
		Assert.assertTrue("Should default to aria hidden", span.isAriaHidden());
	}

	@Test
	public void testCreateSpanIconWithNullClassString() {
		WSpan span = HtmlIconUtil.createSpanIcon((String) null);
		Assert.assertNull("Span should have null HTML class", span.getHtmlClass());
		Assert.assertTrue("Span should default to aria hidden", span.isAriaHidden());
	}

}
