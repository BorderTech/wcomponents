package com.github.bordertech.wcomponents.monitor;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.monitor.UicStats.Stat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link UicStats}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class UicStats_Test extends AbstractWComponentTestCase {

	/**
	 * the app for the uic.
	 */
	private WApplication app;

	/**
	 * the button of the app.
	 */
	private WButton button;

	/**
	 * the label of the app.
	 */
	private WLabel label;

	/**
	 * the stats produced.
	 */
	private UicStats stats;

	@Before
	public void setUp() {
		UIContext uic;
		uic = new UIContextImpl();
		setActiveContext(uic);

		app = new WApplication();
		button = new WButton("PUSH");
		app.add(button);
		label = new WLabel("HERE");
		app.add(label);
		uic.setUI(app);
		stats = new UicStats(uic);
	}

	/**
	 * Test getUI.
	 */
	@Test
	public void testGetUI() {
		WComponent resultComponent = stats.getUI();
		Assert.assertEquals("should return component from constructor", app, resultComponent);
	}

	/**
	 * Test getRootWCs.
	 */
	@Test
	public void testGetRootWCs() {
		Set<WComponent> resultWcs = stats.getRootWCs();

		Assert.assertNotNull("resultWcs cannot be null", resultWcs);
		Assert.assertEquals("there should be 1 rootWc", 1, resultWcs.size());
		Assert.assertTrue("the single rootWc should be App", resultWcs.contains(app));
	}

	/**
	 * Test getWCsAnalysed - when one has been analysed.
	 */
	@Test
	public void testGetWCsAnalysedForSpecificApp() {
		stats.analyseWC(app);

		int expectedLoopCount = 1;
		int loopCount = 0;
		for (Iterator<WComponent> resultWcs = stats.getWCsAnalysed(); resultWcs.hasNext(); loopCount++) {
			WComponent resultComponent = resultWcs.next();
			Assert.assertEquals("the analysed component should be app", app, resultComponent);
		}
		Assert.assertEquals("there should be only one analysed components", expectedLoopCount,
				loopCount);
	}

	/**
	 * Test getWCsAnalysed - when none have been analysed.
	 */
	@Test
	public void testGetWCsAnalysedForSpecificAppNull() {
		stats.analyseWC(null);

		int expectedLoopCount = 0;
		int loopCount = 0;
		for (Iterator<WComponent> resultWcs = stats.getWCsAnalysed(); resultWcs.hasNext(); loopCount++) {
			Assert.fail("there should be no analysed components");
		}
		Assert.assertEquals("there should be no analysed components", expectedLoopCount, loopCount);
	}

	/**
	 * Test getWCsAnalysed - when all rootWcs have been analysed.
	 */
	@Test
	public void testGetWCsAnalysedForAllRoots() {
		stats.analyseAllRootWCs();

		int expectedLoopCount = 1;
		int loopCount = 0;
		for (Iterator<WComponent> resultWcs = stats.getWCsAnalysed(); resultWcs.hasNext(); loopCount++) {
			WComponent resultComponent = resultWcs.next();
			Assert.assertEquals("the analysed component should be app", app, resultComponent);
		}
		Assert.assertEquals("there should be only one analysed components", expectedLoopCount,
				loopCount);
	}

	/**
	 * Test getWCTreeStats.
	 */
	@Test
	public void testGetWCTreeStats() {
		stats.analyseWC(app);

		Map<WComponent, Stat> resultStats = stats.getWCTreeStats(app);

		// expect to find the WComponent app, WButton button and the WLabel
		// label
		for (Map.Entry<WComponent, Stat> entry : resultStats.entrySet()) {
			WComponent comp = entry.getKey();
			Stat stat = entry.getValue();

			if (comp instanceof WLabel) {
				Assert.assertEquals("this should be the label created", label, comp);
				Assert.assertEquals("stat should have correct label name", label.getId(), stat.
						getName());
			} else if (comp instanceof WButton) {
				Assert.assertEquals("this should be the button in the app", button, comp);
				Assert.assertEquals("stat should have correct button name", button.getId(), stat.
						getName());
			} else if (comp instanceof WApplication) {
				Assert.assertEquals("this should be the app", app, comp);
				Assert.
						assertEquals("stat should have correct app name", app.getId(), stat.
								getName());
			}
		}
	}
}
