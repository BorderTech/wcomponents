package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.subordinate.And;
import com.github.bordertech.wcomponents.subordinate.Condition;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.DisableInGroup;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.EnableInGroup;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.GreaterThan;
import com.github.bordertech.wcomponents.subordinate.GreaterThanOrEqual;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.HideInGroup;
import com.github.bordertech.wcomponents.subordinate.LessThan;
import com.github.bordertech.wcomponents.subordinate.LessThanOrEqual;
import com.github.bordertech.wcomponents.subordinate.Mandatory;
import com.github.bordertech.wcomponents.subordinate.Match;
import com.github.bordertech.wcomponents.subordinate.NotEqual;
import com.github.bordertech.wcomponents.subordinate.Optional;
import com.github.bordertech.wcomponents.subordinate.Or;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.ShowInGroup;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;
import static com.github.bordertech.wcomponents.render.webxml.WSubordinateControlRenderer.*;

/**
 * Junit test case for {@link WSubordinateControlRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSubordinateControlRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WSubordinateControl control = new WSubordinateControl();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(control) instanceof WSubordinateControlRenderer);
	}

	@Test
	public void testBasicCondition() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger = new WCheckBox();
		SubordinateTarget actionTarget = new WTextField();

		// Basic Condition
		Rule rule = new Rule();
		rule.setCondition(new Equal(condTrigger, Boolean.TRUE));
		rule.addActionOnTrue(new Show(actionTarget));
		rule.addActionOnFalse(new Hide(actionTarget));
		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget);
		root.add(control);

		setActiveContext(createUIContext());

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check for basic elements
		assertXpathEvaluatesTo("1", String.format("count(//html:%s)", TAG_SUBORDINATE), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_CONDITION), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONFALSE), root);
		// Check id
		assertXpathEvaluatesTo(control.getId() + "-c0", String.format("//html:%s/@id", TAG_SUBORDINATE), root);
		// Check condition
		assertXpathEvaluatesTo(condTrigger.getId(), String.format("//html:%s/html:%s/@controller", TAG_SUBORDINATE, TAG_CONDITION), root);
		assertXpathEvaluatesTo("true", String.format("//html:%s/html:%s/@value", TAG_SUBORDINATE, TAG_CONDITION), root);
		// Check onTrue
		assertXpathEvaluatesTo("show", String.format("//html:%s/html:%s/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(actionTarget.getId(), String.format("//html:%s/html:%s/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		// Check onFalse
		assertXpathEvaluatesTo("hide", String.format("//html:%s/html:%s/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(actionTarget.getId(), String.format("//html:%s/html:%s/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		// Check action target
		assertXpathEvaluatesTo("true", "//ui:textfield/@hidden", root);
	}

	@Test
	public void testBasicConditionWithGroup() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger = new WCheckBox();
		// Setup Group
		SubordinateTarget actionTarget1 = new WTextField();
		SubordinateTarget actionTarget2 = new WTextField();
		SubordinateTarget actionTarget3 = new WTextField();
		WComponentGroup<SubordinateTarget> group1 = new WComponentGroup<>();
		group1.addToGroup(actionTarget1);
		group1.addToGroup(actionTarget2);
		group1.addToGroup(actionTarget3);
		// Basic Condition
		Rule rule = new Rule();
		rule.setCondition(new Equal(condTrigger, Boolean.TRUE));
		rule.addActionOnTrue(new Show(group1));
		rule.addActionOnFalse(new Hide(group1));
		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget1);
		root.add(actionTarget2);
		root.add(actionTarget3);
		root.add(control);
		root.add(group1);

		setActiveContext(createUIContext());

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check for basic elements
		assertXpathEvaluatesTo("1", String.format("count(//html:%s)", TAG_SUBORDINATE), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_CONDITION), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s/@action)", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s)", WComponentGroupRenderer.TAG_GROUP), root);
		assertXpathEvaluatesTo("3", String.format("count(//html:%s/html:%s)", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
		// Check id
		assertXpathEvaluatesTo(control.getId() + "-c0", String.format("//html:%s/@id", TAG_SUBORDINATE), root);
		// Check condition
		assertXpathEvaluatesTo(condTrigger.getId(), String.format("//html:%s/html:%s/@controller", TAG_SUBORDINATE, TAG_CONDITION), root);
		assertXpathEvaluatesTo("true", String.format("//html:%s/html:%s/@value", TAG_SUBORDINATE, TAG_CONDITION), root);
		// Check onTrue
		assertXpathEvaluatesTo("show", String.format("//html:%s/html:%s/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(group1.getId(), String.format("//html:%s/html:%s/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		// Check onFalse
		assertXpathEvaluatesTo("hide", String.format("//html:%s/html:%s/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(group1.getId(), String.format("//html:%s/html:%s/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		// Check group
		assertXpathEvaluatesTo(group1.getId(), String.format("//html:%s/@id", WComponentGroupRenderer.TAG_GROUP), root);
		assertXpathEvaluatesTo(actionTarget1.getId(), String.format("//html:%s/html:%s[position()=1]/@refid", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
		assertXpathEvaluatesTo(actionTarget2.getId(), String.format("//html:%s/html:%s[position()=2]/@refid", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
		assertXpathEvaluatesTo(actionTarget3.getId(), String.format("//html:%s/html:%s[position()=3]/@refid", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
		// Check action target
		assertXpathEvaluatesTo("true", "//ui:textfield[@id='" + actionTarget1.getId() + "']/@hidden", root);
		assertXpathEvaluatesTo("true", "//ui:textfield[@id='" + actionTarget2.getId() + "']/@hidden", root);
		assertXpathEvaluatesTo("true", "//ui:textfield[@id='" + actionTarget3.getId() + "']/@hidden", root);
	}

	@Test
	public void testMultipleControlsAndGroups() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger = new WCheckBox();
		// Setup Groups
		SubordinateTarget actionTarget1 = new WTextField();
		SubordinateTarget actionTarget2 = new WTextField();
		SubordinateTarget actionTarget3 = new WTextField();
		SubordinateTarget actionTarget4 = new WTextField();
		// Multiple Groups
		WComponentGroup<SubordinateTarget> group1 = new WComponentGroup<>();
		group1.addToGroup(actionTarget1);
		group1.addToGroup(actionTarget2);
		WComponentGroup<SubordinateTarget> group2 = new WComponentGroup<>();
		group2.addToGroup(actionTarget3);
		group2.addToGroup(actionTarget4);
		// Multiple Rules
		Rule rule1 = new Rule();
		rule1.setCondition(new Equal(condTrigger, Boolean.TRUE));
		rule1.addActionOnTrue(new Show(group1));
		rule1.addActionOnFalse(new Hide(group1));
		Rule rule2 = new Rule();
		rule2.setCondition(new Equal(condTrigger, Boolean.FALSE));
		rule2.addActionOnTrue(new Show(group2));
		rule2.addActionOnFalse(new Hide(group2));
		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule1);
		control.addRule(rule2);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget1);
		root.add(actionTarget2);
		root.add(actionTarget3);
		root.add(actionTarget4);
		root.add(control);
		root.add(group1);
		root.add(group2);

		setActiveContext(createUIContext());

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check for basic elements
		assertXpathEvaluatesTo("2", String.format("count(//html:%s)", TAG_SUBORDINATE), root);
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_CONDITION), root);
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s/@action)", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo("2", String.format("count(//html:%s)", WComponentGroupRenderer.TAG_GROUP), root);
		assertXpathEvaluatesTo("2", String.format("count(//html:%s[position()=1]/html:%s)", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
		assertXpathEvaluatesTo("2", String.format("count(//html:%s[position()=2]/html:%s)", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
		// Check ids
		assertXpathEvaluatesTo(control.getId() + "-c0", String.format("//html:%s[position()=1]/@id", TAG_SUBORDINATE), root);
		assertXpathEvaluatesTo(control.getId() + "-c1", String.format("//html:%s[position()=2]/@id", TAG_SUBORDINATE), root);

	}

	@Test
	public void testShowHideActions() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger = new WCheckBox();
		// Setup Group
		SubordinateTarget actionTarget = new WTextField();
		SubordinateTarget actionTarget1 = new WTextField();
		SubordinateTarget actionTarget2 = new WTextField();
		SubordinateTarget actionTarget3 = new WTextField();
		WComponentGroup<SubordinateTarget> group1 = new WComponentGroup<>();
		group1.addToGroup(actionTarget1);
		group1.addToGroup(actionTarget2);
		group1.addToGroup(actionTarget3);

		// Setup Rule with Show/Hide actions
		Rule rule = new Rule();
		rule.setCondition(new Equal(condTrigger, Boolean.TRUE));
		// Single Component Target
		rule.addActionOnTrue(new Show(actionTarget));
		rule.addActionOnFalse(new Hide(actionTarget));
		// Group Target
		rule.addActionOnTrue(new Show(group1));
		rule.addActionOnFalse(new Hide(group1));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget);
		root.add(actionTarget1);
		root.add(actionTarget2);
		root.add(actionTarget3);
		root.add(control);
		root.add(group1);

		setActiveContext(createUIContext());

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check onTrue
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONTRUE), root);
		// Check onTrue - Component
		assertXpathEvaluatesTo("show", String.format("//html:%s/html:%s[position()=1]/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(actionTarget.getId(), String.format("//html:%s/html:%s[position()=1]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=1]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		// Check onTrue - Group
		assertXpathEvaluatesTo("show", String.format("//html:%s/html:%s[position()=2]/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(group1.getId(), String.format("//html:%s/html:%s[position()=2]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=2]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		// Check onFalse
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONFALSE), root);
		// Check onFalse - Component
		assertXpathEvaluatesTo("hide", String.format("//html:%s/html:%s[position()=1]/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(actionTarget.getId(), String.format("//html:%s/html:%s[position()=1]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=1]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		// Check onFalse - Group
		assertXpathEvaluatesTo("hide", String.format("//html:%s/html:%s[position()=2]/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(group1.getId(), String.format("//html:%s/html:%s[position()=2]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=2]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		// Check action target
		assertXpathEvaluatesTo("true", "//ui:textfield[@id='" + actionTarget.getId() + "']/@hidden", root);
		assertXpathEvaluatesTo("true", "//ui:textfield[@id='" + actionTarget1.getId() + "']/@hidden", root);
		assertXpathEvaluatesTo("true", "//ui:textfield[@id='" + actionTarget2.getId() + "']/@hidden", root);
		assertXpathEvaluatesTo("true", "//ui:textfield[@id='" + actionTarget3.getId() + "']/@hidden", root);
	}

	@Test
	public void testEnableDisableActions() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger = new WCheckBox();
		// Setup Group
		SubordinateTarget actionTarget = new WTextField();
		SubordinateTarget actionTarget1 = new WTextField();
		SubordinateTarget actionTarget2 = new WTextField();
		SubordinateTarget actionTarget3 = new WTextField();
		WComponentGroup<SubordinateTarget> group1 = new WComponentGroup<>();
		group1.addToGroup(actionTarget1);
		group1.addToGroup(actionTarget2);
		group1.addToGroup(actionTarget3);

		// Setup Rule with Enable/Disable actions
		Rule rule = new Rule();
		rule.setCondition(new Equal(condTrigger, Boolean.TRUE));
		// Single Component Target
		rule.addActionOnTrue(new Enable(actionTarget));
		rule.addActionOnFalse(new Disable(actionTarget));
		// Group Target
		rule.addActionOnTrue(new Enable(group1));
		rule.addActionOnFalse(new Disable(group1));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget);
		root.add(actionTarget1);
		root.add(actionTarget2);
		root.add(actionTarget3);
		root.add(control);
		root.add(group1);

		setActiveContext(createUIContext());

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check onTrue
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONTRUE), root);
		// Check onTrue - Component
		assertXpathEvaluatesTo("enable", String.format("//html:%s/html:%s[position()=1]/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(actionTarget.getId(),
				String.format("//html:%s/html:%s[position()=1]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=1]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET),
				root);
		// Check onTrue - Group
		assertXpathEvaluatesTo("enable", String.format("//html:%s/html:%s[position()=2]/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(group1.getId(),
				String.format("//html:%s/html:%s[position()=2]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET),
				root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=2]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		// Check onFalse
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONFALSE), root);
		// Check onFalse - Component
		assertXpathEvaluatesTo("disable", String.format("//html:%s/html:%s[position()=1]/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(actionTarget.getId(),
				String.format("//html:%s/html:%s[position()=1]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=1]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET),
				root);
		// Check onFalse - Group
		assertXpathEvaluatesTo("disable", String.format("//html:%s/html:%s[position()=2]/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(group1.getId(),
				String.format("//html:%s/html:%s[position()=2]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET),
				root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=2]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		// Check action target
		assertXpathEvaluatesTo("true",
				"//ui:textfield[@id='" + actionTarget.getId() + "']/@disabled", root);
		assertXpathEvaluatesTo("true",
				"//ui:textfield[@id='" + actionTarget1.getId() + "']/@disabled", root);
		assertXpathEvaluatesTo("true",
				"//ui:textfield[@id='" + actionTarget2.getId() + "']/@disabled", root);
		assertXpathEvaluatesTo("true",
				"//ui:textfield[@id='" + actionTarget3.getId() + "']/@disabled", root);
	}

	@Test
	public void testMandatoryOptionalActions() throws IOException, SAXException, XpathException {
		WCheckBox condTrigger = new WCheckBox();
		// Setup Group
		SubordinateTarget actionTarget = new WTextField();
		SubordinateTarget actionTarget1 = new WTextField();
		SubordinateTarget actionTarget2 = new WTextField();
		SubordinateTarget actionTarget3 = new WTextField();
		WComponentGroup<SubordinateTarget> group1 = new WComponentGroup<>();
		group1.addToGroup(actionTarget1);
		group1.addToGroup(actionTarget2);
		group1.addToGroup(actionTarget3);

		// Setup Rule with Mandatory/Optional actions
		Rule rule = new Rule();
		rule.setCondition(new Equal(condTrigger, Boolean.TRUE));
		// Single Component Target
		rule.addActionOnTrue(new Mandatory(actionTarget));
		rule.addActionOnFalse(new Optional(actionTarget));
		// Group Target
		rule.addActionOnTrue(new Mandatory(group1));
		rule.addActionOnFalse(new Optional(group1));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget);
		root.add(actionTarget1);
		root.add(actionTarget2);
		root.add(actionTarget3);
		root.add(control);
		root.add(group1);

		setActiveContext(createUIContext());
		condTrigger.setSelected(true);

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check onTrue
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONTRUE), root);
		// Check onTrue - Component
		assertXpathEvaluatesTo("mandatory", String.format("//html:%s/html:%s[position()=1]/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(actionTarget.getId(),
				String.format("//html:%s/html:%s[position()=1]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=1]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET),
				root);
		// Check onTrue - Group
		assertXpathEvaluatesTo("mandatory", String.format("//html:%s/html:%s[position()=2]/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(group1.getId(),
				String.format("//html:%s/html:%s[position()=2]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET),
				root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=2]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		// Check onFalse
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONFALSE), root);
		// Check onFalse - Component
		assertXpathEvaluatesTo("optional", String.format("//html:%s/html:%s[position()=1]/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(actionTarget.getId(),
				String.format("//html:%s/html:%s[position()=1]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=1]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET),
				root);
		// Check onFalse - Group
		assertXpathEvaluatesTo("optional", String.format("//html:%s/html:%s[position()=2]/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(group1.getId(),
				String.format("//html:%s/html:%s[position()=2]/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET),
				root);
		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[position()=2]/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		// Check action target
		assertXpathEvaluatesTo("true",
				"//ui:textfield[@id='" + actionTarget.getId() + "']/@required", root);
		assertXpathEvaluatesTo("true",
				"//ui:textfield[@id='" + actionTarget1.getId() + "']/@required", root);
		assertXpathEvaluatesTo("true",
				"//ui:textfield[@id='" + actionTarget2.getId() + "']/@required", root);
		assertXpathEvaluatesTo("true",
				"//ui:textfield[@id='" + actionTarget3.getId() + "']/@required", root);
	}

	@Test
	public void testGroupShowInHideInAction() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger = new WCheckBox();
		// Setup Group
		SubordinateTarget actionTarget1 = new WTextField();
		SubordinateTarget actionTarget2 = new WTextField();
		SubordinateTarget actionTarget3 = new WTextField();
		WComponentGroup<SubordinateTarget> group1 = new WComponentGroup<>();
		group1.addToGroup(actionTarget1);
		group1.addToGroup(actionTarget2);
		group1.addToGroup(actionTarget3);

		// Setup Rule with ShowInGroup/HideInGroup actions
		Rule rule = new Rule();
		rule.setCondition(new Equal(condTrigger, Boolean.TRUE));
		// OnTrue - ShowInGroup
		rule.addActionOnTrue(new ShowInGroup(actionTarget1, group1));
		// OnFalse - HideInGroup
		rule.addActionOnFalse(new HideInGroup(actionTarget1, group1));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget1);
		root.add(actionTarget2);
		root.add(actionTarget3);
		root.add(control);
		root.add(group1);

		setActiveContext(createUIContext());

		// Apply the controls (False conditions)
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);

		// Check onTrue
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo("showin", String.format("//html:%s/html:%s/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(group1.getId(), String.format("//html:%s/html:%s/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		assertXpathEvaluatesTo(actionTarget1.getId(), String.format("//html:%s/html:%s/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET),
				root);

		// Check onFalse
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo("hidein", String.format("//html:%s/html:%s/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(group1.getId(), String.format("//html:%s/html:%s/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET),
				root);
		assertXpathEvaluatesTo(actionTarget1.getId(), String.format("//html:%s/html:%s/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET),
				root);

		// Check action target (Target 1 should be hidden)
		assertXpathEvaluatesTo("true", "//ui:textfield[@id='" + actionTarget1.getId() + "']/@hidden",
				root);
		assertXpathEvaluatesTo("", "//ui:textfield[@id='" + actionTarget2.getId() + "']/@hidden",
				root);
		assertXpathEvaluatesTo("", "//ui:textfield[@id='" + actionTarget3.getId() + "']/@hidden",
				root);
	}

	@Test
	public void testGroupEnableInDisableInAction() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger = new WCheckBox();
		// Setup Group
		SubordinateTarget actionTarget1 = new WTextField();
		SubordinateTarget actionTarget2 = new WTextField();
		SubordinateTarget actionTarget3 = new WTextField();
		WComponentGroup<SubordinateTarget> group1 = new WComponentGroup<>();
		group1.addToGroup(actionTarget1);
		group1.addToGroup(actionTarget2);
		group1.addToGroup(actionTarget3);

		// Setup Rule with ShowInGroup/HideInGroup actions
		Rule rule = new Rule();
		rule.setCondition(new Equal(condTrigger, Boolean.TRUE));
		// OnTrue - EnableInGroup
		rule.addActionOnTrue(new EnableInGroup(actionTarget1, group1));
		// OnFalse - DisableInGroup
		rule.addActionOnFalse(new DisableInGroup(actionTarget1, group1));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget1);
		root.add(actionTarget2);
		root.add(actionTarget3);
		root.add(control);
		root.add(group1);

		setActiveContext(createUIContext());

		// Apply the controls (False conditions)
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);

		// Check onTrue
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo("enablein", String.format("//html:%s/html:%s/@action", TAG_SUBORDINATE, TAG_ONTRUE), root);
		assertXpathEvaluatesTo(group1.getId(), String.format("//html:%s/html:%s/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);
		assertXpathEvaluatesTo(actionTarget1.getId(), String.format("//html:%s/html:%s/html:%s/@refid", TAG_SUBORDINATE, TAG_ONTRUE, TAG_TARGET), root);

		// Check onFalse
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo("disablein", String.format("//html:%s/html:%s/@action", TAG_SUBORDINATE, TAG_ONFALSE), root);
		assertXpathEvaluatesTo(group1.getId(), String.format("//html:%s/html:%s/html:%s/@groupid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);
		assertXpathEvaluatesTo(actionTarget1.getId(), String.format("//html:%s/html:%s/html:%s/@refid", TAG_SUBORDINATE, TAG_ONFALSE, TAG_TARGET), root);

		// Check action target (Target 1 should be disabled)
		assertXpathEvaluatesTo("true",
				"//ui:textfield[@id='" + actionTarget1.getId() + "']/@disabled", root);
		assertXpathEvaluatesTo("", "//ui:textfield[@id='" + actionTarget2.getId() + "']/@disabled", root);
		assertXpathEvaluatesTo("", "//ui:textfield[@id='" + actionTarget3.getId() + "']/@disabled", root);
	}

	@Test
	public void testAndCondition() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger1 = new WCheckBox();
		SubordinateTrigger condTrigger2 = new WCheckBox();
		SubordinateTrigger condTrigger3 = new WCheckBox();
		// Create AND condition
		Condition cond1 = new Equal(condTrigger1, Boolean.TRUE);
		Condition cond2 = new Equal(condTrigger2, Boolean.TRUE);
		Condition cond3 = new Equal(condTrigger3, Boolean.TRUE);
		Condition and = new And(cond1, cond2, cond3);
		SubordinateTarget actionTarget = new WTextField();

		// Setup Rule with AND Condition
		Rule rule = new Rule();
		rule.setCondition(and);
		rule.addActionOnTrue(new Show(actionTarget));
		rule.addActionOnFalse(new Hide(actionTarget));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger1);
		root.add(condTrigger2);
		root.add(condTrigger3);
		root.add(actionTarget);
		root.add(control);

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check AND
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_AND), root);
		assertXpathEvaluatesTo("3", String.format("count(//html:%s/html:%s/html:%s)", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo(condTrigger1.getId(),
				String.format("//html:%s/html:%s/html:%s[position()=1]/@controller", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo(condTrigger2.getId(),
				String.format("//html:%s/html:%s/html:%s[position()=2]/@controller", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo(condTrigger3.getId(),
				String.format("//html:%s/html:%s/html:%s[position()=3]/@controller", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		// Check action target
		assertXpathEvaluatesTo("true", "//ui:textfield/@hidden", root);
	}

	@Test
	public void testOrCondition() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger1 = new WCheckBox();
		SubordinateTrigger condTrigger2 = new WCheckBox();
		SubordinateTrigger condTrigger3 = new WCheckBox();
		// Create OR condition
		Condition cond1 = new Equal(condTrigger1, Boolean.TRUE);
		Condition cond2 = new Equal(condTrigger2, Boolean.TRUE);
		Condition cond3 = new Equal(condTrigger3, Boolean.TRUE);
		Condition orTest = new Or(cond1, cond2, cond3);
		SubordinateTarget actionTarget = new WTextField();

		// Setup rule with OR condition
		Rule rule = new Rule();
		rule.setCondition(orTest);
		rule.addActionOnTrue(new Show(actionTarget));
		rule.addActionOnFalse(new Hide(actionTarget));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger1);
		root.add(condTrigger2);
		root.add(condTrigger3);
		root.add(actionTarget);
		root.add(control);

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check OR
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_OR), root);
		assertXpathEvaluatesTo("3", String.format("count(//html:%s/html:%s/html:%s)", TAG_SUBORDINATE, TAG_OR, TAG_CONDITION), root);
		assertXpathEvaluatesTo(condTrigger1.getId(),
				String.format("//html:%s/html:%s/html:%s[position()=1]/@controller", TAG_SUBORDINATE, TAG_OR, TAG_CONDITION),
				root);
		assertXpathEvaluatesTo(condTrigger2.getId(),
				String.format("//html:%s/html:%s/html:%s[position()=2]/@controller", TAG_SUBORDINATE, TAG_OR, TAG_CONDITION),
				root);
		assertXpathEvaluatesTo(condTrigger3.getId(),
				String.format("//html:%s/html:%s/html:%s[position()=3]/@controller", TAG_SUBORDINATE, TAG_OR, TAG_CONDITION),
				root);
		// Check action target
		assertXpathEvaluatesTo("true", "//ui:textfield/@hidden", root);

	}

	@Test
	public void testNestedConditions() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger = new WCheckBox();
		// Create Nested Condition
		Condition cond1 = new Equal(condTrigger, Boolean.TRUE);
		Condition cond2 = new Equal(condTrigger, Boolean.TRUE);
		Condition cond3 = new Equal(condTrigger, Boolean.TRUE);
		Condition cond4 = new Equal(condTrigger, Boolean.TRUE);
		Condition orTest = new Or(cond1, cond2);
		Condition and1 = new And(cond3, orTest);
		Condition and2 = new And(cond4, and1);
		SubordinateTarget actionTarget = new WTextField();

		// Setup rule with Nested Condition
		Rule rule = new Rule();
		rule.setCondition(and2);
		rule.addActionOnTrue(new Show(actionTarget));
		rule.addActionOnFalse(new Hide(actionTarget));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget);
		root.add(control);

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check Nested
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_SUBORDINATE, TAG_AND), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s/html:%s)", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s/html:%s)", TAG_SUBORDINATE, TAG_AND, TAG_AND), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s/html:%s/html:%s)", TAG_SUBORDINATE, TAG_AND, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s/html:%s/html:%s)", TAG_SUBORDINATE, TAG_AND, TAG_AND, TAG_OR), root);
		assertXpathEvaluatesTo("2", String.format("count(//html:%s/html:%s/html:%s/html:%s/html:%s)", TAG_SUBORDINATE, TAG_AND, TAG_AND, TAG_OR, TAG_CONDITION), root);
		// Check action target
		assertXpathEvaluatesTo("true", "//ui:textfield/@hidden", root);
	}

	@Test
	public void testConditionWithListTrigger() throws IOException, SAXException, XpathException {
		WDropdown condTrigger = new WDropdown();
		SubordinateTarget actionTarget = new WTextField();

		// Setup a rule with a condition using a dropdown
		Rule rule = new Rule();
		rule.setCondition(new Equal(condTrigger, "b"));
		rule.addActionOnTrue(new Show(actionTarget));
		rule.addActionOnFalse(new Hide(actionTarget));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(actionTarget);
		root.add(control);

		condTrigger.setOptions(Arrays.asList(new String[]{"a", "b", "c", "d", "e"}));
		condTrigger.setSelected("e");

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check Condition Value is the "Code Value"
		assertXpathEvaluatesTo("2", String.format("//html:%s/html:%s/@value", TAG_SUBORDINATE, TAG_CONDITION), root);
		// Check action target
		assertXpathEvaluatesTo("true", "//ui:textfield/@hidden", root);
	}

	@Test
	public void testConditionWithRadioButtonGroupTarget() throws IOException, SAXException,
			XpathException {
		RadioButtonGroup rbg = new RadioButtonGroup();

		WRadioButton button1 = rbg.addRadioButton("B1");
		WRadioButton button2 = rbg.addRadioButton("B2");

		SubordinateTarget actionTarget = new WTextField();

		// Setup a rule with a condition using a Radio Button Group
		Rule rule = new Rule();
		rule.setCondition(new Equal(rbg, button2));
		rule.addActionOnTrue(new Show(actionTarget));
		rule.addActionOnFalse(new Hide(actionTarget));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(rbg);
		root.add(button1);
		root.add(button2);
		root.add(actionTarget);
		root.add(control);

		setActiveContext(createUIContext());
		rbg.setSelectedValue("B1");

		// Apply the controls
		control.applyTheControls();

		// Validate Schema
		assertSchemaMatch(root);
		// Check Condition Value is the Radio Button Value
		assertXpathEvaluatesTo(button2.getValue(), String.format("//html:%s/html:%s/@value", TAG_SUBORDINATE, TAG_CONDITION), root);
		// Check action target
		assertXpathEvaluatesTo("true", "//ui:textfield/@hidden", root);
	}

	@Test
	public void testAllConditions() throws IOException, SAXException, XpathException {
		SubordinateTrigger condTrigger = new WNumberField();
		SubordinateTrigger condTrigger2 = new WTextField();
		SubordinateTarget actionTarget = new WTextField();

		BigDecimal value = BigDecimal.valueOf(2);

		Condition cond1 = new Equal(condTrigger, value);
		Condition cond2 = new NotEqual(condTrigger, value);
		Condition cond3 = new LessThan(condTrigger, value);
		Condition cond4 = new LessThanOrEqual(condTrigger, value);
		Condition cond5 = new GreaterThan(condTrigger, value);
		Condition cond6 = new GreaterThanOrEqual(condTrigger, value);
		Condition cond7 = new Match(condTrigger2, "[abc]");

		// Basic Condition
		Rule rule = new Rule();
		rule.setCondition(new And(cond1, cond2, cond3, cond4, cond5, cond6, cond7));
		rule.addActionOnTrue(new Show(actionTarget));
		rule.addActionOnFalse(new Hide(actionTarget));

		// Setup Subordinate
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		WContainer root = new WContainer();
		root.add(condTrigger);
		root.add(condTrigger2);
		root.add(actionTarget);
		root.add(control);

		setActiveContext(createUIContext());

		// Validate Schema
		assertSchemaMatch(root);

		assertXpathNotExists(String.format("//html:%s/html:%s/html:%s[1]/@operator", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo("ne", String.format("//html:%s/html:%s/html:%s[2]/@operator", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo("lt", String.format("//html:%s/html:%s/html:%s[3]/@operator", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo("le", String.format("//html:%s/html:%s/html:%s[4]/@operator", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo("gt", String.format("//html:%s/html:%s/html:%s[5]/@operator", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo("ge", String.format("//html:%s/html:%s/html:%s[6]/@operator", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
		assertXpathEvaluatesTo("rx", String.format("//html:%s/html:%s/html:%s[7]/@operator", TAG_SUBORDINATE, TAG_AND, TAG_CONDITION), root);
	}

}
