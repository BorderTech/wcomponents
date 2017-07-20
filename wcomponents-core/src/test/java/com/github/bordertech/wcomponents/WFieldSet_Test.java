package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WFieldSet.FrameType;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WFieldSet}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WFieldSet_Test extends AbstractWComponentTestCase {

	private static final String TITLE_TEXT = "WFieldSet_Test.titleText";

	@Test
	public void testConstructor1() {
		WFieldSet fieldSet = new WFieldSet(TITLE_TEXT);
		Assert.assertEquals("Title incorrect", TITLE_TEXT, fieldSet.getTitle().getText());
	}

	@Test
	public void testConstructor2() {
		WComponent titleComponent = new WLabel("dummy");
		WFieldSet fieldSet = new WFieldSet(titleComponent);
		Assert.assertSame("Incorrect title", titleComponent, fieldSet.getTitle().getBody());
	}

	@Test
	public void testSetTitleWithString() {
		WFieldSet fieldSet = new WFieldSet("");
		fieldSet.setTitle(TITLE_TEXT);
		Assert.assertEquals("Title incorrect", TITLE_TEXT, fieldSet.getTitle().getText());
	}

	@Test
	public void testSetTitleWithComponent() {
		WComponent titleComponent = new WLabel("dummy");
		WFieldSet fieldSet = new WFieldSet("");
		fieldSet.setTitle(titleComponent);
		Assert.assertSame("Incorrect title", titleComponent, fieldSet.getTitle().getBody());
	}

	@Test
	public void testFrameTypeAccessors() {
		assertAccessorsCorrect(new WFieldSet(""), "frameType", FrameType.NORMAL, FrameType.NO_TEXT,
				FrameType.NO_BORDER);
	}

	@Test
	public void testMandatoryAccessors() {
		assertAccessorsCorrect(new WFieldSet(""), "mandatory", false, true, false);
	}

	@Test
	public void testMandatoryWithMessage() {
		WFieldSet fieldSet = new WFieldSet("");
		fieldSet.setMandatory(true, "test message");
		Diagnostic msg = fieldSet.createMandatoryDiagnostic();
		Assert.assertEquals("Message incorrect", "test message", msg.getDescription());
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WFieldSet(""), "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testValidationNotMandatory() {
		WFieldSet fieldSet = new WFieldSet("");
		WTextField textField = new WTextField();
		fieldSet.add(textField);

		fieldSet.setLocked(true);
		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		fieldSet.validate(diags);
		Assert.assertTrue("Should have no validation errors", diags.isEmpty());
	}

	@Test
	public void testValidationMandatory() {
		WFieldSet fieldSet = new WFieldSet("");
		WTextField textField = new WTextField();
		fieldSet.add(textField);
		fieldSet.setMandatory(true);

		fieldSet.setLocked(true);
		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		fieldSet.validate(diags);
		Assert.assertFalse("Should have validation errors", diags.isEmpty());

		textField.setText("value");
		diags = new ArrayList<>();
		fieldSet.validate(diags);
		Assert.assertTrue("Should have no validation errors with value", diags.isEmpty());
	}

	@Test
	public void testValidationMandatoryInContainer() {
		WFieldSet fieldSet = new WFieldSet("");
		WContainer container = new WContainer();
		WTextField textField = new WTextField();

		fieldSet.add(container);
		container.add(textField);

		fieldSet.setMandatory(true);

		fieldSet.setLocked(true);
		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		fieldSet.validate(diags);
		Assert.assertFalse("Should have validation errors", diags.isEmpty());

		textField.setText("value");
		diags = new ArrayList<>();
		fieldSet.validate(diags);
		Assert.assertTrue("Should have no validation errors with value", diags.isEmpty());
	}

	@Test
	public void testValidationMandatoryInRepeater() {
		WFieldSet fieldSet = new WFieldSet("");

		WContainer container = new WContainer();
		WTextField textField = new WTextField();
		container.add(textField);

		WRepeater repeater = new WRepeater(container);
		repeater.setBeanList(Arrays.asList("A", "B", "C"));

		fieldSet.add(repeater);

		fieldSet.setMandatory(true);

		fieldSet.setLocked(true);
		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		fieldSet.validate(diags);
		Assert.assertFalse("Should have validation errors", diags.isEmpty());

		// Set a value
		UIContext suic = repeater.getRowContexts().get(0);
		UIContextHolder.pushContext(suic);
		try {
			textField.setText("value");
		} finally {
			UIContextHolder.popContext();
		}

		diags = new ArrayList<>();
		fieldSet.validate(diags);
		Assert.assertTrue("Should have no validation errors with value on row", diags.isEmpty());
	}

}
