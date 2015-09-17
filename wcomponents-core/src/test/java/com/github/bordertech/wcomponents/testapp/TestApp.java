package com.github.bordertech.wcomponents.testapp;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.registry.UIRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is the top most wcomponent that represents a test application that we can use to study the performance of
 * wcomponents. It is a simple application that collects some search criteria, displays a list of results, and displays
 * details for one of those results.
 *
 * @author Martin Shevchenko
 */
public class TestApp extends WApplication {

	/**
	 * Page names.
	 */
	private static final String[] PAGE_NAMES = new String[]{"Search", "Results", "Details"};

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(TestApp.class);

	/**
	 * Random number generator used to create data.
	 */
	private static final Random RAND = new Random();

	private final SearchResultsPage searchResultsPage;
	private final WCardManager pages;
	private final DetailsPage detailsPage;

	/**
	 * The search criteria page.
	 */
	private final SearchCriteriaPage searchCriteriaPage;

	/**
	 * Creates a TestApp.
	 */
	public TestApp() {
		WButton logout = new WButton("Logout") {
			@Override
			public void handleRequest(final Request request) {
				super.handleRequest(request);

				if (isPressed()) {
					TestApp.this.reset();
					request.logout();
				}
			}
		};

		logout.setRenderAsLink(true);
		add(logout);

		pages = new WCardManager();
		add(pages);

		searchCriteriaPage = new SearchCriteriaPage();
		pages.add(searchCriteriaPage);

		searchResultsPage = new SearchResultsPage();
		pages.add(searchResultsPage);

		detailsPage = new DetailsPage();
		pages.add(detailsPage);
	}

	/**
	 * @return the search criteria page.
	 */
	public SearchCriteriaPage getSearchCriteriaPage() {
		return searchCriteriaPage;
	}

	/**
	 * @return the single instance of the TestApp that is registered with the {@link UIRegistry}.
	 */
	public static TestApp getInstance() {
		return (TestApp) UIRegistry.getInstance().getUI(TestApp.class.getName());
	}

	/**
	 * Override paintComponent to additionally log some information.
	 *
	 * @param renderContext the renderContext to write send the output to.
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		super.paintComponent(renderContext);

		if (LOG.isInfoEnabled()) {
			int i = pages.getIndexOfChild(pages.getVisible());

			if (i >= 0) {
				LOG.info(PAGE_NAMES[i] + " page, " + getJvmStats());
			}
		}
	}

	/**
	 * @return some JVM statistics for debugging.
	 */
	private String getJvmStats() {
		long heap = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long used = heap - free;

		// Report the stats in kilobytes
		long heapKb = heap >> 10;
		long usedKb = used >> 10;

		return "mem[" + usedKb + "," + heapKb + "] ";
	}

	/**
	 * Performs a search (using dummy data).
	 */
	public void doSearch() {
		// Junk String
		// Number of rows (entry field)
		// rows per page (dropdown)
		// quantity of data per row (radio group)

		searchResultsPage.setRowsPerPage(searchCriteriaPage.getRowsPerPageAsInt());

		int numRows = searchCriteriaPage.getNumRowsAsInt();
		List<SearchResultRowBO> results = new ArrayList<>(numRows);
		String name = searchCriteriaPage.getDataField().getText();
		String detailsSize = (String) searchCriteriaPage.getDetailsSize().getSelected();

		for (int i = 0; i < numRows; i++) {
			//results.add(new SearchResultRowBO(name + " " + i, "123"));
			results.add(createRow(i, name, detailsSize));
		}

		searchResultsPage.setSearchResults(results);

		pages.makeVisible(searchResultsPage);
	}

	/**
	 * Creates a search result row.
	 *
	 * @param rowIndex the row index.
	 * @param name the row name.
	 * @param detailsSize the description text size.
	 * @return a search result BO with the given attributes.
	 */
	public SearchResultRowBO createRow(final int rowIndex, final String name,
			final String detailsSize) {
		SearchResultRowBO row = new SearchResultRowBO();

		row.setName(name + " " + (rowIndex + 1));

		int n;

		List dayOfWeekList = new TestLookupTable().getTable(TestLookupTable.DAY_OF_WEEK_TABLE);
		n = RAND.nextInt(dayOfWeekList.size());
		row.setCountry((TestLookupTable.TableEntry) dayOfWeekList.get(n));

		row.setTicked(Boolean.valueOf(RAND.nextBoolean()));
		row.setHappy(Boolean.valueOf(RAND.nextBoolean()));

		List<String> animals = new ArrayList<>();

		for (int i = RAND.nextInt(4); i > 0; --i) {
			n = RAND.nextInt(SearchResultRowBO.ANIMAL_OPTIONS.length);
			String animal = SearchResultRowBO.ANIMAL_OPTIONS[n];
			if (!animals.contains(animal)) {
				animals.add(animal);
			}
		}
		row.setAnimals(animals);

		if (SearchCriteriaPage.SMALL.equals(detailsSize)) {
			row.setDesc("Small");
		} else if (SearchCriteriaPage.MEDIUM.equals(detailsSize)) {
			row.setDesc(
					"Medium blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah.");
		} else if (SearchCriteriaPage.LARGE.equals(detailsSize)) {
			row.setDesc(
					"Large blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah."
					+ " More blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah."
					+ " More blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah.");
		} else if (SearchCriteriaPage.HUGE.equals(detailsSize)) {
			row.setDesc(
					"Huge blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah."
					+ " More blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah."
					+ " More blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah."
					+ " More blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah."
					+ " More blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah."
					+ " More blah blah blah blah blah blah. Blah blah blah blah blah blah blah blah blah blah.");
		} else {
			row.setDesc("Blah blah blah...");
		}

		return row;
	}

	/**
	 * Displays the details page for the given search result row.
	 *
	 * @param row the row data
	 */
	public void doDetails(final SearchResultRowBO row) {
		detailsPage.setDetails(row);
		pages.makeVisible(detailsPage);
	}

	/**
	 * Displays the search criteria page.
	 */
	public void doNewSearch() {
		pages.makeVisible(searchCriteriaPage);
	}

	/**
	 * Displays the search results page.
	 */
	public void gotoSearchResults() {
		pages.makeVisible(searchResultsPage);
	}
}
