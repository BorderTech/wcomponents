/*
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2003-2008 University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.umd.cs.findbugs;

import edu.umd.cs.findbugs.annotations.ExpectWarning;
import edu.umd.cs.findbugs.annotations.NoWarning;
import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.ba.XFactory;
import edu.umd.cs.findbugs.ba.XField;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.FieldDescriptor;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;
import edu.umd.cs.findbugs.classfile.analysis.AnnotationValue;
import edu.umd.cs.findbugs.plan.AnalysisPass;
import edu.umd.cs.findbugs.plan.ExecutionPlan;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Check uses of the ExpectWarning and NoWarning annotations. This is for internal testing of FindBugs (against
 * findbugsTestCases).
 *
 * @author David Hovemeyer
 * @author Yiannis Paschalidis - added warnings for classes and fields as well.
 */
public class CheckExpectedWarningsCustom implements Detector2, NonReportingDetector {

	private static final boolean DEBUG = SystemProperties.getBoolean("cew.debug");

	private BugCollectionBugReporter reporter;
	private Set<String> possibleBugCodes;
	private Map<ClassDescriptor, Collection<BugInstance>> warningsByClass;
	private Map<MethodDescriptor, Collection<BugInstance>> warningsByMethod;
	private Map<FieldDescriptor, Collection<BugInstance>> warningsByField;

	private ClassDescriptor expectWarning;
	private ClassDescriptor noWarning;
	private boolean warned;

	/**
	 * @param bugReporter the bug reporter
	 */
	public CheckExpectedWarningsCustom(final BugReporter bugReporter) {
		BugCollection realBugReporter = bugReporter.getBugCollection();
		if (realBugReporter instanceof BugCollectionBugReporter) {
			reporter = (BugCollectionBugReporter) realBugReporter;
			expectWarning = DescriptorFactory.createClassDescriptor(ExpectWarning.class);
			noWarning = DescriptorFactory.createClassDescriptor(NoWarning.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitClass(final ClassDescriptor classDescriptor) throws CheckedAnalysisException {
		if (reporter == null) {
			if (!warned) {
				System.err.println(
						"*** NOTE ***: CheckExpectedWarnings disabled because bug reporter doesn't use a BugCollection");
				warned = true;
			}
			return;
		}

		if (warningsByMethod == null) {
			//
			// Build index of all warnings reported so far, by method.
			// Because this detector runs in a later pass than any
			// reporting detector, all warnings should have been
			// produced by this point.
			//
			warningsByMethod = new HashMap<>();
			warningsByField = new HashMap<>();
			warningsByClass = new HashMap<>();
			BugCollection bugCollection = reporter.getBugCollection();

			for (Iterator<BugInstance> i = bugCollection.iterator(); i.hasNext();) {
				BugInstance warning = i.next();
				MethodAnnotation method = warning.getPrimaryMethod();
				FieldAnnotation field = warning.getPrimaryField();
				ClassAnnotation clazz = warning.getPrimaryClass();

				if (method != null) {
					MethodDescriptor methodDesc = method.toXMethod().getMethodDescriptor();
					Collection<BugInstance> warnings = warningsByMethod.get(methodDesc);

					if (warnings == null) {
						warnings = new LinkedList<>();
						warningsByMethod.put(methodDesc, warnings);
					}

					warnings.add(warning);
				} else if (field != null) {
					FieldDescriptor fieldDesc = XFactory.createXField(field).getFieldDescriptor();
					Collection<BugInstance> warnings = warningsByField.get(fieldDesc);

					if (warnings == null) {
						warnings = new LinkedList<>();
						warningsByField.put(fieldDesc, warnings);
					}

					warnings.add(warning);
				} else if (clazz != null) {
					ClassDescriptor classDesc = DescriptorFactory.createClassDescriptorFromDottedClassName(
							clazz.getClassName());
					Collection<BugInstance> warnings = warningsByClass.get(classDesc);

					if (warnings == null) {
						warnings = new LinkedList<>();
						warningsByClass.put(classDesc, warnings);
					}

					warnings.add(warning);
				}
			}

			//
			// Based on enabled detectors, figure out which bug codes
			// could possibly be reported. Don't complain about
			// expected warnings that would be produced by detectors
			// that aren't enabled.
			//
			possibleBugCodes = new HashSet<>();
			ExecutionPlan executionPlan = Global.getAnalysisCache().getDatabase(ExecutionPlan.class);

			for (Iterator<AnalysisPass> i = executionPlan.passIterator(); i.hasNext();) {
				AnalysisPass pass = i.next();

				for (Iterator<DetectorFactory> j = pass.iterator(); j.hasNext();) {
					DetectorFactory factory = j.next();

					Collection<BugPattern> reportedPatterns = factory.getReportedBugPatterns();
					for (BugPattern pattern : reportedPatterns) {
						possibleBugCodes.add(pattern.getAbbrev());
					}
				}
			}

			if (DEBUG) {
				log("CEW: possible warnings are " + possibleBugCodes);
			}
		}

		XClass xclass = Global.getAnalysisCache().getClassAnalysis(XClass.class, classDescriptor);

		for (XMethod xmethod : xclass.getXMethods()) {
			if (DEBUG) {
				log("CEW: checking " + xmethod.toString());
			}

			check(xmethod, expectWarning, true);
			check(xmethod, noWarning, false);
		}

		for (XField xfield : xclass.getXFields()) {
			if (DEBUG) {
				log("CEW: checking " + xfield.toString());
			}

			check(xfield, expectWarning, true);
			check(xfield, noWarning, false);
		}

		check(xclass, expectWarning, true);
		check(xclass, noWarning, false);
	}

	/**
	 * @param xmethod the method
	 * @param annotation the annotation
	 * @param expectWarnings the expected warnings
	 */
	private void check(final XMethod xmethod, final ClassDescriptor annotation,
			final boolean expectWarnings) {
		AnnotationValue expect = xmethod.getAnnotation(annotation);

		if (expect != null) {
			if (DEBUG) {
				log("*** Found " + annotation + " annotation");
			}

			String expectedBugCodes = (String) expect.getValue("value");

			for (StringTokenizer tok = new StringTokenizer(expectedBugCodes, ","); tok.hasMoreTokens();) {
				String bugCode = tok.nextToken();
				int count = countWarnings(xmethod.getMethodDescriptor(), bugCode);

				if (DEBUG) {
					log("  *** Found " + count + " " + bugCode + " warnings");
				}

				if (expectWarnings && count == 0 && possibleBugCodes.contains(bugCode)) {
					reporter.reportBug(new BugInstance(this, "FB_MISSING_EXPECTED_WARNING",
							NORMAL_PRIORITY)
							.addClassAndMethod(xmethod.getMethodDescriptor()).addString(bugCode));
				} else if (!expectWarnings && count > 0) {
					reporter.reportBug(new BugInstance(this, "FB_UNEXPECTED_WARNING",
							NORMAL_PRIORITY)
							.addClassAndMethod(xmethod.getMethodDescriptor()).addString(bugCode));
				}
			}
		}
	}

	/**
	 * @param field the field
	 * @param annotation the annotation
	 * @param expectWarnings the expected warnings
	 */
	private void check(final XField field, final ClassDescriptor annotation,
			final boolean expectWarnings) {
		AnnotationValue expect = field.getAnnotation(annotation);

		if (expect != null) {
			if (DEBUG) {
				log("*** Found " + annotation + " annotation");
			}

			String expectedBugCodes = (String) expect.getValue("value");

			for (StringTokenizer tok = new StringTokenizer(expectedBugCodes, ","); tok.hasMoreTokens();) {
				String bugCode = tok.nextToken();
				int count = countWarnings(field.getFieldDescriptor(), bugCode);

				if (DEBUG) {
					log("  *** Found " + count + " " + bugCode + " warnings");
				}

				if (expectWarnings && count == 0 && possibleBugCodes.contains(bugCode)) {
					reporter.reportBug(new BugInstance(this, "FB_MISSING_EXPECTED_WARNING",
							NORMAL_PRIORITY)
							.addClass(field.getClassDescriptor())
							.addField(field.getFieldDescriptor()).addString(bugCode));
				} else if (!expectWarnings && count > 0) {
					reporter.reportBug(new BugInstance(this, "FB_UNEXPECTED_WARNING",
							NORMAL_PRIORITY)
							.addField(field.getFieldDescriptor()).addString(bugCode));
				}
			}
		}
	}

	/**
	 *
	 * @param xclass the class
	 * @param annotation the annotation
	 * @param expectWarnings the expected warnings
	 */
	private void check(final XClass xclass, final ClassDescriptor annotation,
			final boolean expectWarnings) {
		AnnotationValue expect = xclass.getAnnotation(annotation);

		if (expect != null) {
			if (DEBUG) {
				log("*** Found " + annotation + " annotation");
			}

			String expectedBugCodes = (String) expect.getValue("value");

			for (StringTokenizer tok = new StringTokenizer(expectedBugCodes, ","); tok.hasMoreTokens();) {
				String bugCode = tok.nextToken();
				int count = countWarnings(xclass.getClassDescriptor(), bugCode);

				if (DEBUG) {
					log("  *** Found " + count + " " + bugCode + " warnings");
				}

				if (expectWarnings && count == 0 && possibleBugCodes.contains(bugCode)) {
					reporter.reportBug(new BugInstance(this, "FB_MISSING_EXPECTED_WARNING",
							NORMAL_PRIORITY)
							.addClass(xclass.getClassDescriptor()).addString(bugCode));
				} else if (!expectWarnings && count > 0) {
					reporter.reportBug(new BugInstance(this, "FB_UNEXPECTED_WARNING",
							NORMAL_PRIORITY)
							.addClass(xclass.getClassDescriptor()).addString(bugCode));
				}
			}
		}
	}

	/**
	 * @param methodDescriptor the method descriptor
	 * @param bugCode the bug code
	 * @return the number of warnings
	 */
	private int countWarnings(final MethodDescriptor methodDescriptor, final String bugCode) {
		int count = 0;
		Collection<BugInstance> warnings = warningsByMethod.get(methodDescriptor);

		if (warnings != null) {
			for (BugInstance warning : warnings) {
				BugPattern pattern = warning.getBugPattern();

				if (pattern.getAbbrev().equals(bugCode)) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * @param fieldDescriptor the field descriptor
	 * @param bugCode the bug code
	 * @return the number of warnings
	 */
	private int countWarnings(final FieldDescriptor fieldDescriptor, final String bugCode) {
		int count = 0;
		Collection<BugInstance> warnings = warningsByField.get(fieldDescriptor);

		if (warnings != null) {
			for (BugInstance warning : warnings) {
				BugPattern pattern = warning.getBugPattern();

				if (pattern.getAbbrev().equals(bugCode)) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * @param classDescriptor the class descriptor
	 * @param bugCode the bug code
	 * @return the number of warnings
	 */
	private int countWarnings(final ClassDescriptor classDescriptor, final String bugCode) {
		int count = 0;
		Collection<BugInstance> warnings = warningsByClass.get(classDescriptor);

		if (warnings != null) {
			for (BugInstance warning : warnings) {
				BugPattern pattern = warning.getBugPattern();

				if (pattern.getAbbrev().equals(bugCode)) {
					count++;
				}
			}
		}

		return count;
	}

	@Override
	public void finishPass() {
		// Nothing to do
	}

	@Override
	public String getDetectorClassName() {
		return CheckExpectedWarningsCustom.class.getName();
	}

	/**
	 * Logs a message.
	 *
	 * @param msg the message to log.
	 */
	private void log(final String msg) {
		System.out.println(msg);
	}
}
