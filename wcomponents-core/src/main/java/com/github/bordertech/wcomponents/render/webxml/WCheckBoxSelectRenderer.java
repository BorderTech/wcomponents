package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.List;

/**
 * The Renderer for {@link WCheckBoxSelect}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WCheckBoxSelectRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WCheckBoxSelect.
	 *
	 * @param component the WCheckBoxSelect to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WCheckBoxSelect select = (WCheckBoxSelect) component;
		XmlStringBuilder xml = renderContext.getWriter();
		int tabIndex = select.getTabIndex();
		int cols = select.getButtonColumns();
		boolean encode = select.getDescEncode();
		boolean readOnly = select.isReadOnly();
		int min = select.getMinSelect();
		int max = select.getMaxSelect();

		xml.appendTagOpen("ui:checkBoxSelect");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("disabled", select.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", select.isHidden(), "true");
		xml.appendOptionalAttribute("required", select.isMandatory(), "true");
		xml.appendOptionalAttribute("readOnly", readOnly, "true");
		xml.appendOptionalAttribute("submitOnChange", select.isSubmitOnChange(), "true");
		xml.appendOptionalAttribute("tabIndex", component.hasTabIndex(), tabIndex);
		xml.appendOptionalAttribute("toolTip", component.getToolTip());
		xml.appendOptionalAttribute("accessibleText", component.getAccessibleText());
		xml.appendOptionalAttribute("frameless", select.isFrameless(), "true");

		switch (select.getButtonLayout()) {
			case COLUMNS:
				xml.appendAttribute("layout", "column");
				xml.appendOptionalAttribute("layoutColumnCount", cols > 0, cols);
				break;
			case FLAT:
				xml.appendAttribute("layout", "flat");
				break;
			case STACKED:
				xml.appendAttribute("layout", "stacked");
				break;
			default:
				throw new SystemException("Unknown layout type: " + select.getButtonLayout());
		}

		xml.appendOptionalAttribute("min", min > 0, min);
		xml.appendOptionalAttribute("max", max > 0, max);

		xml.appendClose();

		// Options
		List<?> options = select.getOptions();
		boolean renderSelectionsOnly = readOnly;

		if (options != null) {
			int optionIndex = 0;
			List<?> selections = select.getSelected();

			for (Object option : options) {
				if (option instanceof OptionGroup) {
					throw new SystemException("Option groups not supported in WCheckBoxSelect.");
				} else {
					renderOption(select, option, optionIndex++, xml, selections,
							renderSelectionsOnly, encode);
				}
			}
		}

		xml.appendEndTag("ui:checkBoxSelect");
	}

	/**
	 * Renders a single option within the check box select.
	 *
	 * @param select the check box select being rendered.
	 * @param option the option to render.
	 * @param optionIndex the index of the option. OptionGroups are not counted.
	 * @param html the XmlStringBuilder to paint to.
	 * @param selections the list of selected options.
	 * @param renderSelectionsOnly true to only render selected options, false to render all options.
	 * @param encode true if the option description should be encoded, false if not.
	 */
	private void renderOption(final WCheckBoxSelect select, final Object option,
			final int optionIndex,
			final XmlStringBuilder html, final List<?> selections,
			final boolean renderSelectionsOnly, final boolean encode) {
		boolean selected = selections.contains(option);

		if (selected || !renderSelectionsOnly) {
			// Get Code and Desc
			String code = select.getCode(option, optionIndex);
			String desc = select.getDesc(option, optionIndex);

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
