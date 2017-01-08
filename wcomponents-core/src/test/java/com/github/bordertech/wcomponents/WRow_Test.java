package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.GapSizeUtil;
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

	@Test
	public void testConstructor1() {
		WRow row = new WRow();
		Assert.assertNull("Constructor - Incorrect default hgap", row.getGap());
	}

	@Test
	public void testConstructor2() {
		WRow row = new WRow(GapSizeUtil.Size.SMALL);
		Assert.assertEquals("Constructor - Incorrect hgap", GapSizeUtil.Size.SMALL, row.getGap());
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WRow(), "margin", null, new Margin(GapSizeUtil.Size.SMALL), new Margin(GapSizeUtil.Size.ZERO));
	}
}
