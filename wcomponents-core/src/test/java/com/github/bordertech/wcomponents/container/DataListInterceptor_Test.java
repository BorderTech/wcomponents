package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.DefaultWComponent;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.render.webxml.AbstractWebXmlRendererTestCase;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.LookupTable;
import com.github.bordertech.wcomponents.util.NullWriter;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * DataListInterceptor_Test - unit tests for {@link DataListInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DataListInterceptor_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testInterecptor() throws XpathException, SAXException, IOException {
		String tableKey = TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE;

		// Create interceptor
		DataListInterceptor interceptor = new DataListInterceptor();
		interceptor.attachUI(new DefaultWComponent());

		// Action phase
		MockRequest request = new MockRequest();
		request.setParameter(WServlet.DATA_LIST_PARAM_NAME, tableKey);

		interceptor.serviceRequest(request);

		// Render phase
		MockResponse response = new MockResponse();
		interceptor.attachResponse(response);
		interceptor.paint(new WebXmlRenderContext(new PrintWriter(new NullWriter()))); // interceptor renders directly to response

		String xml = response.getWriterOutput();

		// Ensure that the data matches the test table.
		List<TestLookupTable.TableEntry> table = (List<TestLookupTable.TableEntry>) Factory.
				newInstance(LookupTable.class).getTable(tableKey);

		assertXpathEvaluatesTo(String.valueOf(table.size()), "count(/ui:datalist/ui:option)", xml);

		for (int i = 0; i < table.size(); i++) {
			assertXpathEvaluatesTo(table.get(i).getCode(),
					"/ui:datalist/ui:option[" + (i + 1) + "]/@value", xml);
			assertXpathEvaluatesTo(table.get(i).getDesc(),
					"/ui:datalist/ui:option[" + (i + 1) + "]/text()", xml);
		}
	}

	@Test
	public void testInterecptorWithNullOption() throws XpathException, SAXException, IOException {
		String tableKey = TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE_WITH_NULL_OPTION;

		// Create interceptor
		DataListInterceptor interceptor = new DataListInterceptor();
		interceptor.attachUI(new DefaultWComponent());

		// Action phase
		MockRequest request = new MockRequest();
		request.setParameter(WServlet.DATA_LIST_PARAM_NAME, tableKey);

		interceptor.serviceRequest(request);

		// Render phase
		MockResponse response = new MockResponse();
		interceptor.attachResponse(response);
		interceptor.paint(new WebXmlRenderContext(new PrintWriter(new NullWriter()))); // interceptor renders directly to response

		String xml = response.getWriterOutput();

		// Ensure that the data matches the test table.
		List<TestLookupTable.TableEntry> table = (List<TestLookupTable.TableEntry>) Factory.
				newInstance(LookupTable.class).getTable(tableKey);

		assertXpathEvaluatesTo(String.valueOf(table.size()), "count(/ui:datalist/ui:option)", xml);

		for (int i = 0; i < table.size(); i++) {
			if (table.get(i) == null) {
				assertXpathEvaluatesTo("", "/ui:datalist/ui:option[" + (i + 1) + "]/@value", xml);
				assertXpathEvaluatesTo("", "/ui:datalist/ui:option[" + (i + 1) + "]/text()", xml);
				assertXpathEvaluatesTo("true", "/ui:datalist/ui:option[" + (i + 1) + "]/@isNull",
						xml);
			} else {
				assertXpathEvaluatesTo(table.get(i).getCode(),
						"/ui:datalist/ui:option[" + (i + 1) + "]/@value", xml);
				assertXpathEvaluatesTo(table.get(i).getDesc(),
						"/ui:datalist/ui:option[" + (i + 1) + "]/text()", xml);
				assertXpathNotExists("/ui:datalist/ui:option[" + (i + 1) + "]/@isNull", xml);
			}
		}
	}

}
