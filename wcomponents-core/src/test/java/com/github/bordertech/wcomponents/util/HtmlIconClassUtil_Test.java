package com.github.bordertech.wcomponents.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Mark Reeves
 */
public class HtmlIconClassUtil_Test {

	/**
	 * Test of getIconClasses method, of class HtmlIconUtil.
	 */
	@Test
	public void testGetIconClasses() {
		String icon = "fa-some-icon";
		String expResult = "fa fa-some-icon";
		String result = HtmlIconUtil.getIconClasses(icon);
		Assert.assertEquals(expResult, result);
	}

	/**
	 * Test of getIconClasses method, of class HtmlIconUtil.
	 */
	@Test
	public void testGetIconClassesWithIconPositionNull() {
		String icon = "fa-some-icon";
		HtmlIconUtil.IconPosition position = null;
		String expResult = "fa fa-some-icon";
		String result = HtmlIconUtil.getIconClasses(icon, position);
		Assert.assertEquals(expResult, result);
	}


	/**
	 * Test of getIconClasses method, of class HtmlIconUtil.
	 */
	@Test
	public void testGetIconClassesWithIconPositionAfter() {
		String icon = "fa-some-icon";
		HtmlIconUtil.IconPosition position = HtmlIconUtil.IconPosition.AFTER;
		String expResult = "fa wc-icon-after fa-some-icon";
		String result = HtmlIconUtil.getIconClasses(icon, position);
		Assert.assertEquals(expResult, result);
	}


	/**
	 * Test of getIconClasses method, of class HtmlIconUtil.
	 */
	@Test
	public void testGetIconClassesWithIconPositionBefore() {
		String icon = "fa-some-icon";
		HtmlIconUtil.IconPosition position = HtmlIconUtil.IconPosition.BEFORE;
		String expResult = "fa wc-icon-before fa-some-icon";
		String result = HtmlIconUtil.getIconClasses(icon, position);
		Assert.assertEquals(expResult, result);
	}

	/**
	 * Test of getIconClasses method, of class HtmlIconUtil.
	 */
	@Test
	public void testGetIconClassesWithIconPositionundefined() {
		String icon = "fa-some-icon";
		HtmlIconUtil.IconPosition position = HtmlIconUtil.IconPosition.UNDEFINED;
		String expResult = "fa fa-some-icon";
		String result = HtmlIconUtil.getIconClasses(icon, position);
		Assert.assertEquals(expResult, result);
	}

}
