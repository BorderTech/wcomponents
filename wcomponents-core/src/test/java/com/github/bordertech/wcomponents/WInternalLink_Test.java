package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * WInternalLink_Test - unit tests for {@link WInternalLink}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class WInternalLink_Test extends AbstractWComponentTestCase {

	/**
	 * test string.
	 */
	private static final String TEST_TEXT = "WInternalLink_Test_Text";

	@Test
	public void testEmptyConstructor() {
		WInternalLink link = new WInternalLink();

		Assert.assertEquals("text should be unset", link.getText(), null);
		Assert.assertEquals("reference should be unset", link.getReference(), null);
	}

	@Test
	public void testConstructor() {
		WComponent reference = new DefaultWComponent();
		WInternalLink link = new WInternalLink(TEST_TEXT, reference);

		Assert.assertEquals("text should be text set", link.getText(), TEST_TEXT);
		Assert.assertEquals("reference should be reference set", link.getReference(), reference);
	}

	@Test
	public void testSetReference() {
		WInternalLink link = new WInternalLink();
		WComponent reference = new DefaultWComponent();
		link.setReference(reference);

		Assert.assertEquals("reference should be reference set", link.getReference(), reference);
	}

	@Test
	public void testSetText() {
		WInternalLink link = new WInternalLink();
		link.setText(TEST_TEXT);

		Assert.assertEquals("text should be text set", link.getText(), TEST_TEXT);
	}
}
