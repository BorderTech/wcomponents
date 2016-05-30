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
	 * A different reusable gap value.
	 */
	private static final int BIG_GAP = 18;

	@Test
	public void testDefaultConstructor() {
		FlowLayout flow = new FlowLayout();
		Assert.assertEquals("Default alignment should be CENTER", FlowLayout.Alignment.CENTER, flow.
				getAlignment());
		Assert.assertEquals("Default horizontal gap should be zero", 0, flow.getHgap());
		Assert.assertEquals("Default vertical gap should be zero", 0, flow.getVgap());
		Assert.assertNull("Default content alignment should be null", flow.getContentAlignment());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAlignment() {
		new FlowLayout(null);
	}

	@Test
	public void testAlignmentConstructor() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.VERTICAL);
		Assert.assertEquals("Incorrect alignment", FlowLayout.Alignment.VERTICAL, flow.
				getAlignment());
		Assert.assertEquals("Default horizontal gap should be zero", 0, flow.getHgap());
		Assert.assertEquals("Default vertical gap should be zero", 0, flow.getVgap());
		Assert.assertNull("Default content alignment should be null", flow.getContentAlignment());
	}

	@Test
	public void testAlignmentContentConstructor() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.VERTICAL,
				FlowLayout.ContentAlignment.TOP);
		Assert.assertEquals("Incorrect alignment", FlowLayout.Alignment.VERTICAL, flow.
				getAlignment());
		Assert.assertEquals("Default horizontal gap should be zero", 0, flow.getHgap());
		Assert.assertEquals("Default vertical gap should be zero", 0, flow.getVgap());
		Assert.assertEquals("Incorrect content alignment", FlowLayout.ContentAlignment.TOP, flow.
				getContentAlignment());
	}

	@Test
	public void testAlignmentHgapVgapConstructor() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.VERTICAL, GAP, BIG_GAP);
		Assert.assertEquals("Incorrect alignment", FlowLayout.Alignment.VERTICAL, flow.
				getAlignment());
		Assert.assertEquals("Incorrect horizontal gap", GAP, flow.getHgap());
		Assert.assertEquals("Incorrect vertical gap", BIG_GAP, flow.getVgap());
		Assert.assertNull("Default content alignment should be null", flow.getContentAlignment());
	}

	@Test
	public void testAlignmentHgapVgapContentConstructor() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.VERTICAL, GAP, BIG_GAP,
				FlowLayout.ContentAlignment.TOP);
		Assert.assertEquals("Incorrect alignment", FlowLayout.Alignment.VERTICAL, flow.
				getAlignment());
		Assert.assertEquals("Incorrect horizontal gap", GAP, flow.getHgap());
		Assert.assertEquals("Incorrect vertical gap", BIG_GAP, flow.getVgap());
		Assert.assertEquals("Incorrect content alignment", FlowLayout.ContentAlignment.TOP, flow.
				getContentAlignment());
	}

	@Test
	public void testAlignmentGapConstructorVERTICAL() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.VERTICAL, GAP);
		Assert.assertEquals("Incorrect vertical gap", GAP, flow.getVgap());
		Assert.assertEquals("Default horizontal gap should be zero", 0, flow.getHgap());
	}

	@Test
	public void testAlignmentGapConstructorLEFT() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.LEFT, GAP);
		Assert.assertEquals("Incorrect horizontal gap", GAP, flow.getHgap());
		Assert.assertEquals("Default vertical gap should be zero", 0, flow.getVgap());
	}

	@Test
	public void testAlignmentGapConstructorCENTER() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.CENTER, GAP);
		Assert.assertEquals("Incorrect horizontal gap", GAP, flow.getHgap());
		Assert.assertEquals("Default vertical gap should be zero", 0, flow.getVgap());
	}

	@Test
	public void testAlignmentGapConstructorRIGHT() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.RIGHT, GAP);
		Assert.assertEquals("Incorrect horizontal gap", GAP, flow.getHgap());
		Assert.assertEquals("Default vertical gap should be zero", 0, flow.getVgap());
	}

	@Test
	public void testAlignmentGapContentAlignmentConstructorVERTICAL() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.VERTICAL, GAP, FlowLayout.ContentAlignment.TOP);
		Assert.assertNull("Default content alignment should be null", flow.getContentAlignment());
	}

	@Test
	public void testAlignmentGapContentAlignmentConstructorLEFT() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.LEFT, GAP, FlowLayout.ContentAlignment.TOP);
		Assert.assertEquals("Incorrect content alignment", FlowLayout.ContentAlignment.TOP, flow.
				getContentAlignment());
	}

	@Test
	public void testAlignmentGapContentAlignmentConstructorCENTER() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.CENTER, GAP, FlowLayout.ContentAlignment.TOP);
		Assert.assertEquals("Incorrect content alignment", FlowLayout.ContentAlignment.TOP, flow.
				getContentAlignment());
	}

	@Test
	public void testAlignmentGapContentAlignmentConstructorRIGHT() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.RIGHT, GAP, FlowLayout.ContentAlignment.TOP);
		Assert.assertEquals("Incorrect content alignment", FlowLayout.ContentAlignment.TOP, flow.
				getContentAlignment());
	}

}
