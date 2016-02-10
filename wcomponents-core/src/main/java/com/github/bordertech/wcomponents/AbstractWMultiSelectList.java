package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AbstractWMultiSelectList provides the basis for components that allow the user to select multiple items from a list.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractWMultiSelectList extends AbstractWSelectList {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractWMultiSelectList.class);

	/**
	 * Constant for rendering an empty list, to save object churn.
	 */
	protected static final Object[] EMPTY = new Object[0];

	/**
	 * Constant for representing a blank selection, to save object churn.
	 */
	protected static final List<Object> NO_SELECTION = Collections.emptyList();

	/**
	 * Creates an AbstractWMultiSelectList.
	 *
	 * @param options the list's options.
	 * @param allowNoSelection if true, allow no option to be selected
	 */
	public AbstractWMultiSelectList(final List<?> options, final boolean allowNoSelection) {
		super(options, allowNoSelection);
	}

	/**
	 * Creates an AbstractWMultiSelectList.
	 *
	 * @param lookupTable the lookup table identifier to obtain the list's options from.
	 * @param allowNoSelection if true, allow no option to be selected
	 */
	public AbstractWMultiSelectList(final Object lookupTable, final boolean allowNoSelection) {
		super(lookupTable, allowNoSelection);
	}

	/**
	 * Returns a string value of the selected item for this users session. If multiple selections have been made, this
	 * will be a comma separated list of string values. If no value is selected, null is returned.
	 *
	 * @return the selected item value as a rendered String
	 */
	@Override
	public String getValueAsString() {
		List<?> selected = getValue();

		if (selected == null || selected.isEmpty()) {
			return null;
		}

		StringBuffer stringValues = new StringBuffer();

		for (int i = 0; i < selected.size(); i++) {
			if (i > 0) {
				stringValues.append(", ");
			}
			stringValues.append(optionToString(selected.get(i)));
		}

		return stringValues.toString();
	}

	/**
	 * @return true if nothing selected or if a selection is required and only the null option has been selected.
	 */
	@Override
	public boolean isEmpty() {
		List<?> selected = getValue();
		if (selected == null || selected.isEmpty()) {
			return true;
		}

		// If selection required, check at least one option is selected that is not null and not empty
		if (!isAllowNoSelection()) {
			for (Object option : selected) {
				// Check option is null or empty
				boolean isNull = option == null ? true : option.toString().length() == 0;
				if (!isNull) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	/**
	 * Retrieves the currently selected options.
	 *
	 * @return the selected options in the given UI context.
	 */
	public List<?> getSelected() {
		return getValue();
	}

	/**
	 * Set the selected options for this users session.
	 *
	 * @param selectedOptions the list of selected options.
	 */
	public void setSelected(final List<?> selectedOptions) {
		setData(selectedOptions);
	}

	/**
	 * @param selected the selected option.
	 * @deprecated Use {{@link #setSelected(List)}.
	 */
	@Deprecated
	public void setSelected(final Object selected) {
		setSelected(Arrays.asList(new Object[]{selected}));
	}

	/**
	 * Returns the selected options for the given user's session.
	 *
	 * @return the selected options in the given UI context.
	 */
	public Object[] getSelectedOptionsAsArray() {
		return getValue().toArray();
	}

	/**
	 * Returns the options which are not selected.
	 *
	 * @return The unselected options(s).
	 */
	public List<?> getNotSelected() {
		List options = getOptions();
		if (options == null || options.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		List notSelected = new ArrayList(options);
		notSelected.removeAll(getSelected());
		return Collections.unmodifiableList(notSelected);
	}

	// ================================
	// DataBound
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		// Validate the selected options (allow handle invalid options)
		findValidOptions(getOptions(), convertDataToList(getData()), true);
	}

	/**
	 * Returns a {@link List} of the selected options. If no options have been selected, then it returns an empty list.
	 * <p>
	 * As getValue calls {@link #getData()} for the currently selected options, it usually expects getData to return
	 * null (for no selection) or a {@link List} of selected options. If the data returned by getData is not null and is
	 * not a List, then setData will either (1) if the data is an array, convert the array to a List or (2) create a
	 * List and add the data as the selected option.
	 * </p>
	 * <p>
	 * getValue will verify the selected option/s are valid. If a selected option does not exist, then it will throw an
	 * {@link IllegalArgumentException}.
	 * </p>
	 *
	 * @return the selected options in the given UI context.
	 */
	@Override
	public List<?> getValue() {
		// Convert data to a list (if necessary)
		List<?> data = convertDataToList(getData());

		// Validate the selected options (allow handle invalid options)
		List<?> validOptions = findValidOptions(getOptions(), data, true);

		if (validOptions == null || validOptions.isEmpty()) {
			return NO_SELECTION;
		}
		return validOptions;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * getData expects the data to be either null (for no selection) or a {@link List} of selected options.
	 * </p>
	 * <p>
	 * getData will check that if no options have been selected (ie data is null) and the list component requires an
	 * option to be selected (ie {@link #isAllowNoSelection()} is false), it will return a list containing the first
	 * option as the default selected option. If the data is an empty list or an empty array, then it will be treated
	 * the same as there being no selection.
	 * </p>
	 */
	@Override
	public Object getData() {
		Object data = super.getData();

		// Treat "empty" the same as null (ie no selection)
		boolean empty = false;
		if (data == null) {
			empty = true;
		} else if (data instanceof List<?>) {
			empty = ((List<?>) data).isEmpty();
		} else if (data instanceof Object[]) {
			empty = ((Object[]) data).length == 0;
		}

		// Check if we need to default to the first option
		if (empty && !isAllowNoSelection()) {
			List<?> options = getOptions();
			if (options != null && !options.isEmpty()) {
				// Check if NULL is an option
				if (SelectListUtil.containsOption(options, null)) {
					return Arrays.asList(new Object[]{null});
				} else {
					// Use the first option
					Object firstOption = SelectListUtil.getFirstOption(options);
					return Arrays.asList(new Object[]{firstOption});
				}
			}
		}

		return data;
	}

	/**
	 * Sets the data that this component displays/edits.
	 * <p>
	 * setData expects the data being passed in to be either null (for no selection) or a {@link List} of selected
	 * options. If an empty list is passed in, then it will be treated the same as null.
	 * </p>
	 * <p>
	 * If the data is not null and is not a List, then setData will either (1) if the data is an array, convert the
	 * array to a List or (2) create a List and add the data as the selected option.
	 * </p>
	 * <p>
	 * setData will verify the selected option/s are valid. If a selected option does not exist, then it will throw an
	 * {@link IllegalArgumentException}.
	 * </p>
	 * <p>
	 * If the list component requires a selection (ie {@link #isAllowNoSelection()} is false) and the data being set is
	 * null and null is not a valid option, then setData will throw an {@link IllegalArgumentException}.
	 * </p>
	 *
	 * @param data the data to set which is usually a List containing the selected options or null for no selection.
	 * @throws IllegalArgumentException if the data Object is not null but the options List is null or empty.
	 */
	@Override
	public void setData(final Object data) {
		List<?> options = getOptions();
		if (!isEditable() && data != null && (options == null || options.isEmpty())) {
			throw new IllegalStateException(
					"Should not set selections on a list component with no options.");
		}

		// Convert data to a list (if necessary)
		List<?> selected = convertDataToList(data);

		// Validate the selected options
		List<?> validOptions = findValidOptions(options, selected, false);
		super.setData(validOptions);
	}

	/**
	 * Convert the data to a list (if necessary).
	 *
	 * @param data the data to convert to a list
	 * @return the data converted to a list
	 */
	private List<?> convertDataToList(final Object data) {
		if (data == null) {
			return null;
		} else if (data instanceof List) {
			return (List<?>) data;
		} else if (data instanceof Object[]) {
			return Arrays.asList((Object[]) data);
		} else {
			return Arrays.asList(new Object[]{data});
		}
	}

	/**
	 * Find the valid options for the selected data, allowing for option/code and legacy matching.
	 *
	 * @param options the list of options
	 * @param selected the options to search for
	 * @param handleInvalid true if allow handle invalid options
	 * @return the valid option
	 */
	private List<?> findValidOptions(final List<?> options, final List<?> selected,
			final boolean handleInvalid) {
		// No selection made
		if (selected == null || selected.isEmpty()) {
			// No Selection required, or no options to select
			if (isAllowNoSelection() || options == null || options.isEmpty()) {
				return null;
			} else { // Selection is required
				// Check if NULL is an option
				if (selected == null && SelectListUtil.containsOption(options, null)) {
					return null;
				}

				// Must have a selected option
				throw new IllegalStateException(
						"A valid option must be selected for lists that require a selected option.");
			}
		}

		// Check we have valid options (If options are matched via Option/Code or Legacy Matching, then return a list of
		// the actual options)
		List<Object> validSelections = new ArrayList<>();

		for (Object selectedOption : selected) {
			boolean found = false;

			if (options != null) {
				if (selectedOption == null) {
					found = SelectListUtil.containsOption(options, null);
					if (found) {
						validSelections.add(null);
					}
				} else {
					Object option = SelectListUtil.getOptionWithMatching(options, selectedOption);
					if (option != null) {
						found = true;
						validSelections.add(option);
					}
				}
			}

			if (!found) {
				if (isEditable() && selectedOption instanceof String) {
					validSelections.add(selectedOption);
				} else if (handleInvalid) { // Handle invalid option
					List<?> valid = doHandleInvalidOption(selectedOption);
					setSelected(valid);
					return valid;
				} else {
					throw new IllegalStateException("The selection \"" + selectedOption
							+ "\" is not an available option.");
				}
			}
		}

		return validSelections;

	}

	/**
	 * Handle the situation where a selected option is no longer a valid option.
	 * <p>
	 * By default, this method throws an {@link IllegalStateException}.
	 * </p>
	 * <p>
	 * This method can be overridden to handle an invalid option without throwing the exception. It must return a list
	 * of valid options that will be set as the selected options.
	 * </p>
	 *
	 * @param invalidOption the invalid option
	 * @return the list of valid options to be set as the selected options
	 */
	protected List<?> doHandleInvalidOption(final Object invalidOption) {
		throw new IllegalStateException("The selection \"" + invalidOption
				+ "\" is invalid. It is not an available option.");
	}

	// ================================
	// Handle Request
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		// First we need to figure out if the new selections are the same as the
		// previous selections.
		final List<?> newSelections = getRequestValue(request);
		List<?> priorSelections = getValue();

		boolean changed = !selectionsEqual(newSelections, priorSelections);

		if (changed) {
			setData(newSelections);
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<?> getRequestValue(final Request request) {
		if (isPresent(request)) {
			return getNewSelections(request);
		} else {
			return getValue();
		}
	}

	/**
	 * Determines which selections have been added in the given request.
	 *
	 * @param request the current request
	 * @return a list of selections that have been added in the given request.
	 */
	protected List<?> getNewSelections(final Request request) {
		String[] paramValues = request.getParameterValues(getId());
		if (paramValues == null || paramValues.length == 0) {
			return NO_SELECTION;
		}

		List<String> values = Arrays.asList(paramValues);
		List<Object> newSelections = new ArrayList<>(values.size());

		// Figure out which options have been selected.
		List<?> options = getOptions();
		if (options == null || options.isEmpty()) {
			if (!isEditable()) {
				// User could not have made a selection.
				return NO_SELECTION;
			}
			options = Collections.EMPTY_LIST;
		}

		for (Object value : values) {
			boolean found = false;
			int optionIndex = 0;

			for (Object option : options) {
				if (option instanceof OptionGroup) {
					List<?> groupOptions = ((OptionGroup) option).getOptions();
					if (groupOptions != null) {
						for (Object nestedOption : groupOptions) {
							if (value.equals(optionToCode(nestedOption, optionIndex++))) {
								newSelections.add(nestedOption);
								found = true;
								break;
							}
						}
					}
				} else if (value.equals(optionToCode(option, optionIndex++))) {
					newSelections.add(option);
					found = true;
					break;
				}
			}

			if (!found) {
				if (isEditable()) {
					newSelections.add(value);
				} else {
					LOG.warn(
							"Option \"" + value + "\" on the request is not a valid option. Will be ignored.");
				}
			}
		}

		// If no valid options found, then return the current settings
		if (newSelections.isEmpty()) {
			LOG.warn("No options on the request are valid. Will be ignored.");
			return getValue();
		}

		// If must have selection and more than 1 option selected, remove the "null" entry if it was selected.
		if (!isAllowNoSelection() && newSelections.size() > 1) {
			List<Object> filtered = new ArrayList<>();
			Object nullOption = null;
			for (Object option : newSelections) {
				// Check option is null or empty
				boolean isNull = option == null ? true : option.toString().length() == 0;
				if (isNull) {
					// Hold the option as it could be "null" or "empty"
					nullOption = option;
				} else {
					filtered.add(option);
				}
			}
			// In the case where only null options were selected, then add one nullOption
			if (filtered.isEmpty()) {
				filtered.add(nullOption);
			}
			return filtered;
		} else {
			return newSelections;
		}
	}

	/**
	 * Selection lists are considered equal if they have the same items (order is not important). An empty list is
	 * considered equal to a null list.
	 *
	 * @param list1 the first list to check.
	 * @param list2 the second list to check.
	 * @return true if the lists are equal, false otherwise.
	 */
	private boolean selectionsEqual(final List<?> list1, final List<?> list2) {
		if (isSelectionOrderable()) {
			return Util.equals(list1, list2);
		}

		// Empty or null lists
		if ((list1 == null || list1.isEmpty()) && (list2 == null || list2.isEmpty())) {
			return true;
		}

		// Same size and contain same entries
		return list1 != null && list2 != null && list1.size() == list2.size() && list1.
				containsAll(list2);
	}

	/**
	 * Override WInput's validateComponent to perform further validation on the options selected.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		super.validateComponent(diags);

		List<?> selected = getValue();

		// Only validate max and min if options have been selected
		if (!selected.isEmpty()) {
			int value = selected.size();
			int min = getMinSelect();
			int max = getMaxSelect();

			if (min > 0 && value < min) {
				diags.add(
						createErrorDiagnostic(InternalMessages.DEFAULT_VALIDATION_ERROR_MIN_SELECT,
								this, min));
			}

			if (max > 0 && value > max) {
				diags.add(
						createErrorDiagnostic(InternalMessages.DEFAULT_VALIDATION_ERROR_MAX_SELECT,
								this, max));
			}
		}
	}

	/**
	 * Retrieves the minimum number of options that can be selected.
	 *
	 * @return the minimum number of options that can be selected
	 */
	public int getMinSelect() {
		return getComponentModel().minSelect;
	}

	/**
	 * Sets the minimum number of options that can be selected.
	 *
	 * @param minSelect the minimum number of options can must be selected.
	 */
	public void setMinSelect(final int minSelect) {
		getOrCreateComponentModel().minSelect = minSelect;
	}

	/**
	 * Retrieves the maximum number of options that can be selected.
	 *
	 * @return the maximum number of options that can be selected
	 */
	public int getMaxSelect() {
		return getComponentModel().maxSelect;
	}

	/**
	 * Sets the maximum number of options that can be selected.
	 *
	 * @param maxSelect the maximum number of options can must be selected.
	 */
	public void setMaxSelect(final int maxSelect) {
		getOrCreateComponentModel().maxSelect = maxSelect;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MultiSelectionModel newComponentModel() {
		return new MultiSelectionModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MultiSelectionModel getComponentModel() {
		return (MultiSelectionModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MultiSelectionModel getOrCreateComponentModel() {
		return (MultiSelectionModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the multi select list.
	 */
	public static class MultiSelectionModel extends SelectionModel {

		/**
		 * The minimum number of options that can be selected.
		 */
		private int minSelect;
		/**
		 * The maximum number of options that can be selected.
		 */
		private int maxSelect;
	}

}
