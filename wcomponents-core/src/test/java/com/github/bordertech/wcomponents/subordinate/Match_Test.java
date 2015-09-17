package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractInput;
import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.util.Util;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Match}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Match_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		SubordinateTrigger trigger = new MyTrigger();
		String value = "Test";
		Match compare = new Match(trigger, value);

		Assert.assertEquals("Value for Match is incorrect", value, compare.getValue());
		Assert.
				assertEquals("Trigger for Match should be the trigger", trigger, compare.
						getTrigger());
	}

	@Test
	public void testCompareType() {
		Match compare = new Match(new MyTrigger(), null);
		Assert.assertEquals("Incorrect Compare Type", AbstractCompare.CompareType.MATCH, compare.
				getCompareType());
	}

	@Test
	public void testDoCompare() {
		MyInput trigger = new MyInput();

		// ------------------------------
		// Setup MATCH - with value
		Match compare = new Match(trigger, "foo[abc]");

		trigger.setData(null);
		Assert.assertFalse("Match - Compare for null value should be false", compare.execute());

		trigger.setData("foo");
		Assert.assertFalse("Match - Compare for 'foo' value should be false", compare.execute());

		trigger.setData("fooz");
		Assert.assertFalse("Match - Compare for 'fooz' value should be false", compare.execute());

		trigger.setData("fooa");
		Assert.assertTrue("Match - Compare for 'fooa' value should be true", compare.execute());

		// ------------------------------
		// Setup MATCH - with null value
		compare = new Match(trigger, null);

		trigger.setData(null);
		Assert.assertFalse("Match With Null Value - Compare for null value should be false",
				compare.execute());

		trigger.setData("foo");
		Assert.assertFalse("Match With Null Value - Compare for 'foo' value should be false",
				compare.execute());
	}

	@Test
	public void testDoCompareWithInvalidPattern() {
		// MATCH
		MyInput trigger = new MyInput();

		// ------------------------------
		// Setup MATCH - with value
		Match compare = new Match(trigger, "foo[a");

		trigger.setData("fooa");
		Assert.
				assertFalse("Match - Compare with invalid pattern should be false", compare.
						execute());
	}

	@Test
	public void testToString() {
		MyTrigger trigger = new MyTrigger();

		Match compare = new Match(trigger, "1");
		Assert.assertEquals("Incorrect toString for compare", "MyTrigger matches \"1\"", compare.
				toString());

		WLabel label = new WLabel("test label", trigger);
		Assert.assertEquals("Incorrect toString for compare with a label",
				label.getText() + " matches \"1\"", compare.toString());
	}

	/**
	 * Test component that implements the SubordinateTrigger interface.
	 */
	private static class MyTrigger extends AbstractWComponent implements SubordinateTrigger {
	}

	/**
	 * Test class for AbstractInput.
	 */
	private static class MyInput extends AbstractInput implements SubordinateTrigger {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean doHandleRequest(final Request request) {
			Object value = getRequestValue(request);
			Object current = getValue();
			boolean changed = !Util.equals(value, current);
			if (changed) {
				setData(value);
			}
			return changed;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getRequestValue(final Request request) {
			if (isPresent(request)) {
				return request.getParameter(getId());
			} else {
				return getValue();
			}
		}

	}
}
