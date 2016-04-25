package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Rule}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Rule_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {
		Condition cond = new Equal(new WCheckBox(), null);
		Action onTrue = new Hide(new WTextField());
		Action onFalse = new Hide(new WTextField());

		// Constructor - 1
		Rule rule = new Rule();
		Assert.assertNull("Constructor 1: Condition should be null", rule.getCondition());
		Assert.assertTrue("Constructor 1: onTrue actions should be empty", rule.getOnTrue().
				isEmpty());
		Assert.assertTrue("Constructor 1: onFalse actions should be empty", rule.getOnFalse().
				isEmpty());

		// Constructor - 2
		rule = new Rule(cond);
		Assert.
				assertEquals("Constructor 2: Incorrect condition returned", cond, rule.
						getCondition());
		Assert.assertTrue("Constructor 2: onTrue actions should be empty", rule.getOnTrue().
				isEmpty());
		Assert.assertTrue("Constructor 2: onFalse actions should be empty", rule.getOnFalse().
				isEmpty());

		// Constructor - 3
		rule = new Rule(cond, onTrue);
		Assert.
				assertEquals("Constructor 3: Incorrect condition returned", cond, rule.
						getCondition());
		Assert.assertEquals("Constructor 3: onTrue actions list should have 1 item", 1, rule.
				getOnTrue().size());
		Assert.assertEquals("Constructor 3: Item 1 in onTrue actions list is incorrect", onTrue,
				rule.getOnTrue()
				.get(0));
		Assert.assertTrue("Constructor 3: onFalse actions should be empty", rule.getOnFalse().
				isEmpty());

		// Constructor - 4
		rule = new Rule(cond, onTrue, onFalse);
		Assert.
				assertEquals("Constructor 4: Incorrect condition returned", cond, rule.
						getCondition());
		Assert.assertEquals("Constructor 4: onTrue actions list should have 1 item", 1, rule.
				getOnTrue().size());
		Assert.assertEquals("Constructor 4: Item 1 in onTrue actions list is incorrect", onTrue,
				rule.getOnTrue()
				.get(0));
		Assert.assertEquals("Constructor 4: onFalse actions list should have 1 item", 1, rule.
				getOnFalse().size());
		Assert.assertEquals("Constructor 4: Item 1 in onFalse actions list is incorrect", onFalse,
				rule.getOnFalse()
				.get(0));
	}

	@Test
	public void testAccessors() {
		Condition cond = new Equal(new WCheckBox(), null);
		Action onTrue1 = new Hide(new WTextField());
		Action onTrue2 = new Hide(new WTextField());
		Action onFalse1 = new Hide(new WTextField());
		Action onFalse2 = new Hide(new WTextField());

		Rule rule = new Rule();

		// Condition
		Assert.assertNull("Condition should be null", rule.getCondition());
		rule.setCondition(cond);
		Assert.assertEquals("Incorrect condition returned", cond, rule.getCondition());

		// OnTrue Actions
		Assert.assertTrue("onTrue actions should be empty", rule.getOnTrue().isEmpty());
		rule.addActionOnTrue(onTrue1);
		Assert.assertEquals("onTrue actions list should have 1 item", 1, rule.getOnTrue().size());
		Assert.assertEquals("Item 1 in onTrue actions list is incorrect", onTrue1, rule.getOnTrue().
				get(0));
		rule.addActionOnTrue(onTrue2);
		Assert.assertEquals("onTrue actions list should have 2 items", 2, rule.getOnTrue().size());
		Assert.assertEquals("Item 1 in onTrue actions list is incorrect", onTrue1, rule.getOnTrue().
				get(0));
		Assert.assertEquals("Item 2 in onTrue actions list is incorrect", onTrue2, rule.getOnTrue().
				get(1));
		try {
			rule.addActionOnTrue(null);
			Assert.fail("Should not be able to add a null action.");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Invalid exception message for adding null action", e.getMessage());
		}

		// OnFalse Actions
		Assert.assertTrue("onFalse actions should be empty", rule.getOnFalse().isEmpty());
		rule.addActionOnFalse(onFalse1);
		Assert.assertEquals("onFalse actions list should have 1 item", 1, rule.getOnFalse().size());
		Assert.assertEquals("Item 1 in onFalse actions list is incorrect", onFalse1, rule.
				getOnFalse().get(0));
		rule.addActionOnFalse(onFalse2);
		Assert.assertEquals("onFalse actions list should have 2 item", 2, rule.getOnFalse().size());
		Assert.assertEquals("Item 1 in onFalse actions list is incorrect", onFalse1, rule.
				getOnFalse().get(0));
		Assert.assertEquals("Item 2 in onFalse actions list is incorrect", onFalse2, rule.
				getOnFalse().get(1));
		try {
			rule.addActionOnFalse(null);
			Assert.fail("Should not be able to add a null action.");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Invalid exception message for adding null action", e.getMessage());
		}

	}

	@Test
	public void testExecuteNullCondition() {
		Rule rule = new Rule();

		try {
			rule.execute();
			Assert.fail("Should not be able to execute a rule with no condition set.");
		} catch (SystemException e) {
			Assert.assertNotNull("Invalid exception message for executing rule with no condition",
					e.getMessage());
		}
	}

	@Test
	public void testExecuteRule() {
		WCheckBox box = new WCheckBox();
		WTextField target = new WTextField();

		// Create Rule
		Rule rule = new Rule();
		rule.setCondition(new Equal(box, Boolean.TRUE));
		rule.addActionOnTrue(new Hide(target));
		rule.addActionOnFalse(new Show(target));

		// Test TRUE Action (Hide target)
		setFlag(target, ComponentModel.HIDE_FLAG, false);
		box.setSelected(true);
		rule.execute();
		Assert.assertTrue("True condition should Hide Target", target.isHidden());

		// Test FALSE Action (Show target)
		setFlag(target, ComponentModel.HIDE_FLAG, true);
		box.setSelected(false);
		rule.execute();
		Assert.assertFalse("False condition should Show Target", target.isHidden());
	}

	@Test
	public void testExecuteRuleWithRequest() {
		WCheckBox box = new WCheckBox();
		WTextField target = new WTextField();

		// Create Rule
		Rule rule = new Rule();
		rule.setCondition(new Equal(box, Boolean.TRUE));
		rule.addActionOnTrue(new Hide(target));
		rule.addActionOnFalse(new Show(target));

		// Test TRUE Action (Hide target)
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		setFlag(target, ComponentModel.HIDE_FLAG, false);
		// CheckBox selected in Request
		setupCheckBoxRequest(box, request, true);
		rule.execute(request);
		Assert.assertTrue("True condition should Hide Target", target.isHidden());

		// Test FALSE Action (Show target)
		setActiveContext(createUIContext());
		request = new MockRequest();
		setFlag(target, ComponentModel.HIDE_FLAG, true);
		// CheckBox not selected in Request
		setupCheckBoxRequest(box, request, false);
		rule.execute(request);
		Assert.assertFalse("False condition should Show Target", target.isHidden());
	}

	@Test
	public void testToString() {
		WCheckBox box = new WCheckBox();
		SubordinateTarget target = new WTextField();

		Rule rule = new Rule();

		// No condition and actions
		Assert.assertEquals("Incorrect toString for RULE with no conditions and actions",
				"if (null)\nthen\n   []\nelse\n   []\n", rule.toString());

		// With Conditions and actions;
		rule.setCondition(new Equal(box, Boolean.TRUE));
		rule.addActionOnTrue(new Hide(target));
		rule.addActionOnFalse(new Show(target));

		Assert.assertEquals("Incorrect toString for RULE",
				"if (WCheckBox=\"true\")\nthen\n   [hide WTextField]\nelse\n   [show WTextField]\n",
				rule.toString());

	}

	/**
	 * @param target the check box target
	 * @param request the request being setup
	 * @param condition true if checked
	 */
	private void setupCheckBoxRequest(final WCheckBox target, final MockRequest request,
			final boolean condition) {
		if (condition) {
			request.setParameter(target.getId(), "true");
		}
	}
}
