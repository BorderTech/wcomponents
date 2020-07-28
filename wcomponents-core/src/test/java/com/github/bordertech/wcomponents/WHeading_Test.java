package com.github.bordertech.wcomponents;

import org.junit.Assert;
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
		assertAccessorsCorrect(new WHeading(HeadingLevel.H1, "test"), WHeading::getMargin, WHeading::setMargin,
			null, new Margin(Size.SMALL), new Margin(Size.MEDIUM));
	}

	@Test
	public void testHeadingLevelAccessors() {
		assertAccessorsCorrect(new WHeading(HeadingLevel.H1, "test"), WHeading::getHeadingLevel, WHeading::setHeadingLevel,
			HeadingLevel.H1, HeadingLevel.H2, HeadingLevel.H3);
	}

	@Test
	public void testHeadingTypeToHeadingLevelConversion() {

		testHeadingLevelConversion(WHeading.TITLE, HeadingLevel.H1);
		testHeadingLevelConversion(WHeading.MAJOR, HeadingLevel.H2);
		testHeadingLevelConversion(WHeading.SECTION, HeadingLevel.H3);
		testHeadingLevelConversion(WHeading.MINOR, HeadingLevel.H4);
		testHeadingLevelConversion(WHeading.SUB_HEADING, HeadingLevel.H5);
		testHeadingLevelConversion(WHeading.SUB_SUB_HEADING, HeadingLevel.H6);
	}

	private void testHeadingLevelConversion(int type, HeadingLevel headingLevel) {
		WHeading heading = new WHeading(type, "dummy");
		Assert.assertEquals("WHeading type conversion to HeadingLevel failed",
			headingLevel, heading.getHeadingLevel());
	}
}
