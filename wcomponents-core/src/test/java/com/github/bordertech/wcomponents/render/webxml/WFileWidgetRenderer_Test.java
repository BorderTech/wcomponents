package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WFileWidget;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WFileWidgetRenderer}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFileWidgetRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WFileWidget fileUpload = new WFileWidget();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(fileUpload) instanceof WFileWidgetRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WFileWidget fileUpload = new WFileWidget();

		assertSchemaMatch(fileUpload);

		assertXpathEvaluatesTo(fileUpload.getId(), "//ui:fileUpload/@id", fileUpload);
		assertXpathEvaluatesTo("1", "//ui:fileUpload/@maxFiles", fileUpload);

		assertXpathNotExists("//ui:fileUpload/@disabled", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@hidden", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@required", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@readOnly", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@toolTip", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@accessibleText", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@acceptedMimeTypes", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@maxFileSize", fileUpload);

		fileUpload.setDisabled(true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:fileUpload/@disabled", fileUpload);

		setFlag(fileUpload, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:fileUpload/@hidden", fileUpload);

		fileUpload.setMandatory(true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:fileUpload/@required", fileUpload);

		fileUpload.setReadOnly(true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:fileUpload/@readOnly", fileUpload);

		fileUpload.setToolTip("tooltip");
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo(fileUpload.getToolTip(), "//ui:fileUpload/@toolTip", fileUpload);

		fileUpload.setAccessibleText("accessible");
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo(fileUpload.getAccessibleText(), "//ui:fileUpload/@accessibleText",
				fileUpload);

		fileUpload.setFileTypes(new String[]{"a/b", "c/d"});
		assertXpathEvaluatesTo("a/b,c/d", "//ui:fileUpload/@acceptedMimeTypes", fileUpload);

		fileUpload.setMaxFileSize(12345);
		assertXpathEvaluatesTo("12345", "//ui:fileUpload/@maxFileSize", fileUpload);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WFileWidget fileUpload = new WFileWidget();
		fileUpload.setFileTypes(new String[]{getMaliciousAttribute("ui:fileUpload")});

		assertSafeContent(fileUpload);

		fileUpload.setToolTip(getMaliciousAttribute("ui:fileUpload"));
		assertSafeContent(fileUpload);

		fileUpload.setAccessibleText(getMaliciousAttribute("ui:fileUpload"));
		assertSafeContent(fileUpload);
	}
}
