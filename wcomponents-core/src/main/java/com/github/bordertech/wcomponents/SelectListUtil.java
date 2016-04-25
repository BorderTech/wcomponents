package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.util.List;

/**
 * Utility class used by {@link AbstractWSelectList} for processing options and list of options.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class SelectListUtil {

	/**
	 * Hide the constructor as there are no instance methods.
	 */
	private SelectListUtil() {
		// Do Nothing
	}

	/**
	 * Iterate through the options to determine if an option exists.
	 *
	 * @param options the list of options
	 * @param findOption the option to search for
	 * @return true if the list of options contains the option
	 */
	public static boolean containsOption(final List<?> options, final Object findOption) {
		if (options != null) {
			for (Object option : options) {
				if (option instanceof OptionGroup) {
					List<?> groupOptions = ((OptionGroup) option).getOptions();
					if (groupOptions != null) {
						for (Object nestedOption : groupOptions) {
							if (Util.equals(nestedOption, findOption)) {
								return true;
							}
						}
					}
				} else if (Util.equals(option, findOption)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Iterate through the options to determine if an option exists allowing for Option/Code and Legacy matching.
	 *
	 * @param options the list of options
	 * @param data the test data value
	 * @return true if the list of options contains the option
	 */
	public static boolean containsOptionWithMatching(final List<?> options, final Object data) {
		if (options != null) {
			for (Object option : options) {
				if (option instanceof OptionGroup) {
					List<?> groupOptions = ((OptionGroup) option).getOptions();
					if (groupOptions != null) {
						for (Object nestedOption : groupOptions) {
							if (isEqualWithMatching(nestedOption, data)) {
								return true;
							}
						}
					}
				} else if (isEqualWithMatching(option, data)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return the option that matches the data allowing for Option/Code and Legacy matching.
	 * <p>
	 * This method should be used in conjunction with {@link #containsOptionWithMatching(List, Object)} as this method
	 * will return null if no match found, but null could be a valid option.
	 * </p>
	 *
	 * @param options the list of options
	 * @param data the test data value
	 * @return the option that matches allowing for Option/Code and Legacy matching, otherwise return null
	 */
	public static Object getOptionWithMatching(final List<?> options, final Object data) {
		if (options != null) {
			boolean legacyMatch = false;
			Object legacyOption = null;

			for (Object option : options) {
				if (option instanceof OptionGroup) {
					List<?> groupOptions = ((OptionGroup) option).getOptions();
					if (groupOptions != null) {
						for (Object nestedOption : groupOptions) {
							// Check for match via equals/code
							if (Util.equals(nestedOption, data) || isOptionCodeMatch(nestedOption,
									data)) {
								return nestedOption;
							}

							// Check for legacy match, but continue processing in case of a match via equals/code
							if (!legacyMatch && isLegacyMatch(nestedOption, data)) {
								legacyMatch = true;
								legacyOption = nestedOption;
							}
						}
					}
				} else {
					// Check for match via equals/code
					if (Util.equals(option, data) || isOptionCodeMatch(option, data)) {
						return option;
					}

					// Check for legacy match, but continue processing in case of a match via equals/code
					if (!legacyMatch && isLegacyMatch(option, data)) {
						legacyMatch = true;
						legacyOption = option;
					}
				}
			}

			// Check if a legacy match was found
			if (legacyMatch) {
				return legacyOption;
			}

		}

		return null;
	}

	/**
	 * Retrieve the first option. The first option maybe within an option group.
	 *
	 * @param options the list of options
	 * @return true the first option
	 */
	public static Object getFirstOption(final List<?> options) {
		if (options != null) {
			for (Object option : options) {
				if (option instanceof OptionGroup) {
					List<?> groupOptions = ((OptionGroup) option).getOptions();
					if (groupOptions != null && !groupOptions.isEmpty()) {
						return groupOptions.get(0);
					}
				} else {
					return option;
				}
			}
		}
		return null;
	}

	/**
	 * Check for a valid option. Allowing for option/code and legacy matching.
	 *
	 * @param option the option to test for a match
	 * @param data the test data value
	 * @return true if the option and data are a match
	 */
	private static boolean isEqualWithMatching(final Object option, final Object data) {
		return Util.equals(option, data) || isOptionCodeMatch(option, data) || isLegacyMatch(option,
				data);
	}

	/**
	 * Check for legacy matching, which supported setSelected using String representations.
	 *
	 * @param option the option to test for a match
	 * @param data the test data value
	 * @return true if the option is a legacy match
	 * @deprecated Support for legacy matching will be removed
	 */
	@Deprecated
	private static boolean isLegacyMatch(final Object option, final Object data) {
		// Support legacy matching, which supported setSelected using String representations...
		String optionAsString = String.valueOf(option);
		String matchAsString = String.valueOf(data);
		boolean equal = Util.equals(optionAsString, matchAsString);
		return equal;
	}

	/**
	 * If the option is an instance of {@link Option}, check if the data value matches the Code value of the option.
	 *
	 * @param option the option to test for a match
	 * @param data the test data value
	 * @return true if the option and code are a match
	 */
	private static boolean isOptionCodeMatch(final Object option, final Object data) {
		// If the option is an instance of Option, check if the data value is the "CODE" value on the option
		if (option instanceof Option) {
			String optionCode = ((Option) option).getCode();
			String matchAsString = String.valueOf(data);
			boolean equal = Util.equals(optionCode, matchAsString);
			return equal;
		}
		return false;
	}

}
