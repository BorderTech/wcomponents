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
 * Test cases for {@link GreaterThan}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class GreaterThan_Test extends AbstractWComponentTestCase {

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
		GreaterThan compare = new GreaterThan(trigger, value);

		Assert.assertEquals("Value for GreaterThan is incorrect", value, compare.getValue());
		Assert.assertEquals("Trigger for GreaterThan should be the trigger", trigger, compare.
				getTrigger());
	}

	@Test
	public void testCompareType() {
		GreaterThan compare = new GreaterThan(new MyTrigger(), null);
		Assert.assertEquals("Incorrect Compare Type", AbstractCompare.CompareType.GREATER_THAN,
				compare.getCompareType());
	}

	@Test
	public void testDoCompare() {
		WNumberField trigger = new WNumberField();

		// ------------------------------
		// Setup GREATER THAN - with value
		GreaterThan compare = new GreaterThan(trigger, EQ_VALUE);

		trigger.setNumber(null);
		Assert.assertFalse("Greater Than - Compare for null value should be false", compare.
				execute());

		trigger.setNumber(LT_VALUE);
		Assert.assertFalse("Greater Than - Compare for less value should be false", compare.
				execute());

		trigger.setNumber(EQ_VALUE);
		Assert.assertFalse("Greater Than - Compare for equal value should be false", compare.
				execute());

		trigger.setNumber(GT_VALUE);
		Assert.assertTrue("Greater Than - Compare for greater value should be true", compare.
				execute());

		// ------------------------------
		// Setup GREATER THAN - with null value
		compare = new GreaterThan(trigger, null);

		trigger.setNumber(null);
		Assert.assertFalse("Greater Than With Null Value - Compare for null value should be false",
				compare.execute());

		trigger.setNumber(EQ_VALUE);
		Assert.assertFalse("Greater Than With Null Value - Compare for value should be false",
				compare.execute());
	}

	@Test
	public void testToString() {
		MyTrigger trigger = new MyTrigger();

		GreaterThan compare = new GreaterThan(trigger, "1");
		Assert.assertEquals("Incorrect toString for compare", "MyTrigger>\"1\"", compare.toString());

		WLabel label = new WLabel("test label", trigger);
		Assert.assertEquals("Incorrect toString for compare with a label",
				label.getText() + ">\"1\"", compare.toString());
	}

	/**
	 * Test component that implements the SubordinateTrigger interface.
	 */
	private static class MyTrigger extends AbstractWComponent implements SubordinateTrigger {
	}
}
