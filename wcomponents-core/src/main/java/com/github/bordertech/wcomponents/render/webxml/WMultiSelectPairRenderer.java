package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Renderer for {@link WMultiSelectPair}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
final class WMultiSelectPairRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WMultiSelectPair.
	 *
	 * @param component the WMultiSelectPair to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WMultiSelectPair multiSelectPair = (WMultiSelectPair) component;
		XmlStringBuilder xml = renderContext.getWriter();
		boolean disabled = multiSelectPair.isDisabled();
		boolean readOnly = multiSelectPair.isReadOnly();
		int rows = multiSelectPair.getRows();
		int min = multiSelectPair.getMinSelect();
		int max = multiSelectPair.getMaxSelect();

		xml.appendTagOpen("ui:multiselectpair");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendAttribute("size", rows < 2 ? WMultiSelectPair.DEFAULT_ROWS : rows);
		xml.appendOptionalAttribute("disabled", disabled, "true");
		xml.appendOptionalAttribute("hidden", multiSelectPair.isHidden(), "true");
		xml.appendOptionalAttribute("required", multiSelectPair.isMandatory(), "true");
		xml.appendOptionalAttribute("readOnly", readOnly, "true");
		xml.appendOptionalAttribute("shuffle", multiSelectPair.isShuffle(), "true");
		xml.appendOptionalAttribute("fromListName", multiSelectPair.getAvailableListName());
		xml.appendOptionalAttribute("toListName", multiSelectPair.getSelectedListName());
		xml.appendOptionalAttribute("accessibleText", multiSelectPair.getAccessibleText());
		xml.appendOptionalAttribute("min", min > 0, min);
		xml.appendOptionalAttribute("max", max > 0, max);
		xml.appendClose();

		// Options
		List<?> options = multiSelectPair.getOptions();
		boolean encode = multiSelectPair.getDescEncode();
		boolean renderSelectionsOnly = readOnly;

		if (options != null) {
			if (multiSelectPair.isShuffle()) {
				// We need to render the selected options in order
				renderOrderedOptions(multiSelectPair, options, 0, xml, renderSelectionsOnly, encode);
			} else {
				renderUnorderedOptions(multiSelectPair, options, 0, xml, renderSelectionsOnly,
						encode);
			}
		}

		xml.appendEndTag("ui:multiselectpair");
	}

	/**
	 * Renders the options in selection order.
	 *
	 * @param multiSelectPair the WMultiSelectPair to paint.
	 * @param options the options to render
	 * @param startIndex the starting option index
	 * @param xml the XmlStringBuilder to paint to.
	 * @param renderSelectionsOnly true to only render selected options, false to render all options.
	 * @param encode true if the option description should be encoded, false if not.
	 *
	 * @return the number of options painted.
	 *
	 * TODO: This does not support the legacy allowNull or setSelected using String representations.
	 */
	private int renderOrderedOptions(final WMultiSelectPair multiSelectPair, final List<?> options,
			final int startIndex, final XmlStringBuilder xml,
			final boolean renderSelectionsOnly, final boolean encode) {

		List<?> selections = multiSelectPair.getSelected();
		int optionIndex = startIndex;

		// We can't just render all the unselected options followed by the selected ones
		// in the order they are given to us, as unselected/selected may be intermingled
		// due to the option groups. We therefore recursively render each group.
		// For each group, we:
		// - iterate through the options and
		//     - render the unselected ones
		//     - keep track of the selected ones (index within the selection list + index within the option list)
		// - Once all the unselected items have been rendered, we render the selections
		// This maps selection indices to option indices for the current group
		Map<Integer, Integer> currentSelectionIndices = new HashMap<>();

		for (Object option : options) {
			if (option instanceof OptionGroup) {
				xml.appendTagOpen("ui:optgroup");
				xml.appendAttribute("label", ((OptionGroup) option).getDesc());
				xml.appendClose();

				// Recurse to render options inside option groups.
				List<?> nestedOptions = ((OptionGroup) option).getOptions();
				optionIndex += renderOrderedOptions(multiSelectPair, nestedOptions, optionIndex, xml,
						renderSelectionsOnly, encode);

				xml.appendEndTag("ui:optgroup");
			} else {
				int index = selections.indexOf(option);

				if (index == -1) {
					renderOption(multiSelectPair, option, optionIndex++, xml, selections,
							renderSelectionsOnly, encode);
				} else {
					currentSelectionIndices.put(index, optionIndex++);
				}
			}
		}

		if (!currentSelectionIndices.isEmpty()) {
			// Now sort the selected item's indices and render them in the correct order.
			List<Integer> sortedSelectedIndices = new ArrayList<>(currentSelectionIndices.keySet());
			Collections.sort(sortedSelectedIndices);

			for (int selectionIndex : sortedSelectedIndices) {
				int selectionOptionIndex = currentSelectionIndices.get(selectionIndex);

				renderOption(multiSelectPair, selections.get(selectionIndex), selectionOptionIndex,
						xml, selections, renderSelectionsOnly, encode);
			}
		}

		return optionIndex - startIndex;
	}

	/**
	 * Renders the options in list order.
	 *
	 * @param multiSelectPair the WMultiSelectPair to paint.
	 * @param options the options to render
	 * @param startIndex the starting option index
	 * @param xml the XmlStringBuilder to paint to.
	 * @param renderSelectionsOnly true to only render selected options, false to render all options.
	 * @param encode true if the option description should be encoded, false if not.
	 *
	 * @return the number of options which were rendered.
	 */
	private int renderUnorderedOptions(final WMultiSelectPair multiSelectPair, final List<?> options,
			final int startIndex,
			final XmlStringBuilder xml, final boolean renderSelectionsOnly, final boolean encode) {
		List<?> selections = multiSelectPair.getSelected();
		int optionIndex = startIndex;

		for (Object option : options) {
			if (option instanceof OptionGroup) {
				xml.appendTagOpen("ui:optgroup");
				xml.appendAttribute("label", ((OptionGroup) option).getDesc());
				xml.appendClose();

				// Recurse to render options inside option groups.
				List<?> nestedOptions = ((OptionGroup) option).getOptions();
				optionIndex += renderUnorderedOptions(multiSelectPair, nestedOptions, optionIndex,
						xml, renderSelectionsOnly, encode);

				xml.appendEndTag("ui:optgroup");
			} else {
				renderOption(multiSelectPair, option, optionIndex++, xml, selections,
						renderSelectionsOnly, encode);
			}
		}

		return optionIndex - startIndex;
	}

	/**
	 * Renders a single option within the list box.
	 *
	 * @param multiSelectPair the multi-select pair being rendered.
	 * @param option the option to render.
	 * @param optionIndex the index of the option. OptionGroups are not counted.
	 * @param html the XmlStringBuilder to paint to.
	 * @param selections the list of selected options.
	 * @param renderSelectionsOnly true to only render selected options, false to render all options.
	 * @param encode true if the option description should be encoded, false if not.
	 */
	private void renderOption(final WMultiSelectPair multiSelectPair, final Object option,
			final int optionIndex, final XmlStringBuilder html, final List<?> selections,
			final boolean renderSelectionsOnly, final boolean encode) {
		boolean selected = selections.contains(option);

		if (selected || !renderSelectionsOnly) {
			// Get Code and Desc
			String code = multiSelectPair.getCode(option, optionIndex);
			String desc = multiSelectPair.getDesc(option, optionIndex);

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
