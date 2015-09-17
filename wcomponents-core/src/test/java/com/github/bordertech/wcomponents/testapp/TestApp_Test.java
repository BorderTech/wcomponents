package com.github.bordertech.wcomponents.testapp;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link TestApp}.
 *
 * @author Kishan Bisht
 * @since 1.0.0
 */
public class TestApp_Test extends AbstractWComponentTestCase {

	@Test
	public void testDisplaySearch() {
		TestApp testApp = new TestApp();
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();

		String result = WebUtilities.render(request, testApp);
		LogFactory.getLog(getClass()).debug(result);

		Assert.assertNotNull("Html output should not be null", result);
		Assert.assertTrue("Should be on search criteria page", result.contains("Search Criteria"));
	}

	@Test
	public void testSearch() {
		TestApp testApp = new TestApp();
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();

		//set search criteria details in searchCriteriaPage
		request.setParameter(testApp.getSearchCriteriaPage().getDataField().getId(), "Joe Bloggs");
		request.setParameter(testApp.getSearchCriteriaPage().getNumRows().getId(), "200");
		request.setParameter(testApp.getSearchCriteriaPage().getRowsPerPage().getId(), "20");
		request.setParameter(testApp.getSearchCriteriaPage().getDetailsSize().getId(), "Huge");
		request.addParameterForButton(UIContextHolder.getCurrent(), testApp.getSearchCriteriaPage().
				getSearchBtn());

		// log elapsed time if the logger is set to trace
		long elapsed = System.currentTimeMillis();

		try {
			testApp.serviceRequest(request);
		} finally {
			elapsed = System.currentTimeMillis() - elapsed;
			LogFactory.getLog(getClass()).trace("Service request: " + elapsed);
		}

		testApp.doSearch();

		// log elapsed time if the logger is set to trace
		elapsed = System.currentTimeMillis();
		String result = null;

		try {
			result = WebUtilities.render(request, testApp);
		} finally {
			elapsed = System.currentTimeMillis() - elapsed;
			LogFactory.getLog(getClass()).trace("Render: " + elapsed);
		}

		Assert.assertNotNull("Rendered page should not be null", result);
		Assert.assertTrue("Should be on search results page", result.contains("Search Results"));

		int numRows = testApp.getSearchCriteriaPage().getRowsPerPageAsInt();

		for (int i = 0; i < numRows; i++) {
			Assert.assertTrue("Missing row " + i, result.contains("Joe Bloggs " + (i + 1)));
		}
	}

	@Test
	public void testDetailsTab() {
		TestApp testApp = TestApp.getInstance();
		SearchResultRowBO row = testApp.createRow(5, "test", "Huge");

		setActiveContext(createUIContext());
		testApp.doDetails(row);

		String result = WebUtilities.render(new MockRequest(), testApp);
		Assert.assertTrue("Should be on search results page", result.contains("Details"));
	}
}
