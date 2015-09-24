package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Date;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link RadioButtonGroup}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class RadioButtonGroup_Test extends AbstractWComponentTestCase {

	@Test
	public void testGetValue() {

		RadioButtonGroup group = new RadioButtonGroup();
		group.setLocked(true);

		setActiveContext(createUIContext());

		// Set data as a null value
		group.setData(null);
		Assert.assertNull("getValue should return null when data is null", group.getValue());

		// Set data as a empty string
		group.setData("");
		Assert.assertNull("getValue should return null when data is an empty string", group.
				getValue());

		// Set data as a String value
		group.setData("test data");
		Assert.assertEquals("getValue returned the incorrect value for the data", "test data",
				group.getValue());

		// Set data as an Object
		Object object = new Date();
		group.setData(object);
		Assert.
				assertEquals("getValue should return the string value of the data", object.
						toString(), group.getValue());
	}

	@Test
	public void testDoHandleRequest() {
		// Setup Group
		RadioButtonGroup group = new RadioButtonGroup();
		group.setLocked(true);

		setActiveContext(createUIContext());

		// -----------------------------------------------------
		// Request with null value and group is null (No Change)
		MockRequest request = setupRequest(group, null);
		group.setData(null);
		boolean changed = group.doHandleRequest(request);

		Assert.assertFalse(
				"doHandleRequest should have returned false for request with null value and group is null",
				changed);
		Assert
				.assertNull(
						"Value should still be null after request with null value and group is null",
						group.getData());

		// -----------------------------------------------------
		// Request with null value and group is empty (No Change)
		request = setupRequest(group, null);
		group.setData("");

		changed = group.doHandleRequest(request);

		Assert.assertFalse(
				"doHandleRequest should have returned false for request with null value and group is empty",
				changed);
		Assert.assertEquals(
				"Value should still be empty after request with null value an group is empty", "",
				group.getData());

		// -----------------------------------------------------
		// Request with Empty Value and group is null (No Change)
		request = setupRequest(group, "");
		group.setData(null);

		changed = group.doHandleRequest(request);

		Assert.assertFalse(
				"doHandleRequest should have returned false for request with empty value and group is null",
				changed);
		Assert.assertNull(
				"Value should still be null after request with empty value and group is null",
				group.getData());

		// -----------------------------------------------------
		// Request with Empty Value and group is empty (No Change)
		request = setupRequest(group, "");
		group.setData("");

		changed = group.doHandleRequest(request);

		Assert
				.assertFalse(
						"doHandleRequest should have returned false for request with empty value and group is empty",
						changed);
		Assert.
				assertEquals("Value should still be empty after empty request and group is empty",
						"", group.getData());

		// -----------------------------------------------------
		// Request with a Value and group is null (No Change)
		request = setupRequest(group, "XX");
		group.setData(null);

		changed = group.doHandleRequest(request);

		Assert.assertFalse(
				"doHandleRequest should have returned false for request with a value and group is null",
				changed);
		Assert.assertNull("Value should still be null after request with a value and group is null",
				group.getData());

		// -----------------------------------------------------
		// Request with a Value and group is Empty (No Change)
		request = setupRequest(group, "XX");
		group.setData("");

		changed = group.doHandleRequest(request);

		Assert.assertFalse(
				"doHandleRequest should have returned false for request with a value and group is empty",
				changed);
		Assert.assertEquals(
				"Value should still be empty after a request with a value and group is empty", "",
				group.getData());

		// -----------------------------------------------------
		// Request with null value and group has value (Change)
		request = setupRequest(group, null);
		group.setData("XX");

		changed = group.doHandleRequest(request);

		Assert.assertTrue(
				"doHandleRequest should have returned true for request with null value and group has value",
				changed);
		Assert.assertNull("Value should be null after request with null value and group has value",
				group.getData());

	}

	@Test
	public void testHandleButtonOnRequest() {
		RadioButtonGroup group = new RadioButtonGroup();
		group.setLocked(true);

		setActiveContext(createUIContext());

		// -----------------------------------------------------
		// Request with null value and group is null (No Change)
		group.setData(null);
		MockRequest request = setupRequest(group, null);
		boolean changed = group.handleButtonOnRequest(request);

		Assert
				.assertFalse(
						"handleButtonOnRequest should have returned false for request with null value and group is null",
						changed);
		Assert.assertNull("Value should still be null after null request", group.getData());

		// -----------------------------------------------------
		// Request with null value and group is empty (No Change)
		group.setData("");
		request = setupRequest(group, null);
		changed = group.handleButtonOnRequest(request);

		Assert
				.assertFalse(
						"handleButtonOnRequest should have returned false for request with null value and group is empty",
						changed);
		Assert.assertEquals("Value should still be empty after null request", "", group.getData());

		// -----------------------------------------------------
		// Request with Different Value (Change)
		request = setupRequest(group, "X");
		changed = group.handleButtonOnRequest(request);

		Assert.assertTrue(
				"handleButtonOnRequest should have returned true for request with different value",
				changed);
		Assert.assertEquals("Value not set after request", "X", group.getData());

		// -----------------------------------------------------
		// Request with Same Value (No Change)
		request = setupRequest(group, "X");
		changed = group.handleButtonOnRequest(request);

		Assert.assertFalse(
				"handleButtonOnRequest should have returned false for request with same value",
				changed);
		Assert.assertEquals("Value should not have changed after request with same value", "X",
				group.getData());

		// -----------------------------------------------------
		// Request with null value (Change)
		request = setupRequest(group, null);
		changed = group.handleButtonOnRequest(request);

		Assert.assertTrue(
				"handleButtonOnRequest should have returned true for request going back to a null value",
				changed);
		Assert.assertNull("Value should go back to null for request with null value", group.
				getData());
	}

	@Test
	public void testSetFoccussed() {
		RadioButtonGroup group = new RadioButtonGroup();

		setActiveContext(createUIContext());

		group.setFocussed();

		Assert.assertNull("Foccussed should be null for a radio button group", UIContextHolder.
				getCurrent()
				.getFocussed());
	}

	@Test
	public void testGetRequestValue() {
		RadioButtonGroup group = new RadioButtonGroup();
		group.setLocked(true);

		setActiveContext(createUIContext());

		// Set current value
		group.setSelectedValue("current");

		// Empty Request (not present, should return current value)
		MockRequest request = new MockRequest();
		Assert.
				assertEquals(
						"Current value of the group should have been returned for empty request",
						"current",
						group.getRequestValue(request));

		// Request with "empty" value (should return null as an empty value on the request is treated as null)
		request = setupRequest(group, "");
		Assert
				.assertNull("Null should have been returned for request with empty value", group.
						getRequestValue(request));

		// Request with value (should return the value on the request)
		request = setupRequest(group, "X");
		Assert.assertEquals("Value from the request should have been returned", "X", group.
				getRequestValue(request));
	}

	@Test
	public void testIsPresent() {
		RadioButtonGroup group = new RadioButtonGroup();

		// Empty Request (Not Present)
		MockRequest request = new MockRequest();
		Assert.assertFalse("isPresent should return false for empty request", group.isPresent(
				request));

		// On Request
		request = setupRequest(group, null);
		Assert.assertTrue("isPresent should return true for request with group", group.isPresent(
				request));
	}

	@Test
	public void testSelectedValueAccessors() {
		assertAccessorsCorrect(new RadioButtonGroup(), "selectedValue", null, "A", "B");
	}

	@Test
	public void testSubmitOnChangeAccessors() {
		assertAccessorsCorrect(new RadioButtonGroup(), "submitOnChange", false, true, false);
	}

	@Test
	public void testAddRadioButtonWithValue() {
		RadioButtonGroup group = new RadioButtonGroup();

		WRadioButton radio = group.addRadioButton("test");
		Assert.assertNotNull("Radio button with a value should not be null", radio);
		Assert.assertEquals("Radio button value incorrect", "test", radio.getValue());
	}

	@Test
	public void testAddRadioButton() {
		RadioButtonGroup group = new RadioButtonGroup();

		WRadioButton radio = group.addRadioButton();
		Assert.assertNotNull("Radio button should not be null", radio);
		Assert.assertNull("Radio button value should be null", radio.getValue());
		Assert.assertEquals("Radio button bean property should default to \".\"", ".", radio.
				getBeanProperty());
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
