package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AbstractWSingleSelectList provides the basis for components that allow the user to select a single item from a list.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractWSingleSelectList extends AbstractWSelectList {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractWSingleSelectList.class);

	/**
	 * Constant for rendering an empty list, to save object churn.
	 */
	protected static final Object[] EMPTY = new Object[0];

	/**
	 * Creates an AbstractWSingleSelectList.
	 *
	 * @param options the list's options.
	 * @param allowNoSelection if true, allow no option to be selected
	 */
	public AbstractWSingleSelectList(final List<?> options, final boolean allowNoSelection) {
		super(options, allowNoSelection);
	}

	/**
	 * Creates an AbstractWSingleSelectList.
	 *
	 * @param lookupTable the lookup table identifier to obtain the list's options from.
	 * @param allowNoSelection if true, allow no option to be selected
	 */
	public AbstractWSingleSelectList(final Object lookupTable, final boolean allowNoSelection) {
		super(lookupTable, allowNoSelection);
	}

	/**
	 * Returns the selected option for the given user's session.
	 *
	 * @return the selected option.
	 */
	public Object getSelected() {
		return getValue();
	}

	/**
	 * Set the selected option for this users session.
	 *
	 * @param selected the selected option.
	 */
	public void setSelected(final Object selected) {
		setData(selected);
	}

	/**
	 * @return true if an option has been selected
	 */
	public boolean hasSelection() {
		Object selection = getValue();

		if (selection == null) {
			// Check if NULL is an option
			List<?> options = getOptions();
			return SelectListUtil.containsOption(options, null);
		}

		return true;
	}

	// ================================
	// DataBound
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		// Validate the selected option (allow handle invalid option)
		findValidOption(getOptions(), getData(), true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue() {
		// Validate the selected option (allow handle invalid option)
		Object validOption = findValidOption(getOptions(), getData(), true);
		return validOption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getData() {
		Object data = super.getData();

		// Check if we need to default to the first option
		if (data == null && !isAllowNoSelection()) {
			List<?> options = getOptions();
			if (options != null && !options.isEmpty()) {
				// Check if NULL is an option
				if (SelectListUtil.containsOption(options, null)) {
					return null;
				}
				// Use the first option
				Object firstOption = SelectListUtil.getFirstOption(options);
				return firstOption;
			}
			return null;
		}
		return data;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(final Object data) {
		List<?> options = getOptions();
		if (!(isEditable() && data instanceof String) && (options == null || options.isEmpty())) {
			throw new IllegalStateException(
					"Should not set a selection on a list component with no options.");
		}

		Object validOption = findValidOption(options, data, false);
		super.setData(validOption);
	}

	/**
	 * Find the valid option for the selected data, allowing for option/code and legacy matching.
	 *
	 * @param options the list of options
	 * @param selected the option to search for
	 * @param handleInvalid true if allow handle invalid option
	 * @return the valid option
	 */
	private Object findValidOption(final List<?> options, final Object selected,
			final boolean handleInvalid) {

		// No selection made
		if (selected == null) {
			// No Selection required, or no options to select
			if (isAllowNoSelection() || options == null || options.isEmpty()) {
				return null;
			} else { // Selection is required
				// Check if NULL is an option
				if (SelectListUtil.containsOption(options, null)) {
					return null;
				}

				// Must have a selected option
				throw new IllegalStateException(
						"A valid option must be selected for lists that require a selected option.");
			}
		}

		// Check we have a valid option and if we find it via Option/Code or Legacy matching, return the matching option
		if (options != null) {
			Object option = SelectListUtil.getOptionWithMatching(options, selected);
			if (option != null) {
				return option;
			}
		}

		// Editable with User Entered Text
		if (isEditable() && selected instanceof String) {
			return selected;
		}

		// Handle invalid option
		if (handleInvalid) {
			Object valid = doHandleInvalidOption(selected);
			setSelected(valid);
			return valid;
		} else {
			throw new IllegalStateException(
					"The selected option \"" + selected + "\" is not an available option.");
		}

	}

	/**
	 * Handle the situation where a selected option is no longer a valid option.
	 * <p>
	 * By default, this method throws an {@link IllegalStateException}.
	 * </p>
	 * <p>
	 * This method can be overridden to handle an invalid option without throwing the exception. It must return a valid
	 * option that will be set as the selected option.
	 * </p>
	 *
	 * @param invalidOption the invalid option
	 * @return a valid option to be set as the selected option
	 */
	protected Object doHandleInvalidOption(final Object invalidOption) {
		throw new IllegalStateException("The selected option \"" + invalidOption
				+ "\" is invalid. It is not an available option.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		// First we need to figure out if the new selection is the same as the
		// previous selection.
		final Object newSelection = getRequestValue(request);
		final Object priorSelection = getValue();

		boolean changed = !Util.equals(newSelection, priorSelection);

		if (changed) {
			setData(newSelection);
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getRequestValue(final Request request) {
		if (isPresent(request)) {
			return getNewSelection(request);
		} else {
			return getValue();
		}
	}

	/**
	 * Determines which selection has been included in the given request.
	 *
	 * @param request the current request
	 * @return the selected option in the given request
	 */
	protected Object getNewSelection(final Request request) {
		String paramValue = request.getParameter(getId());

		if (paramValue == null) {
			return null;
		}

		// Figure out which option has been selected.
		List<?> options = getOptions();

		if (options == null || options.isEmpty()) {
			if (!isEditable()) {
				// User could not have made a selection.
				return null;
			}

			options = Collections.EMPTY_LIST;
		}

		int optionIndex = 0;

		for (Object option : options) {
			if (option instanceof OptionGroup) {
				List<?> groupOptions = ((OptionGroup) option).getOptions();
				if (groupOptions != null) {
					for (Object nestedOption : groupOptions) {
						if (paramValue.equals(optionToCode(nestedOption, optionIndex++))) {
							return nestedOption;
						}
					}
				}
			} else if (paramValue.equals(optionToCode(option, optionIndex++))) {
				return option;
			}
		}

		// Option not found, but if editable, user entered text
		if (isEditable()) {
			return paramValue;
		}

		// Invalid option. Ignore and use the current selection
		LOG.warn(
				"Option \"" + paramValue + "\" on the request is not a valid option. Will be ignored.");
		Object currentOption = getValue();
		return currentOption;
	}

}
