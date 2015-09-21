package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WDialog.DialogModel;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
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

	/**
	 * test title.
	 */
	public static final String TEST_TITLE = "This is the title";

	/**
	 * test title alt.
	 */
	public static final String TEST_TITLE_ALT = "This is the alternative title";

	/**
	 * common test result.
	 */
	public static final String TEST_RESULT = "should return instance of DialogModel";

	@After
	public void resetAjaxOperation() {
		AjaxHelper.clearCurrentOperationDetails();
	}

	@Test
	public void testConstructor() {
		WDialog dialog = new WDialog();

		Assert.assertEquals("default mode should be MODELESS", WDialog.MODELESS, dialog.getMode());
	}

	@Test
	public void testConstructorContent() {
		WText wText = new WText("sample text");
		WDialog dialog = new WDialog(wText);

		Assert.assertEquals("content should be set", wText, dialog.getContent());
	}

	@Test
	public void testSetMode() {
		WDialog dialog = new WDialog();
		dialog.setMode(WDialog.MODAL);

		Assert.assertEquals("mode should be set to MODAL", WDialog.MODAL, dialog.getMode());
	}

	@Test
	public void testSetTitle() {
		WDialog dialog = new WDialog();
		dialog.setTitle(TEST_TITLE);

		dialog.setLocked(true);
		setActiveContext(createUIContext());
		dialog.setTitle(TEST_TITLE_ALT);
		Assert.assertEquals("uic1 title should be as set", TEST_TITLE_ALT, dialog.getTitle());

		resetContext();
		Assert.
				assertEquals("shared title should be as initially set", TEST_TITLE, dialog.
						getTitle());
	}

	@Test
	public void testNewComponentModel() {
		WDialog dialog = new WDialog();
		Object result = dialog.newComponentModel();

		Assert.assertTrue(TEST_RESULT, result instanceof DialogModel);
	}

	@Test
	public void testGetComponentModel() {
		WDialog dialog = new WDialog();
		Object result = dialog.getComponentModel();

		Assert.assertTrue(TEST_RESULT, result instanceof DialogModel);
	}

	@Test
	public void testGetOrCreateComponentModel() {
		WDialog dialog = new WDialog();
		setActiveContext(createUIContext());
		Object result = dialog.getOrCreateComponentModel();

		Assert.assertTrue(TEST_RESULT, result instanceof DialogModel);
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
