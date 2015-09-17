package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.AjaxOperation;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.DefaultWComponent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WSection;
import com.github.bordertech.wcomponents.WSection.SectionMode;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WSectionRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSectionRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * The heading to use when testing.
	 */
	private static final String SECTION_HEADING = "WSectionRenderer_Test.heading";

	/**
	 * The content to use when testing.
	 */
	private static final String SECTION_CONTENT = "WSectionRenderer_Test.content";

	@Test
	public void testRendererCorrectlyConfigured() {
		WSection section = new WSection("");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(section) instanceof WSectionRenderer);
	}

	@Test
	public void testRenderedLazyMode() throws IOException, SAXException, XpathException {
		WSection section = new WSection(SECTION_HEADING);
		section.setMode(SectionMode.LAZY);
		section.getContent().add(new WText(SECTION_CONTENT));

		// Content NOT Hidden
		assertSchemaMatch(section);
		// If not hidden, then the section's content should be rendered
		assertXpathEvaluatesTo("", "//ui:section/@type", section);
		assertXpathEvaluatesTo("", "//ui:section/@hidden", section);
		assertXpathEvaluatesTo("lazy", "//ui:section/@mode", section);
		assertXpathEvaluatesTo(SECTION_CONTENT, "normalize-space(//ui:section/ui:panel)", section);

		// Content Hidden
		// Create User Context with UI component
		UIContext uic = createUIContext();
		uic.setUI(new DefaultWComponent());
		setActiveContext(uic);
		setFlag(section, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(section);
		// If hidden, then the section's content should NOT be rendered
		assertXpathEvaluatesTo("", "//ui:section/@type", section);
		assertXpathEvaluatesTo("true", "//ui:section/@hidden", section);
		assertXpathEvaluatesTo("lazy", "//ui:section/@mode", section);
		assertXpathEvaluatesTo("", "normalize-space(//ui:section/ui:panel)", section);
	}

	@Test
	public void testRenderedEagerMode() throws IOException, SAXException, XpathException {
		WSection section = new WSection(SECTION_HEADING);
		section.setMode(SectionMode.EAGER);
		section.getContent().add(new WText(SECTION_CONTENT));

		UIContext uic = createUIContext();
		uic.setUI(new DefaultWComponent());
		setActiveContext(uic);

		// The section's content should NOT be rendered
		assertSchemaMatch(section);
		assertXpathEvaluatesTo("", "//ui:section/@type", section);
		assertXpathEvaluatesTo("", "//ui:section/@hidden", section);
		assertXpathEvaluatesTo("eager", "//ui:section/@mode", section);
		assertXpathEvaluatesTo("", "normalize-space(//ui:section/ui:panel)", section);

		try {
			// Section is the AJAX Trigger, content should be rendered
			AjaxOperation operation = new AjaxOperation(section.getId(), section.getId());
			AjaxHelper.setCurrentOperationDetails(operation, null);
			assertSchemaMatch(section);
			assertXpathEvaluatesTo("", "//ui:section/@type", section);
			assertXpathEvaluatesTo("", "//ui:section/@hidden", section);
			assertXpathEvaluatesTo("eager", "//ui:section/@mode", section);
			assertXpathEvaluatesTo(SECTION_CONTENT, "normalize-space(//ui:section/ui:panel)",
					section);
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WSection section = new WSection(getMaliciousContent());
		section.getContent().add(new WText(getMaliciousContent()));
		assertSafeContent(section);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WSection section = new WSection("");
		assertXpathNotExists("//ui:section/ui:margin", section);

		Margin margin = new Margin(0);
		section.setMargin(margin);
		assertXpathNotExists("//ui:section/ui:margin", section);

		margin = new Margin(1);
		section.setMargin(margin);
		assertSchemaMatch(section);
		assertXpathEvaluatesTo("1", "//ui:section/ui:margin/@all", section);
		assertXpathEvaluatesTo("", "//ui:section/ui:margin/@north", section);
		assertXpathEvaluatesTo("", "//ui:section/ui:margin/@east", section);
		assertXpathEvaluatesTo("", "//ui:section/ui:margin/@south", section);
		assertXpathEvaluatesTo("", "//ui:section/ui:margin/@west", section);

		margin = new Margin(1, 2, 3, 4);
		section.setMargin(margin);
		assertSchemaMatch(section);
		assertXpathEvaluatesTo("", "//ui:section/ui:margin/@all", section);
		assertXpathEvaluatesTo("1", "//ui:section/ui:margin/@north", section);
		assertXpathEvaluatesTo("2", "//ui:section/ui:margin/@east", section);
		assertXpathEvaluatesTo("3", "//ui:section/ui:margin/@south", section);
		assertXpathEvaluatesTo("4", "//ui:section/ui:margin/@west", section);
	}

}
