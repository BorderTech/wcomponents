package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * WFieldLayout_Test - Unit tests for {@link WFieldLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WFieldLayout_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		WFieldLayout layout = new WFieldLayout();
		Assert.assertEquals("Incorrect default layout", WFieldLayout.LAYOUT_FLAT, layout.getLayoutType());
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		Assert.assertEquals("Incorrect layout", WFieldLayout.LAYOUT_STACKED, layout.getLayoutType());
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConstructorNullLayout() {
		WFieldLayout layout = new WFieldLayout((String) null);
	}


	@Test (expected = IllegalArgumentException.class)
	public void testConstructorInvalidLayout() {
		WFieldLayout layout = new WFieldLayout("WFieldLayout_Test.testConstructor.badArg");
	}
	@Test
	public void testLabelWidthAccessors() {
		assertAccessorsCorrect(new WFieldLayout(), WFieldLayout::getLabelWidth, WFieldLayout::setLabelWidth, 0, 1, 2);
	}

	@Test
	public void testSetLabelWidthRange() {

		WFieldLayout layout = new WFieldLayout();

		layout.setLabelWidth(-1);
		Assert.assertEquals("Incorrect width percentage from setter -1", 0, layout.getLabelWidth());

		layout.setLabelWidth(0);
		Assert.assertEquals("Incorrect width percentage from setter 0", 0, layout.getLabelWidth());

		layout.setLabelWidth(100);
		Assert.assertEquals("Incorrect width percentage from setter 100", 100, layout.
				getLabelWidth());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetLabelWidthInvalidGreater100() {
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(101);
	}

	@Test
	public void testTitleAccessors() {
		assertAccessorsCorrect(new WFieldLayout(), WFieldLayout::getTitle, WFieldLayout::setTitle,
			null, "Title1", "Title2");
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WFieldLayout(), WFieldLayout::getMargin, WFieldLayout::setMargin,
			null, new Margin(Size.SMALL), new Margin(Size.MEDIUM));
	}

	@Test
	public void testOrderedAccessors() {
		assertAccessorsCorrect(new WFieldLayout(), WFieldLayout::isOrdered, WFieldLayout::setOrdered, false, true, false);
	}

	@Test
	public void testOrderedOffsetAccessors() {
		assertAccessorsCorrect(new WFieldLayout(), WFieldLayout::getOrderedOffset, WFieldLayout::setOrderedOffset, 1, 2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetOrderedOffsetInvalid() {
		WFieldLayout layout = new WFieldLayout();
		layout.setOrderedOffset(0);
	}

	@Test
	public void testAddFieldWithStringLabel() {
		WFieldLayout layout = new WFieldLayout();
		WField field = layout.addField("Test", new WTextField());
		Assert.assertNotNull(field);
	}

	@Test
	public void testAddFieldWithStringLabelCreatesWLabel() {
		WFieldLayout layout = new WFieldLayout();
		WField field = layout.addField("Test", new WTextField());
		Assert.assertEquals("Expect a WLabel", field.getLabel().getClass(), WLabel.class);
	}

	@Test
	public void testAddFieldWithWLabel() {
		WFieldLayout layout = new WFieldLayout();
		WField field = layout.addField(new WLabel("Test"), new WTextField());
		Assert.assertNotNull(field);
	}

	@Test
	public void testAddFieldWithWButton() {
		WFieldLayout layout = new WFieldLayout();
		WField field = layout.addField(new WButton("Test"));
		Assert.assertNotNull(field);
	}

	@Test
	public void testAddFieldWithWButtonNullLabel() {
		WFieldLayout layout = new WFieldLayout();
		WField field = layout.addField(new WButton("Test"));
		Assert.assertNull(field.getLabel());
	}

}
