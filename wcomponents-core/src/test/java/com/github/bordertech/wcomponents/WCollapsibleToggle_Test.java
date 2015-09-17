package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * WCollapsibleToggle_Test - Unit tests for {@link WCollapsibleToggle}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WCollapsibleToggle_Test extends AbstractWComponentTestCase {

	private WPanel panel;
	private WCollapsible initiallyOpenCollapsible;
	private WCollapsible initiallyCollapsedCollapsible;
	private CollapsibleGroup group;
	private WCollapsibleToggle toggle;

	@Before
	public void setUp() {
		panel = new WPanel();

		toggle = new WCollapsibleToggle(false);
		panel.add(toggle);

		initiallyOpenCollapsible = new WCollapsible(new WText("1"), "1");
		initiallyCollapsedCollapsible = new WCollapsible(new WText("2"), "2");
		panel.add(initiallyOpenCollapsible);

		initiallyOpenCollapsible.setCollapsed(false);
		initiallyCollapsedCollapsible.setCollapsed(true);

		group = new CollapsibleGroup();
		group.addCollapsible(initiallyOpenCollapsible);
		group.addCollapsible(initiallyCollapsedCollapsible);

		UIContext uic = createUIContext();
		uic.setUI(panel);
		setActiveContext(uic);

		panel.add(initiallyCollapsedCollapsible);
	}

	@Test
	public void testExpandAllNoGroup() {
		expandAll();

		Assert.assertFalse("Open collapsible should have stayed open", initiallyOpenCollapsible.
				isCollapsed());
		Assert.assertFalse("Collapsed collapsible should have opened",
				initiallyCollapsedCollapsible.isCollapsed());
	}

	@Test
	public void testCollapseAllNoGroup() {
		collapseAll();

		Assert.assertTrue("Open collapsible should have collapsed", initiallyOpenCollapsible.
				isCollapsed());
		Assert.assertTrue("Collapsed collapsible should have stayed collapsed",
				initiallyCollapsedCollapsible.isCollapsed());
	}

	@Test
	public void testCollapseAllWithGroup() {
		toggle.setGroup(group);
		collapseAll();

		Assert.assertTrue("Open collapsible should have collapsed", initiallyOpenCollapsible.
				isCollapsed());
		Assert.assertTrue("Collapsed collapsible should have stayed collapsed",
				initiallyCollapsedCollapsible.isCollapsed());
	}

	@Test
	public void testExpandAllWithGroup() {
		toggle.setGroup(group);
		expandAll();

		Assert.assertFalse("Open collapsible should have stayed open", initiallyOpenCollapsible.
				isCollapsed());
		Assert.assertFalse("Collapsed collapsible should have opened",
				initiallyCollapsedCollapsible.isCollapsed());
	}

	/**
	 * Runs the "Collapse all" button's action.
	 */
	private void collapseAll() {
		MockRequest request = new MockRequest();
		request.setParameter(toggle.getId(), "collapse");
		panel.serviceRequest(request);
	}

	/**
	 * Runs the "Expand all" button's action.
	 */
	private void expandAll() {
		MockRequest request = new MockRequest();
		request.setParameter(toggle.getId(), "expand");
		panel.serviceRequest(request);
	}
}
