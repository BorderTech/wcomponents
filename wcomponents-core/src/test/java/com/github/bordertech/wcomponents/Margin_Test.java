package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.GapSizeUtil;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link Margin}.
 *
 * @author Jonathan Austin
 * @since 1.GapSizeUtil.Size.ZERO.GapSizeUtil.Size.ZERO
 */
public class Margin_Test {

	@Test
	public void testConstructor1() {
		// Create Margin with "all"
		Margin margin = new Margin(GapSizeUtil.Size.SMALL);
		Assert.assertEquals("Incorrect all margin returned", GapSizeUtil.Size.SMALL, margin.getMargin());
		Assert.assertEquals("Incorrect north margin returned", null, margin.getTop());
		Assert.assertEquals("Incorrect east margin returned", null, margin.getRight());
		Assert.assertEquals("Incorrect south margin returned", null, margin.getBottom());
		Assert.assertEquals("Incorrect west margin returned", null, margin.getLeft());
	}

	@Test
	public void testConstructor2() {

		// Create Margin for all sides
		Margin margin = new Margin(1, 2, 3, 4);
		Assert.assertEquals("Incorrect all margin returned", null, margin.getMargin());
		Assert.assertEquals("Incorrect north margin returned", 1, margin.getTop());
		Assert.assertEquals("Incorrect east margin returned", 2, margin.getRight());
		Assert.assertEquals("Incorrect south margin returned", 3, margin.getBottom());
		Assert.assertEquals("Incorrect west margin returned", 4, margin.getLeft());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidAllAtribute() {
		new Margin(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidNorthAtribute() {
		new Margin(null, GapSizeUtil.Size.ZERO, GapSizeUtil.Size.ZERO, GapSizeUtil.Size.ZERO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidEastAtribute() {
		new Margin(GapSizeUtil.Size.ZERO, null, GapSizeUtil.Size.ZERO, GapSizeUtil.Size.ZERO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidSouthAtribute() {
		new Margin(GapSizeUtil.Size.ZERO, GapSizeUtil.Size.ZERO, null, GapSizeUtil.Size.ZERO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidWestAtribute() {
		new Margin(GapSizeUtil.Size.ZERO, GapSizeUtil.Size.ZERO, GapSizeUtil.Size.ZERO, null);
	}

}
