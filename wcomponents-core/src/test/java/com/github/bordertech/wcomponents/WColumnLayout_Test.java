package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * WColumnLayout_Test - Unit tests for {@link WColumnLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WColumnLayout_Test extends AbstractWComponentTestCase {

	@Test
	public void testHasLeftContent() {
		WColumnLayout col = new WColumnLayout();
		Assert.assertFalse("Should not have any left content by default", col.hasLeftContent());
		col.setRightColumn(new WLabel("right"));
		Assert.assertFalse("Should not have any left content", col.hasLeftContent());
		col.setLeftColumn(new WLabel("left"));
		Assert.assertTrue("Should have left content", col.hasLeftContent());
	}

	@Test
	public void testHasRightContent() {
		WColumnLayout col = new WColumnLayout();
		Assert.assertFalse("Should not have any right content by default", col.hasRightContent());
		col.setLeftColumn(new WLabel("left"));
		Assert.assertFalse("Should not have any right content", col.hasRightContent());
		col.setRightColumn(new WLabel("right"));
		Assert.assertTrue("Should have right content", col.hasRightContent());
	}

	@Test
	public void testSetLeftColumn() {
		WColumnLayout col = new WColumnLayout();
		WLabel label = new WLabel("leftLabel");
		String headingText = "leftHeading";
		WHeading heading = new WHeading(WHeading.MINOR, headingText);

		Assert.assertFalse("Should not have any content by default", col.hasLeftContent());
		col.setLeftColumn(label);
		Assert.assertTrue("Should have left content after setLeftColumn", col.hasLeftContent());
		Assert.assertSame("Incorrect component", label, col.getLeftColumn().getChildAt(0));

		col.getLeftColumn().remove(label);
		col = new WColumnLayout();
		col.setLeftColumn(headingText, label);
		Assert.assertEquals("Should have 2 children after setLeftColumn with heading text", 2, col.
				getLeftColumn().getChildCount());
		Assert.assertTrue("Incorrect heading component",
				col.getLeftColumn().getChildAt(0) instanceof WHeading);
		Assert.assertEquals("Incorrect heading text", headingText, ((WHeading) col.getLeftColumn().
				getChildAt(0)).getText());
		Assert.assertSame("Incorrect component", label, col.getLeftColumn().getChildAt(1));

		col.getLeftColumn().remove(label);
		col = new WColumnLayout();
		col.setLeftColumn(heading, label);
		Assert.assertEquals("Should have 2 children after setLeftColumn with heading", 2, col.
				getLeftColumn().getChildCount());
		Assert.assertSame("Incorrect heading", heading, col.getLeftColumn().getChildAt(0));
		Assert.assertSame("Incorrect component", label, col.getLeftColumn().getChildAt(1));
	}

	@Test
	public void testSetRightColumn() {
		WColumnLayout col = new WColumnLayout();
		WLabel label = new WLabel("rightLabel");
		String headingText = "rightHeading";
		WHeading heading = new WHeading(WHeading.MINOR, headingText);

		Assert.assertFalse("Should not have any content by default", col.hasRightContent());
		col.setRightColumn(label);
		Assert.assertEquals("Should have 1 child after setRightColumn", 1, col.getRightColumn().
				getChildCount());
		Assert.assertSame("Incorrect component", label, col.getRightColumn().getChildAt(0));

		col.getRightColumn().remove(label);
		col = new WColumnLayout();
		col.setRightColumn(headingText, label);
		Assert.assertEquals("Should have 2 children after setRightColumn with heading text", 2, col.
				getRightColumn().getChildCount());
		Assert.assertTrue("Incorrect heading component",
				col.getRightColumn().getChildAt(0) instanceof WHeading);
		Assert.assertEquals("Incorrect heading text", headingText, ((WHeading) col.getRightColumn().
				getChildAt(0)).getText());
		Assert.assertSame("Incorrect component", label, col.getRightColumn().getChildAt(1));

		col.getRightColumn().remove(label);
		col = new WColumnLayout();
		col.setRightColumn(heading, label);
		Assert.assertEquals("Should have 2 children after setRightColumn with heading", 2, col.
				getRightColumn().getChildCount());
		Assert.assertSame("Incorrect heading", heading, col.getRightColumn().getChildAt(0));
		Assert.assertSame("Incorrect component", label, col.getRightColumn().getChildAt(1));
	}
}
