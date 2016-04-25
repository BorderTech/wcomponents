package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WRow}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WRow_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		WRow row = new WRow();
		Assert.assertEquals("Constructor - Incorrect default hgap", 0, row.getHgap());
	}

	@Test
	public void testConstructor2() {
		WRow row = new WRow(1);
		Assert.assertEquals("Constructor - Incorrect hgap", 1, row.getHgap());
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WRow(), "margin", null, new Margin(1), new Margin(2));
	}
}
