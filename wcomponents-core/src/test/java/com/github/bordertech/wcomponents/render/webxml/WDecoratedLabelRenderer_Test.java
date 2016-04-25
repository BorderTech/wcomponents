package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WDecoratedLabelRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WDecoratedLabelRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WDecoratedLabel decoratedLabel = new WDecoratedLabel();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(decoratedLabel) instanceof WDecoratedLabelRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		final String bodyText = "WDecoratedLabelRenderer_Test.testDoPaint.bodyText";
		final String headText = "WDecoratedLabelRenderer_Test.testDoPaint.headText";
		final String tailText = "WDecoratedLabelRenderer_Test.testDoPaint.tailText";

		// Test minimal text content
		WDecoratedLabel decoratedLabel = new WDecoratedLabel(bodyText);

		assertSchemaMatch(decoratedLabel);
		assertXpathEvaluatesTo(bodyText, "normalize-space(//ui:decoratedlabel/ui:labelbody)",
				decoratedLabel);
		assertXpathNotExists("//ui:decoratedlabel/@labelFocus", decoratedLabel);

		decoratedLabel.setHead(new WText(headText));
		decoratedLabel.setTail(new WText(tailText));

		// Test all text content
		assertSchemaMatch(decoratedLabel);
		assertXpathEvaluatesTo(headText, "normalize-space(//ui:decoratedlabel/ui:labelhead)",
				decoratedLabel);
		assertXpathEvaluatesTo(bodyText, "normalize-space(//ui:decoratedlabel/ui:labelbody)",
				decoratedLabel);
		assertXpathEvaluatesTo(tailText, "normalize-space(//ui:decoratedlabel/ui:labeltail)",
				decoratedLabel);
		assertXpathNotExists("//ui:decoratedlabel/@labelFocus", decoratedLabel);

		// Test complex content
		WContainer complexContent = new WContainer();
		complexContent.add(new WLabel("Select"));
		complexContent.add(new WCheckBox());
		decoratedLabel.setBody(complexContent);

		assertXpathExists("//ui:decoratedlabel/ui:labelbody/ui:label/following-sibling::ui:checkbox",
				decoratedLabel);
	}
}
