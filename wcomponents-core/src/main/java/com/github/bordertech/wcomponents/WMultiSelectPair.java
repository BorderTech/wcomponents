package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import java.util.List;

/**
 * <p>
 * This component is functionally the same as {@link WMultiSelect} but it looks different. It has a list of options in a
 * box on the left, and a box on the right containing the selected options. Add and Remove buttons enable users to
 * select from the list of options.</p>
 *
 * @author Ming Gao
 */
public class WMultiSelectPair extends WMultiSelect implements AjaxTrigger, AjaxTarget,
		SubordinateTarget {

	/**
	 * The default number of rows to display in the list boxes.
	 */
	public static final int DEFAULT_ROWS = 7;

	/**
	 * Creates an empty WMultiSelectPair.
	 */
	public WMultiSelectPair() {
		setRows(DEFAULT_ROWS);
	}

	/**
	 * Creates a WMultiSelectPair with the specified list of options.
	 *
	 * @param aList the list of available options.
	 */
	public WMultiSelectPair(final List aList) {
		super(aList);
		setRows(DEFAULT_ROWS);
	}

	/**
	 * Creates a WMultiSelectPair with the specified list of options.
	 *
	 * @param values the list of available options.
	 */
	public WMultiSelectPair(final Object[] values) {
		super(values);
		setRows(DEFAULT_ROWS);
	}

	/**
	 * Creates a WMultiSelectPair with the options provided by the given table.
	 *
	 * @param table the table to obtain the list's options from.
	 */
	public WMultiSelectPair(final Object table) {
		super(table);
	}

	/**
	 * @return Returns the available list name.
	 */
	public String getAvailableListName() {
		return I18nUtilities.format(null, getComponentModel().availableListName);
	}

	/**
	 * Sets the available list name.
	 *
	 * @param availableListName The availableListName to set.
	 */
	public void setAvailableListName(final String availableListName) {
		getOrCreateComponentModel().availableListName = availableListName;
	}

	/**
	 * @return Returns the selectedListName.
	 */
	public String getSelectedListName() {
		return I18nUtilities.format(null, getComponentModel().selectedListName);
	}

	/**
	 * Sets the selected list name.
	 *
	 * @param selectedListName The selectedListName to set.
	 */
	public void setSelectedListName(final String selectedListName) {
		getOrCreateComponentModel().selectedListName = selectedListName;
	}

	/**
	 * Indicates whether options in the selection list can be re-ordered (shuffled). The default value is false (no
	 * shuffle).
	 *
	 * @return true if shuffle is enabled, false otherwise.
	 */
	public boolean isShuffle() {
		return getComponentModel().shuffle;
	}

	/**
	 * Sets whether options in the selection list can be re-ordered (shuffled).
	 *
	 * @param shuffle true if shuffle is enabled, false otherwise.
	 */
	public void setShuffle(final boolean shuffle) {
		getOrCreateComponentModel().shuffle = shuffle;
	}

	/**
	 * Override to return true if shuffle is enabled.
	 *
	 * @return true if shuffle is enabled, false otherwise.
	 */
	@Override
	protected boolean isSelectionOrderable() {
		return isShuffle();
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new MultiSelectPairModel.
	 */
	@Override // For type safety only
	protected MultiSelectPairModel newComponentModel() {
		return new MultiSelectPairModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected MultiSelectPairModel getComponentModel() {
		return (MultiSelectPairModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected MultiSelectPairModel getOrCreateComponentModel() {
		return (MultiSelectPairModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class MultiSelectPairModel extends MultiSelectModel {

		/**
		 * The text to display for the list of available options.
		 */
		private String availableListName = InternalMessages.DEFAULT_MULTI_SELECT_PAIR_OPTIONS_LIST_HEADING;

		/**
		 * The text to display for the list of selected options.
		 */
		private String selectedListName = InternalMessages.DEFAULT_MULTI_SELECT_PAIR_SELECTIONS_LIST_HEADING;

		/**
		 * A flag to indicate whether users are allowed to re-order selected options.
		 */
		private boolean shuffle = false;
	}
}
