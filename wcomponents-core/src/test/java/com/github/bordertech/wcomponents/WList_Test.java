package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WList.Separator;
import com.github.bordertech.wcomponents.WList.Type;
import com.github.bordertech.wcomponents.util.SpaceUtil;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WList}.
 *
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WList_Test extends AbstractWComponentTestCase {

	private static final Size GAP = Size.SMALL;

	@Test
	public void testConstructor1() {
		WList list;
		for (WList.Type t : WList.Type.values()) {
			list = new WList(t);
			Assert.assertEquals("Constructor - Incorrect type", t, list.getType());
			Assert.assertNull("Constructor - Incorrect default gap", list.getSpace());
		}
	}

	@Test
	public void testConstructor2() {
		WList list;
		for (WList.Type t : WList.Type.values()) {
			list = new WList(t, GAP);
			Assert.assertEquals("Constructor - Incorrect type", t, list.getType());
			Assert.assertEquals("Constructor - Incorrect gap", GAP, list.getSpace());
		}
	}

	@Test
	public void testDeprecatedConstructor() {
		WList list;
		for (WList.Type t : WList.Type.values()) {
			list = new WList(t, SpaceUtil.sizeToInt(GAP));
			Assert.assertEquals("Constructor - Incorrect type", t, list.getType());
			Assert.assertEquals("Constructor - Incorrect gap", GAP, list.getSpace());
		}
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WList(Type.FLAT), "margin", null, new Margin(GAP), new Margin(Size.LARGE));
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
		Size bigGap = Size.LARGE;
		for (WList.Type t : WList.Type.values()) {
			list = new WList(t, SpaceUtil.sizeToInt(GAP), SpaceUtil.sizeToInt(bigGap));
			Assert.assertEquals("Constructor - Incorrect type", t, list.getType());

			if (t == WList.Type.FLAT) {
				Assert.assertEquals("Constructor - Incorrect gap", GAP, list.getSpace());
				Assert.assertEquals("Constructor - Incorrect hgap", SpaceUtil.sizeToInt(GAP), list.getHgap());
				Assert.assertEquals("Constructor - Incorrect vgap", 0, list.getVgap());
			} else {
				Assert.assertEquals("Constructor - Incorrect gap", bigGap, list.getSpace());
				Assert.assertEquals("Constructor - Incorrect hgap", 0, list.getHgap());
				Assert.assertEquals("Constructor - Incorrect vgap", SpaceUtil.sizeToInt(bigGap), list.getVgap());
			}
		}
	}

	@Test
	public void testDuplicateComponentModels() {
		WList wList = new WList(Type.STRIPED);
		wList.setSeparator(Separator.DOT);
//		WList.ListModel model = wList.getComponentModel();
//		ComponentModel shared = wList.getDefaultModel();
//		org.junit.Assert.assertTrue(model == shared);
//
//		wList.setLocked(true);
//		setActiveContext(createUIContext());
//
//		/* Model should use default model after context created, including when the model is updated with the same value */
//		wList.setSeparator(Separator.DOT);
//		model = wList.getComponentModel();
//		org.junit.Assert.assertTrue(model == shared);
//
//		/* Test model is created when different to default model, and that it never reverts back to default once created
//		in context */
//		wList.setSeparator(Separator.NONE);
//		WList.ListModel model1 = wList.getComponentModel();
//		org.junit.Assert.assertFalse(model == model1);
//		wList.setSeparator(Separator.DOT);
//		model1 = wList.getComponentModel();
//		org.junit.Assert.assertFalse(model1 == shared);
//
//		/* Test that getOrCreateComponentModel won't be called in setter when the argument is the same as the model
//		 * attribute. This will let us know that a duplicate model wasn't created then destroyed - it will only use the
//		 * model already created. */
//		Separator separator = Separator.DOT;
//		org.junit.Assert.assertEquals(separator, wList.getSeparator());
//		wList.setSeparator(separator);
//		model = wList.getComponentModel();
//		org.junit.Assert.assertTrue(model == model1);
//
//		/* Test when argument is different to model attribute - getOrCreateComponent will be called, but should still
//		 * end up with the same model reference */
//		separator = Separator.BAR;
//		org.junit.Assert.assertNotEquals(separator, wList.getSeparator());
//		wList.setSeparator(separator);
//		model = wList.getComponentModel();
//		org.junit.Assert.assertTrue(model == model1);
//
//		/* Test model uses default on context reset */
//		resetContext();
//		model = wList.getComponentModel();
//		shared = wList.getDefaultModel();
//		org.junit.Assert.assertTrue(model == shared);
		assertNoDuplicateComponentModels(wList, "separator", Separator.BAR, null);
	}

}
