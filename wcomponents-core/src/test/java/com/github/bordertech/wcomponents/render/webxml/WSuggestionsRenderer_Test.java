package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.AjaxOperation;
import com.github.bordertech.wcomponents.DefaultWComponent;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WSuggestions;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WSuggestionsRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSuggestionsRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WSuggestions suggestions = new WSuggestions();
		Assert
				.assertTrue("Incorrect renderer supplied",
						getWebXmlRenderer(suggestions) instanceof WSuggestionsRenderer);
	}

	@Test
	public void testDoBasicPaint() throws IOException, SAXException, XpathException {
		WSuggestions field = new WSuggestions();

		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getId(), "//ui:suggestions/@id", field);
		assertXpathNotExists("//ui:suggestions/@ajax", field);
		assertXpathNotExists("//ui:suggestions/@min", field);
		assertXpathNotExists("//ui:suggestions/@data", field);
		assertXpathNotExists("//ui:suggestions/@autocomplete", field);
		assertXpathNotExists("//ui:suggestions/suggestion", field);

		// Set min
		field.setMinRefresh(2);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("2", "//ui:suggestions/@min", field);
	}

	@Test
	public void testDoPaintLookupTableOptions() throws IOException, SAXException, XpathException {
		WSuggestions field = new WSuggestions(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE);

		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getId(), "//ui:suggestions/@id", field);
		assertXpathNotExists("//ui:suggestions/@ajax", field);
		assertXpathNotExists("//ui:suggestions/@min", field);
		assertXpathNotExists("//ui:suggestions/suggestion", field);
		assertXpathEvaluatesTo(field.getListCacheKey(), "//ui:suggestions/@data", field);

		// If action set with a lookup table, AJAX flag should still be not set
		field.setRefreshAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				// Do nothing
			}
		});
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:suggestions/@ajax", field);
	}

	@Test
	public void testDoPaintStaticOptions() throws IOException, SAXException, XpathException {
		List<String> options = Arrays.asList("A", "B", "C");
		WSuggestions field = new WSuggestions(options);

		assertSchemaMatch(field);

		assertXpathEvaluatesTo(field.getId(), "//ui:suggestions/@id", field);
		assertXpathNotExists("//ui:suggestions/@ajax", field);
		assertXpathNotExists("//ui:suggestions/@min", field);
		assertXpathNotExists("//ui:suggestions/@data", field);
		assertXpathExists("//ui:suggestions/ui:suggestion", field);

		assertXpathEvaluatesTo(options.get(0), "//ui:suggestions/ui:suggestion[1]/@value", field);
		assertXpathEvaluatesTo(options.get(1), "//ui:suggestions/ui:suggestion[2]/@value", field);
		assertXpathEvaluatesTo(options.get(2), "//ui:suggestions/ui:suggestion[3]/@value", field);
	}

	@Test
	public void testDoPaintAjaxOptions() throws IOException, SAXException, XpathException {
		List<String> options = Arrays.asList("A", "B", "C");
		WSuggestions field = new WSuggestions(options);

		// Set action for AJAX refresh
		field.setRefreshAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				// Do nothing
			}
		});

		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getId(), "//ui:suggestions/@id", field);
		assertXpathNotExists("//ui:suggestions/@min", field);
		assertXpathNotExists("//ui:suggestions/@data", field);

		// AJAX flag should be true
		assertXpathEvaluatesTo("true", "//ui:suggestions/@ajax", field);
		// Suggestions should only be rendered when refreshed via AJAX
		assertXpathNotExists("//ui:suggestions/ui:suggestion", field);

		// Setup suggestions as the current AJAX trigger
		UIContext uic = createUIContext();
		uic.setUI(new DefaultWComponent());
		setActiveContext(uic);

		try {
			AjaxOperation operation = new AjaxOperation(field.getId(), field.getId());
			AjaxHelper.setCurrentOperationDetails(operation, null);
			assertSchemaMatch(field);
			assertXpathExists("//ui:suggestions/ui:suggestion", field);
			assertXpathEvaluatesTo(options.get(0), "//ui:suggestions/ui:suggestion[1]/@value", field);
			assertXpathEvaluatesTo(options.get(1), "//ui:suggestions/ui:suggestion[2]/@value", field);
			assertXpathEvaluatesTo(options.get(2), "//ui:suggestions/ui:suggestion[3]/@value", field);
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}
	}


	@Test
	public void testDoPaintAutocomplete() throws IOException, SAXException, XpathException {
		List<String> options = Arrays.asList("A", "B", "C");
		WSuggestions field = new WSuggestions(options);

		assertSchemaMatch(field);
		assertXpathNotExists("//ui:suggestions/@autocomplete", field);
		field.setAutocomplete(WSuggestions.Autocomplete.LIST);
		assertXpathEvaluatesTo("list", "//ui:suggestions/@autocomplete", field);
		field.setAutocomplete(WSuggestions.Autocomplete.BOTH);
		assertXpathNotExists("//ui:suggestions/@autocomplete", field);
	}

}
