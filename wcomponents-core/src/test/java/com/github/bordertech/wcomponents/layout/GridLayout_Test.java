package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.util.GapSizeUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * GridLayout_Test - unit tests for {@link GridLayout}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class GridLayout_Test {
	/**
	 * A small gap.
	 */
	private static final GapSizeUtil.Size GAP = GapSizeUtil.Size.SMALL;
	/**
	 * A big gap.
	 */
	private static final GapSizeUtil.Size BIG_GAP = GapSizeUtil.Size.LARGE;

	@Test
	public void testRowColConstructor() {
		GridLayout grid = new GridLayout(3, 5);
		Assert.assertEquals("Incorrect rows", 3, grid.getRows());
		Assert.assertEquals("Incorrect cols", 5, grid.getCols());
		Assert.assertNull("Incorrect hgap", grid.getHgap());
		Assert.assertNull("Incorrect vgap", grid.getVgap());
	}

	@Test
	public void testRowColHgapVgapConstructor() {
		GridLayout grid = new GridLayout(0, 3, GAP, BIG_GAP);
		Assert.assertEquals("Incorrect rows", 0, grid.getRows()); // unspecified rows, 5 cols
		Assert.assertEquals("Incorrect cols", 3, grid.getCols());
		Assert.assertEquals("Incorrect hgap", GAP, grid.getHgap());
		Assert.assertEquals("Incorrect vgap", BIG_GAP, grid.getVgap());
	}

	@Test
	public void testDeprecatedRowColHgapVgapConstructor() {
		GridLayout grid = new GridLayout(0, 3, GapSizeUtil.sizeToInt(GAP), GapSizeUtil.sizeToInt(BIG_GAP));
		Assert.assertEquals("Incorrect rows", 0, grid.getRows()); // unspecified rows, 5 cols
		Assert.assertEquals("Incorrect cols", 3, grid.getCols());
		Assert.assertEquals("Incorrect hgap", GAP, grid.getHgap());
		Assert.assertEquals("Incorrect vgap", BIG_GAP, grid.getVgap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidRows() {
		new GridLayout(-1, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidCols() {
		new GridLayout(3, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidZeroRowsCols() {
		new GridLayout(0, 0);
	}
}
