package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * WCancelButton_Test - Unit tests for {@link WCancelButton}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WCancelButton_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {
		String text = "WCancelButton_Test.testConstructors";
		char accessKey = 'A';

		WCancelButton cancelButton = new WCancelButton();
		Assert.assertEquals("Incorrect default text", "Cancel", cancelButton.getText());

		cancelButton = new WCancelButton(text);
		Assert.assertEquals("Incorrect text when using text constructor", text, cancelButton.
				getText());

		cancelButton = new WCancelButton(text, accessKey);
		Assert.assertEquals("Incorrect text when using text + accesskey constructor", text,
				cancelButton.getText());
		Assert.assertEquals("Incorrect access key text when using text + accesskey constructor",
				accessKey, cancelButton.getAccessKey());
	}

	@Test
	public void testUnsavedChanges() {
		WCancelButton cancelButton = new WCancelButton();
		Assert.assertFalse("Cancel button should not have unsaved changes by default", cancelButton.
				isUnsavedChanges());

		cancelButton.setLocked(true);
		setActiveContext(createUIContext());
		cancelButton.setUnsavedChanges(true);
		Assert.assertTrue("Failed to set unsaved changes to true", cancelButton.isUnsavedChanges());

		resetContext();
		Assert.assertFalse("Cancel button should not have unsaved changes by default", cancelButton.
				isUnsavedChanges());
	}
}
