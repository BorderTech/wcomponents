/*
 * Contributions to FindBugs
 * Copyright (C) 2009, Tom\u00e1s Pollak
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

import com.github.bordertech.wcomponents.qa.findbugs.CheckComponentModelDefinition;
import com.github.bordertech.wcomponents.qa.findbugs.CheckGetComponentModel;
import com.github.bordertech.wcomponents.qa.findbugs.CheckWComponentFields;
import edu.umd.cs.findbugs.annotations.ExpectWarning;
import edu.umd.cs.findbugs.annotations.NoWarning;
import edu.umd.cs.findbugs.config.UserPreferences;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 * This test runs a FindBugs analysis on the wcomponent qa rules and checks if there are any unexpected bugs.
 *
 * The results are checked for the unexpected bugs of type FB_MISSING_EXPECTED_WARNING or FB_UNEXPECTED_WARNING.
 *
 * @see ExpectWarning
 * @see NoWarning
 * @author Tom\u00e1s Pollak
 *
 * @author Yiannis Paschalidis - modified for WComponents
 */
public class DetectorsTest {

	/**
	 * The bug type that the CheckExpectedWarnings detector reports for unexpected warnings.
	 */
	private static final String FB_UNEXPECTED_WARNING = "FB_UNEXPECTED_WARNING";

	/**
	 * The bug type that the CheckExpectedWarnings detector reports for missing warnings.
	 */
	private static final String FB_MISSING_EXPECTED_WARNING = "FB_MISSING_EXPECTED_WARNING";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(DetectorsTest.class);

	/**
	 * The detectors to test and the bug types they report.
	 */
	private static final DetectorInfo[] DETECTORS
			= {
				new DetectorInfo(CheckWComponentFields.class,
						"WCF_NON_FINAL_WCOMPONENT_FIELD,WCF_COMPONENT_MODEL_FIELD,WCF_UICONTEXT_FIELD"),
				new DetectorInfo(CheckGetComponentModel.class,
						"WCGETM_INCORRECT_USE_OF_GETCOMPONENTMODEL,WCGETM_INCORRECT_USE_OF_GETORCREATECOMPONENTMODEL"),
				new DetectorInfo(CheckComponentModelDefinition.class, "WCCM_NO_PUBLIC_CONSTRUCTOR")
			};

	/**
	 * The BugCollectionReporter to use during the scan.
	 */
	private BugCollectionBugReporter bugReporter;

	/**
	 * The FindBugs engine.
	 */
	private IFindBugsEngine engine;

	@Before
	public void setUp() throws Exception {
		loadFindbugsPlugin();
	}

	@Test
	public void testAllRegressionFiles() throws IOException, InterruptedException {
		setUpEngine("target/test-classes");

		engine.execute();

		// If there are zero bugs, then something's wrong
		assertFalse("No bugs were reported. Something is wrong with the configuration",
				bugReporter.getBugCollection()
				.getCollection().isEmpty());
	}

	@After
	public void checkForUnexpectedBugs() {
		List<BugInstance> unexpectedBugs = new ArrayList<>();

		for (BugInstance bug : bugReporter.getBugCollection()) {
			if (isUnexpectedBug(bug)) {
				unexpectedBugs.add(bug);
				LOG.info(bug.getMessageWithPriorityTypeAbbreviation());
				LOG.info("  " + bug.getPrimarySourceLineAnnotation());
			}
		}

		if (!unexpectedBugs.isEmpty()) {
			Assert.fail("Unexpected bugs (" + unexpectedBugs.size() + "):" + getBugsLocations(
					unexpectedBugs));
		}
	}

	/**
	 * @param unexpectedBugs list of unexpected bugs
	 * @return a printable String concatenating bug locations.
	 */
	private String getBugsLocations(final List<BugInstance> unexpectedBugs) {
		StringBuilder message = new StringBuilder();

		for (BugInstance bugInstance : unexpectedBugs) {
			if (bugInstance.getBugPattern().getType().equals(FB_MISSING_EXPECTED_WARNING)) {
				message.append("\nmissing ");
			} else {
				message.append("\nunexpected ");
			}

			BugAnnotation pattern = bugInstance.getAnnotations().get(1);
			message.append(pattern.getDescription());
			message.append(' ');
			message.append(bugInstance.getPrimarySourceLineAnnotation());
		}

		return message.toString();
	}

	/**
	 * @param bug the bug instance to check Returns if a bug instance is unexpected for this test.
	 * @return true if is unexpected bug
	 */
	private boolean isUnexpectedBug(final BugInstance bug) {
		return FB_MISSING_EXPECTED_WARNING.equals(bug.getType()) || FB_UNEXPECTED_WARNING.equals(
				bug.getType());
	}

	/**
	 * Loads the default detectors from findbugs.jar, to isolate the test from others that use fake plugins.
	 */
	private void loadFindbugsPlugin() {
		DetectorFactoryCollection dfc = new DetectorFactoryCollection();
		DetectorFactoryCollection.resetInstance(dfc);
	}

	/**
	 * Sets up a FB engine to run on the 'findbugsTestCases' project. It enables all the available detectors and reports
	 * all the bug categories. Uses a low priority threshold.
	 *
	 * @param analyzeMe files to analyse
	 */
	private void setUpEngine(final String... analyzeMe) {
		engine = new FindBugs2();
		Project project = new Project();
		project.setProjectName("wcomponentTestCases");
		engine.setProject(project);

		DetectorFactoryCollection detectorFactoryCollection = DetectorFactoryCollection.instance();
		engine.setDetectorFactoryCollection(detectorFactoryCollection);

		bugReporter = new BugCollectionBugReporter(project);
		bugReporter.setPriorityThreshold(Priorities.LOW_PRIORITY);

		engine.setBugReporter(bugReporter);
		UserPreferences preferences = UserPreferences.createDefaultUserPreferences();
		preferences.enableAllDetectors(false);
		preferences.getFilterSettings().clearAllCategories();
		engine.setUserPreferences(preferences);

		for (String s : analyzeMe) {
			project.addFile(s);
		}

		project.addAuxClasspathEntry("../wcomponents-core/target/classes");
		setUpDetectors(preferences);
	}

	/**
	 * @param preferences the user preferences
	 */
	private void setUpDetectors(final UserPreferences preferences) {
		DetectorFactoryCollection detectorFactoryCollection = DetectorFactoryCollection.instance();
		Plugin plugin = detectorFactoryCollection.getCorePlugin();

		//Add Custom Check Warnings
		Class checkClass = CheckExpectedWarningsCustom.class;
		DetectorFactory checkFactory = new DetectorFactory(plugin, checkClass.getSimpleName(), checkClass, true, "fast",
				"", "");
		preferences.enableDetector(checkFactory, true);

		for (DetectorInfo info : DETECTORS) {
			DetectorFactory factory = new DetectorFactory(plugin, info.detector.getSimpleName(), info.detector, true, "fast",
					info.reports, "");
			detectorFactoryCollection.registerDetector(factory);
			plugin.addDetectorFactory(factory);
			preferences.enableDetector(factory, true);

			for (String bugCode : info.reports.split(",")) {
				bugCode = bugCode.trim();
				String abbr = bugCode.replaceAll("_.*", "");
				detectorFactoryCollection.registerBugCode(new BugCode(bugCode, ""));
				detectorFactoryCollection.registerBugPattern(new BugPattern(bugCode, abbr, "", false, "", "",
						"", "", 0));
			}

		}
	}

	/**
	 * Basic information about a detector and the bug types that it reports. This is normally obtained from
	 * findbugs.xml.
	 */
	private static final class DetectorInfo {

		/**
		 * The detector class.
		 */
		private final Class<? extends Detector> detector;

		/**
		 * A comma-separated list of bug types that the detector reports.
		 */
		private final String reports;

		/**
		 * Creates a DetectorInfo.
		 *
		 * @param detector the detector class.
		 * @param reports a comma-separated list of bug types that the detector reports.
		 */
		public DetectorInfo(final Class<? extends Detector> detector, final String reports) {
			this.detector = detector;
			this.reports = reports;
		}
	}
}
