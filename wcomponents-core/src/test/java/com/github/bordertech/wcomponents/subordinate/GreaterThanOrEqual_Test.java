package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WNumberField;
import java.math.BigDecimal;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link GreaterThanOrEqual}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class GreaterThanOrEqual_Test extends AbstractWComponentTestCase {

	/**
	 * Equal to value.
	 */
	private static final BigDecimal EQ_VALUE = BigDecimal.valueOf(10);
	/**
	 * Less than value.
	 */
	private static final BigDecimal LT_VALUE = BigDecimal.valueOf(9);
	/**
	 * Greater than value.
	 */
	private static final BigDecimal GT_VALUE = BigDecimal.valueOf(11);

	@Test
	public void testConstructor() {
		SubordinateTrigger trigger = new MyTrigger();
		Object value = new Object();
		GreaterThanOrEqual compare = new GreaterThanOrEqual(trigger, value);

		Assert.assertEquals("Value for GreaterThanOrEqual is incorrect", value, compare.getValue());
		Assert.assertEquals("Trigger for GreaterThanOrEqual should be the trigger", trigger,
				compare.getTrigger());
	}

	@Test
	public void testCompareType() {
		GreaterThanOrEqual compare = new GreaterThanOrEqual(new MyTrigger(), null);
		Assert.assertEquals("Incorrect Compare Type",
				AbstractCompare.CompareType.GREATER_THAN_OR_EQUAL,
				compare.getCompareType());
	}

	@Test
	public void testDoCompare() {
		WNumberField trigger = new WNumberField();

		// ------------------------------
		// Setup GREATER THAN OR EQUAL - with value
		GreaterThanOrEqual compare = new GreaterThanOrEqual(trigger, EQ_VALUE);

		trigger.setNumber(null);
		Assert.assertFalse("Greater Than Or Equal - Compare for null value should be false",
				compare.execute());

		trigger.setNumber(LT_VALUE);
		Assert.assertFalse("Greater Than Or Equal - Compare for less value should be false",
				compare.execute());

		trigger.setNumber(EQ_VALUE);
		Assert.assertTrue("Greater Than Or Equal - Compare for equal value should be true", compare.
				execute());

		trigger.setNumber(GT_VALUE);
		Assert.assertTrue("Greater Than Or Equal - Compare for greater value should be true",
				compare.execute());

		// ------------------------------
		// Setup GREATER THAN - with null value
		compare = new GreaterThanOrEqual(trigger, null);

		trigger.setNumber(null);
		Assert.assertTrue(
				"Greater Than Or Equal With Null Value - Compare for null value should be true",
				compare.execute());

		trigger.setNumber(EQ_VALUE);
		Assert.assertFalse(
				"Greater Than Or Equal With Null Value - Compare for value should be false",
				compare.execute());
	}

	@Test
	public void testToString() {
		MyTrigger trigger = new MyTrigger();

		GreaterThanOrEqual compare = new GreaterThanOrEqual(trigger, "1");
		Assert.
				assertEquals("Incorrect toString for compare", "MyTrigger>=\"1\"", compare.
						toString());

		WLabel label = new WLabel("test label", trigger);
		Assert.assertEquals("Incorrect toString for compare with a label",
				label.getText() + ">=\"1\"", compare.toString());
	}

	/**
	 * Test component that implements the SubordinateTrigger interface.
	 */
	private static class MyTrigger extends AbstractWComponent implements SubordinateTrigger {
	}
}
