package com.github.bordertech.wcomponents;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The WMultiSelect input component allows the user to select one or more options from a list. The list of options that
 * can be selected are supplied at construction time as a parameter in the constructor or via the
 * {@link #setOptions(List)} method. The list of options are java objects that are rendered using their toString() by
 * default.
 * </p>
 * <p>
 * Use the {@link #getSelected() getSelected} method to determine which of the list of options were chosen by the user.
 * Note that getSelectedOptions will return a sub-set of the object instances supplied in the original list of options.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiSelect extends AbstractWMultiSelectList implements AjaxTrigger, AjaxTarget,
		SubordinateTrigger,
		SubordinateTarget {

	/**
	 * Creates an empty WMultiSelect.
	 */
	public WMultiSelect() {
		this((List) null);
	}

	/**
	 * Creates a WMultiSelect containing the specified options.
	 *
	 * @param options the options to display.
	 */
	public WMultiSelect(final Object[] options) {
		this(Arrays.asList(options));
	}

	/**
	 * Creates a WMultiSelect containing the specified options.
	 *
	 * @param options the options to display.
	 */
	public WMultiSelect(final List options) {
		super(options, true);
	}

	/**
	 * Creates a WMultiSelect with the options provided by the given table.
	 *
	 * @param table the table to obtain the list's options from.
	 */
	public WMultiSelect(final Object table) {
		super(table, true);
	}

	/**
	 * Sets the maximum number of rows that are visible in the list at any one time. If the number of rows is less than
	 * two, then the default number of rows will be displayed.
	 *
	 * @param rows the number of rows to display.
	 */
	public void setRows(final int rows) {
		getOrCreateComponentModel().rows = rows;
	}

	/**
	 * @return the number of rows to display in the list.
	 */
	public int getRows() {
		return getComponentModel().rows;
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new MultiSelectModel.
	 */
	@Override // For type safety only
	protected MultiSelectModel newComponentModel() {
		return new MultiSelectModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected MultiSelectModel getComponentModel() {
		return (MultiSelectModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected MultiSelectModel getOrCreateComponentModel() {
		return (MultiSelectModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class MultiSelectModel extends MultiSelectionModel {

		/**
		 * The number of visible rows to display.
		 */
		private int rows;

		/**
		 * Sets the number of visible rows to display.
		 *
		 * @param rows The number of rows to display.
		 * @deprecated not required. Use setRows on the component.
		 */
		@Deprecated
		protected void setRows(final int rows) {
			this.rows = rows;
		}
	}
}
