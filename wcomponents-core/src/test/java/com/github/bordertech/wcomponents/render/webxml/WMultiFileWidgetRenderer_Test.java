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

		assertXpathEvaluatesTo(fileUpload.getId(), "//ui:multifileupload/@id", fileUpload);
		assertXpathEvaluatesTo("10240000", "//ui:multifileupload/@maxFileSize", fileUpload);

		assertXpathNotExists("//ui:multifileupload/@disabled", fileUpload);
		assertXpathNotExists("//ui:multifileupload/@hidden", fileUpload);
		assertXpathNotExists("//ui:multifileupload/@required", fileUpload);
		assertXpathNotExists("//ui:multifileupload/@readOnly", fileUpload);
		assertXpathNotExists("//ui:multifileupload/@toolTip", fileUpload);
		assertXpathNotExists("//ui:multifileupload/@accessibleText", fileUpload);
		assertXpathNotExists("//ui:multifileupload/@acceptedMimeTypes", fileUpload);
		assertXpathNotExists("//ui:multifileupload/@maxFiles", fileUpload);
		assertXpathNotExists("//ui:multifileupload/ui:file", fileUpload);

		fileUpload.setDisabled(true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:multifileupload/@disabled", fileUpload);

		setFlag(fileUpload, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:multifileupload/@hidden", fileUpload);

		fileUpload.setMandatory(true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:multifileupload/@required", fileUpload);

		fileUpload.setToolTip("tooltip");
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo(fileUpload.getToolTip(), "//ui:multifileupload/@toolTip", fileUpload);

		fileUpload.setAccessibleText("accessible");
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo(fileUpload.getAccessibleText(), "//ui:multifileupload/@accessibleText",
				fileUpload);

		fileUpload.setFileTypes(new String[]{"a/b", "c/d"});
		assertXpathEvaluatesTo("a/b,c/d", "//ui:multifileupload/@acceptedMimeTypes", fileUpload);

		fileUpload.setMaxFileSize(12345);
		assertXpathEvaluatesTo("12345", "//ui:multifileupload/@maxFileSize", fileUpload);

		fileUpload.setMaxFiles(11);
		assertXpathEvaluatesTo("11", "//ui:multifileupload/@maxFiles", fileUpload);

		// Test file rendering
		MockFileItem fileItem = new MockFileItem();
		fileItem.setName("test.bin");
		fileItem.setContentType("application/octet-stream");
		fileItem.set(new byte[123]);

		FileWidgetUpload file = new FileWidgetUpload("X", new FileItemWrap(fileItem));

		fileUpload.setData(Arrays.asList(file));
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("1", "count(//ui:multifileupload/ui:file)", fileUpload);
		assertXpathEvaluatesTo("X", "//ui:multifileupload/ui:file/@id", fileUpload);
		assertXpathEvaluatesTo(fileItem.getName(), "//ui:multifileupload/ui:file/@name", fileUpload);
		assertXpathEvaluatesTo(fileItem.getContentType(), "//ui:multifileupload/ui:file/@type",
				fileUpload);
		assertXpathEvaluatesTo(String.valueOf(fileItem.getSize()), "//ui:multifileupload/ui:file/@size",
				fileUpload);
	}

	@Test
	public void testReadOnly() throws IOException, SAXException, XpathException {
		WMultiFileWidget fileUpload = new WMultiFileWidget();
		fileUpload.setReadOnly(true);
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("true", "//ui:multifileupload/@readOnly", fileUpload);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WMultiFileWidget fileUpload = new WMultiFileWidget();
		fileUpload.setFileTypes(new String[]{getMaliciousAttribute("ui:multifileupload")});

		assertSafeContent(fileUpload);

		fileUpload.setToolTip(getMaliciousAttribute("ui:multifileupload"));
		assertSafeContent(fileUpload);

		fileUpload.setAccessibleText(getMaliciousAttribute("ui:multifileupload"));
		assertSafeContent(fileUpload);
	}
}
