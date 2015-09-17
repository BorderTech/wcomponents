package com.github.bordertech.wcomponents.layout;

import org.junit.Assert;
import org.junit.Test;

/**
 * FlowLayout_Test - unit tests for {@link FlowLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class FlowLayout_Test {

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
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.VERTICAL, 3, 5);
		Assert.assertEquals("Incorrect alignment", FlowLayout.Alignment.VERTICAL, flow.
				getAlignment());
		Assert.assertEquals("Incorrect horizontal gap", 3, flow.getHgap());
		Assert.assertEquals("Incorrect vertical gap", 5, flow.getVgap());
		Assert.assertNull("Default content alignment should be null", flow.getContentAlignment());
	}

	@Test
	public void testAlignmentHgapVgapContentConstructor() {
		FlowLayout flow = new FlowLayout(FlowLayout.Alignment.VERTICAL, 3, 5,
				FlowLayout.ContentAlignment.TOP);
		Assert.assertEquals("Incorrect alignment", FlowLayout.Alignment.VERTICAL, flow.
				getAlignment());
		Assert.assertEquals("Incorrect horizontal gap", 3, flow.getHgap());
		Assert.assertEquals("Incorrect vertical gap", 5, flow.getVgap());
		Assert.assertEquals("Incorrect content alignment", FlowLayout.ContentAlignment.TOP, flow.
				getContentAlignment());
	}

}
