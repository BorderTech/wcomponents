package com.github.bordertech.wcomponents;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The WSingleSelect input component allows the user to select one option from a list. The list of options that can be
 * selected are supplied at construction time as a parameter in the constructor or via the {@link #setOptions(List)}
 * method. The list of options are java objects that are rendered using their toString() by default.
 * </p>
 * <p>
 * Use the {@link #getSelected() getSelected} method to determine which of the list of options was chosen by the user.
 * Note that getSelected returns one of the object instances supplied in the original list of options.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSingleSelect extends AbstractWSingleSelectList implements AjaxTrigger, AjaxTarget,
		SubordinateTrigger,
		SubordinateTarget {

	/**
	 * Creates an empty WSingleSelect.
	 */
	public WSingleSelect() {
		this((List) null);
	}

	/**
	 * Creates a WSingleSelect containing the specified options.
	 *
	 * @param options the options to display.
	 */
	public WSingleSelect(final Object[] options) {
		this(Arrays.asList(options));
	}

	/**
	 * Creates a WSingleSelect containing the specified options.
	 *
	 * @param options the options to display.
	 */
	public WSingleSelect(final List options) {
		super(options, true);
	}

	/**
	 * Creates a WSingleSelect with the options provided by the given table.
	 *
	 * @param table the table to obtain the list's options from.
	 */
	public WSingleSelect(final Object table) {
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
	 * @return a new SingleSelectModel.
	 */
	@Override // For type safety only
	protected SingleSelectModel newComponentModel() {
		return new SingleSelectModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected SingleSelectModel getComponentModel() {
		return (SingleSelectModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected SingleSelectModel getOrCreateComponentModel() {
		return (SingleSelectModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class SingleSelectModel extends SelectionModel {

		/**
		 * The number of visible rows to display.
		 */
		private int rows;
	}
}
