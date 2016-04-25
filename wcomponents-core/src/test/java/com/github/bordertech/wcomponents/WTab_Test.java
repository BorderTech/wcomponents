package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WTabSet.TabMode;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WTab}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class WTab_Test extends AbstractWComponentTestCase {

	/**
	 * error message for constructor - null label - private in class being tested.
	 */
	private static final String ILLEGAL_LABEL_ERROR = "A label must be specified";

	@Test
	public void testConstructor() {
		WComponent component = new DefaultWComponent();
		WDecoratedLabel label = new WDecoratedLabel("label");
		WTab tab = new WTab(component, label, WTabSet.TabMode.SERVER);

		Assert.assertEquals("label should be added as first tab content", label, tab.getChildAt(0));
		Assert.assertEquals("label should be added as second tab content", component, tab.
				getChildAt(1));
	}

	@Test
	public void testConstructorNullLabel() {
		WComponent component = new DefaultWComponent();
		WDecoratedLabel label = null;

		try {
			WTab tab = new WTab(component, label, WTabSet.TabMode.SERVER);
			Assert.fail("should throw IllegalArgumentException - not get tab - " + tab.getId());
		} catch (Exception e) {
			Assert.assertEquals("should get message expected", ILLEGAL_LABEL_ERROR, e.getMessage());
		}
	}

	/**
	 * Test setContent - set non null content - to existing non null content.
	 */
	@Test
	public void testSetContentNonNullToNonNull() {
		WComponent component = new DefaultWComponent();
		WDecoratedLabel label = new WDecoratedLabel("label");
		WTab tab = new WTab(component, label, WTabSet.TabMode.SERVER);
		Assert.assertEquals("should be content as set in constructor", component, tab.getContent());

		WComponent component2 = new DefaultWComponent();
		tab.setContent(component2);
		Assert.assertEquals("should be content as set", component2, tab.getContent());
	}

	/**
	 * Test setContent - set non null content - to existing null content.
	 */
	@Test
	public void testSetContentNonNullToNull() {
		WComponent component = new DefaultWComponent();
		WDecoratedLabel label = new WDecoratedLabel("label");
		WTab tab = new WTab(component, label, WTabSet.TabMode.SERVER);
		Assert.assertEquals("should be content as set in constructor", component, tab.getContent());

		tab.setContent(null);

		WComponent component2 = component;
		tab.setContent(component2);
		Assert.assertEquals("should be content as set", component2, tab.getContent());
	}

	/**
	 * Test setContent - set null content - to existing non null content.
	 */
	@Test
	public void testSetContentNullToNonNull() {
		WComponent component = new DefaultWComponent();
		WDecoratedLabel label = new WDecoratedLabel("label");
		WTab tab = new WTab(component, label, WTabSet.TabMode.SERVER);
		Assert.assertEquals("should be content as set in constructor", component, tab.getContent());

		WComponent component2 = null;
		tab.setContent(component2);
		Assert.assertEquals("should be content as set", component2, tab.getContent());
	}

	/**
	 * Test setContent - set null content - to existing non null content.
	 */
	@Test
	public void testSetContentNullToNull() {
		WComponent component = new DefaultWComponent();
		WDecoratedLabel label = new WDecoratedLabel("label");
		WTab tab = new WTab(component, label, WTabSet.TabMode.SERVER);
		Assert.assertEquals("should be content as set in constructor", component, tab.getContent());

		tab.setContent(null);

		WComponent component2 = new DefaultWComponent();
		tab.setContent(component2);
		Assert.assertEquals("should be content as set", component2, tab.getContent());
	}

	/**
	 * Test setDisabled.
	 */
	@Test
	public void testSetDisabled() {
		WTab tab = new WTab(new DefaultWComponent(), "tab", WTabSet.TabMode.SERVER);
		Assert.assertFalse("Should not be disabled by default", tab.isDisabled());

		tab.setLocked(true);
		setActiveContext(createUIContext());
		tab.setDisabled(true);
		Assert.assertTrue("Should be disabled", tab.isDisabled());

		resetContext();
		Assert.assertFalse("Default disabled status should not have changed", tab.isDisabled());
	}

	/**
	 * Test preparePaintComponent - tabMode LAZY.
	 */
	@Test
	public void testPreparePaintComponentLazy() {
		// Tab - Lazy
		WComponent component = new WText("this is some text");
		WTab tab = new WTab(component, "label", WTabSet.TabMode.LAZY);

		WTabSet tabSet = new WTabSet();
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);
		tabSet.add(tab);
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);

		tab.setLocked(true);
		setActiveContext(createUIContext());

		// Not Open
		MockRequest request = new MockRequest();
		tab.preparePaintComponent(request);
		Assert.assertFalse("content should have been set to invisible", component.isVisible());

		// Open
		request = new MockRequest();
		tabSet.setActiveIndex(1);
		tab.preparePaintComponent(request);
		Assert.assertTrue("content should have been set to visible", component.isVisible());

		// TODO - add assert on result of AjaxHelper.registerComponent
	}

	/**
	 * Test preparePaintComponent - tabMode DYNAMIC.
	 */
	@Test
	public void testPreparePaintComponentDynamic() {
		// Tab - Dynamic
		WComponent component = new WText("this is some text");
		WTab tab = new WTab(component, "label", WTabSet.TabMode.DYNAMIC);

		WTabSet tabSet = new WTabSet();
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);
		tabSet.add(tab);
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);

		setActiveContext(createUIContext());

		// Not Open
		MockRequest request = new MockRequest();
		tab.preparePaintComponent(request);
		Assert.assertFalse("content should have been set to invisible", component.isVisible());

		// Open
		request = new MockRequest();
		tabSet.setActiveIndex(1);
		tab.preparePaintComponent(request);
		Assert.assertTrue("content should have been set to visible", component.isVisible());
		// TODO - add assert on result of AjaxHelper.registerComponent
	}

	/**
	 * Test preparePaintComponent - tabMode EAGER.
	 */
	@Test
	public void testPreparePaintComponentEager() {
		// Tab - Eager
		WComponent component = new WText("this is some text");
		WTab tab = new WTab(component, "label", WTabSet.TabMode.EAGER);

		WTabSet tabSet = new WTabSet();
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);
		tabSet.add(tab);
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);

		UIContext uic = createUIContext();
		setActiveContext(uic);

		// Not Open
		MockRequest request = new MockRequest();
		tab.preparePaintComponent(request);
		Assert.assertTrue("content should be visible", component.isVisible());

		// Open (Eager should still be visible)
		request = new MockRequest();
		tabSet.setActiveIndex(1);
		tab.preparePaintComponent(request);
		Assert.assertTrue("content should be visible", component.isVisible());

		// AJAX request but not for this WTab (Eager should be visible)
		request = new MockRequest();
		try {
			// Setup an AJAX operation that is not for this WTab
			AjaxHelper.setCurrentOperationDetails(new AjaxOperation("testid", "X"), null);
			tab.preparePaintComponent(request);
			Assert.assertTrue("content should be visible", component.isVisible());
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}

		// AJAX request for this WTab (Eager should be visible)
		request = new MockRequest();
		try {
			// Setup an AJAX operation that is not for this WTab
			AjaxHelper.setCurrentOperationDetails(new AjaxOperation(tab.getId(), "X"), null);
			tab.preparePaintComponent(request);
			Assert.assertTrue("content should have been set to visible", component.isVisible());
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}

	}

	/**
	 * Test preparePaintComponent - tabMode SERVER.
	 */
	@Test
	public void testPreparePaintComponentServer() {
		// Tab - Server
		WComponent component = new WText("this is some text");
		WTab tab = new WTab(component, "label", WTabSet.TabMode.SERVER);

		WTabSet tabSet = new WTabSet();
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);
		tabSet.add(tab);
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);

		setActiveContext(createUIContext());

		// Not Open
		MockRequest request = new MockRequest();
		tab.preparePaintComponent(request);
		Assert.assertFalse("content should have been set to invisible", component.isVisible());

		// Open
		request = new MockRequest();
		tabSet.setActiveIndex(1);
		tab.preparePaintComponent(request);
		Assert.assertTrue("content should have been set to visible", component.isVisible());
	}

	/**
	 * Test preparePaintComponent - tabMode CLIENT.
	 */
	@Test
	public void testPreparePaintComponentClient() {
		// Tab - Client
		WComponent component = new WText("this is some text");
		WTab tab = new WTab(component, "label", WTabSet.TabMode.CLIENT);

		WTabSet tabSet = new WTabSet();
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);
		tabSet.add(tab);
		tabSet.addTab(new WText(), "test", WTabSet.TAB_MODE_CLIENT);

		setActiveContext(createUIContext());

		// Not Open
		MockRequest request = new MockRequest();
		tab.preparePaintComponent(request);
		Assert.assertTrue("content should have been set to invisible", component.isVisible());

		// Open
		request = new MockRequest();
		tabSet.setActiveIndex(1);
		tab.preparePaintComponent(request);
		Assert.assertTrue("content should have been set to visible", component.isVisible());
	}

	@Test
	public void testGetTabLabel() {
		final String labelText = "this is a label";
		WContainer component = new WContainer();
		component.add(new WText("this is some text"));
		WDecoratedLabel label = new WDecoratedLabel(labelText);
		WTab tab = new WTab(component, label, WTabSet.TabMode.CLIENT);

		Assert.assertEquals("should get decorated label set in constructor", label, tab.
				getTabLabel());
	}

	@Test
	public void testSetText() {
		final String labelText = "this is a label";
		final String labelTextAlt = "this is also a label but different";

		WContainer component = new WContainer();
		component.add(new WText("this is some text"));
		WDecoratedLabel label = new WDecoratedLabel(labelText);
		WTab tab = new WTab(component, label, WTabSet.TabMode.CLIENT);

		setActiveContext(createUIContext());
		tab.setText(labelTextAlt);

		WDecoratedLabel labelResult = tab.getTabLabel();
		Assert.assertEquals("Incorrect text", labelTextAlt, labelResult.getText());

		resetContext();
		Assert.assertEquals("Default text should not have changed", label, labelResult);
	}

	@Test
	public void testModeAccessors() {
		WTab tab = new WTab(new WContainer(), "test", WTabSet.TabMode.CLIENT);
		assertAccessorsCorrect(tab, "mode", TabMode.CLIENT, TabMode.DYNAMIC, TabMode.SERVER);
	}

	@Test
	public void testAccessKeyAccessors() {
		WTab tab = new WTab(new WContainer(), "test", WTabSet.TabMode.CLIENT);
		assertAccessorsCorrect(tab, "accessKey", '\0', 'A', 'B');
	}

	/**
	 * Test tab in tab group.
	 */
	@Test
	public void testTabInTabGroup() {

		WTabSet tabSet = new WTabSet();
		WTabGroup group = new WTabGroup("Group");
		WTab tab = new WTab(new WText("test in group"), "label", WTabSet.TabMode.DYNAMIC);

		tabSet.add(group);
		group.add(tab);

		setActiveContext(createUIContext());

		// AJAX request but not for this WTab (Eager should be visible)
		MockRequest request = new MockRequest();
		try {
			// Setup an AJAX operation that is not for this WTab
			AjaxHelper.setCurrentOperationDetails(new AjaxOperation(tab.getId(), "X"), null);
			tab.handleRequest(request);
			tab.preparePaintComponent(request);
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}

	}

}
