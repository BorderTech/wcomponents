package com.github.bordertech.wcomponents.render.json;

import com.github.bordertech.wcomponents.*;
import com.github.bordertech.wcomponents.render.webxml.AbstractWebXmlRenderer;
import com.github.bordertech.wcomponents.render.webxml.DiagnosticRenderUtil;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.validation.Diagnostic;

import java.util.List;

/**
 * The {@link Renderer} for {@link WCheckBox}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class WCheckBoxRenderer extends AbstractWebXmlRenderer {

	/**
	 * XML element name.
	 */
	private static final String TAG_NAME = "html:script";

	private static final String LABEL_JSON_1 = "{\"tagName\":\"ui:label\",\"attributes\":[{\"value\":\"Button and Checkbox examples:\"}]}";
	private static final String TEXT_JSON_1 = "{\"tagName\":\"ui:text\",\"attributes\":[{\"value\":\"Button and Checkbox examples:\"}]}";
	private static final String CHECKBOX_JSON_1 = "{\"tagName\":\"ui:checkbox\",\"attributes\":[{\"disabled\":true}]}";
	private static final String CHECKBOX_JSON_2 = "{\"tagName\":\"ui:checkbox\",\"attributes\":[{\"disabled\":false}]}";
	private static final String BUTTON_JSON_1 = "{\"tagName\":\"ui:button\",\"attributes\":[{\"disabled\":true}]}";
	private static final String BUTTON_JSON_2 = "{\"tagName\":\"ui:button\",\"attributes\":[{\"disabled\":false}]}";
	private static final String CONTAINER_JSON_1 = "{\"tagName\":\"ui:container\",\"children\":["+LABEL_JSON_1+","
		+TEXT_JSON_1+","+CHECKBOX_JSON_1+","+CHECKBOX_JSON_2+","+BUTTON_JSON_1+","+BUTTON_JSON_2+","+TEXT_JSON_1+"]}";


	/**
	 * Paints the given WCheckBox.
	 *
	 * @param component the WCheckBox to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WCheckBox checkBox = (WCheckBox) component;
		XmlStringBuilder xml = renderContext.getWriter();
		//boolean readOnly = checkBox.isReadOnly();

		xml.appendTagOpen(TAG_NAME);
		xml.appendOptionalAttribute("id", "checkbox-test");
		xml.appendClose();
		xml.append(CONTAINER_JSON_1);

		DiagnosticRenderUtil.renderDiagnostics(checkBox, renderContext);
		xml.appendEndTag(TAG_NAME);
	}

}
