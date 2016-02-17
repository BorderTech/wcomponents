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

		assertXpathEvaluatesTo(fileUpload.getId(), "//ui:fileupload/@id", fileUpload);
		assertXpathEvaluatesTo("1", "//ui:fileupload/@maxFiles", fileUpload);

		assertXpathNotExists("//ui:fileupload/@disabled", fileUpload);
		assertXpathNotExists("//ui:fileupload/@hidden", fileUpload);
		assertXpathNotExists("//ui:fileupload/@required", fileUpload);
		assertXpathNotExists("//ui:fileupload/@readOnly", fileUpload);
		assertXpathNotExists("//ui:fileupload/@toolTip", fileUpload);
		assertXpathNotExists("//ui:fileupload/@accessibleText", fileUpload);
		assertXpathNotExists("//ui:fileupload/@acceptedMimeTypes", fileUpload);
		assertXpathNotExists("//ui:fileupload/@maxFileSize", fileUpload);

		fileUpload.setDisabled(true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:fileupload/@disabled", fileUpload);

		setFlag(fileUpload, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:fileupload/@hidden", fileUpload);

		fileUpload.setMandatory(true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:fileupload/@required", fileUpload);

		fileUpload.setReadOnly(true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:fileupload/@readOnly", fileUpload);

		fileUpload.setToolTip("tooltip");
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo(fileUpload.getToolTip(), "//ui:fileupload/@toolTip", fileUpload);

		fileUpload.setAccessibleText("accessible");
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo(fileUpload.getAccessibleText(), "//ui:fileupload/@accessibleText",
				fileUpload);

		fileUpload.setFileTypes(new String[]{"a/b", "c/d"});
		assertXpathEvaluatesTo("a/b,c/d", "//ui:fileupload/@acceptedMimeTypes", fileUpload);

		fileUpload.setMaxFileSize(12345);
		assertXpathEvaluatesTo("12345", "//ui:fileupload/@maxFileSize", fileUpload);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WFileWidget fileUpload = new WFileWidget();
		fileUpload.setFileTypes(new String[]{getMaliciousAttribute("ui:fileupload")});

		assertSafeContent(fileUpload);

		fileUpload.setToolTip(getMaliciousAttribute("ui:fileupload"));
		assertSafeContent(fileUpload);

		fileUpload.setAccessibleText(getMaliciousAttribute("ui:fileupload"));
		assertSafeContent(fileUpload);
	}
}
