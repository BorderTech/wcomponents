package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import com.github.bordertech.wcomponents.TestLookupTable.YesNoTable;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WSuggestions}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSuggestions_Test extends AbstractWComponentTestCase {

	/**
	 * Default options.
	 */
	private static final List<String> DEFAULT_OPTIONS = Arrays.asList("A", "B", "C");

	/**
	 * Default options.
	 */
	private static final List<String> DEFAULT_OPTIONS2 = Arrays.asList("X", "Y", "Z");

	@Test
	public void testDefaultConstructor() {
		WSuggestions sugg = new WSuggestions();

		Assert.assertTrue("Default Const - Suggestions list should be empty", sugg.getSuggestions().
				isEmpty());
		Assert.assertNull("Default Const - List cache key should be null", sugg.getListCacheKey());
		Assert.assertNull("Default Const - Lookup table should be null", sugg.getLookupTable());
		Assert.assertNull("Default Const - Refresh action should be null", sugg.getRefreshAction());
		Assert.assertNull("Default Const - AJAX filter should be null", sugg.getAjaxFilter());
		Assert.assertEquals("Default Const - Min refresh characters should be 0", 0, sugg.
				getMinRefresh());
	}

	@Test
	public void testConstructorWithLookupTable() {
		WSuggestions sugg = new WSuggestions(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE);

		Assert.assertFalse("Lookuptable Const - Suggestions list should not be empty", sugg.
				getSuggestions().isEmpty());
		Assert.assertNotNull("Lookuptable Const - List cache key should not be null", sugg.
				getListCacheKey());
		Assert.assertEquals("Lookuptable Const - Incorrect lookup table returned",
				TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE, sugg.getLookupTable());
		Assert.assertNull("Lookuptable Const - Refresh action should be null", sugg.
				getRefreshAction());
		Assert.assertNull("Lookuptable Const - AJAX filter should be null", sugg.getAjaxFilter());
		Assert.assertEquals("Lookuptable Const - Min refresh characters should be 0", 0, sugg.
				getMinRefresh());
	}

	@Test
	public void testConstructorWithOptions() {
		WSuggestions sugg = new WSuggestions(DEFAULT_OPTIONS);

		Assert.assertEquals("Options Const - Suggestions list is not correct", DEFAULT_OPTIONS,
				sugg.getSuggestions());
		Assert.assertNull("Options Const - List cache key should be null", sugg.getListCacheKey());
		Assert.assertNull("Options Const - Lookup table should be null", sugg.getLookupTable());
		Assert.assertNull("Options Const - Refresh action should be null", sugg.getRefreshAction());
		Assert.assertNull("Options Const - AJAX filter should be null", sugg.getAjaxFilter());
		Assert.assertEquals("Options Const - Min refresh characters should be 0", 0, sugg.
				getMinRefresh());
	}

	@Test
	public void testSuggestionAccessors() {
		assertAccessorsCorrect(new WSuggestions(), "suggestions", Collections.EMPTY_LIST,
				DEFAULT_OPTIONS,
				DEFAULT_OPTIONS2);
	}

	@Test
	public void testLookupTableAccessors() {
		assertAccessorsCorrect(new WSuggestions(), "lookupTable", null, "X", "Y");
	}

	@Test
	public void testRefreshActionAccessors() {
		assertAccessorsCorrect(new WSuggestions(), "refreshAction", null, new TestAction(),
				new TestAction());
	}

	@Test
	public void testAutocompleteAccessors() {
		assertAccessorsCorrect(new WSuggestions(), "autocomplete", WSuggestions.Autocomplete.BOTH, WSuggestions.Autocomplete.LIST, WSuggestions.Autocomplete.BOTH);
	}

	@Test
	public void testMinRefreshAccessors() {
		assertAccessorsCorrect(new WSuggestions(), "minRefresh", 0, 1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMinRefreshInvalid() {
		new WSuggestions().setMinRefresh(-1);
	}

	@Test
	public void testSetLookupTable() {
		Object table1 = DayOfWeekTable.class;
		Object table2 = YesNoTable.class;

		List<String> data1 = getTableOptionsAsString(table1);
		List<String> data2 = getTableOptionsAsString(table2);

		WSuggestions list = new WSuggestions();

		list.setLookupTable(table1);
		Assert.assertEquals("Incorrect lookupTable should be table1", table1, list.getLookupTable());
		// Check options
		Assert.assertEquals("Incorrect options returned for table1", data1, list.getSuggestions());

		// Set user context
		list.setLocked(true);
		setActiveContext(createUIContext());
		list.setLookupTable(table2);
		Assert.assertEquals("LookupTable with uic1 should be table2", table2, list.getLookupTable());
		Assert.assertEquals("Incorrect options returned for table2 with uic", data2, list.
				getSuggestions());

		resetContext();
		Assert.assertEquals("Default lookupTable should be table1", table1, list.getLookupTable());
		Assert.assertEquals("Incorrect default options returned for table1", data1, list.
				getSuggestions());
	}

	@Test
	public void testDoHandleRequest() {
		final List<String> options = Arrays.asList("A", "B", "C");
		WSuggestions sugg = new WSuggestions();

		// Set action for AJAX refresh
		sugg.setRefreshAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				// For AJAX refresh set the options
				WSuggestions suggestions = (WSuggestions) event.getSource();
				suggestions.setSuggestions(options);
			}
		});

		UIContext uic = createUIContext();
		uic.setUI(new DefaultWComponent());
		setActiveContext(uic);

		// Request that is not AJAX
		MockRequest request = new MockRequest();
		sugg.serviceRequest(request);

		// Nothing should be set
		Assert.assertTrue("Request - Suggestions list should be empty", sugg.getSuggestions().
				isEmpty());
		Assert.assertNull("Request - AJAX filter should be null", sugg.getAjaxFilter());

		// Mock AJAX Request - Should trigger action and render suggestions
		try {
			AjaxOperation operation = new AjaxOperation(sugg.getId(), sugg.getId());
			AjaxHelper.setCurrentOperationDetails(operation, null);

			request = new MockRequest();
			request.setParameter(sugg.getId(), "TEST");
			sugg.serviceRequest(request);

			// Refresh action should have set the suggestions
			Assert.assertEquals("AJAX Request - Suggestions list should be set", options, sugg.
					getSuggestions());
			Assert.assertEquals("AJAX Request - AJAX filter should be set", "TEST", sugg.
					getAjaxFilter());
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}

	}

	/**
	 * @param table the lookup table
	 * @return the table options as a list of strings
	 */
	private List<String> getTableOptionsAsString(final Object table) {
		TestLookupTable lookup = new TestLookupTable();
		List<String> suggestions = new ArrayList<>();
		List<Object> data1 = lookup.getTable(table);
		for (Object suggestion : data1) {
			String sugg = lookup.getDescription(table, suggestion);
			if (sugg != null) {
				suggestions.add(sugg);
			}
		}

		return suggestions;
	}

}
