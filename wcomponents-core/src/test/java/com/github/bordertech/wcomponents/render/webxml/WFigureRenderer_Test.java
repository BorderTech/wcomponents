package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.AjaxOperation;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.DefaultWComponent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WFigure;
import com.github.bordertech.wcomponents.WFigure.FigureMode;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WFigureRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFigureRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * The heading to use when testing.
	 */
	private static final String FIGURE_HEADING = "WFigureRenderer_Test.heading";

	/**
	 * The content to use when testing.
	 */
	private static final String FIGURE_CONTENT = "WFigureRenderer_Test.content";

	@Test
	public void testRendererCorrectlyConfigured() {
		WFigure figure = new WFigure(new WText(), "");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(figure) instanceof WFigureRenderer);
	}

	@Test
	public void testRenderedLazyMode() throws IOException, SAXException, XpathException {
		WFigure figure = new WFigure(new WText(FIGURE_CONTENT), FIGURE_HEADING);
		figure.setMode(FigureMode.LAZY);

		// Content NOT Hidden
		assertSchemaMatch(figure);
		// If not hidden, then the figure's content should be rendered
		assertXpathEvaluatesTo("", "//ui:figure/@type", figure);
		assertXpathEvaluatesTo("", "//ui:figure/@hidden", figure);
		assertXpathEvaluatesTo("lazy", "//ui:figure/@mode", figure);
		assertXpathEvaluatesTo(FIGURE_CONTENT, "//ui:figure/ui:content", figure);

		// Content Hidden
		// Create User Context with UI component
		UIContext uic = createUIContext();
		uic.setUI(new DefaultWComponent());
		setActiveContext(uic);
		setFlag(figure, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(figure);
		// If hidden, then the figure's content should NOT be rendered
		assertXpathEvaluatesTo("", "//ui:figure/@type", figure);
		assertXpathEvaluatesTo("true", "//ui:figure/@hidden", figure);
		assertXpathEvaluatesTo("lazy", "//ui:figure/@mode", figure);
		assertXpathEvaluatesTo("", "//ui:figure/ui:content", figure);
	}

	@Test
	public void testRenderedEagerMode() throws IOException, SAXException, XpathException {
		WFigure figure = new WFigure(new WText(FIGURE_CONTENT), FIGURE_HEADING);
		figure.setMode(FigureMode.EAGER);

		UIContext uic = createUIContext();
		uic.setUI(new DefaultWComponent());
		setActiveContext(uic);

		// The figure's content should NOT be rendered
		assertSchemaMatch(figure);
		assertXpathEvaluatesTo("", "//ui:figure/@type", figure);
		assertXpathEvaluatesTo("", "//ui:figure/@hidden", figure);
		assertXpathEvaluatesTo("eager", "//ui:figure/@mode", figure);
		assertXpathEvaluatesTo("", "//ui:figure/ui:content", figure);

		try {
			// Figure is the AJAX Trigger, content should be rendered
			AjaxOperation operation = new AjaxOperation(figure.getId(), figure.getId());
			AjaxHelper.setCurrentOperationDetails(operation, null);
			assertSchemaMatch(figure);
			assertXpathEvaluatesTo("", "//ui:figure/@type", figure);
			assertXpathEvaluatesTo("", "//ui:figure/@hidden", figure);
			assertXpathEvaluatesTo("eager", "//ui:figure/@mode", figure);
			assertXpathEvaluatesTo(FIGURE_CONTENT, "//ui:figure/ui:content", figure);
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WFigure figure = new WFigure(new WText(getMaliciousContent()), getMaliciousContent());
		assertSafeContent(figure);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WFigure figure = new WFigure(new WText(FIGURE_CONTENT), FIGURE_HEADING);
		assertXpathNotExists("//ui:figure/ui:margin", figure);

		Margin margin = new Margin(0);
		figure.setMargin(margin);
		assertXpathNotExists("//ui:figure/ui:margin", figure);

		margin = new Margin(1);
		figure.setMargin(margin);
		assertSchemaMatch(figure);
		assertXpathEvaluatesTo("1", "//ui:figure/ui:margin/@all", figure);
		assertXpathEvaluatesTo("", "//ui:figure/ui:margin/@north", figure);
		assertXpathEvaluatesTo("", "//ui:figure/ui:margin/@east", figure);
		assertXpathEvaluatesTo("", "//ui:figure/ui:margin/@south", figure);
		assertXpathEvaluatesTo("", "//ui:figure/ui:margin/@west", figure);

		margin = new Margin(1, 2, 3, 4);
		figure.setMargin(margin);
		assertSchemaMatch(figure);
		assertXpathEvaluatesTo("", "//ui:figure/ui:margin/@all", figure);
		assertXpathEvaluatesTo("1", "//ui:figure/ui:margin/@north", figure);
		assertXpathEvaluatesTo("2", "//ui:figure/ui:margin/@east", figure);
		assertXpathEvaluatesTo("3", "//ui:figure/ui:margin/@south", figure);
		assertXpathEvaluatesTo("4", "//ui:figure/ui:margin/@west", figure);
	}

}
