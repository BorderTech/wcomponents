package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WTabSet_Test - Unit tests for {@link WTabSet}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WTabSet_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		WTabSet tabset = new WTabSet();
		Assert.assertEquals("Incorrect default type", WTabSet.TYPE_TOP, tabset.getType());
	}

	@Test
	public void testConstructor2() {
		WTabSet tabset = new WTabSet(WTabSet.TYPE_LEFT);
		Assert.assertEquals("Incorrect type", WTabSet.TYPE_LEFT, tabset.getType());
	}

	@Test
	public void testGetTabIndex() {
		WTabSet tabset = new WTabSet();

		WComponent tab1 = new WLabel();
		WComponent tab2 = new WLabel();
		WComponent tab3 = new WLabel();

		tabset.addTab(tab1, "tab1", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(tab2, "tab2", WTabSet.TAB_MODE_SERVER);

		// dynamically add tab3
		tabset.setLocked(true);
		setActiveContext(createUIContext());
		tabset.addTab(tab3, "tab3", WTabSet.TAB_MODE_SERVER);

		Assert.assertEquals("Incorrect number of tabs", 3, tabset.getTotalTabs());
		Assert.assertEquals("Incorrect static tab index for 1st tab", 0, tabset.getTabIndex(tab1));
		Assert.assertEquals("Incorrect static tab index for 2nd tab", 1, tabset.getTabIndex(tab2));
		Assert.assertEquals("Incorrect static tab index for 3rd tab", 2, tabset.getTabIndex(tab3));

		resetContext();
		Assert.assertEquals("Incorrect static number of tabs", 2, tabset.getTotalTabs());
		Assert.assertEquals("Incorrect static tab index for 3rd tab", -1, tabset.getTabIndex(tab3));
	}

	@Test
	public void testGetTabIndexWithTabGroup() {
		WTabSet tabset = new WTabSet();

		WComponent tab1 = new WLabel();
		WComponent tab2 = new WLabel();
		WComponent tab3 = new WLabel();
		WComponent tab4 = new WLabel();
		WComponent tab5 = new WLabel();

		tabset.addTab(tab1, "tab1", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(tab2, "tab2", WTabSet.TAB_MODE_SERVER);

		WTabGroup group = new WTabGroup("Group");
		group.addTab(tab3, "tab3", WTabSet.TAB_MODE_SERVER);
		group.addTab(tab4, "tab4", WTabSet.TAB_MODE_SERVER);
		tabset.add(group);

		// Dynamically add tab5
		tabset.setLocked(true);
		setActiveContext(createUIContext());
		tabset.addTab(tab5, "tab5", WTabSet.TAB_MODE_SERVER);

		Assert.assertEquals("Incorrect number of tabs", 5, tabset.getTotalTabs());
		Assert.assertEquals("Incorrect tab index for 1st tab", 0, tabset.getTabIndex(tab1));
		Assert.assertEquals("Incorrect tab index for 2nd tab", 1, tabset.getTabIndex(tab2));
		Assert.assertEquals("Incorrect tab index for 3rd tab", 2, tabset.getTabIndex(tab3));
		Assert.assertEquals("Incorrect tab index for 4th tab", 3, tabset.getTabIndex(tab4));
		Assert.assertEquals("Incorrect tab index for 5th tab", 4, tabset.getTabIndex(tab5));

		resetContext();
		Assert.assertEquals("Incorrect static number of tabs", 4, tabset.getTotalTabs());
		Assert.assertEquals("Incorrect static tab index for 3rd tab", -1, tabset.getTabIndex(tab5));
	}

	@Test
	public void testGetTabByIndex() {
		WTabSet tabset = new WTabSet();

		WTab tab1 = tabset.addTab(new WLabel(), "tab1", WTabSet.TAB_MODE_SERVER);
		WTab tab2 = tabset.addTab(new WLabel(), "tab2", WTabSet.TAB_MODE_SERVER);

		// Dynamically add tab3
		tabset.setLocked(true);
		setActiveContext(createUIContext());
		WTab tab3 = tabset.addTab(new WLabel(), "tab3", WTabSet.TAB_MODE_SERVER);

		Assert.assertEquals("Incorrect number of tabs", 3, tabset.getTotalTabs());
		Assert.assertEquals("Incorrect 1st tab", tab1, tabset.getTab(0));
		Assert.assertEquals("Incorrect 2nd tab", tab2, tabset.getTab(1));
		Assert.assertEquals("Incorrect 3rd tab", tab3, tabset.getTab(2));

		resetContext();
		Assert.assertEquals("Incorrect static number of tabs", 2, tabset.getTotalTabs());
	}

	@Test
	public void testGetTabByIndexWithTabGroup() {
		WTabSet tabset = new WTabSet();

		WTab tab1 = new WTab(new WLabel(), "tab1", WTabSet.TAB_MODE_SERVER);
		WTab tab2 = new WTab(new WLabel(), "tab2", WTabSet.TAB_MODE_SERVER);
		WTab tab3 = new WTab(new WLabel(), "tab3", WTabSet.TAB_MODE_SERVER);
		WTab tab4 = new WTab(new WLabel(), "tab4", WTabSet.TAB_MODE_SERVER);
		WTab tab5 = new WTab(new WLabel(), "tab5", WTabSet.TAB_MODE_SERVER);

		tabset.add(tab1);
		tabset.add(tab2);
		WTabGroup group = new WTabGroup("Group");
		group.add(tab3);
		group.add(tab4);
		tabset.add(group);

		// Dynamically add tab5
		tabset.setLocked(true);
		setActiveContext(createUIContext());
		tabset.add(tab5);

		Assert.assertEquals("Incorrect number of tabs", 5, tabset.getTotalTabs());
		Assert.assertEquals("Incorrect 1st tab", tab1, tabset.getTab(0));
		Assert.assertEquals("Incorrect 2ndt tab", tab2, tabset.getTab(1));
		Assert.assertEquals("Incorrect 3rd tab", tab3, tabset.getTab(2));
		Assert.assertEquals("Incorrect 4th tab", tab4, tabset.getTab(3));
		Assert.assertEquals("Incorrect 5th tab", tab5, tabset.getTab(4));

		resetContext();
		Assert.assertEquals("Incorrect number of static tabs", 4, tabset.getTotalTabs());
	}

	@Test
	public void testGetActiveTab() {
		WTabSet tabset = new WTabSet();

		WComponent tab1 = new WLabel("label1");
		WComponent tab2 = new WLabel("label2");
		WComponent tab3 = new WLabel("label3");

		tabset.addTab(tab1, "tab1", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(tab2, "tab2", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(tab3, "tab3", WTabSet.TAB_MODE_SERVER);

		tabset.setLocked(true);
		setActiveContext(createUIContext());

		// Default to first tab
		Assert.assertEquals("Incorrect active tab", tab1, tabset.getActiveTab().getContent());
		Assert.
				assertEquals("Incorrect active tab", tab1, tabset.getActiveTabs().get(0).
						getContent());
		tabset.setActiveTab(tab3);
		Assert.assertEquals("Incorrect active tab", tab3, tabset.getActiveTab().getContent());
		Assert.
				assertEquals("Incorrect active tab", tab3, tabset.getActiveTabs().get(0).
						getContent());
	}

	@Test
	public void testActiveIndexAccessors() {
		WTabSet tabset = new WTabSet();
		tabset.addTab(new WLabel("tab1"), "tab1", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(new WLabel("tab2"), "tab2", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(new WLabel("tab3"), "tab3", WTabSet.TAB_MODE_SERVER);

		assertAccessorsCorrect(tabset, "activeIndex", 0, 1, 2);
	}

	@Test
	public void testContentHeightAccessors() {
		assertAccessorsCorrect(new WTabSet(), "contentHeight", null, "1", "2");
	}

	@Test
	public void testSetTabVisible() {
		WTabSet tabset = new WTabSet();
		tabset.addTab(new WLabel("tab1"), "tab1", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(new WLabel("tab2"), "tab2", WTabSet.TAB_MODE_SERVER);

		Assert.assertTrue("Tab should be visible by default", tabset.isTabVisible(0));
		Assert.assertTrue("Tab should be visible by default", tabset.isTabVisible(1));

		tabset.setTabVisible(0, false);
		Assert.assertFalse("Tab should not be visible", tabset.isTabVisible(0));
		Assert.assertTrue("Other tabs should be visible", tabset.isTabVisible(1));

		tabset.setTabVisible(0, true);
		tabset.setLocked(true);
		setActiveContext(createUIContext());
		tabset.setTabVisible(0, false);
		Assert.assertFalse("Tab should not be visible for affected context", tabset.isTabVisible(0));
		Assert.assertTrue("Other tabs should be visible for affected context", tabset.
				isTabVisible(1));

		resetContext();
		Assert.assertTrue("Tab should be visible for other contexts", tabset.isTabVisible(0));
		Assert.assertTrue("Other tabs should be visible for other contexts", tabset.isTabVisible(1));
	}

	@Test
	public void testHandleRequest() {
		WTabSet tabset = new WTabSet();
		tabset.addTab(new WLabel("tab1"), "tab1", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(new WLabel("tab2"), "tab2", WTabSet.TAB_MODE_SERVER);
		tabset.addTab(new WLabel("tab3"), "tab3", WTabSet.TAB_MODE_CLIENT);

		tabset.setLocked(true);
		setActiveContext(createUIContext());

		MockRequest request = new MockRequest();
		request.setParameter(tabset.getId(), "1");
		tabset.handleRequest(request);
		Assert.assertEquals("Incorrect dynamic active index after handleRequest", 1, tabset.
				getActiveIndex());

		resetContext();
		Assert.assertEquals("Incorrect static active index after handleRequest", 0, tabset.
				getActiveIndex());

		setActiveContext(createUIContext());
		request.setParameter(tabset.getId(), "2");
		tabset.handleRequest(request);

		Assert.assertEquals("Incorrect dynamic active index after handleRequest", 2, tabset.
				getActiveIndex());

		resetContext();
		Assert.assertEquals("Incorrect static active index after handleRequest", 0, tabset.
				getActiveIndex());

		// Test that selection doesn't change when the tabset is disabled
		tabset.setDisabled(true);
		setActiveContext(createUIContext());
		request.setParameter(tabset.getId(), "1");
		tabset.handleRequest(request);

		Assert.assertEquals("Dynamic active index should not change when disabled", 0, tabset.
				getActiveIndex());

		resetContext();
		Assert.assertEquals("Static active index should not change when disabled", 0, tabset.
				getActiveIndex());
	}

	@Test
	public void testHandleRequestWithInvisibleTabAndTabGroup() {
		WTabSet tabset = new WTabSet(WTabSet.TabSetType.APPLICATION);
		tabset.addTab(new WLabel("tab1"), "tab1", WTabSet.TAB_MODE_SERVER);
		WTabGroup group = new WTabGroup("group1");
		group.addTab(new WLabel("tab2"), "tab2", WTabSet.TAB_MODE_SERVER);
		group.addTab(new WLabel("tab3"), "tab3", WTabSet.TAB_MODE_CLIENT);
		group.addTab(new WLabel("tab4"), "tab4", WTabSet.TAB_MODE_CLIENT);
		group.addTab(new WLabel("tab5"), "tab5", WTabSet.TAB_MODE_CLIENT);
		tabset.add(group);
		tabset.addTab(new WLabel("tab6"), "tab6", WTabSet.TAB_MODE_SERVER);

		// Make tab5 not visible
		tabset.setTabVisible(4, false);

		tabset.setLocked(true);
		setActiveContext(createUIContext());

		// Select "tab6" (will be index 4 on the client as tab2 is not visible)
		MockRequest request = new MockRequest();
		request.setParameter(tabset.getId(), "4");

		tabset.handleRequest(request);

		Assert.assertEquals("Incorrect dynamic active index after handleRequest", 5, tabset.
				getActiveIndex());

		resetContext();
		Assert.assertEquals("Incorrect static active index after handleRequest", 0, tabset.
				getActiveIndex());

		// Select "tab2"
		setActiveContext(createUIContext());
		request.setParameter(tabset.getId(), "1");
		tabset.handleRequest(request);

		Assert.assertEquals("Incorrect dynamic active index after handleRequest", 1, tabset.
				getActiveIndex());

		resetContext();
		Assert.assertEquals("Incorrect static active index after handleRequest", 0, tabset.
				getActiveIndex());
	}

	/**
	 * Test addTab.
	 */
	@Test
	public void testAddTab() {
		WTabSet tabset = new WTabSet();

		// Tab1 - Content, String label, mode
		WComponent content1 = new DefaultWComponent();
		String label1 = "label1";
		WTabSet.TabMode mode1 = WTabSet.TabMode.SERVER;
		tabset.addTab(content1, label1, mode1);

		Assert.
				assertEquals("first tab has correct content", content1, tabset.getTab(0).
						getContent());
		Assert.assertEquals("first tab has correct label", label1, tabset.getTab(0).getTabLabel().
				getText());
		Assert.assertEquals("first tab has correct mode", mode1, tabset.getTab(0).getMode());
		Assert.assertEquals("first tab has correct accessKey", 0, tabset.getTab(0).getAccessKey());

		// Tab2 - Content, String label, mode, accessKey
		WComponent content2 = new DefaultWComponent();
		String label2 = "label2";
		WTabSet.TabMode mode2 = WTabSet.TabMode.SERVER;
		char accessKey2 = 'X';
		tabset.addTab(content2, label2, mode2, accessKey2);

		Assert.assertEquals("second tab has correct content", content2, tabset.getTab(1).
				getContent());
		Assert.assertEquals("second tab has correct label", label2, tabset.getTab(1).getTabLabel().
				getText());
		Assert.assertEquals("second tab has correct mode", mode2, tabset.getTab(1).getMode());
		Assert.assertEquals("second tab has correct accessKey", accessKey2, tabset.getTab(1).
				getAccessKey());

		// Tab3 - Content, label, mode
		WComponent content3 = new DefaultWComponent();
		WDecoratedLabel label3 = new WDecoratedLabel("label3");
		WTabSet.TabMode mode3 = WTabSet.TabMode.SERVER;
		tabset.addTab(content3, label3, mode3);

		Assert.
				assertEquals("third tab has correct content", content3, tabset.getTab(2).
						getContent());
		Assert.assertEquals("third tab has correct label", label3, tabset.getTab(2).getTabLabel());
		Assert.assertEquals("third tab has correct mode", mode3, tabset.getTab(2).getMode());
		Assert.assertEquals("third tab has correct accessKey", 0, tabset.getTab(2).getAccessKey());

		// Tab4 - Content, label, mode, accessKey
		WComponent content4 = new DefaultWComponent();
		WDecoratedLabel label4 = new WDecoratedLabel("label4");
		WTabSet.TabMode mode4 = WTabSet.TabMode.SERVER;
		char accessKey4 = 'X';
		tabset.addTab(content4, label4, mode4, accessKey4);

		Assert.assertEquals("fourth tab has correct content", content4, tabset.getTab(3).
				getContent());
		Assert.assertEquals("fourth tab has correct label", label4, tabset.getTab(3).getTabLabel());
		Assert.assertEquals("fourth tab has correct mode", mode4, tabset.getTab(3).getMode());
		Assert.assertEquals("fourth tab has correct accessKey", accessKey4, tabset.getTab(3).
				getAccessKey());
	}

	/**
	 * Test addSeparator.
	 */
	@Test
	public void testAddSeparator() {
		WTabSet tabset = new WTabSet();
		WComponent content = new DefaultWComponent();
		WDecoratedLabel label = new WDecoratedLabel("label");
		WTabSet.TabMode mode = WTabSet.TabMode.SERVER;
		tabset.addTab(content, label, mode);

		Assert.
				assertEquals("first child should be the tab already added", 1, tabset.
						getChildCount());
		tabset.addSeparator();
		Assert.assertEquals("there should be two children", 2, tabset.getChildCount());
		Assert
				.assertTrue("second child should be the separator just added",
						tabset.getChildAt(1) instanceof WSeparator);
	}

	/**
	 * Test getActiveIndex - without and with tabs in tabset.
	 */
	@Test
	public void testGetActiveIndex() {
		WTabSet tabset = new WTabSet();
		WTabSet.TabMode mode = WTabSet.TabMode.SERVER;

		// no tabs in tabset
		int indexExpected = 0;
		int active = tabset.getActiveIndex();
		Assert.assertEquals("with no active index should return 0", indexExpected, active);

		// add some tabs and set the index to 1
		final int indexSet = 1;
		tabset.addTab(new DefaultWComponent(), new WDecoratedLabel("label1"), mode);
		tabset.addTab(new DefaultWComponent(), new WDecoratedLabel("label2"), mode);

		setActiveContext(createUIContext());
		tabset.setActiveIndex(indexSet);

		Assert.assertEquals("should return indexSet", indexSet, tabset.getActiveIndex());

		resetContext();
		Assert.assertEquals("Default index should not have changed", 0, active);
	}

	/**
	 * Test getActiveIndices - when tabset is empty.
	 */
	@Test
	public void testGetActiveIndices() {
		WTabSet tabset = new WTabSet();
		List<Integer> activeIndices = tabset.getActiveIndices();

		Assert.assertNotNull("should not return null List", activeIndices);
		Assert.assertTrue("should return empty List", activeIndices.isEmpty());
	}

	/**
	 * Test getActiveIndices - in a uic, when removing invisible tabs from the active list.
	 */
	@Test
	public void testGetActiveIndicesWhenSomeInvisisible() {
		WTabSet tabset = new WTabSet();
		WTabSet.TabMode mode = WTabSet.TabMode.SERVER;

		WComponent content1 = new DefaultWComponent();
		WDecoratedLabel label1 = new WDecoratedLabel("label1");

		WComponent content2 = new DefaultWComponent();
		WDecoratedLabel label2 = new WDecoratedLabel("label2");

		WComponent content3 = new DefaultWComponent();
		WDecoratedLabel label3 = new WDecoratedLabel("label3");

		tabset.addTab(content1, label1, mode);
		tabset.addTab(content2, label2, mode);
		tabset.addTab(content3, label3, mode);

		int[] indices = {0, 1, 2};
		tabset.setActiveIndices(indices);
		tabset.setLocked(true);

		// all three visible
		List<Integer> expectedIndices = Arrays.asList(new Integer[]{0, 1, 2});
		List<Integer> activeIndices = tabset.getActiveIndices();
		Assert.assertNotNull("should get non null List", activeIndices);
		Assert.assertEquals("active indices should be those expected", expectedIndices,
				activeIndices);

		// make the first invisible
		expectedIndices = Arrays.asList(new Integer[]{1, 2});
		setActiveContext(createUIContext());
		tabset.setTabVisible(0, false);
		activeIndices = tabset.getActiveIndices();
		Assert.assertNotNull("should get non null List", activeIndices);
		Assert.assertEquals("active indices should be those expected", expectedIndices,
				activeIndices);

		resetContext();
		expectedIndices = Arrays.asList(new Integer[]{0, 1, 2});
		activeIndices = tabset.getActiveIndices();
		Assert.assertEquals("active indices should be those expected", expectedIndices,
				activeIndices);

		// make all invisible - so activeTabs is now empty - recreated as [0]
		setActiveContext(createUIContext());
		expectedIndices = Arrays.asList(new Integer[]{0});
		tabset.setTabVisible(0, false);
		tabset.setTabVisible(1, false);
		tabset.setTabVisible(2, false);
		activeIndices = tabset.getActiveIndices();
		Assert.assertNotNull("should get non null List", activeIndices);
		Assert.assertEquals("active indices should be those expected", expectedIndices,
				activeIndices);
	}

	/**
	 * Test setActiveTab - when the tab to be set active isnt even in the tabset.
	 */
	@Test
	public void testSetActiveTabWhenNotThere() {
		WTabSet tabset = new WTabSet();
		WTabSet.TabMode mode = WTabSet.TabMode.SERVER;

		WComponent content0 = new DefaultWComponent();
		WComponent content1 = new DefaultWComponent();
		WDecoratedLabel label1 = new WDecoratedLabel("label1");
		WComponent content2 = new DefaultWComponent();
		WDecoratedLabel label2 = new WDecoratedLabel("label2");
		tabset.addTab(content1, label1, mode);
		tabset.addTab(content2, label2, mode);
		int activeIndex = 1;
		tabset.setActiveIndex(activeIndex);

		WTab tab = new WTab(content0, "tabname", mode);
		// but not added to tabset

		Assert.assertEquals("activeIndex should be value set", activeIndex, tabset.getActiveIndex());
		tabset.setActiveTab(tab);
		Assert.assertEquals("activeIndex should be unchanged", activeIndex, tabset.getActiveIndex());

		setActiveContext(createUIContext());
		activeIndex = 0;
		tabset.setActiveIndex(activeIndex);
		Assert.assertEquals("activeIndex should be value set", activeIndex, tabset.getActiveIndex());
		tabset.setActiveTab(tab);
		Assert.assertEquals("activeIndex should be unchanged", activeIndex, tabset.getActiveIndex());
	}

	/**
	 * Test setTabVisible - finding by content.
	 */
	@Test
	public void testSetTabVisibleByContent() {
		WTabSet tabset = new WTabSet();
		WTabSet.TabMode mode = WTabSet.TabMode.SERVER;
		WComponent content1 = new DefaultWComponent();
		WDecoratedLabel label1 = new WDecoratedLabel("label1");
		tabset.addTab(content1, label1, mode);

		// test shared at false
		tabset.setTabVisible(content1, false);
		Assert.assertEquals("Tab should be invisible", false, tabset.isTabVisible(content1));

		// set shared back to true and test
		tabset.setTabVisible(content1, true);
		Assert.assertTrue("Tab should be visible", tabset.isTabVisible(content1));

		// set uic to false and test
		tabset.setLocked(true);
		setActiveContext(createUIContext());
		tabset.setTabVisible(content1, false);
		Assert.assertFalse("Tab should be invisible", tabset.isTabVisible(content1));

		resetContext();
		Assert.assertTrue("Default tab visibility should not have changed", tabset.isTabVisible(
				content1));
	}

	/**
	 * Test isActive - finding by tab.
	 */
	@Test
	public void testIsActive() {
		WTabSet tabset = new WTabSet();
		WTabSet.TabMode mode = WTabSet.TabMode.SERVER;

		WComponent content1 = new DefaultWComponent();
		WDecoratedLabel label1 = new WDecoratedLabel("label1");
		tabset.addTab(content1, label1, mode);

		int activeIndex = 0;
		tabset.setActiveIndex(activeIndex);

		boolean isActive = tabset.isActive(tabset.getTab(0));
		Assert.assertTrue("tab active", isActive);
	}

	/**
	 * Test getTabIndex - by content when its there and when it isnt.
	 */
	@Test
	public void testGetTabIndexByContent() {
		WTabSet tabset = new WTabSet();
		WTabSet.TabMode mode = WTabSet.TabMode.SERVER;

		WComponent content1 = new DefaultWComponent();
		WDecoratedLabel label1 = new WDecoratedLabel("label1");
		tabset.addTab(content1, label1, mode);

		WComponent content2 = new DefaultWComponent();
		WDecoratedLabel label2 = new WDecoratedLabel("label1");
		tabset.addTab(content2, label2, mode);

		Assert.assertEquals("should find content2 at index 1", 1, tabset.getTabIndex(content2));
		Assert.assertEquals("should find content2 at index 1", 1, tabset.getTabIndex(content2));

		// now look for content not in a tab in the tabset
		WComponent content3 = new DefaultWComponent();
		Assert.assertEquals("should return -1 for not found", -1, tabset.getTabIndex(content3));
	}

	/**
	 * Test setDisabled - in shared and diff uic models.
	 */
	@Test
	public void testSetDisabled() {
		assertAccessorsCorrect(new WTabSet(), "disabled", false, true, false);
	}

	/**
	 * Test setActionOnChange.
	 */
	@Test
	public void testSetActionOnChange() {
		assertAccessorsCorrect(new WTabSet(), "actionOnChange", null, new TestAction(),
				new TestAction());
	}

	@Test
	public void testSetShowHeadOnly() {
		final boolean headonly = true;
		final boolean headonly2 = false;
		WTabSet tabset = new WTabSet();

		// Default
		tabset.setShowHeadOnly(headonly);
		Assert.assertEquals("ShowHeadOnly should be set", tabset.isShowHeadOnly(), headonly);

		// With user context
		tabset.setLocked(true);
		setActiveContext(createUIContext());
		tabset.setShowHeadOnly(headonly2);
		Assert.assertEquals("User context ShowHeadOnly should be set", headonly2, tabset.
				isShowHeadOnly());

		resetContext();
		Assert.assertEquals("Default ShowHeadOnly should not have changed", headonly, tabset.
				isShowHeadOnly());
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WTabSet(), "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testSingleAccessors() {
		assertAccessorsCorrect(new WTabSet(), "single", false, true, false);
	}

}
