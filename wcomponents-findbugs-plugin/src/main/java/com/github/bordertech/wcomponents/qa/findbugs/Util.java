package com.github.bordertech.wcomponents.qa.findbugs;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.DeepSubtypeAnalysis;
import org.apache.bcel.classfile.JavaClass;

/**
 * Utility methods used by the WComponent detectors.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class Util {

	/**
	 * The WComponent class name.
	 */
	protected static final String WCOMPONENT_CLASS_NAME = "com.github.bordertech.wcomponents.WComponent";

	/**
	 * The ComponentModel class name.
	 */
	protected static final String COMPONENT_MODEL_CLASS_NAME = "com.github.bordertech.wcomponents.ComponentModel";

	/**
	 * The UIContext class name.
	 */
	protected static final String UICONTEXT_CLASS_NAME = "com.github.bordertech.wcomponents.UIContext";

	/**
	 * The reporter used to report bugs.
	 */
	private final BugReporter bugReporter;

	/**
	 * Creates a Util instance.
	 *
	 * @param bugReporter the bugReport to report issues.
	 */
	public Util(final BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	/**
	 * Determines whether the given object is a WComponent.
	 *
	 * @param className the class name to check
	 * @return true if the given class name is a WComponent.
	 */
	protected boolean isWComponent(final String className) {
		return isInstanceOf(className, WCOMPONENT_CLASS_NAME);
	}

	/**
	 * Determines whether the given object is a ComponentModel.
	 *
	 * @param className the class name to check
	 * @return true if the given class name is a ComponentModel.
	 */
	protected boolean isComponentModel(final String className) {
		return isInstanceOf(className, COMPONENT_MODEL_CLASS_NAME);
	}

	/**
	 * Determines whether the given object is a UIContext.
	 *
	 * @param className the class name to check
	 * @return true if the given class name is a UIContext.
	 */
	protected boolean isUIContext(final String className) {
		return isInstanceOf(className, UICONTEXT_CLASS_NAME);
	}

	/**
	 * Determines whether the given object is an instance of the given class/interface.
	 *
	 * @param className the class name to check
	 * @param instanceOfName the class/interface to check against.
	 * @return true if <code>className</code> is an instance of <code>instanceofName</code>.
	 */
	private boolean isInstanceOf(final String className, final String instanceOfName) {
		double certainty = 0.0; // 0% certainty

		try {
			certainty = DeepSubtypeAnalysis.deepInstanceOf(className, instanceOfName);
		} catch (ClassNotFoundException e) {
			bugReporter.reportMissingClass(e);
		}

		return certainty == 1.0;
	}

	/**
	 * Retrieves the class name of a method's return type from the method signature.
	 *
	 * @param sig the method signature.
	 * @return the method's return type.
	 */
	protected String getClassNameFromSignature(final String sig) {
		if (sig != null && sig.length() > 2 && sig.charAt(0) == 'L' && sig.endsWith(";")) {
			return sig.substring(1, sig.length() - 1);
		}

		return sig;
	}

	/**
	 * Indicates whether the given class is a WComponent.
	 *
	 * @param obj the java class to check.
	 * @return true if the class is a WComponent.
	 */
	public boolean isWComponent(final JavaClass obj) {
		return isInstanceOf(obj, WCOMPONENT_CLASS_NAME);
	}

	/**
	 * Indicates whether the given class is a ComponentModel.
	 *
	 * @param obj the java class to check.
	 * @return true if the class is a ComponentModel.
	 */
	public boolean isComponentModel(final JavaClass obj) {
		return isInstanceOf(obj, COMPONENT_MODEL_CLASS_NAME);
	}

	/**
	 * Determines whether the given object is an instance of the given class/interface.
	 *
	 * @param obj the class to check
	 * @param instanceOfName the class/interface to check against.
	 * @return true if <code>obj</code> is an instance of <code>instanceofName</code>.
	 */
	private boolean isInstanceOf(final JavaClass obj, final String instanceOfName) {
		try {
			for (JavaClass interfaze : obj.getAllInterfaces()) {
				if (instanceOfName.equals(interfaze.getClassName())) {
					return true;
				}
			}

			for (JavaClass superClass : obj.getSuperClasses()) {
				if (instanceOfName.equals(superClass.getClassName())) {
					return true;
				}
			}
		} catch (ClassNotFoundException e) {
			bugReporter.reportMissingClass(e);
		}

		return false;
	}

	/**
	 * @return the bug reporter used to report issues.
	 */
	public BugReporter getBugReporter() {
		return bugReporter;
	}
}
