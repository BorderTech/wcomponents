package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link WMultiTextField} component.
 *
 * @author Christina Harris
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiTextField_Test extends AbstractWComponentTestCase {

	/**
	 * Text option 1.
	 */
	private static final String TEXT1 = "A";

	/**
	 * Text option 2.
	 */
	private static final String TEXT2 = "B";

	/**
	 * Entered text1.
	 */
	private static final String[] ENTERED_1 = new String[]{TEXT1};

	/**
	 * Entered text1 and text2.
	 */
	private static final String[] ENTERED_1_2 = new String[]{TEXT1, TEXT2};

	/**
	 * Entered empty.
	 */
	private static final String[] ENTERED_EMPTY = new String[]{};

	@Test
	public void testConstructors() {
		// Constructor 1
		WMultiTextField field = new WMultiTextField();
		Assert.assertNull("Text should be null", field.getTextInputs());

		// Constructor 2
		field = new WMultiTextField(ENTERED_1_2);
		Assert.assertTrue("Wrong text values returned", Arrays.equals(ENTERED_1_2, field.
				getTextInputs()));
	}

	@Test
	public void testGetValue() {
		WMultiTextField field = new WMultiTextField();
		field.setBeanProperty(".");
		field.setLocked(true);

		setActiveContext(createUIContext());

		// Null
		field.setBean(null);
		Assert.assertNull("Value should be null", field.getValue());

		// Array - empty
		field.setBean(ENTERED_EMPTY);
		Assert.assertNull("Value should be null for empty array", field.getValue());

		// Array
		field.setBean(ENTERED_1_2);
		Assert.assertTrue("Value should be an array with the two entrie in the array",
				Arrays.equals(ENTERED_1_2, field.getValue()));

		// List
		field.setBean(Arrays.asList(ENTERED_1_2));
		Assert.assertTrue("Value should be an array with the two entries in the list",
				Arrays.equals(ENTERED_1_2, field.getValue()));

		// List with null
		List<String> data = new ArrayList<>(Arrays.asList(ENTERED_1_2));
		data.add("");
		field.setBean(data);
		Assert.assertTrue(
				"Value should be an array with the two entries in the list without the empty entry",
				Arrays.equals(ENTERED_1_2, field.getValue()));

		// Just object
		field.setBean(TEXT1);
		Assert.assertTrue("Value should be an array with the text item", Arrays.equals(ENTERED_1,
				field.getValue()));
	}

	@Test
	public void testSetData() {
		WMultiTextField field = new WMultiTextField();
		field.setLocked(true);

		setActiveContext(createUIContext());

		field.setData(ENTERED_1_2);
		Assert.assertTrue("Wrong value returned for data", Arrays.equals(ENTERED_1_2,
				(Object[]) field.getData()));

		// Empty array
		field.setData(ENTERED_EMPTY);
		Assert.assertNull("Null should be returned for empty array", field.getData());

		// Empty options
		field.setData(new String[]{"", TEXT1, "", TEXT2, ""});
		Assert.assertTrue("Empty options should have been removed",
				Arrays.equals(ENTERED_1_2, (Object[]) field.getData()));

		// Max options exceeded
		field.setMaxInputs(1);
		field.setData(ENTERED_1_2);
		Assert.assertTrue("Should only return the first option", Arrays.equals(ENTERED_1,
				(Object[]) field.getData()));
	}

	@Test
	public void testTextInputsAccessors() {
		assertAccessorsCorrect(new WMultiTextField(), "textInputs", null, ENTERED_1, ENTERED_1_2);
	}

	@Test
	public void testMaxInputsAccessors() {
		assertAccessorsCorrect(new WMultiTextField(), "maxInputs", 0, 1, 2);
	}

	@Test
	public void testColumnsAccessors() {
		assertAccessorsCorrect(new WMultiTextField(), "columns", 0, 1, 2);
	}

	@Test
	public void testMinLengthAccessors() {
		assertAccessorsCorrect(new WMultiTextField(), "minLength", 0, 1, 2);
	}

	@Test
	public void testMaxLengthAccessors() {
		assertAccessorsCorrect(new WMultiTextField(), "maxLength", 0, 1, 2);
	}

	@Test
	public void testPatternAccessors() {
		assertAccessorsCorrect(new WMultiTextField(), "pattern", null, "test1", "test2");
	}

	@Test(expected = PatternSyntaxException.class)
	public void testSetPatternInvalid() {
		WMultiTextField field = new WMultiTextField();
		field.setPattern("[foo");
	}

	@Test
	public void testGetValueAsString() {
		WMultiTextField field = new WMultiTextField();
		field.setLocked(true);

		setActiveContext(createUIContext());

		// Null
		Assert.assertNull("Value as String should be null", field.getValueAsString());

		// Entered text1
		field.setData(ENTERED_1);
		Assert.assertEquals("Value as String should be text1", TEXT1, field.getValueAsString());

		// Entered text1 and text2
		field.setData(ENTERED_1_2);
		Assert
				.assertEquals("Value as String should be text1 and text2", TEXT1 + ", " + TEXT2,
						field.getValueAsString());
	}

	@Test
	public void testDoHandleRequest() {
		WMultiTextField field = new WMultiTextField();
		field.setMaxInputs(4);

		setActiveContext(createUIContext());

		// Empty Request (no change)
		MockRequest request = new MockRequest();
		request.setParameter(field.getId(), "");
		boolean changed = field.doHandleRequest(request);
		Assert.assertFalse("Should not change for empty request", changed);

		// Request with entries (change)
		request = new MockRequest();
		request.setParameter(field.getId(), ENTERED_1_2);

		changed = field.doHandleRequest(request);

		Assert.assertTrue("Should have changed for request with text entries", changed);
		Assert
				.assertTrue("Unexpected inputs set on WMultiTextField", Arrays.equals(ENTERED_1_2,
						field.getTextInputs()));

		// Same request (no change)
		changed = field.doHandleRequest(request);
		Assert.assertFalse("Should have not changed for request with same text entries", changed);
		Assert
				.assertTrue("Unexpected inputs set on WMultiTextField", Arrays.equals(ENTERED_1_2,
						field.getTextInputs()));

		// Empty Request (change)
		request = new MockRequest();
		request.setParameter(field.getId(), "");
		changed = field.doHandleRequest(request);
		Assert.assertTrue("Should have changed for empty request", changed);
		Assert.assertNull("Text inputs should be null", field.getTextInputs());
	}

	@Test
	public void testGetRequestValue() {
		WMultiTextField field = new WMultiTextField();
		setActiveContext(createUIContext());

		field.setTextInputs(ENTERED_1);

		// Empty Request - return current value
		MockRequest request = new MockRequest();
		Assert.assertTrue("Request value should be the current value for an empty request",
				Arrays.equals(ENTERED_1, field.getRequestValue(request)));

		// Empty values on the request
		String[] value = new String[]{"", null};
		request = new MockRequest();
		request.setParameter(field.getId(), value);
		Assert.assertNull("Request value should be null for empty values on request", field.
				getRequestValue(request));

		// Values on the request
		request = new MockRequest();
		request.setParameter(field.getId(), ENTERED_1_2);
		Assert.assertTrue("Request value should be the values on the request",
				Arrays.equals(ENTERED_1_2, field.getRequestValue(request)));

		// An empty string or null in the array should be removed
		String[] inputs2 = new String[]{"", "x", "y", "", "", null, "z"};
		request.setParameter(field.getId(), inputs2);

		Assert.assertTrue(
				"Request value should be the values on the request without the null and empty entries",
				Arrays.equals(new String[]{"x", "y", "z"}, field.getRequestValue(request)));

	}

	@Test
	public void testValidateMaxLength() {
		String text = "test";
		WMultiTextField field = new WMultiTextField();

		List<Diagnostic> diags = new ArrayList<>();
		field.setLocked(true);
		setActiveContext(createUIContext());

		field.setTextInputs(null);
		field.validate(diags);
		Assert.assertTrue("Null text with no maximum set should be valid", diags.isEmpty());

		// Empty Strings are removed by the component, so should be the same as "null"
		field.setTextInputs(new String[]{""});
		field.validate(diags);
		Assert.assertTrue("Empty text with no maximum set should be valid", diags.isEmpty());

		field.setTextInputs(new String[]{text});
		field.validate(diags);
		Assert.assertTrue("Text with no maximum set should be valid", diags.isEmpty());

		field.setMaxLength(1);

		field.setTextInputs(null);
		field.validate(diags);
		Assert.assertTrue("Null text with maximum set should be valid", diags.isEmpty());

		// Empty Strings are removed by the component, so should be the same as "null"
		field.setTextInputs(new String[]{""});
		field.validate(diags);
		Assert.assertTrue("Empty text with maximum set should be valid", diags.isEmpty());

		field.setTextInputs(new String[]{text});

		field.setMaxLength(text.length() + 1);
		field.validate(diags);
		Assert.assertTrue("Text is less than maximum so should be valid", diags.isEmpty());

		field.setMaxLength(text.length());
		field.validate(diags);
		Assert.assertTrue("Text is the same as maximum so should be valid", diags.isEmpty());

		field.setMaxLength(text.length() - 1);
		field.validate(diags);
		Assert.assertFalse("Text is longer than maximum so should be invalid", diags.isEmpty());
	}

	@Test
	public void testValidateMinLength() {
		String text = "test";
		WMultiTextField field = new WMultiTextField();

		List<Diagnostic> diags = new ArrayList<>();
		setActiveContext(createUIContext());

		field.setTextInputs(null);
		field.validate(diags);
		Assert.assertTrue("Null text with no minimum set should be valid", diags.isEmpty());

		// Empty Strings are removed by the component, so should be the same as "null"
		field.setTextInputs(new String[]{""});
		field.validate(diags);
		Assert.assertTrue("Empty text with no minimum set should be valid", diags.isEmpty());

		field.setTextInputs(new String[]{text});
		field.validate(diags);
		Assert.assertTrue("Text with no minimum set should be valid", diags.isEmpty());

		field.setMinLength(1);

		field.setTextInputs(null);
		field.validate(diags);
		Assert.assertTrue("Null text with minimum set should be valid", diags.isEmpty());

		// Empty Strings are removed by the component, so should be the same as "null"
		field.setTextInputs(new String[]{""});
		field.validate(diags);
		Assert.assertTrue("Empty text with minimum set should be valid", diags.isEmpty());
		diags.clear();

		field.setTextInputs(new String[]{text});

		field.setMinLength(text.length() - 1);
		field.validate(diags);
		Assert.assertTrue("Text is longer than minimum so should be valid", diags.isEmpty());

		field.setMinLength(text.length());
		field.validate(diags);
		Assert.assertTrue("Text is the same as minimum so should be valid", diags.isEmpty());

		field.setMinLength(text.length() + 1);
		field.validate(diags);
		Assert.assertFalse("Text is less than minimum so should be invalid", diags.isEmpty());
	}

	@Test
	public void testValidatePattern() {
		WMultiTextField field = new WMultiTextField();
		field.setLocked(true);

		String text = "test1";
		String pattern = "test[123]";

		List<Diagnostic> diags = new ArrayList<>();
		setActiveContext(createUIContext());

		field.setTextInputs(null);
		field.validate(diags);
		Assert.assertTrue("Null text with no pattern set should be valid", diags.isEmpty());

		field.setTextInputs(new String[]{""});
		field.validate(diags);
		Assert.assertTrue("Empty text with no pattern set should be valid", diags.isEmpty());

		field.setTextInputs(new String[]{text});
		field.validate(diags);
		Assert.assertTrue("Text with no pattern set should be valid", diags.isEmpty());

		field.setPattern(pattern);

		field.setTextInputs(null);
		field.validate(diags);
		Assert.assertTrue("Null text with pattern set should be valid", diags.isEmpty());

		field.setTextInputs(new String[]{""});
		field.validate(diags);
		Assert.assertTrue("Empty text with pattern set should be valid", diags.isEmpty());

		field.setTextInputs(new String[]{text});

		field.validate(diags);
		Assert.assertTrue("Text that matches should be valid", diags.isEmpty());

		field.setTextInputs(new String[]{"no match"});
		field.validate(diags);
		Assert.assertFalse("Text is no match so should be invalid", diags.isEmpty());
	}

}
