package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import java.util.Date;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WRadioButton}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WRadioButton_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {

		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton radio = new WRadioButton(group);

		Assert.assertEquals("Incorrect group returned for radio button", group, radio.getGroup());

		// Invalid Argument
		try {
			radio = new WRadioButton(null);
			Assert.fail("Radio button with a null group should throw an exception.");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Message on exception should not be null", e.getMessage());
		}
	}

	@Test
	public void testHandleRequest() {
		String buttonValue = "A";
		String buttonValue2 = "B";
		String groupValue = "XX";

		// Setup Group
		RadioButtonGroup group = new RadioButtonGroup();

		// Request with value that matches the button (will change the group)
		group.setData(null);

		// Button disabled (no change)
		WRadioButton rb1 = group.addRadioButton(buttonValue);
		MockRequest request = setupRequest(group, rb1.getValue());
		rb1.setDisabled(true);
		rb1.handleRequest(request);

		Assert.assertNull("Group should not have changed for a disabled radio button", group.
				getData());

		rb1.setDisabled(false);

		// Button readonly (no change)
		rb1 = group.addRadioButton(buttonValue);
		request = setupRequest(group, rb1.getValue());
		rb1.setReadOnly(true);
		rb1.handleRequest(request);

		Assert.assertNull("Group should not have changed for a readonly radio button", group.
				getData());

		// Group has a value, but empty request (No change)
		rb1 = group.addRadioButton(buttonValue);
		request = new MockRequest();
		group.setData(groupValue);
		rb1.handleRequest(request);

		Assert.assertEquals("Group should not have changed for empty request", groupValue, group.
				getData());

		// Request with null value and group has value (No Change)
		rb1 = group.addRadioButton(buttonValue);
		request = setupRequest(group, null);
		group.setData(groupValue);

		rb1.handleRequest(request);
		Assert.assertEquals("Group should not have changed for null request", groupValue, group.
				getData());

		// Request with value that does not match button (No Change)
		rb1 = group.addRadioButton(buttonValue);
		request = setupRequest(group, buttonValue2);
		group.setData(groupValue);

		rb1.handleRequest(request);
		Assert.assertEquals(
				"Group should not have changed for value on request that does not match button",
				groupValue, group.getData());

		// Request with value that matches button and group different value (Change)
		rb1 = group.addRadioButton(buttonValue);
		request = setupRequest(group, buttonValue);
		group.setData(groupValue);

		rb1.handleRequest(request);
		Assert.assertEquals(
				"Group should have changed for request with value that matches the button",
				buttonValue,
				group.getData());
	}

	@Test
	public void testHandleRequestFocussed() {
		String buttonValue = "A";

		// Setup Group
		RadioButtonGroup group = new RadioButtonGroup();

		setActiveContext(createUIContext());

		// -------------------
		// No Submit on change (focus not set when change)
		// Setup radio button and request to cause a change
		WRadioButton rb1 = group.addRadioButton(buttonValue);
		MockRequest request = setupRequest(group, buttonValue);
		group.setData(null);

		rb1.handleRequest(request);

		Assert.assertNull("Foccussed should not have been set as submit on change is false",
				UIContextHolder
				.getCurrent().getFocussed());

		// -------------------
		// Submit on change (focus should be set)
		// Setup radio button and request to cause a change
		rb1 = group.addRadioButton(buttonValue);
		request = setupRequest(group, buttonValue);
		group.setData(null);
		group.setSubmitOnChange(true);

		rb1.handleRequest(request);

		Assert.assertEquals("Foccussed should be the radio button as submit on change is true", rb1,
				UIContextHolder
				.getCurrent().getFocussed());

		// -------------------
		// Submit on change but focus already set (focus should not change)
		// Set focus
		WTextField otherField = new WTextField();
		otherField.setFocussed();

		// Setup radiobutton and request to cause a change
		rb1 = group.addRadioButton(buttonValue);
		request = setupRequest(group, buttonValue);
		group.setData(null);

		rb1.handleRequest(request);

		Assert.assertEquals("Foccussed should not have changed as already set", otherField,
				UIContextHolder
				.getCurrent().getFocussed());
	}

	@Test
	public void testGetGroupName() {
		// Setup Group
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton();

		Assert.assertEquals("Incorrect group name returned", group.getId(), rb1.getGroupName());
	}

	@Test
	public void testSelectedAccessors() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton("A");

		WContainer root = new WContainer();
		root.add(group);
		root.add(rb1);

		assertAccessorsCorrect(rb1, "selected", false, true, false);
	}

	@Test
	public void testSelectedAccessorsDisabled() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton("A");

		rb1.setDisabled(true);
		try {
			rb1.setSelected(true);
			Assert.fail("Should not be able to select a disabled radio button.");
		} catch (IllegalStateException e) {
			Assert.assertNotNull(
					"Message on exception for selecting a disabled raido button should not be null",
					e.getMessage());
		}
	}

	@Test
	public void testGetValue() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton();

		setActiveContext(createUIContext());

		// Set data as a null value
		rb1.setData(null);
		Assert.assertNull("getValue should return null when data is null", rb1.getValue());

		// Set data as a empty string
		rb1.setData("");
		Assert.
				assertNull("getValue should return null when data is an empty string", rb1.
						getValue());

		// Set data as a String value
		rb1.setData("A");
		Assert.assertEquals("getValue returned the incorrect value for the data", "A", rb1.
				getValue());

		// Set data as an Object
		Object object = new Date();
		rb1.setData(object);
		Assert.
				assertEquals("getValue should return the string value of the data", object.
						toString(), rb1.getValue());
	}

	@Test
	public void testIsMandatory() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton("A");

		Assert.assertFalse("isMandatory should default to false", rb1.isMandatory());

		// Set mandatory
		group.setMandatory(true);

		Assert.assertTrue("isMandatory should be true", rb1.isMandatory());
	}

	@Test
	public void testIsSubmitOnChange() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton("A");

		Assert.assertFalse("isSubmitOnChange should default to false", rb1.isSubmitOnChange());

		// Set submitOnChange
		group.setSubmitOnChange(true);

		Assert.assertTrue("isSubmitOnChange should be true", rb1.isSubmitOnChange());
	}

	@Test
	public void testDisabledAccessors() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton("A");

		WContainer root = new WContainer();
		root.add(group);
		root.add(rb1);

		assertAccessorsCorrect(rb1, "disabled", false, true, false);
	}

	@Test
	public void testDisabledGroup() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton("A");

		Assert.assertFalse("isDisabled should default to false", rb1.isDisabled());

		// Set group readOnly
		group.setDisabled(true);

		Assert.assertTrue("isDisabled should be true", rb1.isDisabled());
	}

	@Test
	public void testReadOnlyAccessors() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton("A");

		WContainer root = new WContainer();
		root.add(group);
		root.add(rb1);

		assertAccessorsCorrect(rb1, "readOnly", false, true, false);
	}

	@Test
	public void testReadOnlyGroup() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb1 = group.addRadioButton("A");

		Assert.assertFalse("isReadOnly should default to false", rb1.isReadOnly());

		// Set group readOnly
		group.setReadOnly(true);

		Assert.assertTrue("isReadOnly should be true", rb1.isReadOnly());
	}

	@Test
	public void testRadioButtonInRepeater() {
		RadioButtonGroup group = new RadioButtonGroup();

		WBeanContainer repeated = new WBeanContainer();
		repeated.add(group.addRadioButton());

		WRepeater repeater = new WRepeater(repeated);

		WContainer root = new WContainer();
		root.add(group);
		root.add(repeater);

		root.add(group.addRadioButton("X"));
		root.add(group.addRadioButton("Y"));
		root.add(group.addRadioButton("Z"));

		root.setLocked(true);

		setActiveContext(createUIContext());
		repeater.setBeanList(Arrays.asList("A", "B", "C"));

		// Check nothing selected
		Assert.assertNull("Selecte dvalue should be null", group.getSelectedValue());

		// Empty Request
		MockRequest request = new MockRequest();
		root.serviceRequest(request);

		Assert.assertNull("Selected value should still be null after empty request", group.
				getSelectedValue());

		// Setup request with button on repeater selected
		request = setupRequest(group, "B");
		root.serviceRequest(request);

		Assert.assertEquals("Selected value should be 'B' after request", "B", group.
				getSelectedValue());

		// Setup request with button outside repeater
		request = setupRequest(group, "X");
		root.serviceRequest(request);

		Assert.assertEquals("Selected value should be 'X' after request", "X", group.
				getSelectedValue());

		// Setup request with null value
		request = setupRequest(group, null);
		root.serviceRequest(request);

		Assert.assertNull("Selected value should be null after request", group.getSelectedValue());

	}

	/**
	 * @param group the radio group to include on the request
	 * @param value the value for the radio group
	 * @return the request with the radio group
	 */
	private MockRequest setupRequest(final RadioButtonGroup group, final String value) {
		MockRequest request = new MockRequest();
		request.setParameter(group.getId() + "-h", "x");
		if (value != null) {
			request.setParameter(group.getId(), value);
		}
		return request;
	}

}
