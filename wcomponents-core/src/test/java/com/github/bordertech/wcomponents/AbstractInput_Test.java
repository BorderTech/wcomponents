package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validator.AbstractFieldValidator;
import com.github.bordertech.wcomponents.validator.FieldValidator;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link AbstractInput}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractInput_Test extends AbstractWComponentTestCase {

	@Test
	public void testValidatorsAccessors() {
		AbstractInput input = new MyInput();

		Assert.assertFalse("Default input should have no validators", input.getValidators().
				hasNext());

		// Add validator
		FieldValidator validator = new AbstractFieldValidator() {
			@Override
			protected boolean isValid() {
				return false;
			}
		};
		input.addValidator(validator);

		Assert.assertEquals("Validator Iterator did not contain the valid validator", validator,
				input.getValidators()
				.next());
	}

	@Test
	public void testValidateComponent() {
		// No Validation
		AbstractInput input = new MyInput();
		List<Diagnostic> diags = new ArrayList<>();
		input.validate(diags);
		Assert.assertTrue("Input with no validation should return an empty daignostics list", diags.
				isEmpty());

		// Mandatory with no value - Validation Error
		input = new MyInput();
		diags.clear();
		input.setMandatory(true);
		input.validate(diags);
		Assert.assertEquals("Mandatory input with no value should return a diagnostic message", 1,
				diags.size());

		// Switch to ReadOnly and should have no validation errors
		input.setReadOnly(true);
		diags.clear();
		input.validate(diags);
		Assert.assertTrue(
				"Mandatory input that is ReadOnly with no value should return an empty diagnostic message",
				diags.isEmpty());

		// Mandatory with a value - No Validation Errors
		input = new MyInput();
		diags.clear();
		input.setMandatory(true);
		input.setData("value");
		input.validate(diags);
		Assert.assertTrue("Mandatory input with a value should return an empty daignostics list",
				diags.isEmpty());

		// Custom Validator - Validation Error
		FieldValidator validator = new AbstractFieldValidator() {
			@Override
			protected boolean isValid() {
				return false;
			}
		};
		input = new MyInput();
		diags.clear();
		input.addValidator(validator);
		input.validate(diags);
		Assert.assertEquals("Input with custom validator should return a diagnostic message", 1,
				diags.size());
	}

	@Test
	public void testActionOnChangeAccessors() {
		assertAccessorsCorrect(new MyInput(), "actionOnChange", null, new TestAction(),
				new TestAction());
	}

	@Test
	public void testActionObjectAccessors() {
		assertAccessorsCorrect(new MyInput(), "actionObject", null, new Object(), new Object());
	}

	@Test
	public void testActionCommand() {
		String value = "test value";

		AbstractInput input = new MyInput();
		input.setData(value);

		// Action command default to the string value
		Assert.assertEquals("Action command should defualt to the string value", value, input.
				getActionCommand());
	}

	@Test
	public void testDefaultSubmitButtonAccessors() {
		assertAccessorsCorrect(new MyInput(), "defaultSubmitButton", null, new WButton(),
				new WButton());
	}

	@Test
	public void testMandatoryAccessors() {
		assertAccessorsCorrect(new MyInput(), "mandatory", false, true, false);
	}

	@Test
	public void testMandatoryWithMessage() {
		String msg = "test message";

		AbstractInput input = new MyInput();
		// Mandatory with no value - Validation Error
		List<Diagnostic> diags = new ArrayList<>();
		input.setMandatory(true, msg);
		input.validate(diags);
		Assert.assertEquals("Incorrect mandatory message returned", msg, diags.get(0).
				getDescription());
	}

	@Test
	public void testReadOnlyAccessors() {
		assertAccessorsCorrect(new MyInput(), "readOnly", false, true, false);
	}

	@Test
	public void testDisabledAccessors() {
		assertAccessorsCorrect(new MyInput(), "disabled", false, true, false);
	}

	@Test
	public void testHandleRequest() {
		AbstractInput input = new MyInput();
		TestAction action = new TestAction();
		input.setActionOnChange(action);

		// Empty Request (No Change)
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		input.serviceRequest(request);
		Assert.assertNull("Input value after empty request should be null", input.getValue());
		Assert.assertFalse("Change Action should not have triggered for an empty request", action.
				wasTriggered());
		Assert.assertFalse("Changed in last request should be false for an empty request",
				input.isChangedInLastRequest());

		// Request with a value (trigger change action)
		String value = "A";
		request = new MockRequest();
		request.setParameter(input.getId(), value);
		action.reset();
		input.serviceRequest(request);
		Assert.assertEquals("Input value is incorrect after request with a value", value, input.
				getValue());
		Assert.assertTrue("Change Action should have triggered for a request with a value", action.
				wasTriggered());
		Assert.assertTrue("Changed in last request should be true for a request with a value",
				input.isChangedInLastRequest());

		// Request with the same value (No Change)
		request = new MockRequest();
		request.setParameter(input.getId(), value);
		action.reset();
		input.serviceRequest(request);
		Assert.assertEquals("Input value is incorrect after request with same value", value, input.
				getValue());
		Assert.assertFalse("Change Action should not have triggered for a request with same value",
				action.wasTriggered());
		Assert.assertFalse("Changed in last request should be false for a request with same value",
				input.isChangedInLastRequest());

		// Disabled Input - should not change (No Change)
		String value2 = "B";
		request = new MockRequest();
		request.setParameter(input.getId(), value2);
		action.reset();
		input.setDisabled(true);
		input.serviceRequest(request);
		Assert.assertEquals("Input value should not change when disabled", value, input.getValue());
		Assert.assertFalse("Change Action should not have triggered for a disabled input", action.
				wasTriggered());
		Assert.assertFalse("Changed in last request should be false for a disabled input",
				input.isChangedInLastRequest());
		input.setDisabled(false);

		// ReadOnly Input - should not change (No Change)
		request = new MockRequest();
		request.setParameter(input.getId(), value2);
		action.reset();
		input.setReadOnly(true);
		input.serviceRequest(request);
		Assert.assertEquals("Input value should not change when readonly", value, input.getValue());
		Assert.assertFalse("Change Action should not have triggered for a readonly input", action.
				wasTriggered());
		Assert.assertFalse("Changed in last request should be false for a readonly input",
				input.isChangedInLastRequest());
		input.setReadOnly(false);

		resetContext();

	}

	@Test
	public void testFoccussed() {
		AbstractInput input = new MyInput();

		setActiveContext(createUIContext());

		// No submit on change flag (no focus should be set)
		input.doHandleChanged();
		Assert.assertNull("Focussed should be null submit flag set to false", UIContextHolder.
				getCurrent()
				.getFocussed());

		// Submit on change (focus should be set)
		input.setSubmitOnChange(true);
		input.doHandleChanged();
		Assert.assertEquals("Focussed should be set with submit flag set to true", input,
				UIContextHolder.getCurrent()
				.getFocussed());

		// Test focus does not change if already set
		MyInput inputFocussed = new MyInput();
		inputFocussed.setFocussed();
		input.doHandleChanged();
		Assert.
				assertEquals("Focussed should not have changed with focus already set",
						inputFocussed, UIContextHolder
						.getCurrent().getFocussed());

		// Test focused when input has an action set
		resetContext();
		setActiveContext(createUIContext());

		TestAction action = new TestAction();
		input = new MyInput();
		input.setActionOnChange(action);

		// No submit on change flag (no focus should be set)
		input.doHandleChanged();
		UIContextHolder.getCurrent().doInvokeLaters();
		Assert.assertNull(
				"Focussed should be null with submit flag set to false and input has change action",
				UIContextHolder.getCurrent().getFocussed());

		// Submit on change (focus should be set)
		input.setSubmitOnChange(true);
		input.doHandleChanged();
		UIContextHolder.getCurrent().doInvokeLaters();
		Assert.assertEquals(
				"Focussed should be set with submit flag set to true and input has change action",
				input,
				UIContextHolder.getCurrent().getFocussed());

		// Test focus does not change if already set
		inputFocussed.setFocussed();
		input.doHandleChanged();
		UIContextHolder.getCurrent().doInvokeLaters();
		Assert.
				assertEquals("Focussed should not have changed with focus already set",
						inputFocussed, UIContextHolder
						.getCurrent().getFocussed());

		resetContext();
	}

	@Test
	public void testChangedInLastRequestAccessors() {
		AbstractInput input = new MyInput();
		Assert.assertFalse("changedInLastRequest flag should default to false", input.
				isChangedInLastRequest());
		input.setChangedInLastRequest(true);
		Assert.
				assertTrue("changedInLastRequest flag should be true", input.
						isChangedInLastRequest());
	}

	@Test
	public void testIsPresent() {
		AbstractInput input = new MyInput();
		String testValue = "ABC";

		// Empty Request
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		Assert.assertFalse("IsPresent should return false", input.isPresent(request));

		// Input on the request
		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(input.getId(), testValue);
		Assert.assertTrue("IsPresent should return true", input.isPresent(request));
	}

	@Test
	public void testGetValue() {
		AbstractInput input = new MyInput();

		Assert.assertNull("Default value should be null", input.getValue());

		Boolean value = Boolean.TRUE;
		input.setData(value);

		Assert.assertEquals("Incorrect value returned", value, input.getValue());
	}

	@Test
	public void testGetValueAsString() {
		AbstractInput input = new MyInput();

		Assert.assertNull("Default value as string should be null", input.getValueAsString());

		Boolean value = Boolean.TRUE;
		input.setData(value);

		Assert.assertEquals("Incorrect value as string returned", value.toString(), input.
				getValueAsString());
	}

	@Test
	public void testIsEmpty() {
		AbstractInput input = new MyInput();

		Assert.assertTrue("isEmpty for an input with no value should be true", input.isEmpty());

		// Empty String should be "empty"
		input.setData("");
		Assert.assertTrue("isEmpty for an input with an empty string as its value should be true",
				input.isEmpty());

		// Not Empty value
		Boolean value = Boolean.TRUE;
		input.setData(value);
		Assert.assertFalse("isEmpty for an input with a non-empty value should be false", input.
				isEmpty());
	}

	@Test
	public void testSubmitOnChangeAccessors() {
		AbstractInput input = new MyInput();
		Assert.assertFalse("submitOnChange flag should default to false", input.isSubmitOnChange());
		input.setSubmitOnChange(true);
		Assert.assertTrue("submitOnChange flag should be true", input.isSubmitOnChange());
	}

	/**
	 * Test instance of AbstractInput.
	 */
	private static class MyInput extends AbstractInput {

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

	};

}
