package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The WMultiDropdown component allows multiple dropdown elements to be generated dynamically on the client, without
 * requiring the page to be reloaded. This component takes its appearance and attributes from a regular drop down but
 * allows for one or more items of text to be entered via the add link adjacent to the right of the input.
 * </p>
 * <p>
 * This component is useful in instances where the user needs to select one or more items into the interfaces for a
 * particular field. For instance, the user may have one or more aliases that need to be entered into the system.
 * </p>
 * <p>
 * The following attributes can be set on WMultiDropdown:
 * </p>
 * <ul>
 * <li>maxInputs: The maximum number of text inputs the user can add to the component. Client-side functionality will
 * stop users adding more than the allowable number of inputs via the UI. This class chops off any excess inputs if an
 * attempt is made to add them programmatically.</li>
 * </ul>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiDropdown extends AbstractWMultiSelectList implements AjaxTrigger, AjaxTarget,
		SubordinateTarget {

	/**
	 * Creates an empty WMultiDropdown.
	 */
	public WMultiDropdown() {
		this((List) null);
	}

	/**
	 * Creates a WMultiDropdown with the specified list of options.
	 *
	 * @param aList the list of available options.
	 */
	public WMultiDropdown(final List aList) {
		super(aList, false);
	}

	/**
	 * Creates a WMultiDropdown with the specified list of options.
	 *
	 * @param options the list of available options.
	 */
	public WMultiDropdown(final Object[] options) {
		super(Arrays.asList(options), false);
	}

	/**
	 * Creates a WMultiDropdown with the options provided by the given table.
	 *
	 * @param table the table to obtain the list's options from.
	 */
	public WMultiDropdown(final Object table) {
		super(table, false);
	}

	/**
	 * Sets the maximum allowable number of inputs.
	 *
	 * @param maxInputs the maximum allowable number of inputs, or &lt;=0 for unlimited inputs.
	 * @deprecated Use {{@link #setMaxSelect(int)} instead
	 */
	@Deprecated
	public void setMaxInputs(final int maxInputs) {
		setMaxSelect(maxInputs);
	}

	/**
	 * @return the maximum allowable number of inputs
	 * @deprecated Use {{@link #getMaxSelect()} instead
	 */
	@Deprecated
	public int getMaxInputs() {
		return getMaxSelect();
	}

	/**
	 * Override getNewSelections to ensure that the max number of inputs is honoured and that there are no duplicates.
	 * This should not normally occur, as the client side js should prevent it.
	 *
	 * @param request the current request
	 * @return a list of selections that have been added in the given request.
	 */
	@Override
	protected List getNewSelections(final Request request) {
		List selections = super.getNewSelections(request);

		if (selections != null) {
			// Ensure that there are no duplicates
			for (int i = 0; i < selections.size(); i++) {
				Object selection = selections.get(i);

				for (int j = i + 1; j < selections.size(); j++) {
					if (Util.equals(selection, selections.get(j))) {
						selections.remove(j--);
					}
				}
			}
		}

		return selections;
	}

}
