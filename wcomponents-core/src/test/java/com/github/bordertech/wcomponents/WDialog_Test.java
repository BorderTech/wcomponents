package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.Serializable;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

/**
 * Unit tests for {@link WDialog}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class WDialog_Test extends AbstractWComponentTestCase {

	@After
	public void resetAjaxOperation() {
		AjaxHelper.clearCurrentOperationDetails();
	}

	@Test
	public void testConstructor() {
		WDialog dialog = new WDialog();
		Assert.assertEquals("Default state should be INACTIVE", WDialog.INACTIVE_STATE, dialog.getState());
		Assert.assertEquals("Default mode should be MODELESS", WDialog.MODELESS, dialog.getMode());
		Assert.assertNull("Content is null by default", dialog.getContent());
		Assert.assertNull("Trigger is null by default", dialog.getTrigger());
		Assert.assertFalse("Has trigger should be false by defualt", dialog.hasLegacyTriggerButton());
	}

	@Test
	public void testConstructor1() {
		WText wText = new WText("sample text");
		WDialog dialog = new WDialog(wText);
		Assert.assertEquals("Content should be set", wText, dialog.getContent());
	}

	@Test
	public void testConstructor2() {
		WText wText = new WText("sample text");
		WButton trigger = new WButton("trigger");
		WDialog dialog = new WDialog(wText, trigger);
		Assert.assertEquals("Content should be set", wText, dialog.getContent());
		Assert.assertEquals("Trigger should be set", trigger, dialog.getTrigger());
		Assert.assertTrue("Has trigger should be true", dialog.hasLegacyTriggerButton());
	}

	@Test
	public void testModeAccessors() {
		assertAccessorsCorrect(new WDialog(), "mode", WDialog.MODELESS, WDialog.MODAL, WDialog.MODELESS);
	}

	@Test
	public void testContentAccessors() {
		assertAccessorsCorrect(new WDialog(), "content", null, new WText(), new WText());
	}

	@Test
	public void testTitleAccessors() {
		assertAccessorsCorrect(new WDialog(), "title", null, "A", "B", new Serializable[]{});
	}

	@Test
	public void testTriggerAccessors() {
		assertAccessorsCorrect(new WDialog(), "trigger", null, new WButton(), new WMenuItem("A"));
	}
	
	@Test
	public void testHeightAccessors() {
		assertAccessorsCorrect(new WDialog(), "height", 0, 1, 2);
	}

	@Test
	public void testWidthAccessors() {
		assertAccessorsCorrect(new WDialog(), "width", 0, 1, 2);
	}

	@Test
	public void testTriggerOpenActionAccessors() {
		assertAccessorsCorrect(new WDialog(), "triggerOpenAction", null, new TestAction(), new TestAction());
	}

	@Test
	public void testStateDisplay() {
		WDialog dialog = new WDialog();
		// Default
		Assert.assertEquals("Default state should be INACTIVE", WDialog.INACTIVE_STATE, dialog.getState());
		// Display
		dialog.display();
		Assert.assertEquals("State should be MANUAL_OPEN_STATE after calling display", WDialog.MANUAL_OPEN_STATE, dialog.getState());
		// Active after prepare paint
		setActiveContext(createUIContext());
		dialog.preparePaint(new MockRequest());
		Assert.assertEquals("State should be ACTIVE after prepare paint for display", WDialog.ACTIVE_STATE, dialog.getState());
		// Check stays active while AJAX Targetted
		setActiveAjaxOperation(dialog);
		dialog.handleRequest(new MockRequest());
		Assert.assertEquals("State should still be ACTIVE after handle request and ajax targetted", WDialog.ACTIVE_STATE, dialog.getState());
		dialog.handleRequest(new MockRequest());
		Assert.assertEquals("State should still be ACTIVE after prepare paint and ajax targetted", WDialog.ACTIVE_STATE, dialog.getState());
		// Not AJAX Targetted - Handle Request
		resetAjaxOperation();
		dialog.handleRequest(new MockRequest());
		Assert.assertEquals("State should be INACTIVE after handle request and NOT ajax targetted", WDialog.INACTIVE_STATE, dialog.getState());
		// Put back to active so test in prepare paint (VIA AJAX Targetted)
		setActiveAjaxOperation(dialog);
		dialog.handleRequest(new MockRequest());
		Assert.assertEquals("State should be ACTIVE for being ajax targetted in handle request", WDialog.ACTIVE_STATE, dialog.getState());
		// Not AJAX Targetted - Prepare Paint
		resetAjaxOperation();
		dialog.preparePaint(new MockRequest());
		Assert.assertEquals("State should be INACTIVE after prepare paint and NOT ajax targetted", WDialog.INACTIVE_STATE, dialog.getState());
	}

	@Test
	public void testIsAjaxTargeted() {
		WApplication app = new WApplication();
		WPanel appPanel = new WPanel();
		WDialog dialog = new WDialog();
		WPanel dialogPanel = new WPanel();

		app.add(appPanel);
		app.add(dialog);
		dialog.setContent(dialogPanel);
		app.setLocked(true);

		UIContext uic = createUIContext();
		uic.setUI(app);
		setActiveContext(uic);

		dialog.display();
		app.serviceRequest(new MockRequest());

		Assert.assertFalse("Should not be targeted if there is no AJAX operation", dialog.
				isAjaxTargeted());

		setActiveAjaxOperation(appPanel);
		Assert.assertFalse("Should not be targeted if targeting another component", dialog.
				isAjaxTargeted());

		setActiveAjaxOperation(dialog);
		Assert.assertTrue("Should be targeted when targeted directly", dialog.isAjaxTargeted());

		setActiveAjaxOperation(dialogPanel);
		Assert.assertTrue("Should be targeted when a child component is targeted", dialog.
				isAjaxTargeted());
	}

	/**
	 * A convenience method to create an AJAX operation.
	 *
	 * @param target the component to target
	 */
	private void setActiveAjaxOperation(final WComponent target) {
		AjaxOperation operation = new AjaxOperation(target.getId(), target.getId());
		AjaxHelper.setCurrentOperationDetails(operation, new ComponentWithContext(target,
				UIContextHolder.getCurrent()));
	}
}
