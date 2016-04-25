package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.util.List;

/**
 * The Renderer for {@link WMultiSelect}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WMultiSelectRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WMultiSelect.
	 *
	 * @param component the WMultiSelect to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WMultiSelect listBox = (WMultiSelect) component;
		XmlStringBuilder xml = renderContext.getWriter();
		String dataKey = listBox.getListCacheKey();
		boolean encode = listBox.getDescEncode();
		boolean readOnly = listBox.isReadOnly();
		int rows = listBox.getRows();
		int min = listBox.getMinSelect();
		int max = listBox.getMaxSelect();

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
		xml.appendOptionalAttribute("min", min > 0, min);
		xml.appendOptionalAttribute("max", max > 0, max);
		xml.appendClose();

		// Options
		List<?> options = listBox.getOptions();
		boolean renderSelectionsOnly = readOnly || dataKey != null;

		if (options != null) {
			int optionIndex = 0;
			List<?> selections = listBox.getSelected();
			for (Object option : options) {
				if (option instanceof OptionGroup) {
					xml.appendTagOpen("ui:optgroup");
					xml.appendAttribute("label", ((OptionGroup) option).getDesc());
					xml.appendClose();

					for (Object nestedOption : ((OptionGroup) option).getOptions()) {
						renderOption(listBox, nestedOption, optionIndex++, xml, selections,
								renderSelectionsOnly, encode);
					}

					xml.appendEndTag("ui:optgroup");
				} else {
					renderOption(listBox, option, optionIndex++, xml, selections,
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
	 * @param selections the list of selected options.
	 * @param renderSelectionsOnly true to only render selected options, false to render all options.
	 * @param encode true if the option description should be encoded, false if not.
	 */
	private void renderOption(final WMultiSelect listBox, final Object option,
			final int optionIndex, final XmlStringBuilder html,
			final List<?> selections, final boolean renderSelectionsOnly,
			final boolean encode) {
		boolean selected = selections.contains(option);

		if (selected || !renderSelectionsOnly) {
			// Get Code and Desc
			String code = listBox.getCode(option, optionIndex);
			String desc = listBox.getDesc(option, optionIndex);
			// Render Option
			html.appendTagOpen("ui:option");
			html.appendAttribute("value", code);
			html.appendOptionalAttribute("selected", selected, "true");
			html.appendClose();
			html.append(desc, encode);
			html.appendEndTag("ui:option");
		}
	}
}
