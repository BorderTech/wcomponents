package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import com.github.bordertech.wcomponents.TestLookupTable.YesNoTable;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * AbstractWSelectList_Test - unit tests for {@link AbstractWSelectList}.
 *
 * @author Anthony O'Connor
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractWSelectList_Test extends AbstractWComponentTestCase {

	/**
	 * Hold the original config.
	 */
	private static Configuration originalConfig;

	@BeforeClass
	public static void setUp() {
		originalConfig = Config.getInstance();
		CompositeConfiguration config = new CompositeConfiguration(originalConfig);

		MapConfiguration overrides = new MapConfiguration(new HashMap<String, Object>());
		overrides.setProperty("bordertech.wcomponents.integrity.terminate.mode", "true");
		config.addConfiguration(overrides);

		Config.setConfiguration(config);
	}

	@AfterClass
	public static void tearDown() {
		// Remove overrides
		Config.setConfiguration(originalConfig);
	}

	@Test
	public void testConstructor1() {
		// Constructor - 1
		List<String> options = Arrays.asList("A", "B");
		AbstractWSelectList list = new MyWSelectList(options, true);

		Assert.assertEquals("Incorrect default options returned", options, list.getOptions());
		Assert.assertEquals("Incorrect options returned with uic", options, list.getOptions());
		Assert.assertTrue("allowNoSelection should be true", list.isAllowNoSelection());

		list = new MyWSelectList(options, false);
		Assert.assertFalse("allowNoSelection should be false", list.isAllowNoSelection());
	}

	@Test
	public void testConstructor2() {
		// Constructor - 2
		AbstractWSelectList list = new MyWSelectList(DayOfWeekTable.class, true);
		Assert.assertEquals("Incorrect table returned", DayOfWeekTable.class, list.getLookupTable());
		Assert.assertTrue("allowNoSelection should be true", list.isAllowNoSelection());

		list = new MyWSelectList(DayOfWeekTable.class, false);
		Assert.assertFalse("allowNoSelection should be false", list.isAllowNoSelection());

		List<Object> data = new TestLookupTable().getTable(DayOfWeekTable.class);
		Assert.assertEquals("Incorrect default options returned", data, list.getOptions());
		Assert.assertEquals("Incorrect options returned with uic", data, list.getOptions());
	}

	@Test
	public void testSetSubmitOnChange() {
		assertAccessorsCorrect(new MyWSelectList(null, true), "submitOnChange", false, true, false);
	}

	@Test
	public void testSetDescEncode() {
		assertAccessorsCorrect(new MyWSelectList(null, true), "descEncode", true, false, true);
	}

	@Test
	public void testOptionsAccessors() {
		List<String> list1 = Arrays.asList("A", "B");
		List<String> list2 = Arrays.asList("X", "Y");
		assertAccessorsCorrect(new MyWSelectList(null, true), "options", null, list1, list2);
	}

	@Test
	public void testOptionToCodeNullOptionIn() {
		AbstractWSelectList list = new MyWSelectList(Arrays.asList("A", null, "B", "C"), true);
		// Test the code for null option
		Object option = null;
		String result = list.optionToCode(option);
		Assert.assertEquals("Incorrect code returned for null option", "2", result);
	}

	@Test
	public void testOptiontoCodeNullOptionsOrEmptyOptions() {
		// Test Null options
		AbstractWSelectList list1 = new MyWSelectList(null, true);
		String result1 = null;
		try {
			result1 = list1.optionToCode("dont care");
			Assert.fail("Integrity issue should have been thrown for null options");
		} catch (IntegrityException e) {
			Assert.assertNull("result1 should be null", result1);
		}

		// Test Empty options
		AbstractWSelectList list2 = new MyWSelectList(new ArrayList<String>(), true);
		String result2 = null;
		try {
			result2 = list2.optionToCode("dont care");
			Assert.fail("Integrity issue should have been thrown for empty options");
		} catch (IntegrityException e) {
			Assert.assertNull("result2 should be null", result2);
		}
	}

	@Test
	public void testOptionToCodeNoMatches() {
		AbstractWSelectList list = new MyWSelectList(Arrays.asList("A", "B", "C"), true);

		// Right class - but no matching value
		String result1 = null;
		try {
			result1 = list.optionToCode("X");
			Assert.fail("Integrity issue should have been thrown for option with no match");
		} catch (IntegrityException e) {
			Assert.assertNull("result1 should be null", result1);
		}

		// Wrong class
		String result2 = null;
		try {
			result2 = list.optionToCode(new Integer(0));
			Assert.fail("Integrity issue should have been thrown for wrong class");
		} catch (IntegrityException e) {
			Assert.assertNull("result2 should be null", result2);
		}
	}

	@Test
	public void testOptionToCodeMatch() {
		AbstractWSelectList list = new MyWSelectList(Arrays.asList("A", "B", "C"), true);

		// Right class - matching value
		int expectedIndex = 2;
		String lookedForOption = "B";
		String result = list.optionToCode(lookedForOption);
		Assert.
				assertEquals("should have found option in options set", String.
						valueOf(expectedIndex), result);
	}

	@Test
	public void testOptionToCodeWithOptionInstance() {
		Option option1 = new MyOption("A", "Test A");
		Option option2 = new MyOption("B", "Test B");
		Option option3 = new MyOption("C", "Test C");
		AbstractWSelectList list = new MyWSelectList(Arrays.asList(option1, option2, option3), true);

		// Right class - matching value
		String result = list.optionToCode(option2);
		Assert.assertEquals("Should have found option2 and returned its code", option2.getCode(),
				result);
	}

	@Test
	public void testOptionToCodeWithTableEntry() {
		AbstractWSelectList list = new MyWSelectList(TestLookupTable.YesNoTable.class, true);
		List<Object> data = new TestLookupTable().getTable(TestLookupTable.YesNoTable.class);
		TestLookupTable.TableEntry yesEntry = (TestLookupTable.TableEntry) data.get(0);

		// Right class - matching value
		String result = list.optionToCode(yesEntry);
		Assert.assertEquals("Should have found table entry and returned its code", yesEntry.
				getCode(), result);
	}

	@Test
	public void testGetListCacheKey() {
		// With Cache Key
		AbstractWSelectList list = new MyWSelectList(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE,
				true);
		Assert.assertEquals("Incorrect cache key returned",
				TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE,
				list.getListCacheKey());
		// No Cache Key
		list = new MyWSelectList(TestLookupTable.DayOfWeekTable.class, true);
		Assert.assertNull("Cache key should be null", list.getListCacheKey());
	}

	@Test
	public void testOptionToString() {
		AbstractWSelectList list = new MyWSelectList(null, true);
		Assert.assertEquals("Incorrect string returned for option", "true", list.optionToString(
				Boolean.TRUE));
	}

	@Test
	public void testSetLookupTable() {
		Object table1 = DayOfWeekTable.class;
		Object table2 = YesNoTable.class;

		List<Object> data1 = new TestLookupTable().getTable(table1);
		List<Object> data2 = new TestLookupTable().getTable(table2);

		AbstractWSelectList list = new MyWSelectList(null, true);

		list.setLookupTable(table1);
		Assert.assertEquals("Incorrect lookupTable should be table1", table1, list.getLookupTable());
		// Check options
		Assert.assertEquals("Incorrect options returned for table1", data1, list.getOptions());

		// Set user context
		list.setLocked(true);
		setActiveContext(createUIContext());
		list.setLookupTable(table2);
		Assert.assertEquals("LookupTable with uic1 should be table2", table2, list.getLookupTable());
		Assert.assertEquals("Incorrect options returned for table2 with uic", data2, list.
				getOptions());

		resetContext();
		Assert.assertEquals("Default lookupTable should be table1", table1, list.getLookupTable());
		Assert.assertEquals("Incorrect default options returned for table1", data1, list.
				getOptions());
	}

	@Test
	public void testSetEditable() {
		AbstractWSelectList list = new MyWSelectList(null, true);

		list.setEditable(false);

		list.setLocked(true);
		setActiveContext(createUIContext());
		list.setEditable(true);

		Assert.assertTrue("uic should be editable", list.isEditable());

		resetContext();
		Assert.assertFalse("default should not be editable", list.isEditable());
	}

	@Test
	public void testSetAjaxTarget() {

		AbstractWSelectList list = new MyWSelectList(null, true);
		Assert.assertNull("Ajax Target should default to null", list.getAjaxTarget());
		Assert.assertFalse("Ajax should defualt to false", list.isAjax());

		// Default target
		WPanel target1 = new WPanel();
		list.setAjaxTarget(target1);
		Assert.assertEquals("Incorrect default Ajax Target returned", target1, list.getAjaxTarget());
		Assert.assertEquals("Incorrect default Ajax Target returned for uic", target1, list.
				getAjaxTarget());
		Assert.assertTrue("isAjax should default to true", list.isAjax());

		// Change target on user context
		list.setLocked(true);
		UIContext uic = createUIContext();
		setActiveContext(uic);
		WPanel target2 = new WPanel();
		list.setAjaxTarget(target2);
		Assert.assertEquals("Incorrect Ajax Target returned for uic", target2, list.getAjaxTarget());
		Assert.assertTrue("isAjax should defualt to true", list.isAjax());

		resetContext();
		Assert.assertEquals("Incorrect default Ajax Target returned", target1, list.getAjaxTarget());

		// Clear default
		list.setAjaxTarget(null);
		Assert.assertNull("Incorrect default Ajax Target returned", list.getAjaxTarget());

		setActiveContext(uic);
		Assert.assertEquals("Incorrect Ajax Target returned for uic", target2, list.getAjaxTarget());
		Assert.assertTrue("isAjax should be true", list.isAjax());
	}

	@Test
	public void testIsPresent() {
		// Allow null option
		AbstractWSelectList list = new MyWSelectList(null, true);
		// Empty Request (Not Present)
		MockRequest request = new MockRequest();
		Assert.assertFalse("List should not be present in the request", list.isPresent(request));
		// "id" parameter (Not Present)
		request = new MockRequest();
		request.setParameter(list.getId(), "x");
		Assert.assertFalse("List should not be present in the request", list.isPresent(request));
		// "h" parameter (Present)
		request = new MockRequest();
		request.setParameter(list.getId() + "-h", "x");
		Assert.assertTrue("List should be present in the request", list.isPresent(request));

		// Allow NO null option
		list = new MyWSelectList(null, false);
		// Empty Request (Not Present)
		request = new MockRequest();
		Assert.assertFalse("List should not be present in the request", list.isPresent(request));
		// "h" parameter (Not Present)
		request = new MockRequest();
		request.setParameter(list.getId() + "-h", "x");
		Assert.assertFalse("List should not be present in the request", list.isPresent(request));
		// "id" parameter (Present)
		request = new MockRequest();
		request.setParameter(list.getId(), "x");
		Assert.assertTrue("List should be present in the request", list.isPresent(request));
	}

	/**
	 * Test class for AbstractWSelectList.
	 */
	private static final class MyWSelectList extends AbstractWSelectList {

		/**
		 * @param options the list's options.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MyWSelectList(final List<?> options, final boolean allowNoSelection) {
			super(options, allowNoSelection);
		}

		/**
		 * @param lookupTable the lookup table identifier to obtain the list's options from.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MyWSelectList(final Object lookupTable, final boolean allowNoSelection) {
			super(lookupTable, allowNoSelection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean doHandleRequest(final Request request) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getRequestValue(final Request request) {
			return null;
		}
	}

	/**
	 * Test class for Option.
	 */
	private static final class MyOption implements Option {

		/**
		 * Option code value.
		 */
		private final String code;
		/**
		 * Option desc value.
		 */
		private final String desc;

		/**
		 * Construct option.
		 *
		 * @param code the option code value.
		 * @param desc the option description value.
		 */
		private MyOption(final String code, final String desc) {
			this.code = code;
			this.desc = desc;
		}

		/**
		 * @return the option code
		 */
		@Override
		public String getCode() {
			return code;
		}

		/**
		 * @return the option description
		 */
		@Override
		public String getDesc() {
			return desc;
		}
	}

}
