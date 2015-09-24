package com.github.bordertech.wcomponents;

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
		Margin margin = new Margin(1);
		Assert.assertEquals("Incorrect all margin returned", 1, margin.getAll());
		Assert.assertEquals("Incorrect north margin returned", -1, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", -1, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", -1, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", -1, margin.getWest());
	}

	@Test
	public void testConstructor2() {

		// Create Margin for all sides
		Margin margin = new Margin(1, 2, 3, 4);
		Assert.assertEquals("Incorrect all margin returned", -1, margin.getAll());
		Assert.assertEquals("Incorrect north margin returned", 1, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", 2, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", 3, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", 4, margin.getWest());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidAllAtribute() {
		new Margin(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidNorthAtribute() {
		new Margin(-1, 0, 0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidEastAtribute() {
		new Margin(0, -1, 0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidSouthAtribute() {
		new Margin(0, 0, -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidWestAtribute() {
		new Margin(0, 0, 0, -1);
	}

}
