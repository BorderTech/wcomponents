package com.github.bordertech.wcomponents.layout;

import org.junit.Assert;
import org.junit.Test;

/**
 * BorderLayout_Test - unit tests for {@link BorderLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class BorderLayout_Test {

	@Test
	public void testDefaultConstructor() {
		BorderLayout layout = new BorderLayout();
		Assert.assertEquals("Incorrect hgap", 0, layout.getHgap());
		Assert.assertEquals("Incorrect vgap", 0, layout.getVgap());
	}

	@Test
	public void testHgapVgapConstructor() {
		BorderLayout layout = new BorderLayout(3, 5);
		Assert.assertEquals("Incorrect hgap", 3, layout.getHgap());
		Assert.assertEquals("Incorrect vgap", 5, layout.getVgap());

		layout = new BorderLayout(1, 0);
		Assert.assertEquals("Incorrect hgap", 1, layout.getHgap());
		Assert.assertEquals("Incorrect vgap", 0, layout.getVgap());

		layout = new BorderLayout(0, 1);
		Assert.assertEquals("Incorrect hgap", 0, layout.getHgap());
		Assert.assertEquals("Incorrect vgap", 1, layout.getVgap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidHgap() {
		new BorderLayout(-1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidHVap() {
		new BorderLayout(0, -1);
	}
}
