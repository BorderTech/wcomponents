package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WFilterControl;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WFilterControlRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFilterControlRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test label text.
	 */
	private static final String TEST_LABEL = "Test Label";

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WFilterControl filter = new WFilterControl(new WDecoratedLabel());
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(filter) instanceof WFilterControlRenderer);
	}

	@Test(expected = SystemException.class)
	public void testNoTargetException() throws IOException, SAXException, XpathException {
		WFilterControl filter = new WFilterControl(new WDecoratedLabel(TEST_LABEL));
		render(filter);
	}

	@Test
	public void testDoPaintBasic() throws IOException, SAXException, XpathException {
		WDecoratedLabel label = new WDecoratedLabel(TEST_LABEL);
		WTextField target = new WTextField();
		target.setText("TEXT");
		String testValue = "TEST";
		WFilterControl filter = new WFilterControl(label, target, testValue);

		WContainer test = new WContainer();
		test.add(filter);
		test.add(target);

		// Validate Schema
		assertSchemaMatch(test);
		// Check Attributes
		assertXpathEvaluatesTo(filter.getId(), "//ui:filterControl/@id", filter);
		assertXpathEvaluatesTo(target.getId(), "//ui:filterControl/@for", filter);
		assertXpathEvaluatesTo(testValue, "//ui:filterControl/@value", filter);
		assertXpathNotExists("//ui:filterControl[@active]", filter);
		assertXpathNotExists("//ui:filterControl[@hidden]", filter);
		// Check Label
		assertXpathExists("//ui:filterControl/ui:decoratedLabel", filter);

		// Test with Null Value
		filter.setValue(null);
		assertSchemaMatch(test);
		assertXpathEvaluatesTo("", "//ui:filterControl/@value", filter);

		// Test with Empty Value
		filter.setValue("");
		assertSchemaMatch(test);
		assertXpathEvaluatesTo("", "//ui:filterControl/@value", filter);
	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WDecoratedLabel label = new WDecoratedLabel(TEST_LABEL);
		WTextField target = new WTextField();
		target.setText("TEXT");
		String testValue = "TEST";
		WFilterControl filter = new WFilterControl(label, target, testValue);
		// Set Active
		filter.setActive(true);
		// Set Hidden
		setFlag(filter, ComponentModel.HIDE_FLAG, true);

		WContainer test = new WContainer();
		test.add(filter);
		test.add(target);

		// Validate Schema
		assertSchemaMatch(test);
		// Check Attributes
		assertXpathEvaluatesTo(filter.getId(), "//ui:filterControl/@id", filter);
		assertXpathEvaluatesTo(target.getId(), "//ui:filterControl/@for", filter);
		assertXpathEvaluatesTo(testValue, "//ui:filterControl/@value", filter);
		assertXpathExists("//ui:filterControl[@active='true']", filter);
		assertXpathExists("//ui:filterControl[@hidden='true']", filter);
		// Check Label
		assertXpathExists("//ui:filterControl/ui:decoratedLabel", filter);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WFilterControl filter = new WFilterControl(new WDecoratedLabel(TEST_LABEL), new WTextField(),
				getMaliciousAttribute("ui:filterControl"));
		filter.setActive(true);

		assertSafeContent(filter);
	}
}
