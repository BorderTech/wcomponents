package com.github.bordertech.wcomponents.qa.findbugs;

import com.github.bordertech.wcomponents.ComponentModel;
import edu.umd.cs.findbugs.annotations.ExpectWarning;
import edu.umd.cs.findbugs.annotations.NoWarning;

/**
 * Test code for the {@link CheckComponentModelDefinition} detector.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CheckComponentModelDefinition_Test {

	/**
	 * public + static -- ok.
	 */
	@NoWarning(value = "WCCM")
	public static final class PublicStaticModel extends ComponentModel {
	}

	/**
	 * Missing static -- should log an error.
	 */
	@ExpectWarning(value = "WCCM")
	public final class PublicModel extends ComponentModel {
	}

	/**
	 * Missing public -- should log an error.
	 */
	@ExpectWarning(value = "WCCM")
	static final class StaticModel extends ComponentModel {
	}

	/**
	 * Missing public + static -- should log an error.
	 */
	@ExpectWarning(value = "WCCM")
	final class Model extends ComponentModel {
	}

	/**
	 * Has default constructor -- ok.
	 */
	@NoWarning(value = "WCCM")
	public static final class PublicStaticModelWithDefaultConstructor extends ComponentModel {

		/**
		 * Default constructor.
		 */
		public PublicStaticModelWithDefaultConstructor() {
		}

		/**
		 * @param arg argument
		 */
		public PublicStaticModelWithDefaultConstructor(final String arg) {
			setAttribute("arg", arg);
		}
	}

	/**
	 * Has no default constructor -- should log an error.
	 */
	@ExpectWarning(value = "WCCM")
	public static final class PublicStaticModelNoDefaultConstructor extends ComponentModel {

		/**
		 * @param arg argument
		 */
		public PublicStaticModelNoDefaultConstructor(final String arg) {
			setAttribute("arg", arg);
		}
	}
}
