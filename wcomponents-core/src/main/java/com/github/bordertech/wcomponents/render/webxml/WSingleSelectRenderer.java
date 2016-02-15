package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WSingleSelect;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Util;
import java.util.List;

/**
 * The Renderer for {@link WSingleSelect}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WSingleSelectRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WSingleSelect.
	 *
	 * @param component the WSingleSelect to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WSingleSelect listBox = (WSingleSelect) component;
		XmlStringBuilder xml = renderContext.getWriter();
		String dataKey = listBox.getListCacheKey();
		boolean encode = listBox.getDescEncode();
		boolean readOnly = listBox.isReadOnly();
		int rows = listBox.getRows();

		xml.appendTagOpen("ui:listbox");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("data", dataKey != null && !readOnly, dataKey);
		xml.appendOptionalAttribute("disabled", listBox.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", listBox.isHidden(), "true");
		xml.appendOptionalAttribute("required", listBox.isMandatory(), "true");
		xml.appendOptionalAttribute("readOnly", readOnly, "true");
		xml.appendOptionalAttribute("submitOnChange", listBox.isSubmitOnChange(), "true");
		xml.appendOptionalAttribute("tabIndex", component.hasTabIndex(), listBox.getTabIndex());
		xml.appendOptionalAttribute("toolTip", component.getToolTip());
		xml.appendOptionalAttribute("accessibleText", component.getAccessibleText());
		xml.appendOptionalAttribute("rows", rows >= 2, rows);
		xml.appendAttribute("single", "true");

		xml.appendClose();

		// Options
		List<?> options = listBox.getOptions();
		boolean renderSelectionsOnly = readOnly || dataKey != null;

		if (options != null) {
			int optionIndex = 0;
			Object selectedOption = listBox.getSelected();

			for (Object option : options) {
				if (option instanceof OptionGroup) {
					xml.appendTagOpen("ui:optgroup");
					xml.appendAttribute("label", ((OptionGroup) option).getDesc());
					xml.appendClose();

					for (Object nestedOption : ((OptionGroup) option).getOptions()) {
						renderOption(listBox, nestedOption, optionIndex++, xml, selectedOption,
								renderSelectionsOnly, encode);
					}

					xml.appendEndTag("ui:optgroup");
				} else {
					renderOption(listBox, option, optionIndex++, xml, selectedOption,
							renderSelectionsOnly, encode);
				}
			}
		}

		xml.appendEndTag("ui:listbox");
	}

	/**
	 * Renders a single option within the list box.
	 *
	 * @param listBox the list box being rendered.
	 * @param option the option to render.
	 * @param optionIndex the index of the option. OptionGroups are not counted.
	 * @param html the XmlStringBuilder to paint to.
	 * @param selectedOption the selected option.
	 * @param renderSelectionsOnly true to only render selected options, false to render all options.
	 * @param encode true if option descriptions should be encoded, false if not.
	 */
	private void renderOption(final WSingleSelect listBox, final Object option,
			final int optionIndex, final XmlStringBuilder html,
			final Object selectedOption, final boolean renderSelectionsOnly,
			final boolean encode) {
		boolean selected = Util.equals(option, selectedOption);

		if (selected || !renderSelectionsOnly) {
			// Get Code and Desc
			String code = listBox.getCode(option, optionIndex);
			String desc = listBox.getDesc(option, optionIndex);

			// Check for null option (ie null or empty). Match isEmpty() logic.
			boolean isNull = option == null ? true : (option.toString().length() == 0);

			// Render Option
			html.appendTagOpen("ui:option");
			html.appendAttribute("value", code);
			html.appendOptionalAttribute("selected", selected, "true");
			html.appendOptionalAttribute("isNull", isNull, "true");
			html.appendClose();
			html.append(desc, encode);
			html.appendEndTag("ui:option");
		}
	}
}
