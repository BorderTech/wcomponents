package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.util.SpaceUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * ColumnLayout_Test - unit tests for {@link ColumnLayout}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class ColumnLayout_Test {
	/**
	 * A small gap.
	 */
	private static final Size GAP = Size.SMALL;
	/**
	 * A big gap.
	 */
	private static final Size BIG_GAP = Size.LARGE;

	/**
	 * Integer equivalent of the small gap.
	 */
	private static final int INT_GAP = SpaceUtil.sizeToInt(GAP);

	/**
	 * Integer equivalent of the big gap.
	 */
	private static final int INT_BIG_GAP = SpaceUtil.sizeToInt(BIG_GAP);

	@Test
	public void testIntConstructor() {
		final int[] cols = new int[]{20, 30, 50};

		ColumnLayout layout = new ColumnLayout(cols);
		Assert.assertEquals("Incorrect column count", 3, layout.getColumnCount());
		Assert.assertEquals("Incorrect column 1 width", 20, layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column 2 width", 30, layout.getColumnWidth(1));
		Assert.assertEquals("Incorrect column 3 width", 50, layout.getColumnWidth(2));

		for (int i = 0; i < cols.length; i++) {
			Assert.assertEquals("Column " + (i + 1) + " alignment should be LEFT",
					ColumnLayout.Alignment.LEFT, layout.getColumnAlignment(i));
		}

		Assert.assertNull("Incorrect default HGAP", layout.getHorizontalGap());
		Assert.assertNull("Incorrect default VGAP", layout.getVerticalGap());
	}

	@Test
	public void testIntAlignConstructor() {
		final int[] cols = new int[]{30, 70};
		final ColumnLayout.Alignment[] align = new ColumnLayout.Alignment[]{ColumnLayout.Alignment.CENTER, ColumnLayout.Alignment.RIGHT};

		ColumnLayout layout = new ColumnLayout(cols, align);
		Assert.assertEquals("Incorrect column count", 2, layout.getColumnCount());
		Assert.assertEquals("Incorrect column 1 width", 30, layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column 2 width", 70, layout.getColumnWidth(1));
		Assert.assertEquals("Incorrect column 1 alignment", ColumnLayout.Alignment.CENTER, layout.
				getColumnAlignment(0));
		Assert.assertEquals("Incorrect column 2 alignment", ColumnLayout.Alignment.RIGHT, layout.
				getColumnAlignment(1));
		Assert.assertNull("Incorrect default HGAP", layout.getHorizontalGap());
		Assert.assertNull("Incorrect default VGAP", layout.getVerticalGap());
	}

	@Test
	public void testIntAlignGapConstructor() {
		final int[] cols = new int[]{100};
		final ColumnLayout.Alignment[] align = new ColumnLayout.Alignment[]{ColumnLayout.Alignment.RIGHT};

		ColumnLayout layout = new ColumnLayout(cols, align, GAP, BIG_GAP);
		Assert.assertEquals("Incorrect column count", 1, layout.getColumnCount());
		Assert.assertEquals("Incorrect column width", 100, layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column alignment", ColumnLayout.Alignment.RIGHT, layout.
				getColumnAlignment(0));
		Assert.assertEquals("Incorrect HGAP", GAP, layout.getHorizontalGap());
		Assert.assertEquals("Incorrect VGAP", BIG_GAP, layout.getVerticalGap());
	}

	@Test
	public void testDeprecatedIntAlignGapConstructor() {
		final int[] cols = new int[]{100};
		final ColumnLayout.Alignment[] align = new ColumnLayout.Alignment[]{ColumnLayout.Alignment.RIGHT};

		ColumnLayout layout = new ColumnLayout(cols, align, INT_GAP, INT_BIG_GAP);
		Assert.assertEquals("Incorrect column count", 1, layout.getColumnCount());
		Assert.assertEquals("Incorrect column width", 100, layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column alignment", ColumnLayout.Alignment.RIGHT, layout.
				getColumnAlignment(0));
		Assert.assertEquals("Incorrect HGAP", GAP, layout.getHorizontalGap());
		Assert.assertEquals("Incorrect VGAP", BIG_GAP, layout.getVerticalGap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCols() {
		new ColumnLayout(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoCols() {
		new ColumnLayout(new int[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidColumnWidthLessZero() {
		new ColumnLayout(new int[]{-1});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidColumnWidthGreater100() {
		new ColumnLayout(new int[]{101});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMismatchingColumnCount() {
		new ColumnLayout(new int[]{50, 50},
				new ColumnLayout.Alignment[]{ColumnLayout.Alignment.LEFT});
	}
}
