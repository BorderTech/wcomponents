package com.github.bordertech.wcomponents.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Mark Reeves
 */
public class HtmlIconClassUtilTest {

	/**
	 * Test of getIconClasses method, of class HtmlIconClassUtil.
	 */
	@Test
	public void testGetIconClasses() {
		String icon = "fa-some-icon";
		String expResult = "wc-icon fa-some-icon";
		String result = HtmlIconClassUtil.getIconClasses(icon);
		Assert.assertEquals(expResult, result);
	}

	/**
	 * Test of getIconClasses method, of class HtmlIconClassUtil.
	 */
	@Test
	public void testGetIconClassesWithIconPositionNull() {
		String icon = "fa-some-icon";
		HtmlIconClassUtil.IconPosition position = null;
		String expResult = "wc-icon fa-some-icon";
		String result = HtmlIconClassUtil.getIconClasses(icon, position);
		Assert.assertEquals(expResult, result);
	}


	/**
	 * Test of getIconClasses method, of class HtmlIconClassUtil.
	 */
	@Test
	public void testGetIconClassesWithIconPositionAfter() {
		String icon = "fa-some-icon";
		HtmlIconClassUtil.IconPosition position = HtmlIconClassUtil.IconPosition.AFTER;
		String expResult = "wc-icon-after fa-some-icon";
		String result = HtmlIconClassUtil.getIconClasses(icon, position);
		Assert.assertEquals(expResult, result);
	}


	/**
	 * Test of getIconClasses method, of class HtmlIconClassUtil.
	 */
	@Test
	public void testGetIconClassesWithIconPositionBefore() {
		String icon = "fa-some-icon";
		HtmlIconClassUtil.IconPosition position = HtmlIconClassUtil.IconPosition.BEFORE;
		String expResult = "wc-icon-before fa-some-icon";
		String result = HtmlIconClassUtil.getIconClasses(icon, position);
		Assert.assertEquals(expResult, result);
	}

	/**
	 * Test of getIconClasses method, of class HtmlIconClassUtil.
	 */
	@Test
	public void testGetIconClassesWithIconPositionundefined() {
		String icon = "fa-some-icon";
		HtmlIconClassUtil.IconPosition position = HtmlIconClassUtil.IconPosition.UNDEFINED;
		String expResult = "wc-icon fa-some-icon";
		String result = HtmlIconClassUtil.getIconClasses(icon, position);
		Assert.assertEquals(expResult, result);
	}

}
