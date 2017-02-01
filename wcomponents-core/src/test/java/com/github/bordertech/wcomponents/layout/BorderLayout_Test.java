package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.util.GapSizeUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * BorderLayout_Test - unit tests for {@link BorderLayout}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class BorderLayout_Test {
	/**
	 * A small gap.
	 */
	private static final GapSizeUtil.Size GAP = GapSizeUtil.Size.SMALL;
	/**
	 * A big gap.
	 */
	private static final GapSizeUtil.Size BIG_GAP = GapSizeUtil.Size.LARGE;

	@Test
	public void testDefaultConstructor() {
		BorderLayout layout = new BorderLayout();
		Assert.assertNull("Incorrect hgap", layout.getHorizontalGap());
		Assert.assertNull("Incorrect vgap", layout.getVerticalGap());
	}

	@Test
	public void testHgapVgapConstructor() {
		BorderLayout layout = new BorderLayout(GAP, BIG_GAP);
		Assert.assertEquals("Incorrect hgap", GAP, layout.getHorizontalGap());
		Assert.assertEquals("Incorrect vgap", BIG_GAP, layout.getVerticalGap());

		layout = new BorderLayout(GAP, null);
		Assert.assertEquals("Incorrect hgap", GAP, layout.getHorizontalGap());
		Assert.assertNull("Incorrect vgap", layout.getVerticalGap());

		layout = new BorderLayout(null, BIG_GAP);
		Assert.assertNull("Incorrect hgap", layout.getHorizontalGap());
		Assert.assertEquals("Incorrect vgap", BIG_GAP, layout.getVerticalGap());
	}

	@Test
	public void testDeprecatedHgapVgapConstructor() {
		BorderLayout layout = new BorderLayout(GapSizeUtil.sizeToInt(GAP), GapSizeUtil.sizeToInt(BIG_GAP));
		Assert.assertEquals("Incorrect hgap", GAP, layout.getHorizontalGap());
		Assert.assertEquals("Incorrect vgap", BIG_GAP, layout.getVerticalGap());

		layout = new BorderLayout(GapSizeUtil.sizeToInt(GAP), 0);
		Assert.assertEquals("Incorrect hgap", GAP, layout.getHorizontalGap());
		Assert.assertNull("Incorrect vgap", layout.getVerticalGap());

		layout = new BorderLayout(0, GapSizeUtil.sizeToInt(GAP));
		Assert.assertNull("Incorrect hgap", layout.getHorizontalGap());
		Assert.assertEquals("Incorrect vgap", GAP, layout.getVerticalGap());
	}
}
