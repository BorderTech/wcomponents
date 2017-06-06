package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.util.SpaceUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * FlowLayout_Test - unit tests for {@link FlowLayout}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class FlowLayout_Test {

	/**
	 * A reusable gap value.
	 */
	private static final Size GAP = Size.MEDIUM;

	/**
	 * A different reusable gap value. This is used to differentiate the (now deprecated) hgap and vgap properties.
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

	@Test(expected = IllegalArgumentException.class)
	public void testNullAlignment() {
		new FlowLayout(null);
	}

	@Test
	public void testDefaultConstructor() {
		FlowLayout flow = new FlowLayout();
		Assert.assertEquals("Default alignment should be CENTER", FlowLayout.Alignment.CENTER, flow.getAlignment());
		Assert.assertNull("Default gap should be null", flow.getSpace());
		Assert.assertNull("Default content alignment should be null", flow.getContentAlignment());
	}

	@Test
	public void testAlignmentConstructor() {
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			FlowLayout flow = new FlowLayout(a);
			Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
		}
	}

	@Test
	public void testAlignmentContentConstructorAlignment() {
		FlowLayout flow;
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			flow = new FlowLayout(a, FlowLayout.ContentAlignment.TOP);
			Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
		}
	}

	@Test
	public void testAlignmentContentConstructor() {
		FlowLayout flow;
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			for (FlowLayout.ContentAlignment c : FlowLayout.ContentAlignment.values()) {
				flow = new FlowLayout(a, c);
				Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
				if (FlowLayout.Alignment.VERTICAL.equals(a)) {
					Assert.assertNull(flow.getContentAlignment());
				} else {
					Assert.assertEquals("Incorrect content alignment alignment", c, flow.getContentAlignment());
				}
			}
		}
	}

	@Test
	public void testAlignmentGapConstructor() {
		FlowLayout flow;
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			flow = new FlowLayout(a, GAP);
			Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
			Assert.assertEquals("Incorrect gap", GAP, flow.getSpace());
		}
	}

	@Test
	public void testAlignmentGapContentAlignmentConstructor() {
		FlowLayout flow;
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			for (FlowLayout.ContentAlignment c : FlowLayout.ContentAlignment.values()) {
				flow = new FlowLayout(a, GAP, c);
				Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
				Assert.assertEquals("Incorrect gap", GAP, flow.getSpace());
				if (FlowLayout.Alignment.VERTICAL.equals(a)) {
					Assert.assertNull(flow.getContentAlignment());
				} else {
					Assert.assertEquals("Incorrect content alignment alignment", c, flow.getContentAlignment());
				}
			}
		}
	}

	// Tests of deprecated members
	@Test
	public void testAlignmentHgapVgapConstructor() {
		FlowLayout flow;
		boolean isVertical;
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			isVertical = FlowLayout.Alignment.VERTICAL.equals(a);
			flow = new FlowLayout(a, INT_GAP, INT_BIG_GAP);
			Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
			Assert.assertEquals("Incorrect horizontal gap", isVertical ? BIG_GAP : GAP, flow.getSpace());
		}
	}

	@Test
	public void testAlignmentHgapVgapContentConstructor() {
		FlowLayout flow;
		boolean isVertical;
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			isVertical = FlowLayout.Alignment.VERTICAL.equals(a);
			for (FlowLayout.ContentAlignment c : FlowLayout.ContentAlignment.values()) {
				flow = new FlowLayout(a, INT_GAP, INT_BIG_GAP, c);
				Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
				Assert.assertEquals("Incorrect gap", isVertical ? BIG_GAP : GAP, flow.getSpace());

				if (isVertical) {
					Assert.assertNull(flow.getContentAlignment());
				} else {
					Assert.assertEquals("Incorrect content alignment alignment", c, flow.getContentAlignment());
				}
			}
		}
	}

	// VERTICAL Alignment should have vgap but 0 hgap.
	// non-VERTICAL Alignment should have hgap but 0 vgap.
	@Test
	public void testHGapVGapAccessors() {
		FlowLayout flow;
		boolean isVertical;
		int intGap = 12;
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			isVertical = FlowLayout.Alignment.VERTICAL.equals(a);
			flow = new FlowLayout(a, intGap);
			Assert.assertEquals("Incorrect vertical gap", isVertical ? intGap : 0, flow.getVgap());
			Assert.assertEquals("incorrect horizontal gap", isVertical ? 0 : intGap, flow.getHgap());
		}
	}
}
