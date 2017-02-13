package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SpaceUtil;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link Margin}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Margin_Test {

	@Test
	public void testConstructor1() {
		// Create Margin with "all"
		Margin margin = new Margin(SpaceUtil.Size.SMALL);
		Assert.assertEquals("Incorrect all margin returned", SpaceUtil.Size.SMALL, margin.getMargin());
		Assert.assertEquals("Incorrect north margin returned", null, margin.getTop());
		Assert.assertEquals("Incorrect east margin returned", null, margin.getRight());
		Assert.assertEquals("Incorrect south margin returned", null, margin.getBottom());
		Assert.assertEquals("Incorrect west margin returned", null, margin.getLeft());
	}

	@Test
	public void testConstructor2() {
		// Create Margin for all sides
		Margin margin = new Margin(SpaceUtil.Size.SMALL, SpaceUtil.Size.MEDIUM, SpaceUtil.Size.LARGE, 	SpaceUtil.Size.XL);
		Assert.assertEquals("Incorrect all margin returned", null, margin.getMargin());
		Assert.assertEquals("Incorrect north margin returned", SpaceUtil.Size.SMALL, margin.getTop());
		Assert.assertEquals("Incorrect east margin returned", SpaceUtil.Size.MEDIUM, margin.getRight());
		Assert.assertEquals("Incorrect south margin returned", SpaceUtil.Size.LARGE, margin.getBottom());
		Assert.assertEquals("Incorrect west margin returned", SpaceUtil.Size.XL, margin.getLeft());
	}

	@Test
	public void testMarginOnComponent() {
		WPanel panel = new WPanel();
		Assert.assertNull(panel.getMargin());
		panel.setMargin(new Margin(SpaceUtil.Size.MEDIUM));
		Assert.assertNotNull(panel.getMargin());
		Assert.assertEquals(SpaceUtil.Size.MEDIUM, panel.getMargin().getMargin());
		Assert.assertNull(panel.getMargin().getTop());
		Assert.assertNull(panel.getMargin().getRight());
		Assert.assertNull(panel.getMargin().getBottom());
		Assert.assertNull(panel.getMargin().getLeft());
		panel.setMargin(null);
		Assert.assertNull(panel.getMargin());
		panel.setMargin(new Margin(SpaceUtil.Size.SMALL, SpaceUtil.Size.MEDIUM, SpaceUtil.Size.LARGE, 	SpaceUtil.Size.ZERO));
		Assert.assertNull(panel.getMargin().getMargin());
		Assert.assertEquals(SpaceUtil.Size.SMALL, panel.getMargin().getTop());
		Assert.assertEquals(SpaceUtil.Size.MEDIUM, panel.getMargin().getRight());
		Assert.assertEquals(SpaceUtil.Size.LARGE, panel.getMargin().getBottom());
		Assert.assertEquals(SpaceUtil.Size.ZERO, panel.getMargin().getLeft());
	}
}
