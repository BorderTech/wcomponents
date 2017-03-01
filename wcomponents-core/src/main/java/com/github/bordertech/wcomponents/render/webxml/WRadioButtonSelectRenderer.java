package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.util.List;

/**
 * The Renderer for {@link WRadioButtonSelect}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WRadioButtonSelectRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WRadioButtonSelect.
	 *
	 * @param component the WRadioButtonSelect to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WRadioButtonSelect rbSelect = (WRadioButtonSelect) component;
		XmlStringBuilder xml = renderContext.getWriter();
		int tabIndex = rbSelect.getTabIndex();
		int cols = rbSelect.getButtonColumns();
		boolean readOnly = rbSelect.isReadOnly();

		xml.appendTagOpen("ui:radiobuttonselect");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", rbSelect.isHidden(), "true");
		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
		} else {
			xml.appendOptionalAttribute("disabled", rbSelect.isDisabled(), "true");
			xml.appendOptionalAttribute("required", rbSelect.isMandatory(), "true");
			xml.appendOptionalAttribute("submitOnChange", rbSelect.isSubmitOnChange(), "true");
			xml.appendOptionalAttribute("tabIndex", component.hasTabIndex(), String.valueOf(tabIndex));
			xml.appendOptionalAttribute("toolTip", component.getToolTip());
			xml.appendOptionalAttribute("accessibleText", component.getAccessibleText());
		}
		xml.appendOptionalAttribute("frameless", rbSelect.isFrameless(), "true");

		switch (rbSelect.getButtonLayout()) {
			case COLUMNS:
				xml.appendAttribute("layout", "column");
				xml.appendOptionalAttribute("layoutColumnCount", cols > 0, String.valueOf(cols));
				break;
			case FLAT:
				xml.appendAttribute("layout", "flat");
				break;
			case STACKED:
				xml.appendAttribute("layout", "stacked");
				break;
			default:
				throw new SystemException("Unknown radio button layout: " + rbSelect.getButtonLayout());
		}

		xml.appendClose();

		// Options
		List<?> options = rbSelect.getOptions();
		boolean renderSelectionsOnly = readOnly;

		if (options != null) {
			int optionIndex = 0;
			Object selectedOption = rbSelect.getSelected();

			for (Object option : options) {
				if (option instanceof OptionGroup) {
					throw new SystemException("Option groups not supported in WRadioButtonSelect.");
				} else {
					renderOption(rbSelect, option, optionIndex++, xml, selectedOption, renderSelectionsOnly);
				}
			}
		}

		xml.appendEndTag("ui:radiobuttonselect");

		if (rbSelect.isAjax()) {
			paintAjax(rbSelect, xml);
		}
	}

	/**
	 * Renders a single option within the group.
	 *
	 * @param rbSelect the radio button select being rendered.
	 * @param option the option to render.
	 * @param optionIndex the index of the option. OptionGroups are not counted.
	 * @param html the XmlStringBuilder to paint to.
	 * @param selectedOption the selected option
	 * @param renderSelectionsOnly true to only render selected options, false to render all options.
	 */
	private void renderOption(final WRadioButtonSelect rbSelect, final Object option,
			final int optionIndex, final XmlStringBuilder html,
			final Object selectedOption, final boolean renderSelectionsOnly) {
		boolean selected = Util.equals(option, selectedOption);

		if (selected || !renderSelectionsOnly) {
			// Get Code and Desc
			String code = rbSelect.getCode(option, optionIndex);
			String desc = rbSelect.getDesc(option, optionIndex);

			// Check for null option (ie null or empty). Match isEmpty() logic.
			boolean isNull = option == null ? true : (option.toString().length() == 0);

			// Render Option
			html.appendTagOpen("ui:option");
			html.appendAttribute("value", code);
			html.appendOptionalAttribute("selected", selected, "true");
			html.appendOptionalAttribute("isNull", isNull, "true");
			html.appendClose();
			html.appendEscaped(desc);
			html.appendEndTag("ui:option");
		}
	}

	/**
	 * Paints the AJAX information for the given WRadioButtonSelect.
	 *
	 * @param rbSelect the WRadioButtonSelect to paint.
	 * @param xml the XmlStringBuilder to paint to.
	 */
	private void paintAjax(final WRadioButtonSelect rbSelect, final XmlStringBuilder xml) {
		// Start tag
		xml.appendTagOpen("ui:ajaxtrigger");
		xml.appendAttribute("triggerId", rbSelect.getId());
		xml.appendClose();

		// Target
		xml.appendTagOpen("ui:ajaxtargetid");
		xml.appendAttribute("targetId", rbSelect.getAjaxTarget().getId());
		xml.appendEnd();

		// End tag
		xml.appendEndTag("ui:ajaxtrigger");
	}
}
