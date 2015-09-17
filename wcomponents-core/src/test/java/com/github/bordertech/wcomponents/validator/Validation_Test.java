package com.github.bordertech.wcomponents.validator;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test wcomponent validation features.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class Validation_Test extends AbstractWComponentTestCase {

	@Test
	public void testMandatory() {
		List<Diagnostic> diags = new ArrayList<>();

		WTextField textField = new WTextField();
		Assert.assertFalse("Text field should not be mandatory by default", textField.isMandatory());

		// No validation logic yet, but that should still be ok.
		textField.validate(diags);
		Assert.assertEquals("Diags should be empty when there are no validators", 0, diags.size());

		// Add a mandatory validation
		textField.setMandatory(true);
		Assert.assertTrue("Text field should be mandatory", textField.isMandatory());
		textField.validate(diags);
		Assert.assertEquals("Diags should contain mandatory validation error", 1, diags.size());

		// Add some text to the text field
		textField.setText("Blah");
		diags.clear();
		textField.validate(diags);
		Assert.assertEquals("Diags should be empty when mandatory field is filled in", 0, diags.
				size());
	}

	@Test
	public void testMandatoryMessage() {
		List<Diagnostic> diags = new ArrayList<>();
		Diagnostic diag;

		// Test for standard message
		WTextField textField = new WTextField();
		textField.setMandatory(true);
		textField.validate(diags);
		Assert.assertEquals("Diags should contain mandatory validation error", 1, diags.size());

		diag = diags.get(0);
		Assert.assertTrue("Default validation error text should start with \"Please enter\"",
				diag.getDescription().startsWith("Please enter"));

		// Test mandatory message customisation
		textField.setMandatory(true, "Must have {0}.");
		diags.clear();
		textField.validate(diags);
		Assert.assertEquals("Diags should contain mandatory validation error", 1, diags.size());

		diag = diags.get(0);
		Assert.assertTrue("Incorrect custom error text", diag.getDescription().startsWith(
				"Must have"));
	}

	@Test
	public void testMandatorySpecificToUser() {
		UIContext uic1 = new UIContextImpl();
		UIContext uic2 = new UIContextImpl();
		List<Diagnostic> diags = new ArrayList<>();

		// uic 1 has mandatory check
		// uic 2 does not.
		WTextField textField = new WTextField();
		textField.setLocked(true);

		setActiveContext(uic1);
		textField.setMandatory(true);
		textField.validate(diags);
		Assert.assertEquals("UIC 1 should have a validation error", 1, diags.size());

		diags.clear();
		setActiveContext(uic2);
		textField.validate(diags);
		Assert.assertEquals("UIC 2 should not have a validation error", 0, diags.size());
	}
}
