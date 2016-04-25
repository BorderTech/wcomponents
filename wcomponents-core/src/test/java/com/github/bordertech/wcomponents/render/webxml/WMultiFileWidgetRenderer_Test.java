package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WMultiFileWidget;
import com.github.bordertech.wcomponents.WMultiFileWidget.FileWidgetUpload;
import com.github.bordertech.wcomponents.file.FileItemWrap;
import com.github.bordertech.wcomponents.util.mock.MockFileItem;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WMultiFileWidgetRenderer}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiFileWidgetRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WMultiFileWidget fileUpload = new WMultiFileWidget();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(fileUpload) instanceof WMultiFileWidgetRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WMultiFileWidget fileUpload = new WMultiFileWidget();

		assertSchemaMatch(fileUpload);

		assertXpathEvaluatesTo(fileUpload.getId(), "//ui:fileupload/@id", fileUpload);
		assertXpathEvaluatesTo("10240000", "//ui:fileupload/@maxFileSize", fileUpload);

		assertXpathNotExists("//ui:fileupload/@disabled", fileUpload);
		assertXpathNotExists("//ui:fileupload/@hidden", fileUpload);
		assertXpathNotExists("//ui:fileupload/@required", fileUpload);
		assertXpathNotExists("//ui:fileupload/@readOnly", fileUpload);
		assertXpathNotExists("//ui:fileupload/@toolTip", fileUpload);
		assertXpathNotExists("//ui:fileupload/@accessibleText", fileUpload);
		assertXpathNotExists("//ui:fileupload/@acceptedMimeTypes", fileUpload);
		assertXpathNotExists("//ui:fileupload/@maxFiles", fileUpload);
		assertXpathNotExists("//ui:fileupload/ui:file", fileUpload);

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

		fileUpload.setMaxFiles(11);
		assertXpathEvaluatesTo("11", "//ui:fileupload/@maxFiles", fileUpload);

		// Test file rendering
		MockFileItem fileItem = new MockFileItem();
		fileItem.setName("test.bin");
		fileItem.setContentType("application/octet-stream");
		fileItem.set(new byte[123]);

		FileWidgetUpload file = new FileWidgetUpload("X", new FileItemWrap(fileItem));

		fileUpload.setData(Arrays.asList(file));
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("1", "count(//ui:fileupload/ui:file)", fileUpload);
		assertXpathEvaluatesTo("X", "//ui:fileupload/ui:file/@id", fileUpload);
		assertXpathEvaluatesTo(fileItem.getName(), "//ui:fileupload/ui:file/@name", fileUpload);
		assertXpathEvaluatesTo(fileItem.getContentType(), "//ui:fileupload/ui:file/@type",
				fileUpload);
		assertXpathEvaluatesTo(String.valueOf(fileItem.getSize()), "//ui:fileupload/ui:file/@size",
				fileUpload);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WMultiFileWidget fileUpload = new WMultiFileWidget();
		fileUpload.setFileTypes(new String[]{getMaliciousAttribute("ui:fileupload")});

		assertSafeContent(fileUpload);

		fileUpload.setToolTip(getMaliciousAttribute("ui:fileupload"));
		assertSafeContent(fileUpload);

		fileUpload.setAccessibleText(getMaliciousAttribute("ui:fileupload"));
		assertSafeContent(fileUpload);
	}
}
