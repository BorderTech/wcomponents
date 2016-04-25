package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Disableable;
import com.github.bordertech.wcomponents.Input;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponent;
import junit.framework.Assert;

/**
 * Utility class used by the Subordinate Control tests.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class AssertTargetUtil {

	/**
	 * No instance methods here.
	 */
	private AssertTargetUtil() {
		// Private Constructor
	}

	/**
	 * Check the targets are disabled.
	 *
	 * @param target1 test target 1
	 * @param target2 test target 2
	 * @param target3 test target 3
	 */
	public static void assertTargetsEnabled(final Disableable target1, final Disableable target2,
			final Disableable target3) {
		// Check Enabled
		Assert.assertFalse("Target1 should be enabled", target1.isDisabled());
		Assert.assertFalse("Target2 should be enabled", target2.isDisabled());
		Assert.assertFalse("Target3 should be enabled", target3.isDisabled());
	}

	/**
	 * Check the targets are disabled.
	 *
	 * @param target1 test target 1
	 * @param target2 test target 2
	 * @param target3 test target 3
	 */
	public static void assertTargetsDisabled(final Disableable target1, final Disableable target2,
			final Disableable target3) {
		// Check Disabled
		Assert.assertTrue("Target1 should be disabled", target1.isDisabled());
		Assert.assertTrue("Target2 should be disabled", target2.isDisabled());
		Assert.assertTrue("Target3 should be disabled", target3.isDisabled());
	}

	/**
	 * Check the targets are visible.
	 *
	 * @param target1 test target 1
	 * @param target2 test target 2
	 * @param target3 test target 3
	 */
	public static void assertTargetsVisible(final WComponent target1, final WComponent target2,
			final WComponent target3) {
		// Check Visible
		Assert.assertTrue("Target1 should be visible", target1.isVisible());
		Assert.assertTrue("Target2 should be visible", target2.isVisible());
		Assert.assertTrue("Target3 should be visible", target3.isVisible());
	}

	/**
	 * Check the targets are not visible.
	 *
	 * @param target1 test target 1
	 * @param target2 test target 2
	 * @param target3 test target 3
	 */
	public static void assertTargetsNotVisible(final WComponent target1, final WComponent target2,
			final WComponent target3) {
		// Check Not Visible
		Assert.assertFalse("Target1 should not be visible", target1.isVisible());
		Assert.assertFalse("Target2 should not be visible", target1.isVisible());
		Assert.assertFalse("Target3 should not be visible", target1.isVisible());
	}

	/**
	 * Check the targets are hidden.
	 *
	 * @param target1 test target 1
	 * @param target2 test target 2
	 * @param target3 test target 3
	 */
	public static void assertTargetsHidden(final WComponent target1, final WComponent target2,
			final WComponent target3) {
		// Check Hidden
		Assert.assertTrue("Target1 should be hidden", target1.isHidden());
		Assert.assertTrue("Target2 should be hidden", target2.isHidden());
		Assert.assertTrue("Target3 should be hidden", target3.isHidden());
	}

	/**
	 * Check the targets are not hidden.
	 *
	 * @param target1 test target 1
	 * @param target2 test target 2
	 * @param target3 test target 3
	 */
	public static void assertTargetsNotHidden(final WComponent target1, final WComponent target2,
			final WComponent target3) {
		// Check Not Hidden
		Assert.assertFalse("Target1 should not be hidden", target1.isHidden());
		Assert.assertFalse("Target2 should not be hidden", target2.isHidden());
		Assert.assertFalse("Target3 should not be hidden", target3.isHidden());
	}

	/**
	 * Check the targets are mandatory.
	 *
	 * @param target1 test target 1
	 * @param target2 test target 2
	 * @param target3 test target 3
	 */
	public static void assertTargetsMandatory(final SubordinateTarget target1,
			final SubordinateTarget target2,
			final SubordinateTarget target3) {
		// Check Mandatory
		Assert.assertTrue("Target1 should be mandatory",
				(target1 instanceof Input) && ((Input) target1).isMandatory());
		Assert.assertTrue("Target2 should be mandatory",
				(target2 instanceof Input) && ((Input) target2).isMandatory());
		Assert.assertTrue("Target3 should be mandatory",
				(target3 instanceof Input) && ((Input) target3).isMandatory());
	}

	/**
	 * Check the targets are optional.
	 *
	 * @param target1 test target 1
	 * @param target2 test target 2
	 * @param target3 test target 3
	 */
	public static void assertTargetsOptional(final SubordinateTarget target1,
			final SubordinateTarget target2,
			final SubordinateTarget target3) {
		// Check Optional
		Assert.assertFalse("Target1 should be optional",
				(target1 instanceof Input) && ((Input) target1).isMandatory());
		Assert.assertFalse("Target2 should be optional",
				(target2 instanceof Input) && ((Input) target2).isMandatory());
		Assert.assertFalse("Target3 should be optional",
				(target3 instanceof Input) && ((Input) target3).isMandatory());
	}

}
