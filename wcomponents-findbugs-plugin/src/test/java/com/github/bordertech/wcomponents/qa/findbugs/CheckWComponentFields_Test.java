package com.github.bordertech.wcomponents.qa.findbugs;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.UIContext;
import edu.umd.cs.findbugs.annotations.ExpectWarning;
import edu.umd.cs.findbugs.annotations.NoWarning;

/**
 * Test code for the {@link CheckWComponentFields} detector.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CheckWComponentFields_Test {

	/**
	 * A class which is not a component, to ensure false positives are not reported against other classes.
	 */
	public static final class NotAComponent {

		@NoWarning(value = "WCFF,WCCMF,WCUF")
		private String field;
	}

	/**
	 * A component with assorted bad fields, which should be detected.
	 */
	public static final class MyBadComponent extends AbstractWComponent {

		// All WComponent fields must be final
		@ExpectWarning(value = "WCFF")
		@NoWarning(value = "WCCMF,WCUF")
		private String nonFinalField;

		// Component models must not be stored
		@ExpectWarning(value = "WCCMF")
		@NoWarning(value = "WCFF,WCUF")
		private ComponentModel model;

		// UIContext must not be stored
		@ExpectWarning(value = "WCUF")
		@NoWarning(value = "WCFF,WCCMF")
		private UIContext uic;
	}

	/**
	 * A component with fields which are acceptable, to test for false positives.
	 */
	public static final class MyGoodComponent extends AbstractWComponent {

		// Static + final is ok - only set during class initialisation
		@NoWarning(value = "WCFF,WCCMF,WCUF")
		private static final String STATIC_FINAL_FIELD = "x";

		// final is ok - only set during construction
		@NoWarning(value = "WCFF,WCCMF,WCUF")
		private final String finalField = "x";

		// Static is assumed ok - developers have probably realised that it's shared
		@NoWarning(value = "WCFF,WCCMF,WCUF")
		private static String staticField;
	}
}
