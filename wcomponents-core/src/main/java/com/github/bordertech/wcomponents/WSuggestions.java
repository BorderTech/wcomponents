package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.LookupTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WSuggestions represents a device for providing suggested input for a text-like input field. The suggestions may be a
 * static list, derived from a data url or acquired on the fly (via AJAX) based on user input into an associated input.
 * <p>
 * WSuggestions has no effect unless it is associated with a text-like input control such as WTextField. If it is
 * associated with a constrained input (such as WEmailField) then it is expected (but not enforced) that the suggestions
 * would be in line with the associated field's constraints.
 * </p>
 * <p>
 * It allows for client caching of frequently used lists via a data key or lists that can be produced via AJAX depending
 * on the text entered in the related TextField.
 * </p>
 * <p>
 * Suggestions provided via a lookup table are cached on the client and filtered on the client.
 * </p>
 * <p>
 * To have a suggestion list dynamically updated via AJAX, do not use a lookup table, but manually set the options and
 * set a refresh action via {@link #setRefreshAction(Action)}. The text entered by the user that triggered the refresh
 * is provided by {@link #getAjaxFilter()}.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSuggestions extends AbstractWComponent implements AjaxTarget {

	/**
	 * The Application-wide lookup-table to use.
	 */
	private static final LookupTable APPLICATION_LOOKUP_TABLE = Factory.newInstance(
			LookupTable.class);

	/**
	 * AJAX refresh command.
	 */
	public static final String AJAX_REFRESH_ACTION_COMMAND = "Refresh";

	/**
	 * The way in which the suggestion is provided to and selected by the user. Defaults to BOTH and should remain as
	 * this for combobox implementations. LIST should be used for implementations which enforce selection of an
	 * existing value. We may want to consider INLINE in the future but I see no reason for NONE since then it would
	 * not be a combo.
	 */
	public enum Autocomplete {
		/**
		 * Indicates that autocomplete may be from the textbox or from the suggestion list.
		 */
		BOTH,
		/**
		 * Indicates the autocomplete must only be from the suggestion list.
		 */
		LIST
	};

	/**
	 * Create a WSuggestions.
	 */
	public WSuggestions() {
		super();
	}

	/**
	 * Creates a WSuggestions with predefined suggestions.
	 *
	 * @param suggestions the list of suggestions.
	 */
	public WSuggestions(final List<String> suggestions) {
		getComponentModel().setSuggestions(suggestions);
	}

	/**
	 * Creates a WSuggestions using a lookup table for the suggestions.
	 *
	 * @param lookupTable the lookup table identifier to obtain the list of suggestions.
	 */
	public WSuggestions(final Object lookupTable) {
		getComponentModel().setLookupTable(lookupTable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final Request request) {
		// Check if this suggestion list is the current AJAX trigger
		if (AjaxHelper.isCurrentAjaxTrigger(this)) {
			String filter = request.getParameter(getId());
			setAjaxFilter(filter);
			doHandleAjaxRefresh();
		}
	}

	/**
	 * Handle the AJAX refresh request.
	 */
	protected void doHandleAjaxRefresh() {
		final Action action = getRefreshAction();
		if (action == null) {
			return;
		}

		final ActionEvent event = new ActionEvent(this, AJAX_REFRESH_ACTION_COMMAND, getAjaxFilter());
		Runnable later = new Runnable() {
			@Override
			public void run() {
				action.execute(event);
			}
		};

		invokeLater(later);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		UIContext uic = UIContextHolder.getCurrent();

		// Register for AJAX if not using a cached list and have a refresh action.
		if (uic.getUI() != null && getListCacheKey() == null && getRefreshAction() != null) {
			AjaxHelper.registerComponentTargetItself(getId(), request);
		}
	}

	/**
	 * Returns the complete list of suggestions available for selection for this user's session.
	 *
	 * @return the list of suggestions available for the given user's session.
	 */
	public List<String> getSuggestions() {
		// Lookup table
		Object table = getLookupTable();

		if (table == null) {
			SuggestionsModel model = getComponentModel();
			List<String> suggestions = model.getSuggestions();
			return suggestions == null ? Collections.EMPTY_LIST : suggestions;
		} else {
			List<?> lookupSuggestions = APPLICATION_LOOKUP_TABLE.getTable(table);
			if (lookupSuggestions == null || lookupSuggestions.isEmpty()) {
				return Collections.EMPTY_LIST;
			}
			// Build list of String suggestions
			List<String> suggestions = new ArrayList<>(lookupSuggestions.size());
			for (Object suggestion : lookupSuggestions) {
				String sugg = APPLICATION_LOOKUP_TABLE.getDescription(table, suggestion);
				if (sugg != null) {
					suggestions.add(sugg);
				}
			}
			return Collections.unmodifiableList(suggestions);
		}
	}

	/**
	 * Retrieves the data list cache key for this component.
	 *
	 * @return the cache key if client-side caching is enabled, null otherwise.
	 */
	public String getListCacheKey() {
		Object table = getLookupTable();

		if (table != null && Config.getInstance().getBoolean(
				AbstractWSelectList.DATALIST_CACHING_PARAM_KEY, false)) {
			String key = APPLICATION_LOOKUP_TABLE.getCacheKeyForTable(table);
			return key;
		}

		return null;
	}

	/**
	 * Set the complete list of suggestions available for selection for this user's session.
	 *
	 * @param suggestions the list of suggestions available to the user.
	 */
	public void setSuggestions(final List<String> suggestions) {
		SuggestionsModel model = getOrCreateComponentModel();
		model.setSuggestions(suggestions);
	}

	/**
	 * Set the lookupTable for this user's session.
	 *
	 * @param lookupTable the lookup table identifier to obtain the suggestions for the list.
	 */
	public void setLookupTable(final Object lookupTable) {
		getOrCreateComponentModel().setLookupTable(lookupTable);
	}

	/**
	 * Get the lookupTable for this user's session.
	 *
	 * @return the lookupTable for the suggestions
	 */
	public Object getLookupTable() {
		return getComponentModel().getLookupTable();
	}

	/**
	 * @param action the refresh action. Ignored if using a lookup table.
	 */
	public void setRefreshAction(final Action action) {
		getOrCreateComponentModel().action = action;
	}

	/**
	 * @return the refresh action. Ignored if using a lookup table.
	 */
	public Action getRefreshAction() {
		return getComponentModel().action;
	}

	/**
	 * @param filter the refresh filter value passed on the AJAX request.
	 */
	protected void setAjaxFilter(final String filter) {
		getOrCreateComponentModel().filter = filter;
	}

	/**
	 * @return the refresh filter value passed on the AJAX request. Ignored if using a lookup table.
	 */
	public String getAjaxFilter() {
		return getComponentModel().filter;
	}

	/**
	 * @param autocomplete The Autocomplete to set for this instance.
	 */
	public void setAutocomplete(final Autocomplete autocomplete) {
		getOrCreateComponentModel().autocomplete = autocomplete;
	}

	/**
	 * @return The autocomplete for this instance.
	 */
	public Autocomplete getAutocomplete() {
		return getComponentModel().autocomplete;
	}

	/**
	 * The minimum number of characters entered before refreshing suggestions. A value of zero indicates to use the
	 * theme default, which is usually 3.
	 *
	 * @param min the minimum number of characters entered before refreshing suggestions.
	 */
	public void setMinRefresh(final int min) {
		if (min < 0) {
			throw new IllegalArgumentException(
					"Minimum refresh value cannot be less than 0. Where zero indicates use the default value.");
		}
		getOrCreateComponentModel().min = min;
	}

	/**
	 * The minimum characters entered before triggering the refresh action.
	 * <p>
	 * A value of zero indicates the theme default will be used (usually 3).
	 * </p>
	 *
	 * @return the minimum characters entered before triggering the refresh action.
	 */
	public int getMinRefresh() {
		return getComponentModel().min;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected SuggestionsModel getComponentModel() {
		return (SuggestionsModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected SuggestionsModel getOrCreateComponentModel() {
		return (SuggestionsModel) super.getOrCreateComponentModel();
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new PanelModel.
	 */
	@Override
	protected SuggestionsModel newComponentModel() {
		return new SuggestionsModel();
	}

	/**
	 * A class used to hold the list of options for this component.
	 */
	public static class SuggestionsModel extends ComponentModel {

		/**
		 * The suggestions for this list.
		 */
		private List<String> suggestions;

		/**
		 * The name of the lookup table which will be used to obtain the list of suggestions.
		 */
		private Object lookupTable;

		/**
		 * Minimum characters entered before refresh suggestions. Zero means use theme default.
		 */
		private int min = 0;

		/**
		 * Action when refresh requested via AJAX.
		 */
		private Action action;

		/**
		 * Filter value passed on the AJAX request.
		 */
		private String filter;

		/**
		 * @return returns the suggestions.
		 */
		private List<String> getSuggestions() {
			return suggestions;
		}

		/**
		 * The autocomplete model for the suggestions.
		 */
		private Autocomplete autocomplete = Autocomplete.BOTH;

		/**
		 * @param suggestions the suggestions to set.
		 */
		private void setSuggestions(final List<String> suggestions) {
			this.suggestions = suggestions == null ? null : Collections.
					unmodifiableList(suggestions);
			lookupTable = null;
		}

		/**
		 * @param lookupTable the lookup table name to set.
		 */
		private void setLookupTable(final Object lookupTable) {
			this.lookupTable = lookupTable;
			suggestions = null;
		}

		/**
		 * @return the lookupTable.
		 */
		private Object getLookupTable() {
			return lookupTable;
		}
	}

}
