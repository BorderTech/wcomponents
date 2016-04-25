package com.github.bordertech.wcomponents.layout;

import org.junit.Assert;
import org.junit.Test;

/**
 * GridLayout_Test - unit tests for {@link GridLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class GridLayout_Test {

	@Test
	public void testRowColConstructor() {
		GridLayout grid = new GridLayout(3, 5);
		Assert.assertEquals("Incorrect rows", 3, grid.getRows());
		Assert.assertEquals("Incorrect cols", 5, grid.getCols());
		Assert.assertEquals("Incorrect hgap", 0, grid.getHgap());
		Assert.assertEquals("Incorrect vgap", 0, grid.getVgap());
	}

	@Test
	public void testRowColHgapVgapConstructor() {
		GridLayout grid = new GridLayout(0, 3, 5, 7);
		Assert.assertEquals("Incorrect rows", 0, grid.getRows()); // unspecified rows, 5 cols
		Assert.assertEquals("Incorrect cols", 3, grid.getCols());
		Assert.assertEquals("Incorrect hgap", 5, grid.getHgap());
		Assert.assertEquals("Incorrect vgap", 7, grid.getVgap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidRows() {
		new GridLayout(-1, 3, 3, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidCols() {
		new GridLayout(3, -1, 3, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidHgap() {
		new GridLayout(3, 3, -1, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidHVap() {
		new GridLayout(3, 3, 3, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidZeroRowsCols() {
		new GridLayout(0, 0);
	}
}
