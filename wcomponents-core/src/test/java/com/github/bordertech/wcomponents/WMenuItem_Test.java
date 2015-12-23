package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.Serializable;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WMenuItem_Test - Unit tests for {@link WMenuItem}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMenuItem_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		WDecoratedLabel lbl = new WDecoratedLabel("test");
		WMenuItem item = new WMenuItem(lbl);
		Assert.assertEquals("Incorrect label set by constructor", lbl, item.getDecoratedLabel());
	}

	@Test
	public void testConstructor2() {
		WDecoratedLabel lbl = new WDecoratedLabel("test");
		String url = "A";
		WMenuItem item = new WMenuItem(lbl, url);
		Assert.assertEquals("Incorrect label set by constructor", lbl, item.getDecoratedLabel());
		Assert.assertEquals("Incorrect url set by constructor", url, item.getUrl());
	}

	@Test
	public void testConstructor3() {
		WDecoratedLabel lbl = new WDecoratedLabel("test");
		Action action = new TestAction();
		WMenuItem item = new WMenuItem(lbl, action);
		Assert.assertEquals("Incorrect label set by constructor", lbl, item.getDecoratedLabel());
		Assert.assertEquals("Incorrect action set by constructor", action, item.getAction());
	}

	@Test
	public void testConstructor4() {
		String text = "A";
		String url = "B";
		WMenuItem item = new WMenuItem(text, url);
		Assert.assertEquals("Incorrect text set by constructor", text, item.getText());
		Assert.assertEquals("Incorrect url set by constructor", url, item.getUrl());
	}

	@Test
	public void testConstructor5() {
		String text = "A";
		Action action = new TestAction();
		WMenuItem item = new WMenuItem(text, action);
		Assert.assertEquals("Incorrect text set by constructor", text, item.getText());
		Assert.assertEquals("Incorrect action set by constructor", action, item.getAction());
	}

	@Test
	public void testConstructor6() {
		String text = "A";
		WMenuItem item = new WMenuItem(text);
		Assert.assertEquals("Incorrect text set by constructor", text, item.getText());
	}

	@Test
	public void testConstructor7() {
		String text = "A";
		char key = 'K';
		WMenuItem item = new WMenuItem(text, key);
		Assert.assertEquals("Incorrect text set by constructor", text, item.getText());
		Assert.assertEquals("Incorrect accesskey set by constructor", key, item.getAccessKey());
	}

	@Test
	public void testConstructor8() {
		String text = "A";
		char key = 'K';
		Action action = new TestAction();
		WMenuItem item = new WMenuItem(text, key, action);
		Assert.assertEquals("Incorrect text set by constructor", text, item.getText());
		Assert.assertEquals("Incorrect accesskey set by constructor", key, item.getAccessKey());
		Assert.assertEquals("Incorrect action set by constructor", action, item.getAction());
	}

	@Test
	public void testActionAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "action", null, new TestAction(), new TestAction());
	}

	@Test
	public void testUrlAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "url", null, "A", "B");
	}

	@Test
	public void testSubmitFlag() {
		WMenuItem item = new WMenuItem("");
		Assert.assertFalse("Submit flag should default to false when no action provided", item.isSubmit());

		// Set url
		item.setUrl("A");
		Assert.assertFalse("Submit flag should be false when a URL set", item.isSubmit());

		// Set action
		item.setAction(new TestAction());
		Assert.assertTrue("Submit flag should be true when an action is set", item.isSubmit());
		Assert.assertNull("Url should be null if Action set", item.getUrl());

		// Set URL
		item.setUrl("A");
		Assert.assertFalse("Submit flag should be false if URL set or action set null", item.isSubmit());
		Assert.assertNull("Action should be null if url set", item.getAction());
	}

	@Test
	public void testTextAccessors() {
		assertAccessorsCorrect(new WMenuItem("test"), "text", "test", "A", "B");
	}

	@Test
	public void testTargetWindowAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "targetWindow", null, "A", "B");
	}

	@Test
	public void testSelectableAccessors() {
		WMenuItem item = new WMenuItem("");
		Assert.assertNull("Selectable should be null by default", item.isSelectable());

		item.setSelectable(Boolean.FALSE);
		item.setLocked(true);
		setActiveContext(createUIContext());
		item.setSelectable(Boolean.TRUE);

		Assert.assertTrue("Should be selectable in session", item.isSelectable());

		resetContext();
		Assert.assertFalse("Default should not be selectable", item.isSelectable());
	}

	@Test
	public void testIsSelected() {
		WMenu menu = new WMenu();
		WMenuItem item = new WMenuItem("");
		menu.add(item);

		// Not selected
		Assert.assertFalse("Menu should not be selected by default", item.isSelected());

		// Set as selected
		menu.setSelectedItem(item);
		Assert.assertTrue("Menu should be selected by default", item.isSelected());
	}

	@Test
	public void testSelectabilityAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "selectability", null, true, false);
	}

	@Test
	public void testDisabledAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "disabled", false, true, false);
	}

	@Test
	public void testAccessKeyAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "accessKey", '\0', 'A', 'B');
	}

	@Test
	public void testGetAccessKeyAsString() {
		WMenuItem item = new WMenuItem("");
		Assert.assertNull("Incorrect acesskey as string", item.getAccessKeyAsString());

		item.setAccessKey('C');
		Assert.assertEquals("Incorrect acesskey as string", "C", item.getAccessKeyAsString());

		item.setAccessKey('\0');
		Assert.assertNull("Incorrect acesskey as string", item.getAccessKeyAsString());
	}

	@Test
	public void testActionCommandAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "actionCommand", null, "A", "B");
	}

	@Test
	public void testActionObjectAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "actionObject", null, "A", "B");
	}

	@Test
	public void testMessageAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "message", null, "A", "B", new Serializable[]{});
	}

	@Test
	public void testCancelAccessors() {
		assertAccessorsCorrect(new WMenuItem(""), "cancel", false, true, false);
	}

	@Test
	public void testHandleRequest() {
		TestAction action = new TestAction();
		WMenu menu = new WMenu();
		WMenuItem item = new WMenuItem("", action);
		menu.add(item);

		menu.setLocked(true);

		// Menu not in request
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		menu.serviceRequest(request);
		Assert.assertFalse("Action should not have been called when item was not selected", action.
				wasTriggered());

		// Menu in request, but item not selected
		request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		menu.serviceRequest(request);
		Assert.assertFalse("Action should not have been called when item was not selected", action.
				wasTriggered());

		// Menu in request and item selected
		request = new MockRequest();
		request.setParameter(menu.getId() + "-h", "x");
		request.setParameter(item.getId(), "x");
		menu.serviceRequest(request);
		Assert.assertTrue("Action should have been called when item is selected", action.
				wasTriggered());
	}

	@Test
	public void testHandleRequestWhenDisabled() {
		TestAction action = new TestAction();
		WMenuItem item = new WMenuItem("", action);

		item.setLocked(true);
		setActiveContext(createUIContext());
		item.setDisabled(true);
		MockRequest request = new MockRequest();

		item.serviceRequest(request);
		Assert.assertFalse("Action should not have been called when item was not selected", action.
				wasTriggered());

		request.setParameter(item.getId(), "x");
		item.serviceRequest(request);
		Assert.assertFalse("Action should not have been called on a disabled item", action.
				wasTriggered());
	}
}
