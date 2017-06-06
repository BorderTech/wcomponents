package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SpaceUtil;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WRow}.
 *
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WRow_Test extends AbstractWComponentTestCase {

	private static final Size GAP = Size.MEDIUM;

	@Test
	public void testConstructor1() {
		WRow row = new WRow();
		Assert.assertNull("Constructor - Incorrect default hgap", row.getSpace());
	}

	@Test
	public void testConstructor2() {
		WRow row = new WRow(GAP);
		Assert.assertEquals("Constructor - Incorrect hgap", GAP, row.getSpace());
	}

	@Test
	public void testDeprecatedConstructor() {
		WRow row = new WRow(SpaceUtil.sizeToInt(GAP));
		Assert.assertEquals("Constructor - Incorrect hgap", GAP, row.getSpace());
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WRow(), "margin", null, new Margin(Size.SMALL), new Margin(Size.ZERO));
	}
}
