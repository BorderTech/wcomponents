package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Response;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor.CacheType;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.LookupTable;
import com.github.bordertech.wcomponents.util.XMLUtil;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This interceptor is used to render data lists back to the client.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DataListInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(DataListInterceptor.class);

	/**
	 * The Application-wide lookup-table to use.
	 */
	private static final LookupTable LOOKUP_TABLE = Factory.newInstance(LookupTable.class);

	/**
	 * The data list key is stored for use during the paint phase.
	 */
	private String key;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serviceRequest(final Request request) {
		key = request.getParameter(WServlet.DATA_LIST_PARAM_NAME);
		if (key == null) {
			super.serviceRequest(request);
		}
	}

	/**
	 * No need to run prepare paint for data lists.
	 * @param request {@inheritDoc}
	 */
	@Override
	public void preparePaint(final Request request) {
		// No-Op
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		if (key == null) {
			super.paint(renderContext);
			return;
		}

		Response response = getResponse();

		Object table = LOOKUP_TABLE.getTableForCacheKey(key);
		List<?> data = LOOKUP_TABLE.getTable(table);
		response.setContentType(WebUtilities.CONTENT_TYPE_XML);
		response.setHeader("Cache-Control", CacheType.DATALIST_CACHE.getSettings());

		XmlStringBuilder xml = new XmlStringBuilder(((WebXmlRenderContext) renderContext).getWriter());
		xml.write(XMLUtil.XML_DECLERATION);

		if (data != null) {
			xml.appendTagOpen("ui:datalist");
			xml.append(XMLUtil.UI_NAMESPACE);
			xml.appendAttribute("id", key);
			xml.appendClose();

			for (Object item : data) {
				// Check for null option (ie null or empty). Match isEmpty() logic.
				boolean isNull = item == null ? true : (item.toString().length() == 0);

				xml.appendTagOpen("ui:option");
				xml.appendAttribute("value", LOOKUP_TABLE.getCode(table, item));
				xml.appendOptionalAttribute("isNull", isNull, "true");
				xml.appendClose();
				xml.append(WebUtilities.encode(LOOKUP_TABLE.getDescription(table, item)));
				xml.appendEndTag("ui:option");
			}

			xml.appendEndTag("ui:datalist");
		}
	}
}
