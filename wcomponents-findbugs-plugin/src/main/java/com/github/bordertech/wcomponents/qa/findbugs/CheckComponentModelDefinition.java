package com.github.bordertech.wcomponents.qa.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.classfile.IClassConstants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;

/**
 * A FindBugs detector to ensure that ComponentModels are declared correctly.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CheckComponentModelDefinition extends BytecodeScanningDetector {

	/**
	 * The utility instance for this detector.
	 */
	private final Util util;

	/**
	 * Creates a CheckWComponentFields detector.
	 *
	 * @param bugReporter the reporter used to report bugs.
	 */
	public CheckComponentModelDefinition(final BugReporter bugReporter) {
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
		return util.isComponentModel(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitClassContext(final ClassContext classContext) {
		if (!util.isComponentModel(classContext.getJavaClass())) {
			return;
		}

		// TODO: This is nasty, but classContext.getXClass().isStatic() returns FALSE for static inner classes.
		boolean isStatic = false;

		if (classContext.getXClass().getImmediateEnclosingClass() != null) {
			for (Attribute attribute : classContext.getJavaClass().getAttributes()) {
				if (attribute instanceof InnerClasses) {
					InnerClass inner = ((InnerClasses) attribute).getInnerClasses()[0];
					isStatic = (inner.getInnerAccessFlags() & IClassConstants.ACC_STATIC) != 0;
					break;
				}
			}
		}

		if (!classContext.getXClass().isPublic() || classContext.getClassDescriptor().isAnonymousClass()) {
			reportBug(classContext);
		} else if (classContext.getXClass().getImmediateEnclosingClass() != null && !isStatic) {
			reportBug(classContext);
		} else {
			boolean foundPublicNoArgsConstructor = false;
			boolean foundConstructor = false;

			for (XMethod method : classContext.getXClass().getXMethods()) {
				if ("<init>".equals(method.getMethodDescriptor().getName())) {
					foundConstructor = true;

					if (method.isPublic() && method.getNumParams() == 0) {
						foundPublicNoArgsConstructor = true;
						break;
					}
				}
			}

			if (foundConstructor && !foundPublicNoArgsConstructor) {
				reportBug(classContext);
			}
		}
	}

	/**
	 * Reports a bug against the definition of the ComponentModel.
	 *
	 * @param classContext the class to report against.
	 */
	private void reportBug(final ClassContext classContext) {
		util.getBugReporter().reportBug(new BugInstance(this, "WCCM_NO_PUBLIC_CONSTRUCTOR",
				HIGH_PRIORITY)
				.addClass(classContext.getClassDescriptor()));
	}
}
