package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.util.SpaceUtil;
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
		BorderLayout layout = new BorderLayout(INT_GAP, INT_BIG_GAP);
		Assert.assertEquals("Incorrect hgap", GAP, layout.getHorizontalGap());
		Assert.assertEquals("Incorrect vgap", BIG_GAP, layout.getVerticalGap());

		layout = new BorderLayout(INT_GAP, 0);
		Assert.assertEquals("Incorrect hgap", GAP, layout.getHorizontalGap());
		Assert.assertNull("Incorrect vgap", layout.getVerticalGap());

		layout = new BorderLayout(0, INT_GAP);
		Assert.assertNull("Incorrect hgap", layout.getHorizontalGap());
		Assert.assertEquals("Incorrect vgap", GAP, layout.getVerticalGap());
	}
}
