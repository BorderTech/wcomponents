package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * WProgressBar_Test - Unit tests for {@link WProgressBar}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WProgressBar_Test extends AbstractWComponentTestCase {

	@Test
	public void testDefaultConstructor() {
		WProgressBar bar = new WProgressBar();
		Assert.assertEquals(bar.getProgressBarType(), WProgressBar.DEFAULT_TYPE);
		Assert.assertEquals(0, bar.getMax());
		Assert.assertEquals(0, bar.getValue());
	}

	@Test
	public void testConstructorWithType() {
		WProgressBar bar = new WProgressBar(WProgressBar.ProgressBarType.SMALL);
		Assert.assertEquals(bar.getProgressBarType(), WProgressBar.ProgressBarType.SMALL);
		Assert.assertEquals(0, bar.getMax());
		Assert.assertEquals(0, bar.getValue());
		bar = new WProgressBar(WProgressBar.ProgressBarType.NORMAL);
		Assert.assertEquals(bar.getProgressBarType(), WProgressBar.ProgressBarType.NORMAL);
		Assert.assertEquals(0, bar.getMax());
		Assert.assertEquals(0, bar.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullType() {
		WProgressBar progressBar = new WProgressBar(null);
	}

	@Test
	public void testContructorWithMax() {
		int max = 10;
		WProgressBar bar = new WProgressBar(max);
		Assert.assertEquals(max, bar.getMax());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeMaxInConstructor() {
		WProgressBar progressBar = new WProgressBar(-1);
	}

	@Test
	public void testConstructorTypeAndMax() {
		int max = 10;
		WProgressBar.ProgressBarType type = WProgressBar.ProgressBarType.SMALL;
		WProgressBar bar = new WProgressBar(type, max);
		Assert.assertEquals(type, bar.getProgressBarType());
		Assert.assertEquals(max, bar.getMax());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithTypeAndMaxWithNullType() {
		WProgressBar progressBar = new WProgressBar(null, 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithTypeAndMaxWithNegativeMax() {
		WProgressBar progressBar = new WProgressBar(WProgressBar.ProgressBarType.SMALL, -1);
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WProgressBar(), "progressBarType", WProgressBar.DEFAULT_TYPE, WProgressBar.ProgressBarType.SMALL,
				WProgressBar.ProgressBarType.NORMAL);
	}

	@Test
	public void testNullTypeInSetter() {
		WProgressBar progressBar = new WProgressBar(WProgressBar.ProgressBarType.SMALL);
		progressBar.setProgressBarType(null);
		Assert.assertEquals("Expected type to be set to NORMAL", WProgressBar.DEFAULT_TYPE, progressBar.getProgressBarType());
	}

	@Test
	public void testMaxAccessors() {
		assertAccessorsCorrect(new WProgressBar(), "max", 0, 10, 20);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeMaxInSetter() {
		WProgressBar progressBar = new WProgressBar();
		progressBar.setMax(-1);
	}

	@Test
	public void testValueAccessors() {
		int max = 10;
		assertAccessorsCorrect(new WProgressBar(max), "value", 0, (max - 2), (max - 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeValue() {
		WProgressBar progressBar = new WProgressBar();
		progressBar.setValue(-1);
	}

	@Test
	public void testSetValueGreaterThanMaxGetsMax() {
		int max = 10;
		WProgressBar bar = new WProgressBar(max);
		bar.setValue(max + 1);
		Assert.assertEquals(max, bar.getValue());
	}
}
