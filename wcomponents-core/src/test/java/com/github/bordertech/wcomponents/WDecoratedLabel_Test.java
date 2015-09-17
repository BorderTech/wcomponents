package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit test cases for {@link WDecoratedLabel}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WDecoratedLabel_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		// Constructor 1
		WDecoratedLabel label = new WDecoratedLabel();
		Assert.assertNull("Constructor 1 - Label head should be null.", label.getHead());
		Assert.assertTrue("Constructor 1 - Label body should be by default a WText.",
				label.getBody() instanceof WText);
		Assert.assertNull("Constructor 1 - Label tail should be null.", label.getTail());
	}

	@Test
	public void testConstructor2() {
		String labelText = "Test";
		// Constructor 2
		WDecoratedLabel label = new WDecoratedLabel(labelText);
		Assert.assertNull("Constructor 2 - Label head should be null.", label.getHead());
		Assert.assertTrue("Constructor 2 - Label body should be by default a WText.",
				label.getBody() instanceof WText);
		Assert.assertEquals("Constructor 2 - Label body has an invalid value.", labelText, label.
				getText());
		Assert.assertNull("Constructor 2 - Label tail should be null.", label.getTail());
	}

	@Test
	public void testConstructor3() {
		WComponent body1 = new DefaultWComponent();
		// Constructor 3
		WDecoratedLabel label = new WDecoratedLabel(body1);
		Assert.assertNull("Constructor 3 - Label head should be null.", label.getHead());
		Assert.assertEquals("Constructor 3 - Incorrect label body.", body1, label.getBody());
		Assert.assertNull("Constructor 3 - Label tail should be null.", label.getTail());
	}

	@Test
	public void testConstructor4() {
		WComponent head1 = new DefaultWComponent();
		WComponent body1 = new DefaultWComponent();
		WComponent tail1 = new DefaultWComponent();
		// Constructor 4
		WDecoratedLabel label = new WDecoratedLabel(head1, body1, tail1);
		Assert.assertEquals("Constructor 4 - Incorrect label head.", head1, label.getHead());
		Assert.assertEquals("Constructor 4 - Incorrect label body.", body1, label.getBody());
		Assert.assertEquals("Constructor 4 - Incorrect label tail.", tail1, label.getTail());
	}

	@Test
	public void testConstructor5() {
		WComponent head1 = new DefaultWComponent();
		WComponent body1 = new DefaultWComponent();
		WComponent tail1 = new DefaultWComponent();
		// Constructor 5
		WDecoratedLabel label = new WDecoratedLabel(head1, body1, tail1);
		Assert.assertEquals("Constructor 5 - Incorrect label head.", head1, label.getHead());
		Assert.assertEquals("Constructor 5 - Incorrect label body.", body1, label.getBody());
		Assert.assertEquals("Constructor 5 - Incorrect label tail.", tail1, label.getTail());
	}

	@Test
	public void testAccessors() {
		WComponent head1 = new DefaultWComponent();
		WComponent head2 = new DefaultWComponent();
		WComponent body1 = new DefaultWComponent();
		WComponent body2 = new DefaultWComponent();
		WComponent tail1 = new DefaultWComponent();
		WComponent tail2 = new DefaultWComponent();

		WDecoratedLabel label = new WDecoratedLabel();

		// Head
		label.setHead(head1);
		Assert.assertEquals("Incorrect label head.", head1, label.getHead());

		label.setLocked(true);
		setActiveContext(createUIContext());
		label.setHead(head2);
		Assert.assertEquals("Incorrect session label head.", head2, label.getHead());

		resetContext();
		Assert.assertEquals("Incorrect shared label head.", head1, label.getHead());

		// Body
		label.setBody(body1);
		Assert.assertEquals("Incorrect label body.", body1, label.getBody());
		try {
			label.setBody(null);
			Assert.fail("Should not be able to set the body to null.");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Label Body should not have changed.", body1, label.getBody());
		}

		setActiveContext(createUIContext());
		label.setBody(body2);
		Assert.assertEquals("Incorrect session label body.", body2, label.getBody());

		resetContext();
		Assert.assertEquals("Incorrect shared label body.", body1, label.getBody());

		// Tail
		label.setTail(tail1);
		Assert.assertEquals("Incorrect label tail.", tail1, label.getTail());

		setActiveContext(createUIContext());
		label.setTail(tail2);
		Assert.assertEquals("Incorrect session label tail.", tail2, label.getTail());

		resetContext();
		Assert.assertEquals("Incorrect shared label tail.", tail1, label.getTail());
	}

	@Test
	public void testLabelText() {
		WDecoratedLabel label = new WDecoratedLabel();

		// WText
		WText wText = new WText();
		wText.setText("WTEXT");
		label.setBody(wText);

		label.setLocked(true);
		UIContext uic = createUIContext();
		setActiveContext(uic);
		wText.setText("WTEXT - UIC");
		Assert.assertEquals("Incorrect text value for WText Component with uic", wText.getText(),
				label.getText());

		resetContext();
		Assert.assertEquals("Incorrect text value for WText Component", wText.getText(), label.
				getText());

		// Change via Label
		label.setText("WTEXT2");
		Assert.assertEquals("Incorrect text value set for WText Component", wText.getText(), label.
				getText());

		setActiveContext(uic);
		Assert.
				assertEquals("Incorrect text value set for WText Component with uic", wText.
						getText(), label.getText());
		label.setText("WTEXT2 - UIC");
		Assert.
				assertEquals("Incorrect text value set for WText Component with uic", wText.
						getText(), label.getText());

		resetContext();
		Assert.assertEquals("Incorrect text value set for WText Component", wText.getText(), label.
				getText());

		// WLabel
		WLabel wLabel = new WLabel();
		wLabel.setText("WLABEL");
		label.setBody(wLabel);

		setActiveContext(createUIContext());
		wLabel.setText("WLABEL - UIC");
		Assert.assertEquals("Incorrect text value for WLabel Component with uic", wLabel.getText(),
				label.getText());

		resetContext();
		Assert.assertEquals("Incorrect text value for WLabel Component", wLabel.getText(), label.
				getText());

		// Change via Label
		label.setText("WLABEL2");
		Assert.assertEquals("Incorrect text value set for WLabel Component", wLabel.getText(),
				label.getText());

		setActiveContext(uic);
		Assert.assertEquals("Incorrect text value set for WLabel Component with uic", wLabel.
				getText(), label.getText());
		label.setText("WLABEL2 - UIC");
		Assert.assertEquals("Incorrect text value set for WLabel Component with uic", wLabel.
				getText(), label.getText());

		resetContext();
		Assert.assertEquals("Incorrect text value set for WLabel Component", wLabel.getText(),
				label.getText());

		// WButton
		WButton wButton = new WButton();
		wButton.setText("WBUTTON");
		label.setBody(wButton);

		setActiveContext(uic);
		wButton.setText("WBUTTON - UIC");
		Assert.
				assertEquals("Incorrect text value for WButton Component with uic", wButton.
						getText(), label.getText());

		resetContext();
		Assert.assertEquals("Incorrect text value for WButton Component", wButton.getText(), label.
				getText());

		// Change via Label
		label.setText("WBUTTON2");
		Assert.assertEquals("Incorrect text value set for WButton Component", wButton.getText(),
				label.getText());

		setActiveContext(uic);
		Assert.assertEquals("Incorrect text value set for WButton Component with uic", wButton.
				getText(), label.getText());
		label.setText("WBUTTON2 - UIC");
		Assert.assertEquals("Incorrect text value set for WButton Component with uic", wButton.
				getText(), label.getText());

		resetContext();
		Assert.assertEquals("Incorrect text value set for WButton Component", wButton.getText(),
				label.getText());

		// WLink
		WLink wLink = new WLink();
		wLink.setText("WLINK");
		label.setBody(wLink);

		setActiveContext(uic);
		wLink.setText("WLINK - UIC");
		Assert.assertEquals("Incorrect text value for WLink Component with uic", wLink.getText(),
				label.getText());

		resetContext();
		Assert.assertEquals("Incorrect text value for WLink Component", wLink.getText(), label.
				getText());

		// Change via Label
		label.setText("WLINK2");
		Assert.assertEquals("Incorrect text value set for WLink Component", wLink.getText(), label.
				getText());

		setActiveContext(uic);
		Assert.assertEquals("Incorrect text value set for WLink Component with ", wLink.getText(),
				label.getText());
		label.setText("WLINK2 - ");
		Assert.assertEquals("Incorrect text value set for WLink Component with ", wLink.getText(),
				label.getText());

		resetContext();
		Assert.assertEquals("Incorrect text value set for WLink Component", wLink.getText(), label.
				getText());
	}

}
