package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.LookupTable;
import com.github.bordertech.wcomponents.util.LookupTableHelper;
import com.github.bordertech.wcomponents.util.Util;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * AbstractWSelectList provides the basis for components that allow the user to select an item from a list.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractWSelectList extends AbstractInput {

	/**
	 * The Application-wide lookup-table to use.
	 */
	private static final LookupTable APPLICATION_LOOKUP_TABLE = Factory.newInstance(
			LookupTable.class);

	/**
	 * Indicates whether having no selection is allowed.
	 */
	private final boolean allowNoSelection;

	/**
	 * Data list caching parameter key.
	 */
	public static final String DATALIST_CACHING_PARAM_KEY = "bordertech.wcomponents.dataListCaching.enabled";

	/**
	 * Creates an AbstractWSelectList.
	 *
	 * @param options the list's options.
	 * @param allowNoSelection if true, allow no option to be selected
	 */
	public AbstractWSelectList(final List<?> options, final boolean allowNoSelection) {
		getComponentModel().setOptions(options);
		this.allowNoSelection = allowNoSelection;
	}

	/**
	 * Creates an AbstractWSelectList.
	 *
	 * @param lookupTable the lookup table identifier to obtain the list's options from.
	 * @param allowNoSelection if true, allow no option to be selected
	 */
	public AbstractWSelectList(final Object lookupTable, final boolean allowNoSelection) {
		getComponentModel().setLookupTable(lookupTable);
		this.allowNoSelection = allowNoSelection;
	}

	/**
	 * @return if true, allow no option to be selected
	 */
	protected boolean isAllowNoSelection() {
		return allowNoSelection;
	}

	/**
	 * Setting this flag to true will cause this list component to post the form to the server when it's selection is
	 * changed.
	 *
	 * @param flag if true, the form is submitted when the selection changes.
	 */
	@Override
	public void setSubmitOnChange(final boolean flag) {
		super.setSubmitOnChange(flag);
	}

	/**
	 * Indicates whether the form should submit to server when the list component's selection changes.
	 *
	 * @return true if the form is submitted when the selection changes.
	 */
	@Override
	public boolean isSubmitOnChange() {
		return super.isSubmitOnChange();
	}

	/**
	 * A flag if set to true the option description will be encoded. Defaults to <code>true</code>.
	 *
	 * @param encode If <code>true</code>, option descriptions will be encoded.
	 */
	public void setDescEncode(final boolean encode) {
		setFlag(ComponentModel.ENCODE_TEXT_FLAG, encode);
	}

	/**
	 * @return Flag indicating if option descriptions are to be encoded.
	 */
	public boolean getDescEncode() {
		return isFlagSet(ComponentModel.ENCODE_TEXT_FLAG);
	}

	/**
	 * Retrieves the code for the given option. Will return null if there is no matching option.
	 *
	 * @param option the option
	 * @return the code for the given option, or null if there is no matching option.
	 */
	public final String optionToCode(final Object option) {
		return optionToCode(option, getOptionIndex(option));
	}

	/**
	 * Retrieves the code for the given option. Will return null if there is no matching option.
	 *
	 * @param option the option
	 * @param index the index of the option in the list.
	 * @return the code for the given option, or null if there is no matching option.
	 */
	protected String optionToCode(final Object option, final int index) {
		if (index < 0) {
			List<?> options = getOptions();

			if (options == null || options.isEmpty()) {
				Integrity.issue(this, "No options available, so cannot convert the option \""
						+ option + "\" to a code.");
			} else {
				StringBuffer message = new StringBuffer();
				message.append("The option \"").append(option).append(
						"\" is not one of the available options.");
				Object firstOption = SelectListUtil.getFirstOption(options);

				if (firstOption != null && option != null && firstOption.getClass() != option.
						getClass()) {
					message.append(" The options in this list component are of type \"");
					message.append(firstOption.getClass().getName())
							.append("\", the selection you supplied is of type \"");
					message.append(option.getClass().getName()).append("\".");
				}

				Integrity.issue(this, message.toString());
			}

			return null;
		} else if (option instanceof Option) {
			Option opt = (Option) option;
			return opt.getCode() == null ? "" : opt.getCode();
		} else {
			String code = APPLICATION_LOOKUP_TABLE.getCode(getLookupTable(), option);

			if (code == null) {
				return String.valueOf(index + 1);
			} else {
				return code;
			}
		}
	}

	/**
	 * Retrieves the index of the given option. The index is not necessarily the index of the option in the options
	 * list, as there may be options nested in OptionGroups.
	 *
	 * @param option the option
	 * @return the index of the given option, or -1 if there is no matching option.
	 */
	protected int getOptionIndex(final Object option) {
		int optionCount = 0;
		List<?> options = getOptions();

		if (options != null) {
			for (Object obj : getOptions()) {
				if (obj instanceof OptionGroup) {
					List<?> groupOptions = ((OptionGroup) obj).getOptions();

					int groupIndex = groupOptions.indexOf(option);

					if (groupIndex != -1) {
						return optionCount + groupIndex;
					}

					optionCount += groupOptions.size();
				} else if (Util.equals(option, obj)) {
					return optionCount;
				} else {
					optionCount++;
				}
			}
		}

		return -1;
	}

	/**
	 * Retrieves the data list cache key for this component.
	 *
	 * @return the cache key if client-side caching is enabled, null otherwise.
	 */
	public String getListCacheKey() {
		Object table = getLookupTable();

		if (table != null && Config.getInstance().getBoolean(DATALIST_CACHING_PARAM_KEY, false)) {
			String key = APPLICATION_LOOKUP_TABLE.getCacheKeyForTable(table);
			return key;
		}

		return null;
	}

	/**
	 * This method converts an option object into a string. List components that need to control the format of the
	 * string can override this method. By default this method simply calls toString() on the given option object.
	 *
	 * @param option the option to return a String representation of.
	 * @return a String representation of the given option.
	 */
	protected String optionToString(final Object option) {
		if (option == null) {
			return null;
		}
		return option.toString();
	}

	/**
	 * Returns the complete list of options available for selection for this user's session.
	 *
	 * @return the list of options available for the given user's session.
	 */
	public List<?> getOptions() {
		if (getLookupTable() == null) {
			SelectionModel model = getComponentModel();
			return model.getOptions();
		} else {
			return APPLICATION_LOOKUP_TABLE.getTable(getLookupTable());
		}
	}

	/**
	 * Set the complete list of options available for selection for this user's session.
	 *
	 * @param aList the list of options available to the user.
	 */
	public void setOptions(final List<?> aList) {
		SelectionModel model = getOrCreateComponentModel();
		model.setOptions(aList);
	}

	/**
	 * Set the complete list of options available for selection for this users session.
	 *
	 * @param aArray the list of options available to the user.
	 */
	public void setOptions(final Object[] aArray) {
		setOptions(aArray == null ? null : Arrays.asList(aArray));
	}

	/**
	 * Set the lookupTable for this user's session.
	 *
	 * @param lookupTable the lookup table identifier to obtain the options for the list.
	 */
	public void setLookupTable(final Object lookupTable) {
		getOrCreateComponentModel().setLookupTable(lookupTable);
	}

	/**
	 * Get the lookupTable for this user's session.
	 *
	 * @return the lookupTable for the options
	 */
	public Object getLookupTable() {
		return getComponentModel().getLookupTable();
	}

	/**
	 * Sets whether users are able to enter in an arbitrary value, rather than having to pick one from the list. This
	 * method is marked protected, as not all list type controls will support editing. Controls which do support editing
	 * should override this method and make it public.
	 *
	 * @param editable true for editable, false for fixed.
	 * @deprecated Editable no longer required. WSuggestions and a WTextfield should be used instead
	 */
	@Deprecated
	protected void setEditable(final boolean editable) {
		getOrCreateComponentModel().editable = editable;
	}

	/**
	 * Indicates whether users are able to enter in an arbitrary value, rather than having to pick one from the list.
	 * This method is marked protected, as not all list type controls will support editing. Controls which do support
	 * editing should override this method and make it public.
	 *
	 * @return true if the user can enter arbitrary values, false if not.
	 * @deprecated Editable no longer required. WSuggestions and a WTextfield should be used instead
	 */
	@Deprecated
	protected boolean isEditable() {
		return getComponentModel().editable;
	}

	/**
	 * Indicates whether this component is AJAX enabled. A list is an AJAX list if it has a
	 * {@link #setAjaxTarget(AjaxTarget) target set}.
	 * <p>
	 * This method is protected due to this being an abstract class and not all the classes that extend it support AJAX.
	 * </p>
	 *
	 * @return true if this list is AJAX enabled, false otherwise.
	 */
	protected boolean isAjax() {
		return getComponentModel().ajaxTarget != null;
	}

	/**
	 * Retrieves the default AJAX target.
	 * <p>
	 * This method is protected due to this being an abstract class and not all the classes that extend it support AJAX.
	 * </p>
	 *
	 * @return the default AJAX target for this list.
	 */
	protected AjaxTarget getAjaxTarget() {
		return getComponentModel().ajaxTarget;
	}

	/**
	 * Sets the AJAX target for the list. If a target is supplied, as an AJAX request is made rather than a round-trip
	 * to the server. The AJAX response will only contain the (possibly updated) target element rather than the entire
	 * UI.
	 * <p>
	 * This method is protected due to this being an abstract class and not all the classes that extend it support AJAX.
	 * </p>
	 *
	 * @param ajaxTarget the AJAX target.
	 */
	protected void setAjaxTarget(final AjaxTarget ajaxTarget) {
		getOrCreateComponentModel().ajaxTarget = ajaxTarget;
	}

	/**
	 * <p>
	 * Indicates whether this list was present in the request.
	 * </p>
	 * <p>
	 * Lists that allow no option to be selected have a hidden input field, whose name is name-h to indicate that it is
	 * in the request.
	 * </p>
	 *
	 * @param request the request being responded to.
	 * @return true if this list was present in the request, false if not.
	 */
	@Override
	protected boolean isPresent(final Request request) {
		if (isAllowNoSelection()) {
			String id = getId();
			return request.getParameter(id + "-h") != null;
		} else {
			return super.isPresent(request);
		}
	}

	/**
	 * Override preparePaintComponent to register an AJAX operation if this list is AJAX enabled.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (isAjax() && UIContextHolder.getCurrent().getUI() != null) {
			AjaxTarget target = getAjaxTarget();
			AjaxHelper.registerComponent(target.getId(), request, getId());
		}

		String cacheKey = getListCacheKey();

		if (cacheKey != null) {
			LookupTableHelper.registerList(cacheKey, request);
		}
	}

	/**
	 * Indicates whether the selection be ordered.
	 *
	 * @return true if the selection can be ordered, false otherwise.
	 */
	protected boolean isSelectionOrderable() {
		return false;
	}

	/**
	 * Creates a new SelectionModel which holds Extrinsic state management of the list.
	 *
	 * @return a new SelectionModel
	 */
	@Override
	protected SelectionModel newComponentModel() {
		return new SelectionModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected SelectionModel getComponentModel() {
		return (SelectionModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected SelectionModel getOrCreateComponentModel() {
		return (SelectionModel) super.getOrCreateComponentModel();
	}

	/**
	 * Retrieves the description for the given option. Intended for use by Renderers.
	 *
	 * @param option the option to retrieve the description for.
	 * @param index the option index.
	 * @return the description for the given option.
	 */
	public String getDesc(final Object option, final int index) {
		String desc = "";

		if (option instanceof Option) {
			String optDesc = ((Option) option).getDesc();

			if (optDesc != null) {
				desc = optDesc;
			}
		} else {
			String tableDesc = APPLICATION_LOOKUP_TABLE.getDescription(getLookupTable(), option);

			if (tableDesc != null) {
				desc = tableDesc;
			} else if (option != null) {
				desc = option.toString();
			}
		}

		return I18nUtilities.format(null, desc);
	}

	/**
	 * Retrieves the code for the given option. Intended for use by Renderers.
	 *
	 * @param option the option to retrieve the description for.
	 * @param index the option index.
	 * @return the description for the given code.
	 */
	public String getCode(final Object option, final int index) {
		return optionToCode(option, index);
	}

	/**
	 * Holds the extrinsic state information of the list.
	 */
	public static class SelectionModel extends InputModel {

		/**
		 * The options for this list.
		 */
		private List<?> options;

		/**
		 * The name of the lookup table which will be used to obtain the list options.
		 */
		private Object lookupTable;

		/**
		 * Indicates whether the user is able to enter in an arbitrary value, rather than having to pick one from the
		 * list.
		 */
		private boolean editable;

		/**
		 * The target component to repaint (via AJAX).
		 */
		private AjaxTarget ajaxTarget;

		/**
		 * @return Returns the options.
		 */
		private List<?> getOptions() {
			return options == null ? null : Collections.unmodifiableList(options);
		}

		/**
		 * @param options The options to set.
		 */
		private void setOptions(final List<?> options) {
			this.options = options;
			lookupTable = null;
		}

		/**
		 * @param lookupTable The lookup table name to set.
		 */
		private void setLookupTable(final Object lookupTable) {
			this.lookupTable = lookupTable;
			options = null;
		}

		/**
		 * @return Returns the lookupTable.
		 */
		private Object getLookupTable() {
			return lookupTable;
		}
	}
}
