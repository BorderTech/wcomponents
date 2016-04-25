package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * AbstractWMultiSelectList_Test - unit tests for {@link AbstractWMultiSelectList}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractWMultiSelectList_Test extends AbstractWComponentTestCase {

	/**
	 * Test option A.
	 */
	private static final String OPTION_A = "A";
	/**
	 * Test option B.
	 */
	private static final String OPTION_B = "B";
	/**
	 * Test option C.
	 */
	private static final String OPTION_C = "C";
	/**
	 * Test null option.
	 */
	private static final String OPTION_NULL = null;
	/**
	 * Options list.
	 */
	private static final List<String> OPTIONS = Arrays.asList(OPTION_A, OPTION_B, OPTION_C);
	/**
	 * Options list with null option.
	 */
	private static final List<String> OPTIONS_WITH_NULL = Arrays.asList(OPTION_A, OPTION_NULL,
			OPTION_B, OPTION_C);

	/**
	 * Invalid option.
	 */
	private static final String OPTION_INVALID = "XX";

	/**
	 * Option A selected list.
	 */
	private static final List<String> SELECTED_A = Arrays.asList(OPTION_A);
	/**
	 * Option B selected list.
	 */
	private static final List<String> SELECTED_B = Arrays.asList(OPTION_B);
	/**
	 * Option A and B selected list.
	 */
	private static final List<String> SELECTED_A_B = Arrays.asList(OPTION_A, OPTION_B);
	/**
	 * Option A and B array.
	 */
	private static final String[] ARRAY_OPTIONS_A_B = new String[]{OPTION_A, OPTION_B};
	/**
	 * Empty array.
	 */
	private static final String[] EMPTY_ARRAY = new String[]{};
	/**
	 * Empty list.
	 */
	private static final List<?> EMPTY_LIST = Collections.EMPTY_LIST;

	/**
	 * First option selected list.
	 */
	private static final List<String> SELECTED_FIRST_OPTION = Arrays.asList(OPTION_A);
	/**
	 * Null option selected list.
	 */
	private static final List<String> SELECTED_NULL = Arrays.asList(new String[]{null});

	/**
	 * Test group 1.
	 */
	private static final OptionGroup GROUP1 = new OptionGroup("Group1", Arrays.asList(OPTION_A, "e",
			"f"));
	/**
	 * Test group 2.
	 */
	private static final OptionGroup GROUP2 = new OptionGroup("Group2", Arrays.asList("x", OPTION_B,
			"z"));
	/**
	 * Group Options List.
	 */
	private static final List<Serializable> OPTIONS_WITH_GROUPS = Arrays.asList(OPTION_C, GROUP1,
			"Two", GROUP2, OPTION_NULL,
			"Three");

	@Test
	public void testConstructor1() {
		// Constructor - 1
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		List<?> result = multi.getOptions();
		Assert.assertEquals("Incorrect options returned", OPTIONS, result);
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());

		multi = new MyWMultiSelectList(OPTIONS, false);
		Assert.assertFalse("allowNoSelection should be false", multi.isAllowNoSelection());
	}

	@Test
	public void testConstructor2() {
		// Constructor - 2
		AbstractWMultiSelectList multi = new MyWMultiSelectList(DayOfWeekTable.class, true);
		Assert.
				assertEquals("Incorrect table returned", DayOfWeekTable.class, multi.
						getLookupTable());
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());

		multi = new MyWMultiSelectList(DayOfWeekTable.class, false);
		Assert.assertFalse("allowNoSelection should be false", multi.isAllowNoSelection());
	}

	@Test
	public void testGetValueAsString() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		multi.setSelected(null);
		Assert.assertNull("Value as String should be null", multi.getValueAsString());

		// Select optionA
		multi.setSelected(SELECTED_A);
		Assert.assertEquals("Value as String should be OptionA", OPTION_A, multi.getValueAsString());

		// Select optionA, optionB
		multi.setSelected(SELECTED_A_B);
		Assert.
				assertEquals("Value as String should be OptionA, OptionB",
						OPTION_A + ", " + OPTION_B,
						multi.getValueAsString());
	}

	@Test
	public void testGetValueAsStringEmptyOptions() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(new ArrayList<String>(), true);
		Assert.assertNull("Value as String should be null - empty list to select from", multi.
				getValueAsString());
	}

	@Test
	public void testGetValueAsStringNullOptions() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(null, true);
		Assert.assertNull("Value as String should be null - null to select from", multi.
				getValueAsString());
	}

	@Test
	public void testSelectedAccessors() {
		assertAccessorsCorrect(new MyWMultiSelectList(OPTIONS, true), "selected",
				Collections.EMPTY_LIST, SELECTED_A,
				SELECTED_A_B);
	}

	@Test
	public void testGetSelectedOptionsAsArray() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		// Nothing selected, empty array
		Assert.assertEquals("Selected options array should be empty", 0, multi.
				getSelectedOptionsAsArray().length);

		// Select options
		multi.setSelected(SELECTED_A_B);
		Assert.assertTrue("Selected options array should have the two entries", Arrays.equals(
				ARRAY_OPTIONS_A_B, multi.getSelectedOptionsAsArray()));
	}

	@Test
	public void testGetUnselectedNoSelection() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		// Nothing selected, all should be are deselected
		Assert.assertEquals("Expect all options to be unselected", OPTIONS, multi.getNotSelected());
	}

	@Test
	public void testGetUnselectedSomeSelection() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		multi.setSelected(Arrays.asList(OPTION_C));
		Assert.assertEquals("Expect a nd b to be unselected", SELECTED_A_B, multi.getNotSelected());
	}

	@Test
	public void testGetUnselectedAllSelected() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		multi.setSelected(OPTIONS);
		Assert.assertEquals("Expect all options to be selected", EMPTY_LIST, multi.getNotSelected());
	}

	@Test
	public void testGetUnselectedNoData() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(null, true);
		Assert.assertEquals("Expect unselected to be an empty collection", Collections.EMPTY_LIST, multi.getNotSelected());
	}

	@Test
	public void testGetValueConvertDataToList() {
		// =======================
		// ALLOW NONE - TRUE

		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		// Retrieve the value from the bean (so not setting via setData)
		multi.setBeanProperty(".");

		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		multi.setBean(null);
		Assert
				.assertEquals("Allow None - getValue for null data should be an empty list",
						EMPTY_LIST, multi.getValue());

		// Data is empty list
		multi.setBean(EMPTY_LIST);
		Assert.assertEquals("Allow None - getValue for empty list data should be an empty list",
				EMPTY_LIST,
				multi.getValue());

		// Data is a list
		multi.setBean(SELECTED_A_B);
		Assert.assertEquals(
				"Allow None - getValue for list data should be the selected options in a list",
				SELECTED_A_B, multi.getValue());

		// Data is an empty array
		multi.setBean(EMPTY_ARRAY);
		Assert.assertEquals("Allow None - getValue for empty array data should be an empty list",
				EMPTY_LIST,
				multi.getValue());

		// Data is an array
		multi.setBean(ARRAY_OPTIONS_A_B);
		Assert.assertEquals(
				"Allow None - getValue for array data should be the selected options in a list",
				SELECTED_A_B, multi.getValue());

		// Data is an Option
		multi.setBean(OPTION_B);
		Assert.assertEquals(
				"Allow None - getValue for Object data should be the selected option in a list",
				SELECTED_B, multi.getValue());

		resetContext();

		// =======================
		// ALLOW NONE - FALSE
		multi = new MyWMultiSelectList(OPTIONS, false);

		// Retrieve the value from the bean (so not setting via setData)
		multi.setBeanProperty(".");

		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		multi.setBean(null);
		Assert.
				assertEquals(
						"No Allow None - getValue for null list data should be the first option",
						SELECTED_FIRST_OPTION, multi.getValue());

		// Data is empty list
		multi.setBean(EMPTY_LIST);
		Assert.assertEquals(
				"No Allow None - getValue for empty list data should be the first option",
				SELECTED_FIRST_OPTION, multi.getValue());

		// Data is a list
		multi.setBean(SELECTED_A_B);
		Assert.assertEquals(
				"No Allow None - getValue for list data should be the selected options in a list",
				SELECTED_A_B, multi.getValue());

		// Data is empty array
		multi.setBean(EMPTY_ARRAY);
		Assert.assertEquals(
				"No Allow None - getValue for empty array data should be the first option",
				SELECTED_FIRST_OPTION, multi.getValue());

		// Data is an array
		multi.setBean(ARRAY_OPTIONS_A_B);
		Assert.assertEquals(
				"No Allow None - getValue for array data should be the selected options in a list",
				SELECTED_A_B, multi.getValue());

		// Data is an Option
		multi.setBean(OPTION_B);
		Assert.assertEquals("getValue for Object data should be the selected option in a list",
				SELECTED_B,
				multi.getValue());

	}

	@Test
	public void testGetValueWithNoOptionsNothingSelected() {
        // =======================
		// ALLOW NONE - TRUE

		// Null Options
		AbstractWMultiSelectList multi = new MyWMultiSelectList(null, true);
		Assert.assertEquals("Allow none - Null Options - should be empty selected", EMPTY_LIST,
				multi.getValue());
		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Allow none - Null Options -  should be empty selected with uic",
				EMPTY_LIST,
				multi.getValue());
		resetContext();

		// Empty Options
		multi = new MyWMultiSelectList(new ArrayList<>(), true);
		Assert.assertEquals("Allow none - Empty Options -  should be empty selected", EMPTY_LIST,
				multi.getValue());
		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Allow none - Empty Options -  should be empty selected with uic",
				EMPTY_LIST,
				multi.getValue());
		resetContext();

		// =======================
		// ALLOW NONE - FALSE
		// Null Options
		multi = new MyWMultiSelectList(null, false);
		Assert.assertEquals("No Allow none - Null Options -  should be empty selected", EMPTY_LIST,
				multi.getValue());
		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("No Allow none - Null Options -  should be empty selected with uic",
				EMPTY_LIST,
				multi.getValue());
		resetContext();

		// Empty Options
		multi = new MyWMultiSelectList(new ArrayList<>(), false);
		Assert.assertEquals("No Allow none - Empty Options -  should be empty selected", EMPTY_LIST,
				multi.getValue());
		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("No Allow none - Empty Options -  should be empty selected with uic",
				EMPTY_LIST,
				multi.getValue());
		resetContext();
	}

	@Test
	public void testGetValueWithOptionsNothingSelected() {
		// =======================
		// ALLOW NONE - TRUE

		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		// Nothing selected - Should return empty
		Assert.assertEquals("Allow none - Nothing selected - should be empty selected", EMPTY_LIST,
				multi.getValue());
		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Allow none - Nothing selected - should be empty selected with uic",
				EMPTY_LIST,
				multi.getValue());
		resetContext();

		// =======================
		// ALLOW NONE - FALSE
		multi = new MyWMultiSelectList(OPTIONS, false);
		// Should be the first option
		Assert.assertEquals("No Allow None - Nothing Selected - should be optionA selected",
				SELECTED_FIRST_OPTION,
				multi.getValue());
		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.
				assertEquals(
						"No Allow None - Nohting Selected - should be optionA selected with uic",
						SELECTED_FIRST_OPTION, multi.getValue());
	}

	@Test
	public void testGetValueWithDefaultSet() {
		// Set a Default option
		List<String> selectedC = Arrays.asList(OPTION_C);
		List<String> selectedB = Arrays.asList(OPTION_B);

		// =======================
		// ALLOW NONE - TRUE
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		// Set OptionC as default
		multi.setSelected(selectedC);
		// Should return optionC
		Assert.assertEquals("Allow None - should be optionC selected", OPTION_C, multi.getValue().
				get(0));

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());

		Assert.assertEquals("Allow None - should be optionC selected with uic", OPTION_C, multi.
				getValue().get(0));

		// Set optionB on the user context
		multi.setSelected(selectedB);
		Assert.assertEquals("Allow None - should be optionB selected with uic", OPTION_B, multi.
				getValue().get(0));

		resetContext();
		Assert.assertEquals("Allow None - should be optionC selected", OPTION_C, multi.getValue().
				get(0));

		// =======================
		// ALLOW NONE - FALSE
		multi = new MyWMultiSelectList(OPTIONS, false);
		// Set OptionC as default
		multi.setSelected(selectedC);
		// Should return optionC
		Assert.assertEquals("No Allow None - should be optionC selected", OPTION_C,
				multi.getValue().get(0));

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());

		Assert.assertEquals("No Allow None - should be optionC selected with uic", OPTION_C, multi.
				getValue().get(0));

		// Set OptionB on the user context
		multi.setSelected(selectedB);
		Assert.assertEquals("No Allow None - should be optionB selected with uic", OPTION_B, multi.
				getValue().get(0));

		resetContext();
		Assert.assertEquals("No Allow None - should be optionC selected", OPTION_C,
				multi.getValue().get(0));
	}

	@Test
	public void testGetValueDefaultWithNullOption() {

		// =======================
		// Allow None - TRUE
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS_WITH_NULL, true);

		// Should return empty
		Assert.assertEquals("Allow None - Selected should be empty", EMPTY_LIST, multi.getValue());
		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Allow None - Selected should be empty with uic", EMPTY_LIST, multi.
				getValue());
		resetContext();

		// =======================
		// Allow None - FALSE
		multi = new MyWMultiSelectList(OPTIONS_WITH_NULL, false);

		// Should return null option
		Assert.assertEquals("No Allow None - Selected should be null option", SELECTED_NULL, multi.
				getValue());

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.
				assertEquals("No Allow None - Selected should be null option with uic",
						SELECTED_NULL, multi.getValue());
		resetContext();
	}

	@Test
	public void testGetValueWithBean() {
		// =======================
		// Allow None - TRUE

		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		// Set Bean Property
		multi.setBeanProperty(".");

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Null Bean Value
		Assert.assertEquals("Allow None - Data should be empty when bean value is null", EMPTY_LIST,
				multi.getValue());

		// Valid Bean Value
		multi.setBean(SELECTED_B);
		Assert.assertEquals("Allow None - Data should be OptionA from bean", SELECTED_B, multi.
				getValue());

		// Invalid Bean Value
		multi.setBean(OPTION_INVALID);
		try {
			multi.getValue();
			Assert.fail("Allow None - Exception should have been thrown for invalid option on bean");
		} catch (IllegalStateException e) {
			Assert.assertNotNull(
					"Allow None - No exception message provided for invalid option on bean",
					e.getMessage());
		}
		resetContext();

		// =======================
		// Allow None - FALSE
		multi = new MyWMultiSelectList(OPTIONS, false);
		// Set Bean Property
		multi.setBeanProperty(".");

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Null Bean Value
		Assert.assertEquals("No Allow None - Data should default to OptionA", SELECTED_A, multi.
				getValue());

		// Valid Bean Value
		multi.setBean(SELECTED_B);
		Assert.assertEquals("No Allow None - Data should be OptionB from bean", SELECTED_B, multi.
				getValue());

		// Invalid Bean Value
		multi.setBean(OPTION_INVALID);
		try {
			multi.getValue();
			Assert.fail(
					"No Allow None - Exception should have been thrown for invalid option on bean");
		} catch (IllegalStateException e) {
			Assert.assertNotNull(
					"No Allow None - No exception message provided for invalid option on bean",
					e.getMessage());
		}
	}

	@Test
	public void testGetValueEditableOption() {

		List<String> userTextList = Arrays.asList("USERTEXT");

		// Editable List
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		multi.setEditable(true);
		// Set Bean Property
		multi.setBeanProperty(".");

		multi.setLocked(true);
		setActiveContext(createUIContext());

		multi.setBean("USERTEXT");
		Assert.assertEquals("Should be user text selected", userTextList, multi.getValue());

		// ===============
		// Null Options
		multi.setOptions((List<?>) null);
		Assert.assertEquals("Should be user text selected", userTextList, multi.getValue());

		// ===============
		// Empty Options
		multi.setOptions(EMPTY_LIST);
		Assert.assertEquals("Should be user text selected", userTextList, multi.getValue());
	}

	@Test
	public void testGetData() {

		// =======================
		// ALLOW NONE - TRUE
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		// Retrieve the value from the bean (so not setting via setData)
		multi.setBeanProperty(".");

		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		multi.setBean(null);
		Assert.assertNull("Allow None - getData for null data should be null", multi.getData());

		// Data is empty list
		multi.setBean(EMPTY_LIST);
		Assert.assertEquals("Allow None - getData for empty list data should be the empty list",
				EMPTY_LIST,
				multi.getData());

		// Data is a list
		multi.setBean(SELECTED_A_B);
		Assert.assertEquals(
				"Allow None - getData for list data should be the selected options in a list",
				SELECTED_A_B, multi.getData());

		// Data is an empty array
		multi.setBean(EMPTY_ARRAY);
		Assert.assertEquals("Allow None - getData for empty array data should be the empty array",
				EMPTY_ARRAY,
				multi.getData());

		// Data is an array
		multi.setBean(ARRAY_OPTIONS_A_B);
		Assert.assertEquals("Allow None - getData for array data should be the array",
				ARRAY_OPTIONS_A_B,
				multi.getData());

		// Data is an Option
		multi.setBean(OPTION_B);
		Assert.assertEquals(
				"Allow None - getData for Object data should be the selected option in a list",
				OPTION_B,
				multi.getData());

		resetContext();

		// =======================
		// ALLOW NONE - FALSE
		multi = new MyWMultiSelectList(OPTIONS, false);

		// Retrieve the value from the bean (so not setting via setData)
		multi.setBeanProperty(".");

		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		multi.setBean(null);
		Assert.assertEquals("No Allow None - getData for null list data should be the first option",
				SELECTED_FIRST_OPTION, multi.getData());

		// Data is empty list
		multi.setBean(EMPTY_LIST);
		Assert.
				assertEquals(
						"No Allow None - getData for empty list data should be the first option",
						SELECTED_FIRST_OPTION, multi.getData());

		// Data is empty array
		multi.setBean(EMPTY_ARRAY);
		Assert.assertEquals(
				"No Allow None - getData for empty array data should be the first option",
				SELECTED_FIRST_OPTION, multi.getData());

		// Data is a list
		multi.setBean(SELECTED_A_B);
		Assert.assertEquals(
				"No Allow None - getData for list data should be the selected options in a list",
				SELECTED_A_B, multi.getData());

		// Data is an array
		multi.setBean(ARRAY_OPTIONS_A_B);
		Assert.assertEquals("No Allow None - getData for array data should be the array",
				ARRAY_OPTIONS_A_B,
				multi.getData());

		// Data is an Option
		multi.setBean(OPTION_B);
		Assert.
				assertEquals("No Allow None - getData for Object data should be the Object",
						OPTION_B, multi.getData());

		// =======================
		// Include "null" as an option
		multi.setOptions(OPTIONS_WITH_NULL);

		// Data is null
		multi.setBean(null);
		Assert
				.assertEquals(
						"No Allow None - Null is an option - getData for null list data should be the a list with the null option",
						SELECTED_NULL, multi.getData());

		// Data is empty list
		multi.setBean(EMPTY_LIST);
		Assert
				.assertEquals(
						"No Allow None - Null is an option -  getData for empty list data should be a list with the null option",
						SELECTED_NULL, multi.getData());

		// Data is empty array
		multi.setBean(EMPTY_ARRAY);
		Assert
				.assertEquals(
						"No Allow None - Null is an option -  getData for empty array data should be a list with the null option",
						SELECTED_NULL, multi.getData());

		// =======================
		// No Options
		multi.setOptions((List<?>) null);

		// Data is null
		multi.setBean(null);
		Assert.assertNull("No Allow None - No options - getData for null list data should be null",
				multi.getData());

		// Data is empty list
		multi.setBean(EMPTY_LIST);
		Assert.assertEquals(
				"No Allow None - No options -  getData for empty list data should be the empty list",
				EMPTY_LIST, multi.getData());

		// Data is empty array
		multi.setBean(EMPTY_ARRAY);
		Assert.assertEquals(
				"No Allow None - No options -  getData for empty array data should be the empty array",
				EMPTY_ARRAY, multi.getData());

		// Data is an Option
		multi.setBean(OPTION_B);
		Assert.assertEquals(
				"No Allow None - No options -  getData for Object data should be the Object",
				OPTION_B,
				multi.getData());

	}

	@Test
	public void testSetData() {
		// Null options
		AbstractWMultiSelectList multi = new MyWMultiSelectList(null, true);

		try {
			multi.setData(SELECTED_A);
			Assert.fail("List has null options so should have thrown exception");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}

		// Empty Options
		multi.setOptions(EMPTY_LIST);
		try {
			multi.setData(SELECTED_A);
			Assert.fail("List has empty options so should have thrown exception");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}

		// Set options
		multi.setOptions(OPTIONS);

		// Set valid option
		multi.setData(SELECTED_A);
		Assert.assertEquals("Should be optionA selected", SELECTED_A, multi.getSelected());

		// Set invalid option
		try {
			multi.setData(Arrays.asList(OPTION_INVALID));
			Assert.fail("Should have thrown exception for setting an option that does not match");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}

		// Set invalid option type (wrong class)
		try {
			multi.setData(Arrays.asList(Boolean.TRUE));
			Assert.fail(
					"Should have thrown exception for setting a boolean when options are strings");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}
	}

	@Test
	public void testSetDataConvertToList() {
		// Null options and allow no selection
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		multi.setData(null);
		Assert.assertNull("getData should return null when setting null", multi.getData());

		// Data is empty list
		multi.setData(EMPTY_LIST);
		Assert.assertNull("getData should return null when setting empty list", multi.getData());

		// Data is a list
		multi.setData(SELECTED_A_B);
		Assert.assertEquals(
				"getData should return a list of the selected options when setting a list",
				SELECTED_A_B,
				multi.getData());

		// Data is an empty array
		multi.setData(EMPTY_ARRAY);
		Assert.assertNull("getData should return null when setting empty array", multi.getData());

		// Data is an array
		multi.setData(ARRAY_OPTIONS_A_B);
		Assert.assertEquals(
				"getData should return a list of the selected options when setting an array",
				SELECTED_A_B,
				multi.getData());

		// Data is an Option
		multi.setData(OPTION_B);
		Assert.assertEquals("getValue for Object data should be the selected option in a list",
				SELECTED_B,
				multi.getValue());
	}

	@Test
	public void testSetDataEditable() {
		List<String> userTextList = new ArrayList<>(Arrays.asList("USERTEXT"));
		List<String> userTextList2 = new ArrayList<>(Arrays.asList("USERTEXT2"));

		// Editable List
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		multi.setEditable(true);

		// Set Default "User Text"
		multi.setData("USERTEXT");
		Assert.assertEquals("Should be user text selected", userTextList, multi.getData());

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Should be user text selected in uic", userTextList, multi.getData());

		// Set "User Text2"
		multi.setData("USERTEXT2");
		Assert.assertEquals("Should be usertext2 selected in uic", userTextList2, multi.getData());

		resetContext();
		Assert.assertEquals("Should be usertext selected", userTextList, multi.getData());

		// Set OptionA
		setActiveContext(createUIContext());
		multi.setData(SELECTED_A);
		Assert.assertEquals("Should be optionA selected in uic", SELECTED_A, multi.getData());

		resetContext();
		Assert.assertEquals("Should be usertext selected", userTextList, multi.getData());
	}

	@Test
	public void testSetDataEditableNoOptions() {
		List<String> userTextList = new ArrayList<>(Arrays.asList("USERTEXT"));
		List<String> userTextList2 = new ArrayList<>(Arrays.asList("USERTEXT2"));

		// ===================
		// Null Options
		AbstractWMultiSelectList multi = new MyWMultiSelectList(null, true);
		multi.setEditable(true);

		// Set Default "User Text"
		multi.setData("USERTEXT");
		Assert.assertEquals("Null Options - Should be user text selected", userTextList, multi.
				getData());

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Null Options - Should be user text selected in uic", userTextList,
				multi.getData());

		// Set "User Text2"
		multi.setData("USERTEXT2");
		Assert.assertEquals("Null Options - Should be usertext2 selected in uic", userTextList2,
				multi.getData());

		resetContext();
		Assert.assertEquals("Null Options - Should be usertext selected", userTextList, multi.
				getData());

		// ===================
		// Empty Options
		multi = new MyWMultiSelectList(EMPTY_LIST, true);
		multi.setEditable(true);

		// Set Default "User Text"
		multi.setData("USERTEXT");
		Assert.assertEquals("Null Options - Should be user text selected", userTextList, multi.
				getData());

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Null Options - Should be user text selected in uic", userTextList,
				multi.getData());

		// Set "User Text2"
		multi.setData("USERTEXT2");
		Assert.assertEquals("Null Options - Should be usertext2 selected in uic", userTextList2,
				multi.getData());

		resetContext();
		Assert.assertEquals("Null Options - Should be usertext selected", userTextList, multi.
				getData());
	}

	@Test
	public void testSetDataGroupOption() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS_WITH_GROUPS, true);

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Null Option
		multi.setData(null);
		Assert.assertNull("Group Options - getData should be null", multi.getData());

		// OptionA
		multi.setData(OPTION_A);
		Assert.assertEquals("Group Options - getData should be optionA", OPTION_A, ((List<?>) multi.
				getData()).get(0));

		// OptionB
		multi.setData(OPTION_B);
		Assert.assertEquals("Group Options - getData should be optionB", OPTION_B, ((List<?>) multi.
				getData()).get(0));

		// OptionC
		multi.setData(OPTION_C);
		Assert.assertEquals("Group Options - getData should be optionC", OPTION_C, ((List<?>) multi.
				getData()).get(0));

		// Option Null
		multi.setData(OPTION_NULL);
		Assert.assertNull("Group Options - getData should be null for null option", multi.getData());

		// Invalid option
		try {
			multi.setData(OPTION_INVALID);
			Assert.fail("Should have thrown an exception for an invalid option");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}
	}

	@Test
	public void testDoHandleRequest() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Set selected to OptionA
		multi.setSelected(SELECTED_A);

		// Setup Single Option Request with optionB
		MockRequest request = setupSingleOptionRequest(multi, OPTION_B);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertTrue("doHandleRequest should have returned true for request with optionB",
				changed);
		Assert.assertEquals("Selected should have changed to optionB", SELECTED_B, multi.
				getSelected());

		// Setup Multi Option Request with optionA and optionC
		request = setupMultiOptionRequest(multi, new String[]{OPTION_A, OPTION_C});
		changed = multi.doHandleRequest(request);
		Assert.assertTrue(
				"doHandleRequest should have returned true for request with optionA and OptionC",
				changed);
		Assert.assertEquals("Selected should have changed to optionA", OPTION_A,
				multi.getSelected().get(0));
		Assert.assertEquals("Selected should have changed to optionC", OPTION_C,
				multi.getSelected().get(1));
	}

	@Test
	public void testDoHandleRequestChange() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Test Nothing Selected and Empty Request (No Change)
		setActiveContext(createUIContext());
		MockRequest request = setupNothingSelectedRequest(multi);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", EMPTY_LIST, multi.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);

		// Setup Request with optionA (Change)
		request = setupSingleOptionRequest(multi, OPTION_A);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", SELECTED_A, multi.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionA (No Change)
		request = setupSingleOptionRequest(multi, OPTION_A);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", SELECTED_A, multi.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);

		// Setup Request with optionA, optionB (Change)
		request = setupMultiOptionRequest(multi, ARRAY_OPTIONS_A_B);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA and optionB", SELECTED_A_B, multi.
				getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Empty Request (Change)
		request = setupNothingSelectedRequest(multi);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", EMPTY_LIST, multi.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestBeanBound() {

		// Set action on change
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		// Set Bean Property
		multi.setBeanProperty("myOptions");

		WBeanContainer beanContainer = new WBeanContainer();
		beanContainer.add(multi);

		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Set a Bean that has no selections
		MyBean bean = new MyBean();
		beanContainer.setBean(bean);

		// Test Nothing Selected and Empty Request (No Change)
		MockRequest request = setupNothingSelectedRequest(multi);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", EMPTY_LIST, multi.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);

		// Setup Request with optionA (Change)
		request = setupSingleOptionRequest(multi, OPTION_A);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", SELECTED_A, multi.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionA (No Change)
		request = setupSingleOptionRequest(multi, OPTION_A);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", SELECTED_A, multi.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);

		// Update Bean (should have optionA on the bean)
		multi.updateBeanValue();
		Assert.assertEquals("Bean should contain optionA", SELECTED_A, bean.getMyOptions());

		// Clear Context so gets value from the bean
		multi.reset();
		Assert.assertEquals("Selected option should be optionA", SELECTED_A, multi.getSelected());

		// Change Bean to optionB (Make sure the value is coming from the bean)
		bean.setMyOptions(SELECTED_B);
		Assert.assertEquals("Selected should be optionB coming from the bean", SELECTED_B, multi.
				getSelected());

		// Setup Request with optionA, optionB (Change)
		request = setupMultiOptionRequest(multi, ARRAY_OPTIONS_A_B);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA and optionB", SELECTED_A_B, multi.
				getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Empty Request (Change)
		request = setupNothingSelectedRequest(multi);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", EMPTY_LIST, multi.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestLegacyMatching() {
		Boolean optionA = Boolean.TRUE;
		Boolean optionB = null;
		Boolean optionC = Boolean.FALSE;
		List<Boolean> options = Arrays.asList(optionA, optionB, optionC);

		List<String> selectedLegacy = Arrays.asList("false");

		// Set action on change
		AbstractWMultiSelectList multi = new MyWMultiSelectList(options, true);

		// Set selected to OptionA - "String Representation" (Legacy String match)
		// Get value from the bean (nut use setData)
		multi.setBeanProperty(".");

		multi.setLocked(true);
		setActiveContext(createUIContext());

		multi.setBean(selectedLegacy);

		// Setup Request with optionC (No Change)
		MockRequest request = setupSingleOptionRequest(multi, optionC);
		boolean changed = multi.doHandleRequest(request);
		// Action should not trigger but selected should now be the correct option
		Assert.assertEquals("Selected should have changed to optionA", optionC, multi.getSelected().
				get(0));
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestEditable() {
		// Set action on change
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		// Set Editable
		multi.setEditable(true);

		// Set selected to OptionA
		multi.setSelected(SELECTED_A);

		// Setup Request with "user entered text"
		String userText = "user entered text";
		setActiveContext(createUIContext());
		MockRequest request = setupNothingSelectedRequest(multi);
		request.setParameter(multi.getId(), userText);

		boolean changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected should have changed to user text", userText, multi.
				getSelected().get(0));
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestWithNullOption() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS_WITH_NULL, true);

		// Set selected to OptionA
		multi.setSelected(SELECTED_A);

		// Setup Request with null option
		MockRequest request = setupSingleOptionRequest(multi, OPTION_NULL);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should have changed to null", SELECTED_NULL, multi.
				getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestWithGroupOptions() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS_WITH_GROUPS, true);

		// Setup Request with optionA
		MockRequest request = setupSingleOptionRequest(multi, OPTION_A);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", OPTION_A, multi.getSelected().
				get(0));
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionB
		request = setupSingleOptionRequest(multi, OPTION_B);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionB", OPTION_B, multi.getSelected().
				get(0));
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionC
		request = setupSingleOptionRequest(multi, OPTION_C);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionC", OPTION_C, multi.getSelected().
				get(0));
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with option null
		request = setupSingleOptionRequest(multi, OPTION_NULL);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be null option", OPTION_NULL, multi.
				getSelected().get(0));
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestNothingSelected() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		// Setup Option Request with nothing selected
		MockRequest request = setupMultiOptionRequest(multi, null);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", EMPTY_LIST, multi.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestNothingSelectedWithNullOption() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS_WITH_NULL, true);

		// Setup Option Request with nothing selected
		MockRequest request = setupMultiOptionRequest(multi, null);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", EMPTY_LIST, multi.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestWithInvalidOption() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		// Set selected to OptionA
		multi.setSelected(SELECTED_A);

		// Setup Request with Invalid Option
		MockRequest request = setupNothingSelectedRequest(multi);
		request.setParameter(multi.getId(), OPTION_INVALID);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", SELECTED_A, multi.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestWithEditableUserText() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		// Editable list
		multi.setEditable(true);

		// Set selected to OptionA
		multi.setSelected(SELECTED_A);

		// Setup Request with UserText
		String userText = "USERTEXT";
		MockRequest request = setupNothingSelectedRequest(multi);
		request.setParameter(multi.getId(), userText);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should be user text", userText, multi.getSelected().
				get(0));
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionB
		request = setupSingleOptionRequest(multi, OPTION_B);
		changed = multi.doHandleRequest(request);
		Assert.assertEquals("Selected option should have changed to optionB", SELECTED_B, multi.
				getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testGetRequestValue() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		// Set selected to OptionA
		multi.setSelected(SELECTED_A);

		// Empty Request, should return OptionA
		MockRequest request = new MockRequest();
		Assert.assertEquals(
				"getRequestValue should return the current selected option for an empty request",
				SELECTED_A, multi.getRequestValue(request));

		// OptionB on the Request
		request = setupSingleOptionRequest(multi, OPTION_B);
		Assert.assertEquals("getRequestValue should return the option on the request", SELECTED_B,
				multi.getRequestValue(request));
	}

	@Test
	public void testGetNewSelection() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);

		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Empty Request
		MockRequest request = new MockRequest();
		Assert.
				assertTrue("new selection should be empty", multi.getNewSelections(request).
						isEmpty());

		// Empty Value On Request
		request = new MockRequest();
		request.setParameter(multi.getId(), "");
		Assert.
				assertTrue("new selection should be empty", multi.getNewSelections(request).
						isEmpty());

		// Valid item on request
		request = setupSingleOptionRequest(multi, OPTION_B);
		Assert.assertEquals("new selection should return optionB", SELECTED_B, multi.
				getNewSelections(request));

		// Invalid item on request (should return the current value)
		multi.setSelected(SELECTED_A);
		request.setParameter(multi.getId(), OPTION_INVALID);
		Assert.assertEquals("new selection should return optionA", SELECTED_A, multi.
				getNewSelections(request));

		// -------------
		// Null Options
		multi.setOptions((List<?>) null);
		// Request with any value
		request = setupNothingSelectedRequest(multi);
		request.setParameter(multi.getId(), "any value");
		Assert.assertTrue("result should be empty when null options", multi.
				getNewSelections(request).isEmpty());

		// -------------
		// Empty Options
		multi.setOptions(EMPTY_LIST);
		// Request with a value
		request = setupNothingSelectedRequest(multi);
		request.setParameter(multi.getId(), "any value");
		Assert.assertTrue("result should be empty when options empty", multi.getNewSelections(
				request).isEmpty());

		// -------------
		// Editable
		multi.setOptions(OPTIONS);
		multi.setEditable(true);
		request = setupSingleOptionRequest(multi, OPTION_B);
		Assert.assertEquals("new selection should return optionB", SELECTED_B, multi.
				getNewSelections(request));

		// UserText
		request.setParameter(multi.getId(), "usertext");
		Assert.assertEquals("new selection should return user text", Arrays.asList("usertext"),
				multi.getNewSelections(request));

		// Null Options
		multi.setOptions((List<?>) null);
		Assert.assertEquals("new selection should return user text", Arrays.asList("usertext"),
				multi.getNewSelections(request));

		// Empty Options
		multi.setOptions(EMPTY_LIST);
		Assert.assertEquals("new selection should return user text", Arrays.asList("usertext"),
				multi.getNewSelections(request));

	}

	@Test
	public void testUpdateBeanDefaultFirstOption() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, false);
		// Set Bean Property
		multi.setBeanProperty("myOptions");

		// With User Context
		multi.setLocked(true);
		setActiveContext(createUIContext());

		// Set Bean Property and Bean
		MyBean bean = new MyBean();
		multi.setBean(bean);

		// Null Bean Value - getValue will default to first Option
		Assert.assertEquals("Data should default to OptionA", SELECTED_A, multi.getValue());

		// Test handle request is "no change"
		MockRequest request = setupSingleOptionRequest(multi, OPTION_A);
		boolean changed = multi.doHandleRequest(request);
		Assert.assertFalse("doHandleRequest should return false as option should not change",
				changed);

		// Update Bean (should have optionA on the bean)
		Assert.assertNull("Bean should still be null", bean.getMyOptions());
		multi.updateBeanValue();
		Assert.assertEquals("Bean should contain optionA", SELECTED_A, bean.getMyOptions());
	}

	@Test
	public void testMinSelectAccessors() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, false);
		assertAccessorsCorrect(multi, "minSelect", 0, 1, 2);
	}

	@Test
	public void testMaxSelectAccessors() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, false);
		assertAccessorsCorrect(multi, "maxSelect", 0, 1, 2);
	}

	@Test
	public void testValidateMaxSelect() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		multi.setLocked(true);

		int valueMinusOne = 1;
		int value = 2;
		int valuePlusOne = 3;

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("None Selected with no maximum set should be valid", diags.isEmpty());

		multi.setSelected(SELECTED_A_B);

		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("Selected with no maximum set should be valid", diags.isEmpty());

		multi.reset();
		multi.setMaxSelect(value);
		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("None Selected with maximum set should be valid", diags.isEmpty());

		multi.setSelected(SELECTED_A_B);

		multi.setMaxSelect(valuePlusOne);
		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("Selected is less than maximum so should be valid", diags.isEmpty());

		multi.setMaxSelect(value);
		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("Selected is the same as maximum so should be valid", diags.isEmpty());

		multi.setMaxSelect(valueMinusOne);
		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertFalse("Selected is larger than maximum so should be invalid", diags.isEmpty());
	}

	@Test
	public void testValidateMinSelect() {
		AbstractWMultiSelectList multi = new MyWMultiSelectList(OPTIONS, true);
		multi.setLocked(true);

		int valueMinusOne = 1;
		int value = 2;
		int valuePlusOne = 3;

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("None Selected with no minimum set should be valid", diags.isEmpty());

		multi.setSelected(SELECTED_A_B);
		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("Selected with no minimum set should be valid", diags.isEmpty());

		multi.reset();
		multi.setMinSelect(value);
		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("None Selected value with minimum set should be valid", diags.isEmpty());

		multi.setSelected(SELECTED_A_B);

		multi.setMinSelect(valueMinusOne);
		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("Selected is greater than minimum so should be valid", diags.isEmpty());

		multi.setMinSelect(value);
		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertTrue("Selected is the same as minimum so should be valid", diags.isEmpty());

		multi.setMinSelect(valuePlusOne);
		diags = new ArrayList<>();
		multi.validate(diags);
		Assert.assertFalse("Selected is less than minimum so should be invalid", diags.isEmpty());
	}

	@Test
	public void testMatchingDuplicateText() {
		MyObject[] options = {
			new MyObject("1", "Desc"),
			new MyObject("2", "Desc")};

		AbstractWMultiSelectList list = new MyWMultiSelectList(Arrays.asList(options), true);

		list.setSelected(Arrays.asList(new Object[]{new MyObject("2", "Desc")}));
		Assert.assertEquals("Incorrect selected option", options[1].code, ((MyObject) list.
				getSelectedOptionsAsArray()[0]).code);

		list.setSelected(Arrays.asList(new Object[]{"Desc"}));
		Assert.assertEquals("Incorrect selected option", options[0].code, ((MyObject) list.
				getSelectedOptionsAsArray()[0]).code);
	}

	/**
	 * Test object for matching options.
	 */
	private static final class MyObject {

		/**
		 * Code value.
		 */
		private final String code;
		/**
		 * Description value.
		 */
		private final String desc;

		/**
		 * @param code the code
		 * @param desc the description
		 */
		MyObject(final String code, final String desc) {
			this.code = code;
			this.desc = desc;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object o) {
			return ((o instanceof MyObject) && ((MyObject) o).code.equals(code));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return code.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return desc;
		}
	}

	/**
	 * @param target the target component
	 * @param option the option to select on the request
	 * @return a mock request with a single option selected
	 */
	private MockRequest setupSingleOptionRequest(final AbstractWMultiSelectList target,
			final Object option) {
		MockRequest request = new MockRequest();

		if (target.isAllowNoSelection()) {
			request.setParameter(target.getId() + "-h", "x");
		}

		request.setParameter(target.getId(), target.optionToCode(option));

		return request;
	}

	/**
	 * @param target the target component
	 * @param options the options to select on the request
	 * @return a mock request with multiple options selected
	 */
	private MockRequest setupMultiOptionRequest(final AbstractWMultiSelectList target,
			final String[] options) {
		MockRequest request = new MockRequest();

		if (target.isAllowNoSelection()) {
			request.setParameter(target.getId() + "-h", "x");
		}

		if (options != null) {
			String[] codes = new String[options.length];
			for (int i = 0; i < options.length; i++) {
				codes[i] = target.optionToCode(options[i]);
			}
			request.setParameter(target.getId(), codes);
		}

		return request;
	}

	/**
	 * @param target the target component
	 * @return a mock request with nothing selected
	 */
	private MockRequest setupNothingSelectedRequest(final AbstractWMultiSelectList target) {
		MockRequest request = new MockRequest();

		if (target.isAllowNoSelection()) {
			request.setParameter(target.getId() + "-h", "x");
		}

		return request;
	}

	/**
	 * Test class for AbstractWMultiSelectList.
	 */
	private static final class MyWMultiSelectList extends AbstractWMultiSelectList {

		/**
		 * @param options the list's options.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MyWMultiSelectList(final List<?> options, final boolean allowNoSelection) {
			super(options, allowNoSelection);
		}

		/**
		 * @param lookupTable the lookup table identifier to obtain the list's options from.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MyWMultiSelectList(final Object lookupTable, final boolean allowNoSelection) {
			super(lookupTable, allowNoSelection);
		}
	}

	/**
	 * Test bean.
	 */
	public static class MyBean {

		/**
		 * Selected options.
		 */
		private List<String> myOptions;

		/**
		 * @return the myOptions
		 */
		public List<String> getMyOptions() {
			return myOptions;
		}

		/**
		 * @param myOptions the myOptions to set
		 */
		public void setMyOptions(final List<String> myOptions) {
			this.myOptions = myOptions;
		}
	}
}
