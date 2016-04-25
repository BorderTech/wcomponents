package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WPanel.PanelMode;
import com.github.bordertech.wcomponents.layout.BorderLayout;
import java.io.Serializable;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WPanel_Test - Unit tests for {@link WPanel}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WPanel_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {
		// Constructor - 1
		WPanel panel = new WPanel();
		Assert.assertEquals("Incorrect default type returned", WPanel.Type.PLAIN, panel.getType());
		// Constructor - 2
		panel = new WPanel(WPanel.Type.CHROME);
		Assert.assertEquals("Incorrect type returned", WPanel.Type.CHROME, panel.getType());
	}

	/**
	 * test addChild - and layout constraint.
	 */
	@Test
	public void testAddChildLayoutConstraint() {
		UIContext uic1 = createUIContext();
		UIContext uic2 = createUIContext();

		WPanel panel = new WPanel();
		WText text1 = new WText("XYZ");
		WText text2 = new WText("ABC");

		// add to shared model - both uic1 and uic2
		panel.add(text1, BorderLayout.EAST);
		panel.setLocked(true);

		// add to uic2 only
		setActiveContext(uic2);
		panel.add(text2, BorderLayout.NORTH);

		setActiveContext(uic1);
		Assert.assertEquals("uic1 should contain text1 with its constraint", BorderLayout.EAST,
				panel.getLayoutConstraints(text1));
		Assert.assertNull("uic1 should not contain text2", panel.getLayoutConstraints(text2));

		setActiveContext(uic2);
		Assert.assertEquals("uic2 should contain text1 with its constraint", BorderLayout.EAST,
				panel.getLayoutConstraints(text1));
		Assert.assertEquals("uic2 should contain text2 with its constraint", BorderLayout.NORTH,
				panel.getLayoutConstraints(text2));
	}

	/**
	 * test getLayoutConstraints when there are no constraints.
	 */
	@Test
	public void testGetLayoutContraintsNone() {
		WPanel panel = new WPanel();
		panel.setLocked(true);

		setActiveContext(createUIContext());
		WText text = new WText("ABC");
		panel.add(text); // no constraints

		Assert.assertNull("uic2 contains text2 - but no layout constraints", panel.
				getLayoutConstraints(text));
	}

	/**
	 * test addChild - and layout contraints - using variable argument forms.
	 */
	@Test
	public void testAddChildLayoutConstraintVariableArguments() {
		WPanel panel = new WPanel();
		WText text1 = new WText("XYZ");
		WText text2 = new WText("ABC");
		UIContext uic1 = createUIContext();
		UIContext uic2 = createUIContext();

		// add text1 and constraints ( set 1 ) to shared list - both uic1 and uic2
		panel.add(text1, BorderLayout.EAST, BorderLayout.WEST);
		panel.setLocked(true);

		// add text2 and constraints ( set 2 ) to uic2 list only
		setActiveContext(uic2);
		panel.add(text2, BorderLayout.NORTH, BorderLayout.SOUTH, BorderLayout.WEST);

		setActiveContext(uic1);
		Assert.assertNotNull("uic1 should contain text1 with its constraint", panel.
				getLayoutConstraints(text1));
		Serializable[] result0 = (Serializable[]) panel.getLayoutConstraints(text1);
		Assert.assertEquals("result0 should have the 2 constraints from set 1", 2, result0.length);
		Assert.
				assertEquals("result0 should have the first constraint from set 1",
						BorderLayout.EAST, result0[0]);
		Assert.assertEquals("result0 should have the second constraint from set 1",
				BorderLayout.WEST, result0[1]);

		setActiveContext(uic2);
		Assert.assertNotNull("uic2 should contain text1 with its constraint", panel.
				getLayoutConstraints(text1));
		Serializable[] result1 = (Serializable[]) panel.getLayoutConstraints(text1);
		Assert.assertEquals("result1 should have 2 constraints from set 1", 2, result1.length);
		Assert.
				assertEquals("result1 should have the first constraint from set 1",
						BorderLayout.EAST, result1[0]);
		Assert.assertEquals("result1 should have the second constraint from set 1",
				BorderLayout.WEST, result1[1]);

		setActiveContext(uic1);
		Assert.assertNull("uic1 should not contain text2 and its constraints", panel.
				getLayoutConstraints(text2));

		setActiveContext(uic2);
		Assert.assertNotNull("uic2 should contain text2 with its constraint", panel.
				getLayoutConstraints(text2));
		Serializable[] result2 = (Serializable[]) panel.getLayoutConstraints(text2);
		Assert.assertEquals("result2 should have the 3 constraints from set 2", 3, result2.length);
		Assert.assertEquals("result2 should have the first constraint from set 2",
				BorderLayout.NORTH, result2[0]);
		Assert.assertEquals("result2 should have the second constraint from set 2",
				BorderLayout.SOUTH, result2[1]);
		Assert.
				assertEquals("result2 should have the third constraint from set 2",
						BorderLayout.WEST, result2[2]);
	}

	@Test
	public void testDefaultSubmitButtonAccessors() {
		WPanel panel = new WPanel();
		WButton button = new WButton();
		panel.setDefaultSubmitButton(button);
		Assert.assertEquals("Incorrect button returned", button, panel.getDefaultSubmitButton());
	}

	@Test
	public void testModeAccessors() {
		assertAccessorsCorrect(new WPanel(), "mode", null, PanelMode.EAGER, PanelMode.LAZY);
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WPanel(), "type", WPanel.Type.PLAIN, WPanel.Type.CHROME,
				WPanel.Type.BOX);
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WPanel(), "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testMargin() {
		// Create Margin with "all"
		Margin margin = new Margin(1);
		Assert.assertEquals("Incorrect all margin returned", 1, margin.getAll());
		Assert.assertEquals("Incorrect north margin returned", -1, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", -1, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", -1, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", -1, margin.getWest());

		// Create Margin for all sides
		margin = new Margin(1, 2, 3, 4);
		Assert.assertEquals("Incorrect all margin returned", -1, margin.getAll());
		Assert.assertEquals("Incorrect north margin returned", 1, margin.getNorth());
		Assert.assertEquals("Incorrect east margin returned", 2, margin.getEast());
		Assert.assertEquals("Incorrect south margin returned", 3, margin.getSouth());
		Assert.assertEquals("Incorrect west margin returned", 4, margin.getWest());

	}
}
