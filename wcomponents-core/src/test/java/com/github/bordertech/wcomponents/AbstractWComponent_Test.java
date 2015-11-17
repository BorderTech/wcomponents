package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validator.AbstractFieldValidator;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test of basic WComponent features.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class AbstractWComponent_Test extends AbstractWComponentTestCase {

	@Test
	public void testStaticStructure() {
		UIContext uic = createUIContext();

		AbstractWComponent parent = new MockContainer();
		AbstractWComponent child1 = new SimpleComponent();
		AbstractWComponent child2 = new SimpleComponent();

		// No children
		Assert.assertNull("New components should not have a parent", parent.getParent());
		Assert.assertNull("New components should not have a parent", child1.getParent());
		setActiveContext(uic);
		Assert.assertNull("New components should not have a parent", child2.getParent());
		resetContext();

		// One child
		parent.add(child1);
		Assert.assertEquals("getChildCount() incorrect for 1 child", 1, parent.getChildCount());
		setActiveContext(uic);
		Assert.assertEquals("getChildCount(uic) incorrect for 1 child", 1, parent.getChildCount());
		resetContext();
		Assert.assertEquals("getChildCount() incorrect for 0 children", 0, child1.getChildCount());
		setActiveContext(uic);
		Assert.
				assertEquals("getChildCount(uic) incorrect for 0 children", 0, child1.
						getChildCount());
		resetContext();
		Assert.assertEquals("getChildAt(0) incorrect", child1, parent.getChildAt(0));
		Assert.assertNull("getParent() incorrect for no parent", parent.getParent());
		Assert.assertEquals("getParent() incorrect for child", parent, child1.getParent());
		setActiveContext(uic);
		Assert.assertEquals("getParent(uic) incorrect for child", parent, child1.getParent());
		resetContext();
		Assert.assertEquals("Incorrect indexOfChild for child", 0, parent.getIndexOfChild(child1));

		// Two children
		parent.add(child2);
		Assert.assertEquals("getChildCount() incorrect for 2 children", 2, parent.getChildCount());
		setActiveContext(uic);
		Assert.
				assertEquals("getChildCount(uic) incorrect for 2 children", 2, parent.
						getChildCount());
		resetContext();
		Assert.assertEquals("getChildAt(1) incorrect", child2, parent.getChildAt(1));
		setActiveContext(uic);
		Assert.assertEquals("getParent(uic) incorrect for 2nd child", parent, child2.getParent());
		resetContext();
		Assert.assertEquals("Incorrect indexOfChild for 2nd child", 1, parent.
				getIndexOfChild(child2));

		setActiveContext(uic);
		Assert.assertEquals("getTop(uic, comp) incorrect for 1st child", parent, WebUtilities.
				getTop(child1));
		resetContext();
		Assert.assertEquals("getTop(uic, comp) incorrect for 2nd child", parent, WebUtilities.
				getTop(child2));

		// Remove
		parent.remove(child1);
		Assert.assertNull("Removed child's parent should be null", child1.getParent());
		Assert.assertEquals("getChildCount() incorrect after remove", 1, parent.getChildCount());
		setActiveContext(uic);
		Assert.assertEquals("getChildCount(uic) incorrect after remove", 1, parent.getChildCount());
		resetContext();

		// Remove all
		parent.add(child1);
		parent.removeAll();
		Assert.assertNull("Removed child's parent should be null", child1.getParent());
		Assert.assertNull("Removed child's parent should be null", child2.getParent());
		Assert.assertEquals("getChildCount() incorrect after remove", 0, parent.getChildCount());
		setActiveContext(uic);
		Assert.assertEquals("getChildCount(uic) incorrect after remove", 0, parent.getChildCount());
		resetContext();

		// Re-parenting should not be allowed
		parent.add(child1);

		try {
			child2.add(child1);
			Assert.fail("Runtime exception expected when trying to re-parent");
		} catch (RuntimeException expected) {
			Assert.assertSame("Parent should not have changed", parent, child1.getParent());
		}
	}

	@Test
	public void testDynamicStructure() {
		UIContext uic = createUIContext();

		AbstractWComponent a = new MockContainer();
		AbstractWComponent b = new SimpleComponent();
		AbstractWComponent d1 = new MockContainer();
		AbstractWComponent d2 = new SimpleComponent();

		// Shared child
		a.add(b);
		a.setLocked(true);

		// Dynamic children
		setActiveContext(uic);
		a.add(d1);
		d1.add(d2);
		resetContext();

		// Ensure that shared structure has not changed.
		Assert.assertEquals("Incorrect number of static children", 1, a.getChildCount());
		Assert.assertEquals("Incorrect parent for static child", a, b.getParent());

		// Check dynamic structure.
		setActiveContext(uic);
		Assert.assertEquals("Incorrect child count for 'a'", 2, a.getChildCount());
		Assert.assertEquals("Incorrect child count for 'd1'", 1, d1.getChildCount());
		Assert.assertEquals("Incorrect parent for 'd1'", a, d1.getParent());
		Assert.assertEquals("Incorrect parent for 'd2'", d1, d2.getParent());
		Assert.assertEquals("Incorrect top component", a, WebUtilities.getTop(d2));

		Assert.assertTrue("'b' should be in default state", b.isDefaultState());
		Assert.assertFalse("'a' should not be in default state", a.isDefaultState());
		Assert.assertFalse("'d1' should not be in default state", d1.isDefaultState());
		Assert.assertFalse("'d2' should not be in default state", d2.isDefaultState());

		// Reset
		a.reset();
		Assert.
				assertTrue("'b' should be in default state after UIContext reset", b.
						isDefaultState());
		Assert.
				assertTrue("'a' should be in default state after UIContext reset", a.
						isDefaultState());
		Assert.assertTrue("'s1' should be in default state after UIContext reset", d1.
				isDefaultState());
		Assert.assertTrue("'d2' should be in default state after UIContext reset", d2.
				isDefaultState());

		// Static structure will still be in place, so b is child of a.
		Assert.
				assertEquals("Incorrect number of children for 'a' after reset", 1, a.
						getChildCount());
		a.remove(b);
		Assert.assertEquals("Incorrect number of children for 'a' after remove", 0, a.
				getChildCount());
		Assert.assertFalse("'a' should not be in default state after remove", a.isDefaultState());
		Assert.assertNull("Incorrect parent for 'b' after remove", b.getParent());
		Assert.assertFalse("'b' should be in default state after remove", b.isDefaultState());
	}

	@Test
	public void testDynamicAddAndReset() {
		AbstractWComponent parent = new MockContainer();
		AbstractWComponent child = new SimpleComponent();

		setActiveContext(createUIContext());
		parent.add(child);
		child.reset();

		Assert.assertEquals("Child component should have reference to parent", parent, child.
				getParent());
		Assert.assertEquals("Parent component should have reference to child", 0, parent.
				getIndexOfChild(child));
	}

	@Test
	public void testDynamicMove() {
		UIContext uic = createUIContext();
		AbstractWComponent parent1 = new MockContainer();
		AbstractWComponent parent2 = new MockContainer();
		AbstractWComponent child = new SimpleComponent();

		parent1.add(child);
		setActiveContext(uic);
		parent1.remove(child);
		parent2.add(child);

		Assert.assertEquals("Child component should have been moved to parent2", parent2, child.
				getParent());
	}

	@Test
	public void testDynamicChildOverride() {
		AbstractWComponent parent = new MockContainer();
		AbstractWComponent staticChild = new SimpleComponent();
		AbstractWComponent dynamicChild = new SimpleComponent();

		parent.add(staticChild);
		setActiveContext(createUIContext());
		parent.removeAll();
		parent.add(dynamicChild);

		Assert.assertEquals("Incorrect number of children", 1, parent.getChildCount());
		Assert.assertSame("Incorrect child component", dynamicChild, parent.getChildAt(0));
	}

	@Test
	public void testAttributes() {
		AbstractWComponent root = new MockContainer();
		AbstractWComponent comp = new SimpleComponent();
		root.add(comp);
		root.setLocked(true);

		setActiveContext(createUIContext());

		Assert.assertTrue("Component should be in default state by default", comp.isDefaultState());

		// Store some attributes
		comp.setAttribute("a1", "x");
		comp.setAttribute("a2", "y");
		comp.setAttribute("a3", "w");
		Assert.assertFalse("Should not be default state if attributes are set in context",
				comp.isDefaultState());

		// Check we can get the attributes back
		Assert.assertEquals("Incorrect value for attribute \"a1\"",
				"x", comp.getAttribute("a1"));
		Assert.assertEquals("Incorrect value for attribute \"a2\"",
				"y", comp.getAttribute("a2"));
		Assert.assertEquals("Incorrect value for attribute \"a3\"",
				"w", comp.getAttribute("a3"));

		// Check removal of attribute
		comp.removeAttribute("a3");
		Assert.assertNull("Attribute should have been removed",
				comp.getAttribute("a3"));

		// Ensure attribute is isolated to individual component.
		// Root should not see attributes on a.
		Assert.assertNull("Parent should not see child's attributes",
				root.getAttribute("a1"));
		Assert.assertNull("Parent should not see child's attributes",
				root.getAttribute("a2"));

		// Set and get on the new root component
		root.setAttribute("r1", "z");
		Assert.assertEquals("Incorrect value for attribute \"r1\"",
				"z", root.getAttribute("r1"));

		// Old component should not see attribute on root
		Assert.assertNull("Child should not see parent's attributes",
				comp.getAttribute("r1"));

		// Resetting root should remove attributes
		root.reset();

		Assert.assertNull("Attribute should be removed after reset",
				root.getAttribute("r1"));
		Assert.assertTrue("Should be in default state after reset",
				root.isDefaultState());

		// Resetting root should also reset a.
		Assert.assertNull("Child's attributes should be removed after reset",
				comp.getAttribute("a1"));
		Assert.assertNull("Child's attributes should be removed after reset",
				comp.getAttribute("a2"));

		UIContextHolder.pushContext(null);
		UIContextHolder.popContext();

		Assert.assertTrue("Child should be in default state after reset",
				comp.isDefaultState());
	}

	@Test
	public void testLock() {
		AbstractWComponent parent = new MockContainer();
		AbstractWComponent child1 = new SimpleComponent();
		AbstractWComponent child2 = new SimpleComponent();
		parent.add(child1);
		parent.add(child2);
		child2.setVisible(false);

		Assert.assertFalse("Parent should not be locked", parent.isLocked());
		Assert.assertFalse("Child 1 should not be locked", child1.isLocked());
		Assert.assertFalse("Child 2 should not be locked", child2.isLocked());

		parent.setLocked(true);
		Assert.assertTrue("Parent should be locked", parent.isLocked());
		Assert.assertTrue("Child 1 should be locked", child1.isLocked());
		Assert.assertTrue("Child 2 should be locked", child2.isLocked());
	}

	@Test
	public void testVisibilityAndFlow() {
		//    a
		//   /|\
		//  b c d
		//  |
		//  e

		TestComp a = new TestComp("a");
		TestComp b = new TestComp("b");
		TestComp c = new TestComp("c");
		TestComp d = new TestComp("d");
		TestComp e = new TestComp("e");

		a.add(b);
		a.add(c);
		a.add(d);
		b.add(e);

		// c is invisible by default.
		c.setVisible(false);
		a.setLocked(true);

		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		// The standard lifecycle flow.
		a.serviceRequest(request);
		a.preparePaint(request);
		a.paint(new WebXmlRenderContext(printWriter));

		// Check that lifecycle methods were called as expected.
		Assert.assertEquals("Handle request should have been called on 'a'", 1, a.
				getHandleRequestCount());
		Assert.assertEquals("Handle request should have been called on 'b'", 1, b.
				getHandleRequestCount());
		Assert.assertEquals("Handle request should have been called on static invisible 'c'", 0, c.
				getHandleRequestCount());
		Assert.assertEquals("Handle request should have been calle on 'd'", 1, d.
				getHandleRequestCount());
		Assert.assertEquals("Handle request should have been calle on 'e'", 1, e.
				getHandleRequestCount());

		Assert.assertEquals("Prepare paint should have been called on 'a'", 1, a.
				getPreparePaintCount());
		Assert.assertEquals("Prepare paint should have been called on 'b'", 1, b.
				getPreparePaintCount());
		Assert.assertEquals("Prepare paint should have been called on static invisible 'c'", 0, c.
				getPreparePaintCount());
		Assert.assertEquals("Prepare paint should have been called on 'd'", 1, d.
				getPreparePaintCount());
		Assert.assertEquals("Prepare paint should have been called on 'e'", 1, e.
				getPreparePaintCount());

		Assert.assertEquals("Incorrect paint order", "abed", stringWriter.toString());

		// Reset the TestComp call flags.
		a.reset();

		// Make b invisible for user
		b.setVisible(false);

		// Create a new writer.
		stringWriter = new StringWriter();
		printWriter = new PrintWriter(stringWriter);

		a.serviceRequest(request);
		a.preparePaint(request);
		a.paint(new WebXmlRenderContext(printWriter));

		// Check that lifecycle methods were called as expected.
		Assert.assertEquals("Handle request should have been called on 'a'", 1, a.
				getHandleRequestCount());
		Assert.assertEquals("Handle request should have been called on dynamic invisible 'b'", 0, b.
				getHandleRequestCount());
		Assert.assertEquals("Handle request should have been called on static invisible 'c'", 0, c.
				getHandleRequestCount());
		Assert.assertEquals("Handle request should have been calle on 'd'", 1, d.
				getHandleRequestCount());
		Assert.assertEquals(
				"Handle request should have been calle on child 'e' of dynamic invisible 'b'", 0, e.
				getHandleRequestCount());

		Assert.assertEquals("Prepare paint should have been called on 'a'", 1, a.
				getPreparePaintCount());
		Assert.assertEquals("Prepare paint should have been called on dynamic invisible 'b'", 0, b.
				getPreparePaintCount());
		Assert.assertEquals("Prepare paint should have been called on static invisible 'c'", 0, c.
				getPreparePaintCount());
		Assert.assertEquals("Prepare paint should have been called on 'd'", 1, d.
				getPreparePaintCount());
		Assert.assertEquals(
				"Prepare paint should have been called on child 'e' of dynamic invisible 'b'", 0, e.
				getPreparePaintCount());

		Assert.assertEquals("Incorrect paint order", "ad", stringWriter.toString());
	}

	@Test
	public void testInitialisedAccessors() {
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);
		setActiveContext(createUIContext());

		comp.setInitialised(true);
		Assert.assertTrue("Should be initialised", comp.isInitialised());

		resetContext();
		Assert.assertFalse("Should not be initialised by default", comp.isInitialised());
	}

	@Test
	public void testTagAccessors() {
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);
		setActiveContext(createUIContext());

		String tag = "myTestTag";
		comp.setTag(tag);
		Assert.assertEquals("Dynamic tag incorrect", tag, comp.getTag());

		// Session tag should not affect default tag
		resetContext();
		Assert.assertNull("Default tag incorrect", comp.getTag());
	}

	@Test
	public void testToolTipAccessors() {
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);
		setActiveContext(createUIContext());
		String title = "my test title";

		comp.setToolTip(title);
		Assert.assertEquals("Dynamic title incorrect", title, comp.getToolTip());

		resetContext();
		Assert.assertNull("Default tooltip incorrect", comp.getToolTip());
	}

	@Test
	public void testVisibility() {
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);
		setActiveContext(createUIContext());

		// Test session visibility
		comp.setVisible(false);
		Assert.assertFalse("Should be in visible", comp.isVisible());

		resetContext();
		Assert.assertTrue("Should be visible by default", comp.isVisible());
	}

	@Test
	public void testForward() {
		String testUrl = "www.invalid";

		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);

		setActiveContext(createUIContext());
		comp.forward(testUrl);

		try {
			comp.invokeLaters();
			Assert.fail("forward when run should throw a forward exception");
		} catch (ForwardException e) {
			Assert.assertEquals("Incorrect forward URL", testUrl, e.getForwardTo());
		}
	}

	@Test
	public void testSetTemplate() {
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);
		setActiveContext(createUIContext());
		String testUrl = "/test.vm";

		setActiveContext(createUIContext());
		comp.setTemplate(testUrl);
		Assert.assertEquals("Incorrect template url", testUrl, comp.getTemplate());

		resetContext();
		Assert.assertNull("Default template url should be null", comp.getTemplate());
	}

	/**
	 * Test validate - where isVisible, isValidate and it not Disableable and there are validation errors.
	 */
	@Test
	public void testValidate() {
		AbstractWComponent comp = new MockContainer();
		comp.setLocked(true);
		setActiveContext(createUIContext());

		WTextField date = new WTextField();
		WField partialDateField = new WField("Text", date);
		partialDateField.addValidator(new MyValidator());
		comp.add(partialDateField);

		List<Diagnostic> diags = new ArrayList<>();
		comp.validate(diags);

		Assert.assertEquals("There be an error diagnostic", 1, diags.size());
	}

	/**
	 * test validate - where isInvisible.
	 */
	@Test
	public void testValidateIfIsInvisible() {
		AbstractWComponent comp = new MockContainer();
		comp.setLocked(true);
		setActiveContext(createUIContext());

		comp.setVisible(false); // skips validation

		WTextField date = new WTextField();
		WField partialDateField = new WField("Text", date);
		partialDateField.addValidator(new MyValidator());
		comp.add(partialDateField);

		List<Diagnostic> diags = new ArrayList<>();
		comp.validate(diags);

		Assert.assertEquals("There be no error diagnostic", 0, diags.size());
	}

	/**
	 * test validate - where isVisbile but isValidate returns false.
	 */
	@Test
	public void testValidateIfIsValidate() {
		AbstractWComponent comp = new MockContainer() {
			@Override
			protected void validateComponent(final List<Diagnostic> diags) {
				diags.add(createErrorDiagnostic("invalid"));
			}
		};

		comp.setLocked(true);
		setActiveContext(createUIContext());
		comp.setValidate(false); // stops validation

		List<Diagnostic> diags = new ArrayList<>();
		comp.validate(diags);

		Assert.assertEquals("There should be no error diagnostic", 0, diags.size());

		resetContext();
		comp.validate(diags);
		Assert.assertEquals("There should be an error diagnostic", 1, diags.size());
	}

	/**
	 * Test validate - where isDisableable and isDisabled.
	 */
	@Test
	public void testValidateIfIsDisableable() {
		// instanceOf Disableable and isDisabled implies abort validate()
		WTextField date = new WTextField();
		date.addValidator(new MyValidator());

		date.setLocked(true);
		setActiveContext(createUIContext());
		date.setDisabled(true);

		List<Diagnostic> diags = new ArrayList<>();
		date.validate(diags); // this calls validate(uic,diags) in WComponent parent class - the one being tested

		Assert.assertTrue("diags list should be empty", diags.isEmpty());

		resetContext();
		date.validate(diags);
		Assert.assertFalse("Diags list should not be empty", diags.isEmpty());
	}

	/**
	 * Test showErrorIndicators - component not visible.
	 */
	@Test
	public void testShowErrorIndicatorsIfNotIsVisible() {
		AbstractWComponent comp = new SimpleComponent();

		comp.setLocked(true);
		setActiveContext(createUIContext());
		comp.setVisible(false);

		List<Diagnostic> errors = new ArrayList<>();
		comp.showErrorIndicators(errors);

		Assert.assertTrue("errors list should be empty", errors.isEmpty());
	}

	/**
	 * test showWarningIndicators - component not visible.
	 */
	@Test
	public void testShowWarningIndicatorsIfNotIsVisible() {
		AbstractWComponent comp = new SimpleComponent();

		comp.setLocked(true);
		setActiveContext(createUIContext());
		comp.setVisible(false);

		List<Diagnostic> warnings = new ArrayList<>();
		comp.showWarningIndicators(warnings);

		Assert.assertTrue("warnings list should be empty", warnings.isEmpty());
	}

	/**
	 * Test setFocus - on itself.
	 */
	@Test
	public void testSetFocussed() {
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);

		UIContext uic = createUIContext();
		setActiveContext(uic);

		comp.setFocussed();
		Assert.assertEquals("comp itself should have the focus", comp, uic.getFocussed());
	}

	/**
	 * test hasNoComponentModel - when there isnt one.
	 */
	@Test
	public void testHasNoComponentModelWhenThereIsnt() {
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);

		UIContext uic = createUIContext();
		setActiveContext(uic);

		boolean result = comp.hasNoComponentModel(uic);
		Assert.assertTrue("should have no component model", result);
	}

	/**
	 * test hasNoComponentModel - when there is one.
	 */
	@Test
	public void testHasNoComponentModelsWhenThereIs() {
		AbstractWComponent comp = new MockContainer();
		comp.setLocked(true);

		UIContext uic = createUIContext();
		setActiveContext(uic);

		comp.setToolTip("test");

		boolean result = comp.hasNoComponentModel(uic);
		Assert.assertFalse("should have a component model", result);
	}

	/**
	 * test reset.
	 */
	@Test
	public void testReset() {
		UIContext uic1 = createUIContext();
		UIContext uic2 = createUIContext();

		AbstractWComponent comp = new MockContainer();
		WText text1 = new WText("XYZ");
		WText text2 = new WText("ABC");
		comp.setLocked(true);

		setActiveContext(uic1);
		comp.add(text1);

		setActiveContext(uic2);
		comp.add(text2);

		setActiveContext(uic1);
		Assert.assertFalse("should have a component model", comp.hasNoComponentModel(uic1));
		Assert.assertFalse("should have a component model", comp.hasNoComponentModel(uic1));
		comp.reset(); // do it for uic1 only

		Assert.assertTrue("uic1 should not have a component model", comp.hasNoComponentModel(uic1));
		Assert.assertFalse("uic2 should still have a component model", comp.
				hasNoComponentModel(uic2));
	}

	/**
	 * test tidyUpUIContextForTree.
	 */
	@Test
	public void testTidyUpUIContextForTree() {
		UIContext uic1 = createUIContext();
		UIContext uic2 = createUIContext();
		AbstractWComponent comp = new MockContainer();
		comp.setLocked(true);

		WText text1 = new WText("XYZ");
		WText text2 = new WText("ABC");

		setActiveContext(uic1);
		comp.add(text1);

		setActiveContext(uic2);
		comp.add(text2);

		Assert.assertFalse("should have a component model", comp.hasNoComponentModel(uic1));
		Assert.assertFalse("should have a component model", comp.hasNoComponentModel(uic2));

		setActiveContext(uic1);
		comp.remove(text1);
		comp.tidyUpUIContextForTree(); // do it for uic1 only

		setActiveContext(uic2);
		comp.tidyUpUIContextForTree(); // do it for uic2 only - but no effect since not in default state

		Assert.assertTrue("uic1 should not have a component model", comp.hasNoComponentModel(uic1));
		Assert.assertFalse("uic2 should still have a component model", comp.
				hasNoComponentModel(uic2));
	}

	/**
	 * test remove - child from shared list.
	 */
	@Test
	public void testRemoveCommon() {
		AbstractWComponent comp = new MockContainer();
		WText text1 = new WText("XYZ");
		WText text2 = new WText("ABC");
		comp.add(text1);
		comp.add(text2);

		comp.remove(text1); // from shared list

		Assert.assertNull("shared should not contain text1", text1.getParent());
		Assert.assertSame("shared should contain text2", text2, comp.getChildAt(0));
	}

	/**
	 * test remove - child from specific uic list.
	 */
	@Test
	public void testRemoveSpecific() {
		UIContext uic1 = createUIContext();
		UIContext uic2 = createUIContext();

		AbstractWComponent comp = new MockContainer();

		WText text1 = new WText("XYZ");
		WText text2 = new WText("ABC");
		comp.add(text1);
		comp.add(text2);
		comp.setLocked(true);

		// from uic2 list
		setActiveContext(uic2);
		comp.remove(text1);

		resetContext();
		Assert.assertSame("shared should contain text1", text1, comp.getChildAt(0));
		Assert.assertSame("shared should contain text2", text2, comp.getChildAt(1));

		setActiveContext(uic1);
		Assert.assertSame("uic1 should contain text1", text1, comp.getChildAt(0));
		Assert.assertSame("uic1 should contain text2", text2, comp.getChildAt(1));

		setActiveContext(uic2);
		Assert.assertNull("uic2 should not contain text1", text1.getParent());
		Assert.assertSame("uic2 should contain text2", text2, comp.getChildAt(0));
	}

	/**
	 * test removeAll - child from shared list.
	 */
	@Test
	public void testRemoveAllCommon() {
		AbstractWComponent comp = new MockContainer();

		WText text1 = new WText("XYZ");
		WText text2 = new WText("ABC");
		comp.add(text1);
		comp.add(text2);

		comp.removeAll(); // from shared list

		Assert.assertEquals("shared should not contain any children", 0, comp.getChildCount());
		Assert.assertEquals("uic should not contain any children", 0, comp.getChildCount());
	}

	/**
	 * test removeAll - child from specific uic list.
	 */
	@Test
	public void testRemoveAllSpecific() {
		AbstractWComponent comp = new MockContainer();
		WText text1 = new WText("XYZ");
		WText text2 = new WText("ABC");
		comp.add(text1);
		comp.add(text2);
		comp.setLocked(true);

		UIContext uic1 = createUIContext();
		UIContext uic2 = createUIContext();

		setActiveContext(uic2);
		comp.removeAll(); // from uic2 list
		resetContext();

		Assert.assertSame("shared should contain text1", text1, comp.getChildAt(0));
		Assert.assertSame("shared should contain text2", text2, comp.getChildAt(1));

		setActiveContext(uic1);
		Assert.assertSame("uic1 should contain text1", text1, comp.getChildAt(0));
		Assert.assertSame("uic1 should contain text2", text2, comp.getChildAt(1));

		setActiveContext(uic2);
		Assert.assertEquals("uic2 should not contain any children", 0, comp.getChildCount());
	}

	@Test
	public void testSetEnvironment() {
		AbstractWComponent comp = new SimpleComponent();
		setActiveContext(createUIContext());

		Environment environment = new MockWEnvironment();
		comp.setEnvironment(environment);

		Assert.assertEquals("should return environment set", environment, comp.getEnvironment());
	}

	@Test
	public void testGetBaseUrl() {
		AbstractWComponent comp = new SimpleComponent();
		String testUrl = "www.invalid";

		setActiveContext(createUIContext());

		// no environment
		Assert.assertEquals("should be unknown", "unknown", comp.getBaseUrl());

		// environment - but not baseUrl
		Environment environment = new MockWEnvironment();
		comp.setEnvironment(environment);
		Assert.assertEquals("should be empty string", "", comp.getBaseUrl());

		// environment with baseUrl
		MockWEnvironment environment2 = new MockWEnvironment();
		environment2.setBaseUrl(testUrl);
		comp.setEnvironment(environment2);
		Assert.assertEquals("should be url assigned", testUrl, comp.getBaseUrl());
	}

	@Test
	public void testRemoveAttribute() {
		final String key1 = "ABC";
		final String testData1 = "ABCDEFGHJ";

		AbstractWComponent comp = new SimpleComponent();
		setActiveContext(createUIContext());

		comp.setAttribute(key1, testData1);
		Assert.assertEquals("should return testData1", testData1, comp.getAttribute(key1));

		Serializable result = comp.removeAttribute(key1);
		Assert.assertEquals("should find and remove testData1", testData1, result);

		Assert.assertNull("should not return the removed testData1", comp.getAttribute(key1));
	}

	/**
	 * Test removeAttribute - where there is no model.
	 */
	@Test
	public void testRemoveAttributeSharedModel() {
		final String key1 = "ABC";
		final String testData1 = "ABCDEFGHJ";

		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);
		setActiveContext(createUIContext());

		comp.setAttribute(key1, testData1);
		Assert.assertEquals("should return testData1", testData1, comp.getAttribute(key1));

		comp.removeComponentModel();

		Serializable result = comp.removeAttribute(key1);
		Assert.assertNull("should return null - shared model", result);

		Assert.assertNull("should not return testData1 - no model", comp.getAttribute(key1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidIdNull() {
		new SimpleComponent().setIdName(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidIdEmpty() {
		new SimpleComponent().setIdName("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidIdStartReserved() {
		new SimpleComponent().setIdName("_");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidIdStartDigit() {
		new SimpleComponent().setIdName("1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidIdContainInvalidCharacterDot() {
		new SimpleComponent().setIdName("A.");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidIdContainInvalidCharacterDash() {
		new SimpleComponent().setIdName("A-");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidIdContainSpace() {
		new SimpleComponent().setIdName("A A");
	}

	@Test
	public void testValidId() {
		new SimpleComponent().setIdName("A_09azAZ");
		new SimpleComponent().setIdName(
				"abcdefghijhklmnopqrstuvwxyz_0123456789_ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}

	@Test
	public void testIdNameAccessors() {
		assertAccessorsCorrect(new SimpleComponent(), "idName", null, "A", "B");
	}

	@Test
	public void testGetId() {
		// No Parent
		AbstractWComponent component = new SimpleComponent();
		Assert.assertEquals("Incorrect ID with no parent", WComponent.DEFAULT_NO_ID, component.
				getId());

		// With parent
		WContainer parent = new WContainer();
		parent.add(component);
		Assert.assertEquals("Incorrect ID for with parent", WComponent.DEFAULT_NO_ID + "0",
				component.getId());
	}

	@Test
	public void testGetIdInTree() {
		WContainer root = new WContainer();
		Assert.assertEquals("Incorrect defualt id", WComponent.DEFAULT_NO_ID, root.getId());

		// Child
		WContainer child = new WContainer();
		root.add(child);
		Assert.assertEquals("Incorrect id for child", WComponent.DEFAULT_NO_ID + "0", child.getId());

		// Grand child
		WContainer grandchild = new WContainer();
		child.add(grandchild);
		Assert.assertEquals("Incorrect id for grand child", WComponent.DEFAULT_NO_ID + "0a",
				grandchild.getId());
	}

	@Test
	public void testGetIdInNamingContext() {
		WNamingContext root = new WNamingContext("TEST");
		Assert.assertEquals("Incorrect context id", "TEST", root.getId());

		String prefix = "TEST" + WComponent.ID_CONTEXT_SEPERATOR + WComponent.ID_FRAMEWORK_ASSIGNED_SEPERATOR;

		// Child
		WContainer child = new WContainer();
		root.add(child);
		Assert.assertEquals("Incorrect id for child in context", prefix + "0", child.getId());

		// Grand child
		WContainer grandchild = new WContainer();
		child.add(grandchild);
		Assert.assertEquals("Incorrect id for grand child in context", prefix + "0a", grandchild.
				getId());
	}

	@Test
	public void testGetIdInNamingContextWithIdNames() {
		WNamingContext root = new WNamingContext("TEST");
		Assert.assertEquals("Incorrect context id", "TEST", root.getId());

		String prefix = root.getId() + WComponent.ID_CONTEXT_SEPERATOR;

		// Child
		WContainer child = new WContainer();
		child.setIdName("CHILD");
		root.add(child);
		Assert.assertEquals("Incorrect id for child in context with idname", prefix + "CHILD",
				child.getId());

		// Grand child
		WContainer grandchild = new WContainer();
		child.add(grandchild);
		// No id name
		Assert.assertEquals("Incorrect id for grand child in context with no idname",
				child.getId() + WComponent.ID_FRAMEWORK_ASSIGNED_SEPERATOR + "0", grandchild.getId());

		// Grand child with ID
		grandchild.setIdName("GRAND");
		Assert.assertEquals("Incorrect id for grand child in context with idname", prefix + "GRAND",
				grandchild.getId());
	}

	@Test(expected = SystemException.class)
	public void testDuplicateId() {
		WNamingContext root = new WNamingContext("TEST");
		Assert.assertEquals("Incorrect context id", "TEST", root.getId());

		SimpleComponent component = new SimpleComponent();
		component.setIdName("X");
		root.add(component);

		component = new SimpleComponent();
		component.setIdName("X");
		root.add(component);

		setActiveContext(new UIContextImpl());

		PrintWriter writer = new XmlStringBuilder(new StringWriter());
		root.preparePaint(new MockRequest());
		root.paint(new WebXmlRenderContext(writer));
	}

	@Test
	public void testInternalId() {
		// No Parent
		AbstractWComponent component = new SimpleComponent();
		Assert.assertEquals("Incorrect internal ID with no parent", WComponent.DEFAULT_INTERNAL_ID,
				component.getInternalId());

		// With parent
		WContainer parent = new WContainer();
		parent.add(component);
		Assert.assertEquals("Incorrect internal ID for with parent",
				WComponent.DEFAULT_INTERNAL_ID + "0", component.getInternalId());
	}

	@Test
	public void testInternalIdInTree() {
		WContainer root = new WContainer();
		Assert.assertEquals("Incorrect defualt internal id", WComponent.DEFAULT_INTERNAL_ID, root.
				getInternalId());

		// Child
		WContainer child = new WContainer();
		root.add(child);
		Assert.assertEquals("Incorrect internal id for child", WComponent.DEFAULT_INTERNAL_ID + "0",
				child.getInternalId());

		// Grand child
		WContainer grandchild = new WContainer();
		child.add(grandchild);
		Assert.assertEquals("Incorrect internal id for grand child",
				WComponent.DEFAULT_INTERNAL_ID + "0a", grandchild.getInternalId());
	}

	@Test
	public void testTrackingEnabledAccessors() {
		assertAccessorsCorrect(new SimpleComponent(), "trackingEnabled", false, true, false);
	}

	@Test
	public void testTrack() {
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);
		setActiveContext(createUIContext());

		// Default should be not track
		Assert.assertFalse("Tracking should be false by default", comp.isTracking());

		// Set track true but should still be false as no id name set
		comp.setTrackingEnabled(true);
		Assert.assertFalse("Tracking should be false as no id name set", comp.isTracking());

		// Set id name
		comp.setIdName("X");
		Assert.assertTrue("Tracking should be true as id name set", comp.isTracking());

		// Set track false
		comp.setTrackingEnabled(false);
		Assert.assertFalse("Tracking should be false even though id name set", comp.isTracking());

		resetContext();
		Assert.assertFalse("Tracking should be false", comp.isTracking());
	}

	/** @deprecated */
	@Test
	public void testAccessibleTextAccessors() {
		//assertAccessorsCorrect(new SimpleComponent(), "accessibleText", null, "foo", "bar");
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);
		setActiveContext(createUIContext());
		String text = "my test text";

		comp.setAccessibleText(text);
		Assert.assertEquals("Dynamic accessible text incorrect", text, comp.getAccessibleText());

		resetContext();
		Assert.assertNull("Default accessible text incorrect", comp.getAccessibleText());
	}

	@Test
	public void testHtmlClassAccessors() {
		//assertAccessorsCorrect(new SimpleComponent(), "htmlClass", null, "foo", "bar");
		AbstractWComponent comp = new SimpleComponent();
		comp.setLocked(true);
		setActiveContext(createUIContext());
		String text = "my test text";

		comp.setHtmlClass(text);
		Assert.assertEquals("Dynamic accessible text incorrect", text, comp.getHtmlClass());

		resetContext();
		Assert.assertNull("Default accessible text incorrect", comp.getHtmlClass());
	}

	/**
	 * A test component which records if certain methods have been called.
	 */
	public static class TestComp extends MockContainer {

		/**
		 * @param content the test content
		 */
		public TestComp(final String content) {
			WText text = new WText(content);
			text.setEncodeText(false);
			add(text);
		}
	}

	/**
	 * A trivial WComponent implementation.
	 */
	private static class SimpleComponent extends AbstractWComponent {
	}

	/**
	 * Test Validator used for testing validation logic.
	 */
	private static class MyValidator extends AbstractFieldValidator {

		@Override
		protected boolean isValid() {
			return false;
		}
	}
}
