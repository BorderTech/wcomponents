package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * WHeading_Test - Unit tests for {@link WHeading}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WHeading_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		WHeading heading = new WHeading(WHeading.MAJOR, "dummy");
		Assert.assertEquals("Constructor - Incorrect type", WHeading.MAJOR, heading.getType());
	}

	@Test
	public void testConstructor2() {
		WHeading heading = new WHeading(WHeading.MAJOR, new WDecoratedLabel("test"));
		Assert.assertEquals("Constructor - Incorrect type", WHeading.MAJOR, heading.getType());
	}

	@Test
	public void testConstructor3() {
		WHeading heading = new WHeading(HeadingLevel.H1, "dummy");
		Assert.assertEquals("Constructor - Incorrect type", HeadingLevel.H1, heading.
				getHeadingLevel());
	}

	@Test
	public void testConstructor4() {
		WHeading heading = new WHeading(HeadingLevel.H1, new WDecoratedLabel("test"));
		Assert.assertEquals("Constructor - Incorrect type", HeadingLevel.H1, heading.
				getHeadingLevel());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorInvalidType1() {
		new WHeading(123456, "dummy");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorInvalidType2() {
		new WHeading(-1, "dummy");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorInvalidType3() {
		new WHeading(123456, new WDecoratedLabel());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorInvalidType4() {
		new WHeading(-1, new WDecoratedLabel());
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WHeading(HeadingLevel.H1, "test"), "margin", null, new Margin(1),
				new Margin(2));
	}

	@Test
	public void testHeadingLevelAccessors() {
		assertAccessorsCorrect(new WHeading(HeadingLevel.H1, "test"), "headingLevel",
				HeadingLevel.H1, HeadingLevel.H2,
				HeadingLevel.H3);
	}

}
