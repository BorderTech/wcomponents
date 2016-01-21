package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The WShuffler is a WComponent that allows a list of options to have its order manually shuffled by the user. The
 * component accepts a list of objects and the toString method is used to render the option in the response.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WShuffler extends AbstractInput implements AjaxTrigger, AjaxTarget, SubordinateTarget {

	/**
	 * Creates an empty WShuffler.
	 */
	public WShuffler() {
		this((List<?>) null);
	}

	/**
	 * Creates a WShuffler with the specified options.
	 *
	 * @param options the shuffler options.
	 */
	public WShuffler(final List<?> options) {
		getOrCreateComponentModel().setData(options);
	}

	/**
	 * Sets the shuffler options.
	 *
	 * @param options the shuffler options
	 */
	public void setOptions(final List<?> options) {
		setData(options);
	}

	/**
	 * Retrieves the shuffler options.
	 *
	 * @return the shuffler options
	 */
	public List<?> getOptions() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<?> getValue() {
		// Convert data to list (if necessary)
		Object data = getData();
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
	 * Sets the maximum number of rows that are visible in the list at any one time.
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
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		List<?> values = getRequestValue(request);
		List<?> current = getOptions();

		boolean changed = !Util.equals(values, current);

		if (changed) {
			setOptions(values);
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<?> getRequestValue(final Request request) {
		if (isPresent(request)) {
			return getNewOptions(request.getParameterValues(getId()));
		} else {
			return getOptions();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isPresent(final Request request) {
		// Check the number of options have not changed
		String[] paramValues = request.getParameterValues(getId());
		return (paramValues != null && getOptions() != null && paramValues.length == getOptions().
				size());
	}

	/**
	 * Shuffle the options.
	 *
	 * @param paramValues the array of option indexes as shuffled by the user
	 * @return the shuffled options
	 */
	private List<?> getNewOptions(final String[] paramValues) {
		// Take a copy of the old options
		List<?> copyOldOptions = new ArrayList(getOptions());

		// Create a new list to hold the shuffled options
		List<Object> newOptions = new ArrayList<>(paramValues.length);

		// Process the option parameters
		for (String param : paramValues) {
			for (Object oldOption : copyOldOptions) {
				// Match the string value of the option
				String stringOldOption = String.valueOf(oldOption);
				if (Util.equals(stringOldOption, param)) {
					newOptions.add(oldOption);
					copyOldOptions.remove(oldOption);
					break;
				}
			}
		}

		return newOptions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ShufflerModel newComponentModel() {
		return new ShufflerModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected ShufflerModel getComponentModel() {
		return (ShufflerModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected ShufflerModel getOrCreateComponentModel() {
		return (ShufflerModel) super.getOrCreateComponentModel();
	}

	/**
	 * The ShufflerModel holds the state management of the shuffler.
	 */
	public static class ShufflerModel extends InputModel {

		/**
		 * The number of visible rows to display.
		 */
		private int rows;
	}

}
