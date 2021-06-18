package com.github.bordertech.wcomponents;

import static com.github.bordertech.wcomponents.Size.*;
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
		Margin margin = new Margin(2);
		Assert.assertEquals("Incorrect all margin returned", 2, margin.getAll());
		Assert.assertEquals("Incorrect north margin returned", -1, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", -1, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", -1, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", -1, margin.getWest());

		Assert.assertEquals("Incorrect all margin returned", SpaceUtil.intToSize(2), margin.getMargin());
		Assert.assertEquals("Incorrect top margin returned", null, margin.getTop());
		Assert.assertEquals("Incorrect right margin returned", null, margin.getRight());
		Assert.assertEquals("Incorrect bottom margin returned", null, margin.getBottom());
		Assert.assertEquals("Incorrect left margin returned", null, margin.getLeft());
	}

	@Test
	public void testConstructor4() {
		// Create Margin for all sides
		Margin margin = new Margin(1, 5, 9, 17);
		Assert.assertEquals("Incorrect all margin returned", -1, margin.getAll());
		Assert.assertEquals("Incorrect north margin returned", 1, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", 5, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", 9, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", 17, margin.getWest());

		Assert.assertEquals("Incorrect all margin returned", null, margin.getMargin());
		Assert.assertEquals("Incorrect top margin returned", SpaceUtil.intToSize(1), margin.getTop());
		Assert.assertEquals("Incorrect right margin returned", SpaceUtil.intToSize(5), margin.getRight());
		Assert.assertEquals("Incorrect bottom margin returned", SpaceUtil.intToSize(9), margin.getBottom());
		Assert.assertEquals("Incorrect left margin returned", SpaceUtil.intToSize(17), margin.getLeft());
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

	@Test
	public void testConvenienceInstances() {

		Assert.assertNull(Margin.all(ZERO).getTop());
		Assert.assertNull(Margin.all(ZERO).getRight());
		Assert.assertNull(Margin.all(ZERO).getBottom());
		Assert.assertNull(Margin.all(ZERO).getLeft());
		Assert.assertEquals(ZERO, Margin.all(ZERO).getMargin());

		Assert.assertNull(Margin.all(SMALL).getTop());
		Assert.assertNull(Margin.all(SMALL).getRight());
		Assert.assertNull(Margin.all(SMALL).getBottom());
		Assert.assertNull(Margin.all(SMALL).getLeft());
		Assert.assertEquals(SMALL, Margin.all(SMALL).getMargin());

		Assert.assertNull(Margin.all(MEDIUM).getTop());
		Assert.assertNull(Margin.all(MEDIUM).getRight());
		Assert.assertNull(Margin.all(MEDIUM).getBottom());
		Assert.assertNull(Margin.all(MEDIUM).getLeft());
		Assert.assertEquals(MEDIUM, Margin.all(MEDIUM).getMargin());

		Assert.assertNull(Margin.all(LARGE).getTop());
		Assert.assertNull(Margin.all(LARGE).getRight());
		Assert.assertNull(Margin.all(LARGE).getBottom());
		Assert.assertNull(Margin.all(LARGE).getLeft());
		Assert.assertEquals(LARGE, Margin.all(LARGE).getMargin());

		Assert.assertNull(Margin.all(XL).getTop());
		Assert.assertNull(Margin.all(XL).getRight());
		Assert.assertNull(Margin.all(XL).getBottom());
		Assert.assertNull(Margin.all(XL).getLeft());
		Assert.assertEquals(XL, Margin.all(XL).getMargin());
		
		Assert.assertNull(Margin.all(null));
	}
}
