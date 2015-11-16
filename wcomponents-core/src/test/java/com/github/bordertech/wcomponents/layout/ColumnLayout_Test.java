package com.github.bordertech.wcomponents.layout;

import org.junit.Assert;
import org.junit.Test;

/**
 * ColumnLayout_Test - unit tests for {@link ColumnLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ColumnLayout_Test {

	@Test
	public void testIntConstructor() {
		final int[] cols = new int[]{20, 30, 50};

		ColumnLayout layout = new ColumnLayout(cols);
		Assert.assertEquals("Incorrect column count", 3, layout.getColumnCount());
		Assert.assertEquals("Incorrect column 1 width", "20%", layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column 2 width", "30%", layout.getColumnWidth(1));
		Assert.assertEquals("Incorrect column 3 width", "50%", layout.getColumnWidth(2));

		for (int i = 0; i < cols.length; i++) {
			Assert.assertEquals("Column " + (i + 1) + " alignment should be LEFT",
					ColumnLayout.Alignment.LEFT, layout.getColumnAlignment(i));
		}

		Assert.assertEquals("Incorrect default HGAP", 0, layout.getHgap());
		Assert.assertEquals("Incorrect default VGAP", 0, layout.getVgap());
	}

	@Test
	public void testStrConstructor() {
		final String[] cols = new String[]{"20em", "30px", "50%"};

		ColumnLayout layout = new ColumnLayout(cols);
		Assert.assertEquals("Incorrect column count", 3, layout.getColumnCount());
		Assert.assertEquals("Incorrect column 1 width", "20em", layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column 2 width", "30px", layout.getColumnWidth(1));
		Assert.assertEquals("Incorrect column 3 width", "50%", layout.getColumnWidth(2));

		for (int i = 0; i < cols.length; i++) {
			Assert.assertEquals("Column " + (i + 1) + " alignment should be LEFT",
					ColumnLayout.Alignment.LEFT, layout.getColumnAlignment(i));
		}

		Assert.assertEquals("Incorrect default HGAP", 0, layout.getHgap());
		Assert.assertEquals("Incorrect default VGAP", 0, layout.getVgap());
	}

	@Test
	public void testIntAlignConstructor() {
		final int[] cols = new int[]{30, 70};
		final ColumnLayout.Alignment[] align = new ColumnLayout.Alignment[]{ColumnLayout.Alignment.CENTER, ColumnLayout.Alignment.RIGHT};

		ColumnLayout layout = new ColumnLayout(cols, align);
		Assert.assertEquals("Incorrect column count", 2, layout.getColumnCount());
		Assert.assertEquals("Incorrect column 1 width", "30%", layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column 2 width", "70%", layout.getColumnWidth(1));
		Assert.assertEquals("Incorrect column 1 alignment", ColumnLayout.Alignment.CENTER, layout.
				getColumnAlignment(0));
		Assert.assertEquals("Incorrect column 2 alignment", ColumnLayout.Alignment.RIGHT, layout.
				getColumnAlignment(1));
		Assert.assertEquals("Incorrect default HGAP", 0, layout.getHgap());
		Assert.assertEquals("Incorrect default VGAP", 0, layout.getVgap());
	}

	@Test
	public void testStringAlignConstructor() {
		final String[] cols = new String[]{"30em", "70px"};
		final ColumnLayout.Alignment[] align = new ColumnLayout.Alignment[]{ColumnLayout.Alignment.CENTER, ColumnLayout.Alignment.RIGHT};

		ColumnLayout layout = new ColumnLayout(cols, align);
		Assert.assertEquals("Incorrect column count", 2, layout.getColumnCount());
		Assert.assertEquals("Incorrect column 1 width", "30em", layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column 2 width", "70px", layout.getColumnWidth(1));
		Assert.assertEquals("Incorrect column 1 alignment", ColumnLayout.Alignment.CENTER, layout.
				getColumnAlignment(0));
		Assert.assertEquals("Incorrect column 2 alignment", ColumnLayout.Alignment.RIGHT, layout.
				getColumnAlignment(1));
		Assert.assertEquals("Incorrect default HGAP", 0, layout.getHgap());
		Assert.assertEquals("Incorrect default VGAP", 0, layout.getVgap());
	}

	@Test
	public void testIntAlignGapConstructor() {
		final int[] cols = new int[]{100};
		final ColumnLayout.Alignment[] align = new ColumnLayout.Alignment[]{ColumnLayout.Alignment.RIGHT};

		ColumnLayout layout = new ColumnLayout(cols, align, 3, 5);
		Assert.assertEquals("Incorrect column count", 1, layout.getColumnCount());
		Assert.assertEquals("Incorrect column width", "100%", layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column alignment", ColumnLayout.Alignment.RIGHT, layout.
				getColumnAlignment(0));
		Assert.assertEquals("Incorrect HGAP", 3, layout.getHgap());
		Assert.assertEquals("Incorrect VGAP", 5, layout.getVgap());
	}

	@Test
	public void testStrAlignGapConstructor() {
		final String[] cols = new String[]{"100em"};
		final ColumnLayout.Alignment[] align = new ColumnLayout.Alignment[]{ColumnLayout.Alignment.RIGHT};

		ColumnLayout layout = new ColumnLayout(cols, align, 3, 5);
		Assert.assertEquals("Incorrect column count", 1, layout.getColumnCount());
		Assert.assertEquals("Incorrect column width", "100em", layout.getColumnWidth(0));
		Assert.assertEquals("Incorrect column alignment", ColumnLayout.Alignment.RIGHT, layout.
				getColumnAlignment(0));
		Assert.assertEquals("Incorrect HGAP", 3, layout.getHgap());
		Assert.assertEquals("Incorrect VGAP", 5, layout.getVgap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCols() {
		new ColumnLayout((int[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullColsString() {
		new ColumnLayout((String[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoColsStr() {
		new ColumnLayout(new String[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidColumnWidthLessOne() {
		new ColumnLayout(new int[]{0});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidColumnWidthGreater100() {
		new ColumnLayout(new int[]{101});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidColumnWidthEmpty() {
		new ColumnLayout(new String[]{""});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMismatchingColumnCount() {
		new ColumnLayout(new int[]{50, 50},
				new ColumnLayout.Alignment[]{ColumnLayout.Alignment.LEFT});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMismatchingColumnCountString() {
		new ColumnLayout(new String[]{"50px", "50em"},
				new ColumnLayout.Alignment[]{ColumnLayout.Alignment.LEFT});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidHgap() {
		new ColumnLayout(new int[]{100}, -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidVgap() {
		new ColumnLayout(new int[]{100}, 0, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidHgapStr() {
		new ColumnLayout(new String[]{"auto"}, -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidVgapStr() {
		new ColumnLayout(new String[]{"auto"}, 0, -1);
	}
}
