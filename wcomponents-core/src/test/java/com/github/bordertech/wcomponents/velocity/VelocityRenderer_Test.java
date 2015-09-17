package com.github.bordertech.wcomponents.velocity;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.render.webxml.VelocityRenderer;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

/**
 * VelocityRenderer_Test - JUnit tests for {@link VelocityRenderer}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class VelocityRenderer_Test extends AbstractWComponentTestCase {

	@After
	public void resetConfig() {
		Config.reset();
	}

	@Test
	public void testGetUrl() {
		String layoutUrl = "com/github/bordertech/wcomponents/velocity/VelocityRenderer_Test.vm";
		VelocityRenderer layout = new VelocityRenderer(layoutUrl);
		Assert.assertEquals("Incorrect url", layoutUrl, layout.getUrl());
	}

	@Test
	public void testPaint() {
		TestComponent component = new TestComponent();
		VelocityRenderer layout = new VelocityRenderer(
				"com/github/bordertech/wcomponents/velocity/VelocityRenderer_Test.vm");
		setActiveContext(createUIContext());

		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		layout.render(component, new WebXmlRenderContext(printWriter));
		printWriter.close();

		String result = writer.toString();
		Assert.assertTrue("Missing 'this'", result.contains(
				"this=com.github.bordertech.wcomponents.velocity.VelocityRenderer_Test$TestComponent"));
		Assert.assertTrue("Missing 'this.someProperty'", result.contains(
				"[this.someProperty=somePropertyValue]"));
		Assert.assertTrue("Missing 'uicontext'", result.contains(
				"[uicontext=com.github.bordertech.wcomponents.UIContextImpl"));
		Assert.assertTrue("Missing 'uic'", result.contains(
				"uicontext=com.github.bordertech.wcomponents.UIContextImpl"));
		Assert.assertTrue("Missing 'children'", result.contains("[children=[A, B, C, D]]"));
		Assert.assertTrue("Missing 'childA'", result.contains("[childA=A]"));
		Assert.assertTrue("Missing 'childB'", result.contains("[childB=B]"));
		Assert.assertTrue("Missing 'childC'", result.contains("[childC=]"));
		Assert.assertTrue("Missing 'test_list'", result.contains("[test_list=[C, D]]"));
		Assert.assertTrue("Missing velocity map params", result.contains(
				"[velocityMapParam=velocityMapParamValue]"));
		Assert.assertTrue("Map should have been used", component.mapUsedCalled);
		Assert.assertFalse("Should not contain debug markers", result.contains("<!-- Start"));

		Config.getInstance().setProperty("bordertech.wcomponents.velocity.debugLayout", "true");

		writer = new StringWriter();
		printWriter = new PrintWriter(writer);
		layout.render(component, new WebXmlRenderContext(printWriter));
		printWriter.close();

		result = writer.toString();
		int startIndex = result.indexOf("<!-- Start " + layout.getUrl() + " -->");
		int endIndex = result.indexOf("<!-- End   " + layout.getUrl() + " -->");
		Assert.assertTrue("Should contain start debug marker", startIndex != -1);
		Assert.assertTrue("Should contain end debug marker", endIndex != -1);
		Assert.assertTrue("Start debug marker should be before end debug marker",
				startIndex < endIndex);
	}

	@Test
	public void testPaintMissingTemplate() {
		TestComponent component = new TestComponent();
		VelocityRenderer layout = new VelocityRenderer(
				"/com/github/bordertech/wcomponents/velocity/VelocityRenderer_Test_missing_template.vm");

		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		layout.render(component, new WebXmlRenderContext(printWriter));
		printWriter.close();

		// This should still render, and just dump out the contents of the Velocity context.
		String result = writer.toString();
		Assert.assertTrue("Should have rendered output", result.contains("<td>this</td>"));
		Assert.assertTrue("Should have rendered output", result.contains("<td>" + component.
				getClass().getName() + "</td>"));
	}

	/**
	 * A test WComponent to render using the VelocityRenderer.
	 */
	public static final class TestComponent extends WContainer implements VelocityProperties {

		/**
		 * A child component.
		 */
		private final WText childA = new WText("A");

		/**
		 * Another child component.
		 */
		private final WText childB = new WText("B");

		/**
		 * A child component to use in a list.
		 */
		private final WText childC = new WText("C");

		/**
		 * Another child component to use in a list.
		 */
		private final WText childD = new WText("D");

		/**
		 * Flag indicating whether the mapUsed method has been called.
		 */
		private boolean mapUsedCalled = false;

		/**
		 * Creates a test component.
		 */
		public TestComponent() {
			add(childA, "childA");
			add(childB, "childB");

			add(childC, "test_list");
			add(childD, "test_list");
		}

		/**
		 * @return a property for testing in the template.
		 */
		public String getSomeProperty() {
			return "somePropertyValue";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Map getVelocityMap() {
			Map<String, String> map = new HashMap<>(1);
			map.put("velocityMapParam", "velocityMapParamValue");
			return map;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mapUsed() {
			mapUsedCalled = true;
		}

		@Override
		public String toString() {
			return getClass().getName();
		}
	}
}
