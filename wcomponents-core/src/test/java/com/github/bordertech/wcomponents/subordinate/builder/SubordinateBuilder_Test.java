package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.util.SystemException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * JUnit tests for the {@link SubordinateBuilder} class.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 */
public final class SubordinateBuilder_Test extends AbstractWComponentTestCase {

	@Test(expected = SystemException.class)
	public void testInvalidSyntax() {
		SubordinateBuilder builder = new SubordinateBuilder();
		builder.condition().equals(new WTextField(), "x").or();
		builder.build();
	}

	@Test(expected = SystemException.class)
	public void testMissingActions() {
		SubordinateBuilder builder = new SubordinateBuilder();
		builder.condition().equals(new WTextField(), "x");
		builder.build();
	}

	@Test
	public void testEnable() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input = new WCheckBox();
		builder.condition().equals(input, "false");
		builder.whenTrue().disable(input);

		setActiveContext(createUIContext());
		Assert.assertFalse("Component should be initially enabled", input.isDisabled());

		builder.build().applyTheControls();
		Assert.assertTrue("Component should be disabled", input.isDisabled());
	}

	@Test
	public void testDisable() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input = new WCheckBox();
		builder.condition().equals(input, "false");
		builder.whenTrue().disable(input);

		setActiveContext(createUIContext());
		Assert.assertFalse("Component should be initially enabled", input.isDisabled());

		builder.build().applyTheControls();
		Assert.assertTrue("Component should be disabled", input.isDisabled());
	}

	@Test
	public void testShow() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input = new WCheckBox();
		builder.condition().equals(input, "false");
		builder.whenTrue().show(input);

		setActiveContext(createUIContext());
		setFlag(input, ComponentModel.HIDE_FLAG, true);
		Assert.assertTrue("Component should be initially hidden", input.isHidden());

		builder.build().applyTheControls();
		Assert.assertFalse("Component should be visible", input.isHidden());
	}

	@Test
	public void testHide() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input = new WCheckBox();
		builder.condition().equals(input, "false");
		builder.whenTrue().hide(input);

		setActiveContext(createUIContext());
		Assert.assertFalse("Component should be initially visible", input.isHidden());

		builder.build().applyTheControls();
		Assert.assertTrue("Component should be hidden", input.isHidden());
	}

	@Test
	public void testMandatory() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input = new WCheckBox();
		builder.condition().equals(input, "false");
		builder.whenTrue().setMandatory(input);

		setActiveContext(createUIContext());
		Assert.assertFalse("Component should be initially optional", input.isMandatory());

		builder.build().applyTheControls();
		Assert.assertTrue("Component should be required", input.isMandatory());
	}

	@Test
	public void testOptional() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input = new WCheckBox();
		builder.condition().equals(input, "false");
		builder.whenTrue().setOptional(input);

		setActiveContext(createUIContext());
		input.setMandatory(true);
		Assert.assertTrue("Component should be initially mandatory", input.isMandatory());

		builder.build().applyTheControls();
		Assert.assertFalse("Component should be mandatory", input.isHidden());
	}

	@Test
	public void testShowIn() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input1 = new WCheckBox();
		WCheckBox input2 = new WCheckBox();
		WCheckBox input3 = new WCheckBox();

		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		group.addToGroup(input1);
		group.addToGroup(input2);
		group.addToGroup(input3);

		// True Condition
		builder.condition().equals(new WCheckBox(), "false");

		// ShowIn Action
		builder.whenTrue().showIn(input2, group);

		setActiveContext(createUIContext());

		// Set initial states (opposite to end state)
		setFlag(input1, ComponentModel.HIDE_FLAG, false);
		setFlag(input2, ComponentModel.HIDE_FLAG, true);
		setFlag(input3, ComponentModel.HIDE_FLAG, false);

		Assert.assertFalse("showIn - Input1 Component should not be hidden", input1.isHidden());
		Assert.assertTrue("showIn - Input2 Component should be initially hidden", input2.isHidden());
		Assert.assertFalse("showIn - Input3 Component should not be hidden", input3.isHidden());

		builder.build().applyTheControls();

		Assert.assertTrue("showIn - Input1 Component should be hidden", input1.isHidden());
		Assert.assertFalse("showIn - Input2 Component should not be hidden", input2.isHidden());
		Assert.assertTrue("showIn - Input3 Component should be hidden", input3.isHidden());
	}

	@Test
	public void testHideIn() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input1 = new WCheckBox();
		WCheckBox input2 = new WCheckBox();
		WCheckBox input3 = new WCheckBox();

		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		group.addToGroup(input1);
		group.addToGroup(input2);
		group.addToGroup(input3);

		// True Condition
		builder.condition().equals(new WCheckBox(), "false");

		// HideIn Action
		builder.whenTrue().hideIn(input2, group);

		setActiveContext(createUIContext());

		// Set initial states (opposite to end state)
		setFlag(input1, ComponentModel.HIDE_FLAG, true);
		setFlag(input2, ComponentModel.HIDE_FLAG, false);
		setFlag(input3, ComponentModel.HIDE_FLAG, true);

		Assert.assertTrue("hideIn - Input1 Component should be hidden", input1.isHidden());
		Assert.assertFalse("hideIn - Input2 Component should not be hidden", input2.isHidden());
		Assert.assertTrue("hideIn - Input3 Component should be hidden", input3.isHidden());

		builder.build().applyTheControls();

		Assert.assertFalse("hideIn - Input1 Component should not be hidden", input1.isHidden());
		Assert.assertTrue("hideIn - Input2 Component should be hidden", input2.isHidden());
		Assert.assertFalse("hideIn - Input3 Component should not be hidden", input3.isHidden());
	}

	@Test
	public void testEnableIn() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input1 = new WCheckBox();
		WCheckBox input2 = new WCheckBox();
		WCheckBox input3 = new WCheckBox();

		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		group.addToGroup(input1);
		group.addToGroup(input2);
		group.addToGroup(input3);

		// True Condition
		builder.condition().equals(new WCheckBox(), "false");

		// EnableIn Action
		builder.whenTrue().enableIn(input2, group);

		setActiveContext(createUIContext());

		// Set initial states (opposite to end state)
		input1.setDisabled(false);
		input2.setDisabled(true);
		input3.setDisabled(false);

		Assert.assertFalse("enableIn - Input1 Component should be enabled", input1.isDisabled());
		Assert.assertTrue("enableIn - Input2 Component should be disabled", input2.isDisabled());
		Assert.assertFalse("enableIn - Input3 Component should be enabled", input3.isDisabled());

		builder.build().applyTheControls();

		Assert.assertTrue("enableIn - Input1 Component should be disabled", input1.isDisabled());
		Assert.assertFalse("enableIn - Input2 Component should be enabled", input2.isDisabled());
		Assert.assertTrue("enableIn - Input3 Component should be disabled", input3.isDisabled());
	}

	@Test
	public void testDisableIn() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WCheckBox input1 = new WCheckBox();
		WCheckBox input2 = new WCheckBox();
		WCheckBox input3 = new WCheckBox();

		WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
		group.addToGroup(input1);
		group.addToGroup(input2);
		group.addToGroup(input3);

		// True Condition
		builder.condition().equals(new WCheckBox(), "false");

		// DisableIn Action
		builder.whenTrue().disableIn(input2, group);

		setActiveContext(createUIContext());

		// Set initial states (opposite to end state)
		input1.setDisabled(true);
		input2.setDisabled(false);
		input3.setDisabled(true);

		Assert.assertTrue("disableIn - Input1 Component should be disabled", input1.isDisabled());
		Assert.assertFalse("disableIn - Input2 Component should be enabled", input2.isDisabled());
		Assert.assertTrue("disableIn - Input3 Component should be disabled", input3.isDisabled());

		builder.build().applyTheControls();

		Assert.assertFalse("disableIn - Input1 Component should be enabled", input1.isDisabled());
		Assert.assertTrue("disableIn - Input2 Component should be disabled", input2.isDisabled());
		Assert.assertFalse("disableIn - Input3 Component should be enabled", input3.isDisabled());
	}

	@Test
	public void testWhenTrue() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WTextField comp1 = new WTextField();
		WTextField comp2 = new WTextField();

		builder.condition().equals(comp1, "x");
		builder.whenTrue().disable(comp1);
		builder.whenFalse().enable(comp2);

		setActiveContext(createUIContext());
		Assert.assertFalse("comp1 should not be disabled until rule executes", comp1.isDisabled());
		Assert.assertFalse("comp2 should not be disabled", comp2.isDisabled());

		comp1.setText("x");
		builder.build().applyTheControls();

		Assert.assertTrue("comp1 should be disabled", comp1.isDisabled());
		Assert.assertFalse("comp2 should not be disabled", comp2.isDisabled());
	}

	@Test
	public void testWhenFalse() {
		SubordinateBuilder builder = new SubordinateBuilder();
		WTextField comp1 = new WTextField();
		WTextField comp2 = new WTextField();

		builder.condition().equals(comp1, "x");
		builder.whenFalse().disable(comp1);
		builder.whenTrue().enable(comp2);

		Assert.assertFalse("comp1 should not be disabled until rule executes", comp1.isDisabled());
		Assert.assertFalse("comp2 should not be disabled", comp2.isDisabled());

		setActiveContext(createUIContext());
		comp1.setText("y");
		builder.build().applyTheControls();

		Assert.assertTrue("comp1 should be disabled", comp1.isDisabled());
		Assert.assertFalse("comp2 should not be disabled", comp2.isDisabled());
	}
}
