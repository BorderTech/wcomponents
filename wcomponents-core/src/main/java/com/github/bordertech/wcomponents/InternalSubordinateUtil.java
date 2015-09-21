package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.subordinate.AbstractSetVisible;

/**
 * <p>
 * This class is a work-around to having the subordinate classes declared in a different package. It must not be used by
 * application code.</p>
 *
 * <p>
 * TODO: When the package hierarchy is renamed, the subordinate classes should be moved to the main package and this
 * class deleted.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class InternalSubordinateUtil {

	/**
	 * Hide Utility class constructor.
	 */
	private InternalSubordinateUtil() {
	}

	/**
	 * Applies the hidden flag on behalf of a AbstractSetVisible implementation.
	 *
	 * @param action the AbstractSetVisible implementation to apply the flag for.
	 * @param target the target to apply the flag to.
	 * @param value the value to apply.
	 */
	public static void applyAction(final AbstractSetVisible action, final SubordinateTarget target,
			final Object value) {
		// Try and apply the visibility.
		if (action != null && target instanceof AbstractWComponent && value instanceof Boolean) {
			boolean visible = ((Boolean) value);
			target.setValidate(visible);
			((AbstractWComponent) target).setHidden(!visible);
		}
	}
}
