package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMultiDropdown;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.util.List;

/**
 * The Renderer for {@link WMultiDropdown}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WMultiDropdownRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WMultiDropdown.
	 *
	 * @param component the WMultiDropdown to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WMultiDropdown dropdown = (WMultiDropdown) component;
		XmlStringBuilder xml = renderContext.getWriter();
		String dataKey = dropdown.getListCacheKey();
		int tabIndex = dropdown.getTabIndex();
		boolean encode = dropdown.getDescEncode();
		boolean readOnly = dropdown.isReadOnly();
		int min = dropdown.getMinSelect();
		int max = dropdown.getMaxSelect();

		xml.appendTagOpen("ui:multiDropdown");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("data", dataKey != null && !readOnly, dataKey);
		xml.appendOptionalAttribute("disabled", dropdown.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", dropdown.isHidden(), "true");
		xml.appendOptionalAttribute("required", dropdown.isMandatory(), "true");
		xml.appendOptionalAttribute("readOnly", readOnly, "true");
		xml.appendOptionalAttribute("submitOnChange", dropdown.isSubmitOnChange(), "true");
		xml.appendOptionalAttribute("tabIndex", component.hasTabIndex(), tabIndex);
		xml.appendOptionalAttribute("toolTip", component.getToolTip());
		xml.appendOptionalAttribute("accessibleText", component.getAccessibleText());
		xml.appendOptionalAttribute("min", min > 0, min);
		xml.appendOptionalAttribute("max", max > 0, max);

		xml.appendClose();

		// Options
		List<?> options = dropdown.getOptions();
		boolean renderSelectionsOnly = dropdown.isReadOnly() || dataKey != null;

		if (options != null) {
			int optionIndex = 0;
			List<?> selections = dropdown.getSelected();

			for (Object option : options) {
				if (option instanceof OptionGroup) {
					xml.appendTagOpen("ui:optgroup");
					xml.appendAttribute("label", ((OptionGroup) option).getDesc());
					xml.appendClose();

					for (Object nestedOption : ((OptionGroup) option).getOptions()) {
						renderOption(dropdown, nestedOption, optionIndex++, xml, selections,
								renderSelectionsOnly, encode);
					}

					xml.appendEndTag("ui:optgroup");
				} else {
					renderOption(dropdown, option, optionIndex++, xml, selections,
							renderSelectionsOnly, encode);
				}
			}
		}

		// End tag
		xml.appendEndTag("ui:multiDropdown");
	}

	/**
	 * Renders a single option within the dropdown.
	 *
	 * @param dropdown the dropdown being rendered.
	 * @param option the option to render.
	 * @param optionIndex the index of the option. OptionGroups are not counted.
	 * @param html the XmlStringBuilder to paint to.
	 * @param selections the list of selected options.
	 * @param renderSelectionsOnly true to only render selected options, false to render all options.
	 * @param encode true if the option description should be encoded, false if not.
	 */
	private void renderOption(final WMultiDropdown dropdown, final Object option,
			final int optionIndex, final XmlStringBuilder html,
			final List<?> selections, final boolean renderSelectionsOnly,
			final boolean encode) {
		boolean selected = (selections != null && selections.contains(option));

		if (selected || !renderSelectionsOnly) {
			// Get Code and Desc
			String code = dropdown.getCode(option, optionIndex);
			String desc = dropdown.getDesc(option, optionIndex);

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
