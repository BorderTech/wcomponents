package com.github.bordertech.wcomponents.layout;

import org.junit.Assert;
import org.junit.Test;

/**
 * FlowLayout_Test - unit tests for {@link FlowLayout}.
 *
 * @author Yiannis Paschalidis, Mark Reeves
 * @since 1.0.0
 */
public class FlowLayout_Test {

	/**
	 * A reusable gap value.
	 */
	private static final int GAP = 12;

	/**
	 * A different reusable gap value. This is used to differentiate the (now deprecated) hgap and vgap properties.
	 */
	private static final int BIG_GAP = 18;

	@Test(expected = IllegalArgumentException.class)
	public void testNullAlignment() {
		new FlowLayout(null);
	}

	@Test
	public void testDefaultConstructor() {
		FlowLayout flow = new FlowLayout();
		Assert.assertEquals("Default alignment should be CENTER", FlowLayout.Alignment.CENTER, flow.getAlignment());
		Assert.assertEquals("Default gap should be zero", 0, flow.getGap());
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
			Assert.assertEquals("Incorrect gap", GAP, flow.getGap());
		}
	}

	@Test
	public void testAlignmentGapContentAlignmentConstructor() {
		FlowLayout flow;
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			for (FlowLayout.ContentAlignment c : FlowLayout.ContentAlignment.values()) {
				flow = new FlowLayout(a, GAP, c);
				Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
				Assert.assertEquals("Incorrect gap", GAP, flow.getGap());
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
			flow = new FlowLayout(a, GAP, BIG_GAP);
			Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
			Assert.assertEquals("Incorrect horizontal gap", isVertical ? 0 : GAP, flow.getHgap());
			Assert.assertEquals("Incorrect vertical gap", isVertical ? BIG_GAP : 0, flow.getVgap());
		}
	}

	@Test
	public void testAlignmentHgapVgapContentConstructor() {
		FlowLayout flow;
		boolean isVertical;
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			isVertical = FlowLayout.Alignment.VERTICAL.equals(a);
			for (FlowLayout.ContentAlignment c : FlowLayout.ContentAlignment.values()) {
				flow = new FlowLayout(a, GAP, BIG_GAP, c);
				Assert.assertEquals("Incorrect alignment", a, flow.getAlignment());
				Assert.assertEquals("Incorrect horizontal gap", isVertical ? 0 : GAP, flow.getHgap());
				Assert.assertEquals("Incorrect vertical gap", isVertical ? BIG_GAP : 0, flow.getVgap());

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
		for (FlowLayout.Alignment a : FlowLayout.Alignment.values()) {
			isVertical = FlowLayout.Alignment.VERTICAL.equals(a);
			flow = new FlowLayout(a, GAP);
			Assert.assertEquals("Incorrect vertical gap", isVertical ? GAP : 0, flow.getVgap());
			Assert.assertEquals("incorrect horizontal gap", isVertical ? 0 : GAP, flow.getHgap());
		}
	}
}
