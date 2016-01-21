package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.TrackingUtil;
import com.github.bordertech.wcomponents.util.Util;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link Renderer} for the {@link WApplication} component.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WApplicationRenderer extends AbstractWebXmlRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WApplicationRenderer.class);

	/**
	 * Paints the given WApplication.
	 *
	 * @param component the WApplication to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WApplication application = (WApplication) component;
		XmlStringBuilder xml = renderContext.getWriter();

		UIContext uic = UIContextHolder.getCurrent();
		String focusId = uic.getFocussedId();

		// Check that this is the top level component
		if (application.getParent() != null) {
			LOG.warn("WApplication component should be the top level component.");
		}

		xml.appendTagOpen("ui:application");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendAttribute("applicationUrl", uic.getEnvironment().getPostPath());
		xml.appendAttribute("ajaxUrl", uic.getEnvironment().getWServletPath());
		xml.appendAttribute("dataUrl", uic.getEnvironment().getWServletPath()); // TODO: Rationalise this
		xml.appendOptionalAttribute("unsavedChanges", application.hasUnsavedChanges(), "true");
		xml.appendOptionalAttribute("title", application.getTitle());
		xml.appendOptionalAttribute("defaultFocusId", uic.isFocusRequired() && !Util.empty(focusId),
				focusId);
		xml.appendOptionalAttribute("icon", WApplication.getIcon());
		xml.appendClose();

		// Tracking enabled globally
		if (TrackingUtil.isTrackingEnabled()) {
			xml.appendTagOpen("ui:analytic");
			xml.appendAttribute("clientId", TrackingUtil.getClientId());
			xml.appendOptionalAttribute("cd", TrackingUtil.getCookieDomain());
			xml.appendOptionalAttribute("dcd", TrackingUtil.getDataCollectionDomain());
			xml.appendOptionalAttribute("name", TrackingUtil.getApplicationName());
			xml.appendEnd();
		}

		// Hidden fields
		Map<String, String> hiddenFields = uic.getEnvironment().getHiddenParameters();

		if (hiddenFields != null) {
			for (Map.Entry<String, String> entry : hiddenFields.entrySet()) {
				xml.appendTagOpen("ui:param");
				xml.appendAttribute("name", entry.getKey());
				xml.appendAttribute("value", entry.getValue());
				xml.appendEnd();
			}
		}
		// Custom CSS Resources (if any)
		for (WApplication.ApplicationResource resource : application.getCssResources()) {
			String url = resource.getTargetUrl();
			if (!Util.empty(url)) {
				xml.appendTagOpen("ui:css");
				xml.appendAttribute("url", url);
				xml.appendEnd();
			}
		}

		// Custom JavaScript Resources (if any)
		for (WApplication.ApplicationResource resource : application.getJsResources()) {
			String url = resource.getTargetUrl();
			if (!Util.empty(url)) {
				xml.appendTagOpen("ui:js");
				xml.appendAttribute("url", url);
				xml.appendEnd();
			}
		}

		paintChildren(application, renderContext);

		xml.appendEndTag("ui:application");
	}
}
