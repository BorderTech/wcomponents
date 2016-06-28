package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WList.Separator;
import com.github.bordertech.wcomponents.WList.Type;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WList}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WList_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		WList list;
		for (WList.Type t : WList.Type.values()) {
			list = new WList(t);
			Assert.assertEquals("Constructor - Incorrect type", t, list.getType());
			Assert.assertEquals("Constructor - Incorrect default gap", 0, list.getGap());
		}
	}

	@Test
	public void testConstructor2() {
		WList list;
		for (WList.Type t : WList.Type.values()) {
			list = new WList(t, 1);
			Assert.assertEquals("Constructor - Incorrect type", t, list.getType());
			Assert.assertEquals("Constructor - Incorrect gap", 1, list.getGap());
		}
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WList(Type.FLAT), "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WList(Type.FLAT), "type", Type.FLAT, Type.STACKED, Type.STRIPED);
	}

	@Test
	public void testSeparatorAccessors() {
		assertAccessorsCorrect(new WList(Type.FLAT), "separator", null, Separator.BAR, Separator.DOT);
	}

	@Test
	public void testRenderBorderAccessors() {
		assertAccessorsCorrect(new WList(Type.FLAT), "renderBorder", false, true, false);
	}

	// Test of the deprecated constructor.
	@Test
	public void testConstructor2Gaps() {
		WList list;
		for (WList.Type t : WList.Type.values()) {
			list = new WList(t, 1, 2);
			Assert.assertEquals("Constructor - Incorrect type", t, list.getType());

			if (t == WList.Type.FLAT) {
				Assert.assertEquals("Constructor - Incorrect gap", 1, list.getGap());
				Assert.assertEquals("Constructor - Incorrect hgap", 1, list.getHgap());
				Assert.assertEquals("Constructor - Incorrect vgap", 0, list.getVgap());
			} else {
				Assert.assertEquals("Constructor - Incorrect gap", 2, list.getGap());
				Assert.assertEquals("Constructor - Incorrect hgap", 0, list.getHgap());
				Assert.assertEquals("Constructor - Incorrect vgap", 2, list.getVgap());
			}
		}
	}

}
