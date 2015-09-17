package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WNumberField;
import java.math.BigDecimal;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link NotEqual}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class NotEqual_Test {

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
		NotEqual compare = new NotEqual(trigger, value);

		Assert.assertEquals("Value for NotEqual is incorrect", value, compare.getValue());
		Assert.assertEquals("Trigger for NotEqual should be the trigger", trigger, compare.
				getTrigger());
	}

	@Test
	public void testCompareType() {
		NotEqual compare = new NotEqual(new MyTrigger(), null);
		Assert.assertEquals("Incorrect Compare Type", AbstractCompare.CompareType.NOT_EQUAL,
				compare.getCompareType());
	}

	@Test
	public void testDoCompare() {
		WNumberField trigger = new WNumberField();

		// ------------------------------
		// Setup NOT EQUAL - with value
		NotEqual compare = new NotEqual(trigger, EQ_VALUE);

		trigger.setNumber(null);
		Assert.assertTrue("Not Equal - Compare for null value should be true", compare.execute());

		trigger.setNumber(LT_VALUE);
		Assert.assertTrue("Not Equal - Compare for less value should be true", compare.execute());

		trigger.setNumber(EQ_VALUE);
		Assert.assertFalse("Not Equal - Compare for equal value should be false", compare.execute());

		trigger.setNumber(GT_VALUE);
		Assert.assertTrue("Not Equal - Compare for greater value should be true", compare.execute());

		// ------------------------------
		// Setup NOT EQUAL - with null value
		compare = new NotEqual(trigger, null);

		trigger.setNumber(null);
		Assert.assertFalse("Not Equal With Null Value - Compare for null value should be false",
				compare.execute());

		trigger.setNumber(EQ_VALUE);
		Assert.assertTrue("Not Equal With Null Value - Compare for value should be true", compare.
				execute());
	}

	@Test
	public void testToString() {
		MyTrigger trigger = new MyTrigger();

		NotEqual compare = new NotEqual(trigger, "1");
		Assert.
				assertEquals("Incorrect toString for compare", "MyTrigger!=\"1\"", compare.
						toString());

		WLabel label = new WLabel("test label", trigger);
		Assert.assertEquals("Incorrect toString for compare with a label",
				label.getText() + "!=\"1\"", compare.toString());
	}

	/**
	 * Test component that implements the SubordinateTrigger interface.
	 */
	private static class MyTrigger extends AbstractWComponent implements SubordinateTrigger {
	}
}
