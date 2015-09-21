package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.List;

/**
 * Provides a way of grouping options inside list-type controls.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class OptionGroup implements Serializable {

	/**
	 * The options contained in this group.
	 */
	private List options;

	/**
	 * The textual description of this group.
	 */
	private String description;

	/**
	 * Creates an option group with no options or description.
	 */
	public OptionGroup() {
	}

	/**
	 * Creates an option group with the given description.
	 *
	 * @param description the group description.
	 */
	public OptionGroup(final String description) {
		this.description = description;
	}

	/**
	 * Creates an option group with the given description and contents.
	 *
	 * @param description the group description.
	 * @param options the options.
	 */
	public OptionGroup(final String description, final List options) {
		this.description = description;
		this.options = options;
	}

	/**
	 * @return the options contained in this group.
	 */
	public List getOptions() {
		return options;
	}

	/**
	 * @return the textual description of this group.
	 */
	public String getDesc() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param options The options to set.
	 */
	public void setOptions(final List options) {
		this.options = options;
	}
}
