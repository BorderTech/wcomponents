package com.github.bordertech.wcomponents.qa.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.FieldAnnotation;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

/**
 * A FindBugs detector to ensure that all WComponent fields are marked final.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CheckWComponentFields extends BytecodeScanningDetector {

	/**
	 * The utility class instance for this detector.
	 */
	private final Util util;

	/**
	 * Creates a CheckWComponentFields detector.
	 *
	 * @param bugReporter the reporter used to report bugs.
	 */
	public CheckWComponentFields(final BugReporter bugReporter) {
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
	public void visitField(final Field obj) {
		// All fields inside WComponents must be static or final
		if (!obj.isFinal() && !obj.isStatic()) {
			FieldAnnotation ann = FieldAnnotation.fromVisitedField(this);

			util.getBugReporter().reportBug(new BugInstance(this, "WCF_NON_FINAL_WCOMPONENT_FIELD",
					HIGH_PRIORITY)
					.addClass(this)
					.addField(ann));
		}

		// Component models must never be stored as fields
		if (util.isComponentModel(util.getClassNameFromSignature(obj.getType().getSignature()))) {
			FieldAnnotation ann = FieldAnnotation.fromVisitedField(this);

			util.getBugReporter().reportBug(new BugInstance(this, "WCF_COMPONENT_MODEL_FIELD",
					HIGH_PRIORITY)
					.addClass(this)
					.addField(ann));
		}

		// UIContexts must never be stored as fields
		if (util.isUIContext(util.getClassNameFromSignature(obj.getType().getSignature()))) {
			FieldAnnotation ann = FieldAnnotation.fromVisitedField(this);

			util.getBugReporter().reportBug(new BugInstance(this, "WCF_UICONTEXT_FIELD",
					HIGH_PRIORITY)
					.addClass(this)
					.addField(ann));
		}
	}
}
