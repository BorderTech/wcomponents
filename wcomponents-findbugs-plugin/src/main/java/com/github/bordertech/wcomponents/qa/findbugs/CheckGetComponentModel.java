package com.github.bordertech.wcomponents.qa.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.MethodAnnotation;
import edu.umd.cs.findbugs.OpcodeStack.Item;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import org.apache.bcel.classfile.JavaClass;

/**
 * A FindBugs detector to ensure that WComponent's getComponentModel() method is used correctly.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CheckGetComponentModel extends OpcodeStackDetector {

	/**
	 * The utility class instance for this detector.
	 */
	private final Util util;

	/**
	 * Creates a CheckWComponentFields detector.
	 *
	 * @param bugReporter the reporter used to report bugs.
	 */
	public CheckGetComponentModel(final BugReporter bugReporter) {
		util = new Util(bugReporter);
	}

	/**
	 * Override this as we're only interested in WComponent classes.
	 *
	 * @param obj the java class to check.
	 * @return true if the class is a WComponent.
	 */
	@Override
	public boolean shouldVisit(final JavaClass obj) {
		return util.isWComponent(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sawOpcode(final int seen) {
		String methodName = getMethodName();
		boolean setter = methodName.startsWith("set");
		boolean getter = methodName.startsWith("get");
		String bug = null;
		int priority = NORMAL_PRIORITY;

		switch (seen) {
			case INVOKEVIRTUAL: {
				if (util.isWComponent(getClassConstantOperand())) {
					// Check for call to getComponentModel() & getOrCreateComponentModel().
					// We don't check the specific return type as the code wouldn't have compiled if it's not a ComponentModel.
					if (setter && "getComponentModel".equals(getNameConstantOperand()) && getSigConstantOperand().startsWith(
							"()L")) {
						// Suspicious to call getComponentModel in a setter,
						// but will not necessarily lead to application errors.
						bug = "WCGETM_INCORRECT_USE_OF_GETCOMPONENTMODEL";
					} else if (getter && !"getOrCreateComponentModel".equals(methodName) && "getOrCreateComponentModel".equals(
							getNameConstantOperand()) && getSigConstantOperand().startsWith("()L")) {
						// Suspicious to call getOrCreateComponentModel in a getter,
						// but will not necessarily lead to application errors.
						bug = "WCGETM_INCORRECT_USE_OF_GETORCREATECOMPONENTMODEL";
					}
				} else if (util.isComponentModel(getClassConstantOperand()) && getNameConstantOperand().startsWith(
						"set")) {
					// TODO: this may not work if there are any double or long args.
					Item model = stack.getStackItem(getNumberMethodArguments());
					XMethod from = model.getReturnValueOf();

					if (from != null && "getComponentModel".equals(from.getName())
							&& from.getSignature().startsWith("()L") && util.isWComponent(
							from.getClassName())) {
						bug = "WCGETM_INCORRECT_USE_OF_GETCOMPONENTMODEL";
						priority = HIGH_PRIORITY;
					}
				}

				break;
			}
			case PUTFIELD: {
				if (util.isComponentModel(getClassConstantOperand())) {
					Item model = stack.getStackItem(1);
					XMethod from = model.getReturnValueOf();

					if (from != null
							&& "getComponentModel".equals(from.getName())
							&& from.getSignature().startsWith("()L")
							&& util.isWComponent(from.getClassName())) {
						bug = "WCGETM_INCORRECT_USE_OF_GETCOMPONENTMODEL";
						priority = HIGH_PRIORITY;
					}
				}
			}
		}

		if (bug != null) {
			util.getBugReporter().reportBug(new BugInstance(this, bug, priority)
					.addClass(this)
					.addMethod(MethodAnnotation.fromVisitedMethod(this))
					.addSourceLine(this, getPC()));
		}
	}
}
