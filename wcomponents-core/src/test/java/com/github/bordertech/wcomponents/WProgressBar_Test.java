package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * WProgressBar_Test - Unit tests for {@link WProgressBar}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WProgressBar_Test extends AbstractWComponentTestCase {

	@Test
	public void testTypeAccessors() {
		final WProgressBar.ProgressBarType type1 = WProgressBar.ProgressBarType.NORMAL;
		final WProgressBar.ProgressBarType type2 = WProgressBar.ProgressBarType.SMALL;
		final WProgressBar.ProgressBarType type3 = WProgressBar.ProgressBarType.NORMAL;

		WProgressBar progressBar = new WProgressBar(type1, WProgressBar.UnitType.FRACTION);
		Assert.assertEquals("Incorrect progressBar type", type1, progressBar.getProgressBarType());

		progressBar.setProgressBarType(type2);
		Assert.assertEquals("Incorrect progressBar type after setProgressBarType", type2,
				progressBar.getProgressBarType());

		progressBar.setLocked(true);
		setActiveContext(createUIContext());
		progressBar.setProgressBarType(type3);

		Assert.assertEquals("Incorrect progressBar type after session setProgressBarType", type3,
				progressBar.getProgressBarType());

		resetContext();
		Assert.assertEquals("Incorrect default progressBar type after session setProgressBarType",
				type2, progressBar.getProgressBarType());
	}

	@Test
	public void testUnitTypeAccessors() {
		final WProgressBar.UnitType unitType1 = WProgressBar.UnitType.FRACTION;
		final WProgressBar.UnitType unitType2 = WProgressBar.UnitType.PERCENTAGE;

		WProgressBar progressBar = new WProgressBar(WProgressBar.ProgressBarType.NORMAL, unitType1);
		Assert.assertEquals("Incorrect progressBar unit type", unitType1, progressBar.getUnitType());

		progressBar.setUnitType(unitType2);
		Assert.assertEquals("Incorrect progressBar unit type after setUnitType", unitType2,
				progressBar.getUnitType());

		progressBar.setLocked(true);
		setActiveContext(createUIContext());
		progressBar.setUnitType(unitType1);

		Assert.assertEquals("Incorrect progressBar unit type after session setUnitType", unitType1,
				progressBar.getUnitType());

		resetContext();
		Assert.assertEquals("Incorrect default progressBar unit type after session setUnitType",
				unitType2, progressBar.getUnitType());
	}

	@Test
	public void testTextAccessors() {
		WProgressBar progressBar = new WProgressBar();
		String defaultText = "WProgressBar_Test.testGetText.default";
		String userText = "WProgressBar_Test.testGetText.user";
		progressBar.setText(defaultText);

		Assert.assertEquals("Incorrect default text", defaultText, progressBar.getText());

		// Set test for a users session
		progressBar.setLocked(true);
		setActiveContext(createUIContext());
		progressBar.setText(userText);
		Assert.assertEquals("Modified session should have session text", userText, progressBar.
				getText());

		resetContext();
		Assert.assertEquals("Other session should have default text", defaultText, progressBar.
				getText());

		//Test nulls
		progressBar.setText("");
		Assert.assertEquals("text should be empty string", "", progressBar.getText());
		progressBar.setText(null);
		Assert.assertNull("text should be null", progressBar.getText());
	}

	@Test
	public void testMaxAccessors() {
		int max1 = 121;
		int max2 = 122;
		int max3 = 123;

		WProgressBar progressBar = new WProgressBar(max1);
		Assert.assertEquals("Max value accessors incorrect", max1, progressBar.getMax());

		progressBar.setMax(max2);
		Assert.assertEquals("Incorrect max value after call to setMax(int)", max2, progressBar.
				getMax());

		progressBar.setLocked(true);
		setActiveContext(createUIContext());
		progressBar.setMax(max3);
		Assert.assertEquals("Incorrect max value for changed context", max3, progressBar.getMax());

		resetContext();
		Assert.assertEquals("Default max value should not have changed", max2, progressBar.getMax());
	}
}
