package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SpaceUtil;
import org.junit.Assert;
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
		Margin margin = new Margin(Size.SMALL);
		Assert.assertEquals("Incorrect all margin returned", Size.SMALL, margin.getMargin());
		Assert.assertEquals("Incorrect top margin returned", null, margin.getTop());
		Assert.assertEquals("Incorrect right margin returned", null, margin.getRight());
		Assert.assertEquals("Incorrect bottom margin returned", null, margin.getBottom());
		Assert.assertEquals("Incorrect left margin returned", null, margin.getLeft());

		Assert.assertEquals("Incorrect all margin returned", -1, margin.getAll());
		Assert.assertEquals("Incorrect north margin returned", -1, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", -1, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", -1, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", -1, margin.getWest());
	}

	@Test
	public void testConstructor2() {
		// Create Margin for all sides
		Margin margin = new Margin(Size.SMALL, Size.MEDIUM, Size.LARGE, Size.XL);
		Assert.assertEquals("Incorrect all margin returned", null, margin.getMargin());
		Assert.assertEquals("Incorrect top margin returned", Size.SMALL, margin.getTop());
		Assert.assertEquals("Incorrect right margin returned", Size.MEDIUM, margin.getRight());
		Assert.assertEquals("Incorrect bottom margin returned", Size.LARGE, margin.getBottom());
		Assert.assertEquals("Incorrect left margin returned", Size.XL, margin.getLeft());

		Assert.assertEquals("Incorrect all margin returned", -1, margin.getAll());
		Assert.assertEquals("Incorrect north margin returned", -1, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", -1, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", -1, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", -1, margin.getWest());
	}

	// Deprecated constructors
	@Test
	public void testConstructor3() {
		// Create Margin with "all"
		int intSize = 6;
		Size size = SpaceUtil.intToSize(intSize);
		Margin margin = new Margin(6);
		Assert.assertEquals("Incorrect all margin returned", intSize, margin.getAll());
		Assert.assertEquals("Incorrect all margin returned", size, margin.getMargin());
		Assert.assertEquals("Incorrect north margin returned", -1, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", -1, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", -1, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", -1, margin.getWest());

		Assert.assertEquals("Incorrect all margin returned", size, margin.getMargin());
		Assert.assertEquals("Incorrect north margin returned", null, margin.getTop());
		Assert.assertEquals("Incorrect east margin returned", null, margin.getRight());
		Assert.assertEquals("Incorrect south margin returned", null, margin.getBottom());
		Assert.assertEquals("Incorrect west margin returned", null, margin.getLeft());
	}

	@Test
	public void testConstructor4() {
		// Create Margin for all sides
		final int north = 1;
		final int east = 5;
		final int south = 9;
		final int west = 17;

		Margin margin = new Margin(north, east, south, west);

		Assert.assertEquals("Incorrect north margin returned", -1, margin.getAll());
		Assert.assertEquals("Incorrect north margin returned", north, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", east, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", south, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", west, margin.getWest());

		Assert.assertEquals("Incorrect all margin returned", null, margin.getMargin());
		Assert.assertEquals("Incorrect north margin returned", SpaceUtil.intToSize(north), margin.getTop());
		Assert.assertEquals("Incorrect east margin returned", SpaceUtil.intToSize(east), margin.getRight());
		Assert.assertEquals("Incorrect south margin returned", SpaceUtil.intToSize(south), margin.getBottom());
		Assert.assertEquals("Incorrect west margin returned", SpaceUtil.intToSize(west), margin.getLeft());
	}

	@Test
	public void testMarginOnComponent() {
		WPanel panel = new WPanel();
		Assert.assertNull(panel.getMargin());
		panel.setMargin(new Margin(Size.MEDIUM));
		Assert.assertNotNull(panel.getMargin());
		Assert.assertEquals(Size.MEDIUM, panel.getMargin().getMargin());
		Assert.assertNull(panel.getMargin().getTop());
		Assert.assertNull(panel.getMargin().getRight());
		Assert.assertNull(panel.getMargin().getBottom());
		Assert.assertNull(panel.getMargin().getLeft());
		panel.setMargin(null);
		Assert.assertNull(panel.getMargin());
		panel.setMargin(new Margin(Size.SMALL, Size.MEDIUM, Size.LARGE, Size.ZERO));
		Assert.assertNull(panel.getMargin().getMargin());
		Assert.assertEquals(Size.SMALL, panel.getMargin().getTop());
		Assert.assertEquals(Size.MEDIUM, panel.getMargin().getRight());
		Assert.assertEquals(Size.LARGE, panel.getMargin().getBottom());
		Assert.assertEquals(Size.ZERO, panel.getMargin().getLeft());
	}
}
