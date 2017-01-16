package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WFileWidget;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.util.Iterator;
import java.util.List;

/**
 * The Renderer for {@link WFileWidget}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WFileWidgetRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WFileWidget.
	 *
	 * @param component the WFileWidget to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WFileWidget fileWidget = (WFileWidget) component;
		XmlStringBuilder xml = renderContext.getWriter();
		boolean readOnly = fileWidget.isReadOnly();
		long maxFileSize = fileWidget.getMaxFileSize();

		xml.appendTagOpen("ui:fileupload");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", component.isHidden(), "true");
		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
		} else {
			xml.appendOptionalAttribute("disabled", fileWidget.isDisabled(), "true");
			xml.appendOptionalAttribute("required", fileWidget.isMandatory(), "true");
			xml.appendOptionalAttribute("tabIndex", fileWidget.hasTabIndex(), fileWidget.getTabIndex());
			xml.appendOptionalAttribute("toolTip", fileWidget.getToolTip());
			xml.appendOptionalAttribute("accessibleText", fileWidget.getAccessibleText());
			xml.appendOptionalAttribute("acceptedMimeTypes", typesToString(fileWidget.getFileTypes()));
			xml.appendOptionalAttribute("maxFileSize", maxFileSize > 0, maxFileSize);
			xml.appendAttribute("maxFiles", "1");
			xml.appendAttribute("async", "false");
		}
		xml.appendEnd();
	}

	/**
	 * Flattens the list of accepted mime types into a format suitable for rendering.
	 *
	 * @param types the list of accepted mime types
	 * @return the list of accepted mime types in a format suitable for rendering
	 */
	private String typesToString(final List<String> types) {
		if (types == null || types.isEmpty()) {
			return null;
		}

		StringBuffer typesString = new StringBuffer();

		for (Iterator<String> iter = types.iterator(); iter.hasNext();) {
			typesString.append(iter.next());

			if (iter.hasNext()) {
				typesString.append(',');
			}
		}

		return typesString.toString();
	}
}
