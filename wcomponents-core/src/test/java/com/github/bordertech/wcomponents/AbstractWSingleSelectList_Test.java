package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * AbstractWSingleSelectList_Test - unit tests for {@link AbstractWSingleSelectList}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractWSingleSelectList_Test extends AbstractWComponentTestCase {

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
	 * First default option.
	 */
	private static final String SELECTED_FIRST_OPTION = "A";

	/**
	 * Empty list.
	 */
	private static final List<?> EMPTY_LIST = Collections.EMPTY_LIST;

	/**
	 * Test group 1.
	 */
	private static final OptionGroup GROUP1 = new OptionGroup("Group1",
			Arrays.asList(OPTION_A, "e", "f"));
	/**
	 * Test group 2.
	 */
	private static final OptionGroup GROUP2 = new OptionGroup("Group2",
			Arrays.asList("x", OPTION_B, "z"));
	/**
	 * Group Options List.
	 */
	private static final List<Serializable> OPTIONS_WITH_GROUPS = Arrays.asList(OPTION_C, GROUP1,
			"Two",
			GROUP2, OPTION_NULL, "Three");

	@Test
	public void testConstructor1() {
		// Constructor - 1
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		List<?> result = single.getOptions();
		Assert.assertEquals("Incorrect options returned", OPTIONS, result);
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());

		single = new MyWSingleSelectList(OPTIONS, false);
		Assert.assertFalse("allowNoSelection should be false", single.isAllowNoSelection());
	}

	@Test
	public void testConstructor2() {
		// Constructor - 2
		AbstractWSingleSelectList single = new MyWSingleSelectList(DayOfWeekTable.class, true);
		Assert.assertEquals("Incorrect table returned", DayOfWeekTable.class, single.
				getLookupTable());
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());

		single = new MyWSingleSelectList(DayOfWeekTable.class, false);
		Assert.assertFalse("allowNoSelection should be false", single.isAllowNoSelection());
	}

	@Test
	public void testGetValueAsString() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		single.setSelected(null);
		Assert.assertNull("Value as String should be null", single.getValueAsString());

		// Select optionA
		single.setSelected(OPTION_A);
		Assert.
				assertEquals("Value as String should be OptionA", OPTION_A, single.
						getValueAsString());
	}

	@Test
	public void testGetValueAsStringEmptyOptions() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(new ArrayList<String>(), true);
		Assert.assertNull("Value as String should be null - empty list to select from", single.
				getValueAsString());
	}

	@Test
	public void testGetValueAsStringNullOptions() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(null, true);
		Assert.assertNull("Value as String should be null - null to select from", single.
				getValueAsString());
	}

	@Test
	public void testSelectedAccessors() {
		assertAccessorsCorrect(new MyWSingleSelectList(OPTIONS, true), "selected", null, OPTION_A,
				OPTION_B);
	}

	@Test
	public void testGetValueConvertDataToList() {
		// =======================
		// ALLOW NONE - TRUE

		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		// Retrieve the value from the bean (so not setting via setData)
		single.setBeanProperty(".");

		single.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		single.setBean(null);
		Assert.assertNull("Allow None - getValue for null data should be null", single.getValue());

		// Data is an Option
		single.setBean(OPTION_B);
		Assert.assertEquals("Allow None - getValue for Object data should be the selected option",
				OPTION_B,
				single.getValue());

		resetContext();

		// =======================
		// ALLOW NONE - FALSE
		single = new MyWSingleSelectList(OPTIONS, false);

		// Retrieve the value from the bean (so not setting via setData)
		single.setBeanProperty(".");

		single.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		single.setBean(null);
		Assert.assertEquals("No Allow None - getValue for null data should be the first option",
				SELECTED_FIRST_OPTION,
				single.getValue());

		// Data is an Option
		single.setBean(OPTION_B);
		Assert.assertEquals("getValue for Object data should be the selected optionB", OPTION_B,
				single.getValue());

	}

	@Test
	public void testGetValueWithNoOptionsNothingSelected() {
        // =======================
		// ALLOW NONE - TRUE

		// Null Options
		AbstractWSingleSelectList single = new MyWSingleSelectList(null, true);
		Assert.assertNull("Allow none - Null Options - should be null selected", single.getValue());

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertNull("Allow none - Null Options -  should be null selected with uic", single.
				getValue());
		resetContext();

		// Empty Options
		single = new MyWSingleSelectList(new ArrayList<>(), true);
		Assert.
				assertNull("Allow none - Empty Options -  should be null selected", single.
						getValue());
		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertNull("Allow none - Empty Options -  should be null selected with uic", single.
				getValue());
		resetContext();

		// =======================
		// ALLOW NONE - FALSE
		// Null Options
		single = new MyWSingleSelectList(null, false);
		Assert.assertNull("No Allow none - Null Options -  should be null selected", single.
				getValue());
		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertNull("No Allow none - Null Options -  should be null selected with uic",
				single.getValue());
		resetContext();

		// Empty Options
		single = new MyWSingleSelectList(new ArrayList<>(), false);
		Assert.assertNull("No Allow none - Empty Options -  should be null selected", single.
				getValue());
		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertNull("No Allow none - Empty Options -  should be null selected with uic",
				single.getValue());
		resetContext();
	}

	@Test
	public void testGetValueWithOptionsNothingSelected() {
		// =======================
		// ALLOW NONE - TRUE

		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		// Nothing selected - Should return empty
		Assert.assertNull("Allow none - Nothing selected - should be null selected", single.
				getValue());
		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertNull("Allow none - Nothing selected - should be null selected with uic",
				single.getValue());
		resetContext();

		// =======================
		// ALLOW NONE - FALSE
		single = new MyWSingleSelectList(OPTIONS, false);
		// Should be the first option
		Assert.assertEquals("No Allow None - Nothing Selected - should be optionA selected",
				SELECTED_FIRST_OPTION,
				single.getValue());
		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.
				assertEquals(
						"No Allow None - Nohting Selected - should be optionA selected with uic",
						SELECTED_FIRST_OPTION, single.getValue());
	}

	@Test
	public void testGetValueWithDefaultSet() {
		// =======================
		// ALLOW NONE - TRUE

		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);
		// Set OptionC as default
		single.setSelected(OPTION_C);
		// Should return optionC
		Assert.assertEquals("Allow None - should be optionC selected", OPTION_C, single.getValue());

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		Assert.assertEquals("Allow None - should be optionC selected with uic", OPTION_C, single.
				getValue());

		// Set optionB on the user context
		single.setSelected(OPTION_B);
		Assert.assertEquals("Allow None - should be optionB selected with uic", OPTION_B, single.
				getValue());

		resetContext();
		Assert.assertEquals("Allow None - should be optionC selected", OPTION_C, single.getValue());

		// =======================
		// ALLOW NONE - FALSE
		single = new MyWSingleSelectList(OPTIONS, false);
		// Set OptionC as default
		single.setSelected(OPTION_C);
		// Should return optionC
		Assert.assertEquals("No Allow None - should be optionC selected", OPTION_C, single.
				getValue());

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		Assert.assertEquals("No Allow None - should be optionC selected with uic", OPTION_C, single.
				getValue());

		// Set OptionB on the user context
		single.setSelected(OPTION_B);
		Assert.assertEquals("No Allow None - should be optionB selected with uic", OPTION_B, single.
				getValue());

		resetContext();
		Assert.assertEquals("No Allow None - should be optionC selected", OPTION_C, single.
				getValue());
	}

	@Test
	public void testGetValueDefaultWithNullOption() {

		// =======================
		// Allow None - TRUE
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS_WITH_NULL, true);

		// Should return empty
		Assert.assertNull("Allow None - Selected should be null", single.getValue());
		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertNull("Allow None - Selected should be null with uic", single.getValue());
		resetContext();

		// =======================
		// Allow None - FALSE
		single = new MyWSingleSelectList(OPTIONS_WITH_NULL, false);

		// Should return null option
		Assert.assertNull("No Allow None - Selected should be null", single.getValue());

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertNull("No Allow None - Selected should be null with uic", single.getValue());
		resetContext();
	}

	@Test
	public void testGetValueWithBean() {
		// =======================
		// Allow None - TRUE

		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);
		// Set Bean Property
		single.setBeanProperty(".");

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		// Null Bean Value
		Assert.assertNull("Allow None - Data should be null when bean value is null", single.
				getValue());

		// Valid Bean Value
		single.setBean(OPTION_B);
		Assert.assertEquals("Allow None - Data should be OptionA from bean", OPTION_B, single.
				getValue());

		// Invalid Bean Value
		single.setBean(OPTION_INVALID);
		try {
			single.getValue();
			Assert.fail("Allow None - Exception should have been thrown for invalid option on bean");
		} catch (IllegalStateException e) {
			Assert.assertNotNull(
					"Allow None - No exception message provided for invalid option on bean",
					e.getMessage());
		}
		resetContext();

		// =======================
		// Allow None - FALSE
		single = new MyWSingleSelectList(OPTIONS, false);
		// Set Bean Property
		single.setBeanProperty(".");

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		// Null Bean Value
		Assert.assertEquals("No Allow None - Data should default to OptionA", OPTION_A, single.
				getValue());

		// Valid Bean Value
		single.setBean(OPTION_B);
		Assert.assertEquals("No Allow None - Data should be OptionB from bean", OPTION_B, single.
				getValue());

		// Invalid Bean Value
		single.setBean(OPTION_INVALID);
		try {
			single.getValue();
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
		// Editable List
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);
		single.setEditable(true);
		// Set Bean Property
		single.setBeanProperty(".");

		single.setLocked(true);
		setActiveContext(createUIContext());

		single.setBean("USERTEXT");
		Assert.assertEquals("Should be user text selected", "USERTEXT", single.getValue());

		// ===============
		// Null Options
		single.setOptions((List<?>) null);
		Assert.assertEquals("Should be user text selected", "USERTEXT", single.getValue());

		// ===============
		// Empty Options
		single.setOptions(EMPTY_LIST);
		Assert.assertEquals("Should be user text selected", "USERTEXT", single.getValue());
	}

	@Test
	public void testGetData() {

		// =======================
		// ALLOW NONE - TRUE
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		// Retrieve the value from the bean (so not setting via setData)
		single.setBeanProperty(".");

		single.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		single.setBean(null);
		Assert.assertNull("Allow None - getData for null data should be null", single.getData());

		// Data is an Option
		single.setBean(OPTION_B);
		Assert.assertEquals("Allow None - getData for Object data should be the selected option",
				OPTION_B,
				single.getData());

		resetContext();

		// =======================
		// ALLOW NONE - FALSE
		single = new MyWSingleSelectList(OPTIONS, false);

		// Retrieve the value from the bean (so not setting via setData)
		single.setBeanProperty(".");

		single.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		single.setBean(null);
		Assert.assertEquals("No Allow None - getData for null list data should be the first option",
				SELECTED_FIRST_OPTION, single.getData());

		// Data is an Option
		single.setBean(OPTION_B);
		Assert.
				assertEquals("No Allow None - getData for Object data should be the Object",
						OPTION_B, single.getData());

		// =======================
		// Include "null" as an option
		single.setOptions(OPTIONS_WITH_NULL);

		// Data is null
		single.setBean(null);
		Assert.
				assertNull(
						"No Allow None - Null is an option - getData for null data should be null",
						single.getData());

		// =======================
		// No Options
		single.setOptions((List<?>) null);

		// Data is null
		single.setBean(null);
		Assert.assertNull("No Allow None - No options - getData for null data should be null",
				single.getData());

		// Data is an Option
		single.setBean(OPTION_B);
		Assert.assertEquals(
				"No Allow None - No options -  getData for Object data should be the Object",
				OPTION_B,
				single.getData());

	}

	@Test
	public void testSetData() {
		// Null options
		AbstractWSingleSelectList single = new MyWSingleSelectList(null, true);

		try {
			single.setData(OPTION_A);
			Assert.fail("List has null options so should have thrown exception");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}

		// Empty Options
		single.setOptions(EMPTY_LIST);
		try {
			single.setData(OPTION_A);
			Assert.fail("List has empty options so should have thrown exception");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}

		// Set options
		single.setOptions(OPTIONS);

		// Set valid option
		single.setData(OPTION_A);
		Assert.assertEquals("Should be optionA selected", OPTION_A, single.getSelected());

		// Set invalid option
		try {
			single.setData(OPTION_INVALID);
			Assert.fail("Should have thrown exception for setting an option that does not match");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}

		// Set invalid option type (wrong class)
		try {
			single.setData(Boolean.TRUE);
			Assert.fail(
					"Should have thrown exception for setting a boolean when options are strings");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}
	}

	@Test
	public void testSetDataConvertToList() {
		// Null options and allow no selection
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		single.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		single.setData(null);
		Assert.assertNull("getData should return null when setting null", single.getData());

		// Data is an Option
		single.setData(OPTION_B);
		Assert.assertEquals("getValue for Object data should be the selected option", OPTION_B,
				single.getValue());
	}

	@Test
	public void testSetDataEditable() {
		String userText1 = "UserText1";
		String userText2 = "UserText2";

		// Editable List
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);
		single.setEditable(true);

		// Set Default "User Text"
		single.setData(userText1);
		Assert.assertEquals("Should be user text selected", userText1, single.getData());

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Should be user text selected in uic", userText1, single.getData());

		// Set "User Text2"
		single.setData(userText2);
		Assert.assertEquals("Should be usertext2 selected in uic", userText2, single.getData());

		resetContext();
		Assert.assertEquals("Should be usertext selected", userText1, single.getData());

		// Set OptionA
		setActiveContext(createUIContext());
		single.setData(OPTION_A);
		Assert.assertEquals("Should be optionA selected in uic", OPTION_A, single.getData());

		resetContext();
		Assert.assertEquals("Should be usertext selected", userText1, single.getData());

	}

	@Test
	public void testSetDataEditableWithNoOptions() {
		String userText1 = "UserText1";
		String userText2 = "UserText2";

		// ===================
		// Null Options
		AbstractWSingleSelectList single = new MyWSingleSelectList(null, true);
		single.setEditable(true);

		// Set Default "User Text"
		single.setData(userText1);
		Assert.assertEquals("Null Options - Should be user text selected", userText1, single.
				getData());

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Null Options - Should be user text selected in uic", userText1, single.
				getData());

		// Set "User Text2"
		single.setData(userText2);
		Assert.assertEquals("Null Options - Should be usertext2 selected in uic", userText2, single.
				getData());

		resetContext();
		Assert.assertEquals("Null Options - Should be usertext selected", userText1, single.
				getData());

		// ===================
		// Empty Options
		single = new MyWSingleSelectList(EMPTY_LIST, true);
		single.setEditable(true);

		// Set Default "User Text"
		single.setData(userText1);
		Assert.assertEquals("Empty Options - Should be user text selected", userText1, single.
				getData());

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Empty Options - Should be user text selected in uic", userText1,
				single.getData());

		// Set "User Text2"
		single.setData(userText2);
		Assert.assertEquals("Empty Options - Should be usertext2 selected in uic", userText2,
				single.getData());

		resetContext();
		Assert.assertEquals("Empty Options - Should be usertext selected", userText1, single.
				getData());
	}

	@Test
	public void testSetDataGroupOption() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS_WITH_GROUPS, true);

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		// Null Option
		single.setData(null);
		Assert.assertNull("Group Options - getData should be null", single.getData());

		// OptionA
		single.setData(OPTION_A);
		Assert.assertEquals("Group Options - getData should be optionA", OPTION_A, single.getData());

		// OptionB
		single.setData(OPTION_B);
		Assert.assertEquals("Group Options - getData should be optionB", OPTION_B, single.getData());

		// OptionC
		single.setData(OPTION_C);
		Assert.assertEquals("Group Options - getData should be optionC", OPTION_C, single.getData());

		// Option Null
		single.setData(OPTION_NULL);
		Assert.assertNull("Group Options - getData should be null for optionD", single.getData());

		// Invalid option
		try {
			single.setData(OPTION_INVALID);
			Assert.fail("Should have thrown an exception for an invalid option");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("No exception message provided", e.getMessage());
		}
	}

	@Test
	public void testDoHandleRequest() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		// Set selected to OptionA
		single.setSelected(OPTION_A);

		// Setup Single Option Request with optionB
		MockRequest request = setupRequest(single, OPTION_B);
		boolean changed = single.doHandleRequest(request);
		Assert.assertTrue("doHandleRequest should have returned true for request with optionB",
				changed);
		Assert.assertEquals("Selected should have changed to optionB", OPTION_B, single.
				getSelected());
	}

	@Test
	public void testDoHandleRequestChange() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		// Test Nothing Selected and Empty Request (No Change)
		setActiveContext(createUIContext());
		MockRequest request = setupNothingSelectedRequest(single);
		boolean changed = single.doHandleRequest(request);
		Assert.assertNull("Should have no option selected", single.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);

		// Setup Request with optionA (Change)
		request = setupRequest(single, OPTION_A);
		changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", OPTION_A, single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionA (No Change)
		request = setupRequest(single, OPTION_A);
		changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", OPTION_A, single.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);

		// Setup Request with optionB (Change)
		request = setupRequest(single, OPTION_B);
		changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionB", OPTION_B, single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Empty Request (Change)
		request = setupNothingSelectedRequest(single);
		changed = single.doHandleRequest(request);
		Assert.assertNull("Should have no option selected", single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestBeanBound() {

		// Set action on change
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);
		// Set Bean Property
		single.setBeanProperty("myOption");

		WBeanContainer beanContainer = new WBeanContainer();
		beanContainer.add(single);

		single.setLocked(true);
		setActiveContext(createUIContext());

		// Set a Bean that has no selections
		MyBean bean = new MyBean();
		beanContainer.setBean(bean);

		// Test Nothing Selected and Empty Request (No Change)
		MockRequest request = setupNothingSelectedRequest(single);
		boolean changed = single.doHandleRequest(request);
		Assert.assertNull("Should have no option selected", single.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);

		// Setup Request with optionA (Change)
		request = setupRequest(single, OPTION_A);
		changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", OPTION_A, single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionA (No Change)
		request = setupRequest(single, OPTION_A);
		changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", OPTION_A, single.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);

		// Update Bean (should have optionA on the bean)
		single.updateBeanValue();
		Assert.assertEquals("Bean should contain optionA", OPTION_A, bean.getMyOption());

		// Clear Context so gets value from the bean
		single.reset();
		Assert.assertEquals("Selected option should be optionA", OPTION_A, single.getSelected());

		// Change Bean to optionB (Make sure the value is coming from the bean)
		bean.setMyOption(OPTION_B);
		Assert.assertEquals("Selected should be optionB coming from the bean", OPTION_B, single.
				getSelected());

		// Setup Request with optionC (Change)
		request = setupRequest(single, OPTION_C);
		changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionC", OPTION_C, single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Empty Request (Change)
		request = setupNothingSelectedRequest(single);
		changed = single.doHandleRequest(request);
		Assert.assertNull("Should have no option selected", single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestLegacyMatching() {
		Boolean optionA = Boolean.TRUE;
		Boolean optionB = null;
		Boolean optionC = Boolean.FALSE;
		List<Boolean> options = Arrays.asList(new Boolean[]{optionA, optionB, optionC});

		String selectedLegacy = "false";

		// Set action on change
		AbstractWSingleSelectList single = new MyWSingleSelectList(options, true);

		// Set selected to OptionA - "String Representation" (Legacy String match)
		// Get value from the bean (nut use setData)
		single.setBeanProperty(".");

		single.setLocked(true);
		setActiveContext(createUIContext());

		single.setBean(selectedLegacy);

		// Setup Request with optionC (No Change)
		MockRequest request = setupRequest(single, optionC);
		boolean changed = single.doHandleRequest(request);
		// Action should not trigger but selected should now be the correct option
		Assert.
				assertEquals("Selected should have changed to optionA", optionC, single.
						getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestEditable() {
		// Set action on change
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);
		// Set Editable
		single.setEditable(true);

		// Set selected to OptionA
		single.setSelected(OPTION_A);

		// Setup Request with "user entered text"
		String userText = "user entered text";
		setActiveContext(createUIContext());
		MockRequest request = setupNothingSelectedRequest(single);
		request.setParameter(single.getId(), userText);

		boolean changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected should have changed to user text", userText, single.
				getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestWithNullOption() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS_WITH_NULL, true);

		// Set selected to OptionA
		single.setSelected(OPTION_A);

		// Setup Request with null option
		MockRequest request = setupRequest(single, OPTION_NULL);
		boolean changed = single.doHandleRequest(request);
		Assert.
				assertNull("Selected option should have changed to null option", single.
						getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestWithGroupOptions() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS_WITH_GROUPS, true);

		// Setup Request with optionA
		MockRequest request = setupRequest(single, OPTION_A);
		boolean changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", OPTION_A, single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionB
		request = setupRequest(single, OPTION_B);
		changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionB", OPTION_B, single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionC
		request = setupRequest(single, OPTION_C);
		changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionC", OPTION_C, single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with option null
		request = setupRequest(single, OPTION_NULL);
		changed = single.doHandleRequest(request);
		Assert.assertNull("Selected option should be null optionD", single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestNothingSelected() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		// Setup Option Request with nothing selected
		MockRequest request = setupRequest(single, null);
		boolean changed = single.doHandleRequest(request);
		Assert.assertNull("Should have no option selected", single.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestNothingSelectedWithNullOption() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS_WITH_NULL, true);

		// Setup Option Request with nothing selected
		MockRequest request = setupRequest(single, null);
		boolean changed = single.doHandleRequest(request);
		Assert.assertNull("Should have no option selected", single.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestWithInvalidOption() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		// Set selected to OptionA
		single.setSelected(OPTION_A);

		// Setup Request with Invalid Option
		MockRequest request = setupNothingSelectedRequest(single);
		request.setParameter(single.getId(), OPTION_INVALID);
		boolean changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be optionA", OPTION_A, single.getSelected());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestWithEditableUserText() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);
		// Editable list
		single.setEditable(true);

		// Set selected to OptionA
		single.setSelected(OPTION_A);

		// Setup Request with UserText
		String userText = "USERTEXT";
		MockRequest request = setupNothingSelectedRequest(single);
		request.setParameter(single.getId(), userText);
		boolean changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should be user text", userText, single.getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);

		// Setup Request with optionB
		request = setupRequest(single, OPTION_B);
		changed = single.doHandleRequest(request);
		Assert.assertEquals("Selected option should have changed to optionB", OPTION_B, single.
				getSelected());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testGetRequestValue() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		// Set selected to OptionA
		single.setSelected(OPTION_A);

		// Empty Request, should return OptionA
		MockRequest request = new MockRequest();
		Assert.assertEquals(
				"getRequestValue should return the current selected option for an empty request",
				OPTION_A,
				single.getRequestValue(request));

		// OptionB on the Request
		request = setupRequest(single, OPTION_B);
		Assert.assertEquals("getRequestValue should return the option on the request", OPTION_B,
				single.getRequestValue(request));
	}

	@Test
	public void testGetNewSelection() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		single.setLocked(true);
		setActiveContext(createUIContext());

		// Empty Request
		MockRequest request = new MockRequest();
		Assert.assertNull("new selection should be null", single.getNewSelection(request));

		// Empty Value On Request
		request = new MockRequest();
		request.setParameter(single.getId(), "");
		Assert.assertNull("new selection should be empty", single.getNewSelection(request));

		// Valid item on request
		request = setupRequest(single, OPTION_B);
		Assert.assertEquals("new selection should return optionB", OPTION_B, single.getNewSelection(
				request));

		// Invalid item on request (should return the current value)
		single.setSelected(OPTION_A);
		request.setParameter(single.getId(), OPTION_INVALID);
		Assert.assertEquals("new selection should return optionA", OPTION_A, single.getNewSelection(
				request));

		// -------------
		// Null Options
		single.setOptions((List<?>) null);
		// Request with any value
		request = setupNothingSelectedRequest(single);
		request.setParameter(single.getId(), "any value");
		Assert.assertNull("result should be empty when null options", single.
				getNewSelection(request));

		// -------------
		// Empty Options
		single.setOptions(EMPTY_LIST);
		// Request with a value
		request = setupNothingSelectedRequest(single);
		request.setParameter(single.getId(), "any value");
		Assert.assertNull("result should be empty when options empty", single.getNewSelection(
				request));

		// -------------
		// Editable
		single.setOptions(OPTIONS);
		single.setEditable(true);
		request = setupRequest(single, OPTION_B);
		Assert.assertEquals("new selection should return optionB", OPTION_B, single.getNewSelection(
				request));

		// UserText
		request.setParameter(single.getId(), "usertext");
		Assert.assertEquals("new selection should return user text", "usertext", single.
				getNewSelection(request));

		// Null options
		single.setOptions((List<?>) null);
		Assert.assertEquals("new selection should return user text", "usertext", single.
				getNewSelection(request));

		// Empty options
		single.setOptions(EMPTY_LIST);
		Assert.assertEquals("new selection should return user text", "usertext", single.
				getNewSelection(request));

	}

	@Test
	public void testUpdateBeanDefaultFirstOption() {
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, false);
		// Set Bean Property
		single.setBeanProperty("myOption");

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		// Set Bean Property and Bean
		MyBean bean = new MyBean();
		single.setBean(bean);

		// Null Bean Value - getValue will default to first Option
		Assert.assertEquals("Data should default to OptionA", OPTION_A, single.getValue());

		// Test handle request is "no change"
		MockRequest request = setupRequest(single, OPTION_A);
		boolean changed = single.doHandleRequest(request);
		Assert.assertFalse("doHandleRequest should return false as option should not change",
				changed);

		// Update Bean (should have optionA on the bean)
		Assert.assertNull("Bean should still be null", bean.getMyOption());
		single.updateBeanValue();
		Assert.assertEquals("Bean should contain optionA", OPTION_A, bean.getMyOption());
	}

	@Test
	public void testHasSelection() {
		// Allow None - TRUE
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS, true);

		// Should have no selection by default
		Assert.assertFalse("Allow None - hasSelection should be false", single.hasSelection());
		Assert.assertNull("Allow None - Selected option should be null", single.getSelected());
		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		// Set option on the user context
		single.setSelected(OPTION_B);
		Assert.assertTrue("Allow None - hasSelection should be true", single.hasSelection());
		Assert.assertEquals("Allow None - Selected option should be optionB", OPTION_B, single.
				getSelected());
		resetContext();

		// Allow None - FALSE
		single = new MyWSingleSelectList(OPTIONS, false);

		// With User Context
		single.setLocked(true);
		setActiveContext(createUIContext());

		// Should have a selection by default (ie first option)
		Assert.assertTrue("No Allow None - hasSelection should be true", single.hasSelection());
		Assert.assertEquals("No Allow None - Selected option should be optionA", OPTION_A, single.
				getSelected());
		// Set option on the user context
		single.setSelected(OPTION_B);
		Assert.assertTrue("No Allow None - hasSelection should be true", single.hasSelection());
		Assert.assertEquals("No Allow None - Selected option should be optionB", OPTION_B, single.
				getSelected());
		resetContext();
	}

	@Test
	public void testHasSelectionWithNullOption() {
		// Allow None - TRUE
		AbstractWSingleSelectList single = new MyWSingleSelectList(OPTIONS_WITH_NULL, true);

		// Should be true as the selected option is "null"
		Assert.assertTrue("Allow None - hasSelection should be true", single.hasSelection());
		Assert.assertNull("Allow None - Selected option should be null", single.getSelected());

		// Allow None - FALSE
		single = new MyWSingleSelectList(OPTIONS_WITH_NULL, false);

		// Should be true as the selected option is "null"
		Assert.assertTrue("No Allow None - hasSelection should be true", single.hasSelection());
		Assert.assertNull("No Allow None - Selected option should be null", single.getSelected());
	}

	@Test
	public void testMatchingDuplicateText() {
		MyObject[] options
				= {
					new MyObject("1", "Desc"),
					new MyObject("2", "Desc")};

		AbstractWSingleSelectList list = new MyWSingleSelectList(Arrays.asList(options), true);

		list.setSelected(new MyObject("2", "Desc"));
		Assert.assertEquals("Incorrect selected option", options[1].code, ((MyObject) list.
				getSelected()).code);

		list.setSelected("Desc");
		Assert.assertEquals("Incorrect selected option", options[0].code, ((MyObject) list.
				getSelected()).code);

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
		private MyObject(final String code, final String desc) {
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
	 * @return a mock request with a selected option
	 */
	private MockRequest setupRequest(final AbstractWSingleSelectList target, final Object option) {
		final MockRequest request = new MockRequest();

		if (target.isAllowNoSelection()) {
			request.setParameter(target.getId() + "-h", "x");
		}
		request.setParameter(target.getId(), target.optionToCode(option));

		return request;
	}

	/**
	 * @param target the target component
	 * @return a mock request with nothing selected
	 */
	private MockRequest setupNothingSelectedRequest(final AbstractWSingleSelectList target) {
		final MockRequest request = new MockRequest();
		if (target.isAllowNoSelection()) {
			request.setParameter(target.getId() + "-h", "x");
		}
		return request;
	}

	/**
	 * Test class for AbstractWSingleSelectList.
	 */
	private static final class MyWSingleSelectList extends AbstractWSingleSelectList {

		/**
		 * @param options the list's options.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MyWSingleSelectList(final List<?> options, final boolean allowNoSelection) {
			super(options, allowNoSelection);
		}

		/**
		 * @param lookupTable the lookup table identifier to obtain the list's options from.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MyWSingleSelectList(final Object lookupTable, final boolean allowNoSelection) {
			super(lookupTable, allowNoSelection);
		}
	}

	/**
	 * Test bean.
	 */
	public static class MyBean {

		/**
		 * Selected option.
		 */
		private String myOption;

		/**
		 * @return the myOption
		 */
		public String getMyOption() {
			return myOption;
		}

		/**
		 * @param myOption the myOption to set
		 */
		public void setMyOption(final String myOption) {
			this.myOption = myOption;
		}
	}
}
