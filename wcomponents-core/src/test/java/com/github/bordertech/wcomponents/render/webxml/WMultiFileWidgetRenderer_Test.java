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

		assertXpathEvaluatesTo(fileUpload.getId(), "//ui:fileUpload/@id", fileUpload);
		assertXpathEvaluatesTo("10240000", "//ui:fileUpload/@maxFileSize", fileUpload);

		assertXpathNotExists("//ui:fileUpload/@disabled", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@hidden", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@required", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@readOnly", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@toolTip", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@accessibleText", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@acceptedMimeTypes", fileUpload);
		assertXpathNotExists("//ui:fileUpload/@maxFiles", fileUpload);
		assertXpathNotExists("//ui:fileUpload/ui:file", fileUpload);

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

		fileUpload.setMaxFiles(11);
		assertXpathEvaluatesTo("11", "//ui:fileUpload/@maxFiles", fileUpload);

		// Test file rendering
		MockFileItem fileItem = new MockFileItem();
		fileItem.setName("test.bin");
		fileItem.setContentType("application/octet-stream");
		fileItem.set(new byte[123]);

		FileWidgetUpload file = new FileWidgetUpload("X", new FileItemWrap(fileItem));

		fileUpload.setData(Arrays.asList(file));
		assertSchemaMatch(fileUpload);
		assertXpathEvaluatesTo("1", "count(//ui:fileUpload/ui:file)", fileUpload);
		assertXpathEvaluatesTo("X", "//ui:fileUpload/ui:file/@id", fileUpload);
		assertXpathEvaluatesTo(fileItem.getName(), "//ui:fileUpload/ui:file/@name", fileUpload);
		assertXpathEvaluatesTo(fileItem.getContentType(), "//ui:fileUpload/ui:file/@type",
				fileUpload);
		assertXpathEvaluatesTo(String.valueOf(fileItem.getSize()), "//ui:fileUpload/ui:file/@size",
				fileUpload);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WMultiFileWidget fileUpload = new WMultiFileWidget();
		fileUpload.setFileTypes(new String[]{getMaliciousAttribute("ui:fileUpload")});

		assertSafeContent(fileUpload);

		fileUpload.setToolTip(getMaliciousAttribute("ui:fileUpload"));
		assertSafeContent(fileUpload);

		fileUpload.setAccessibleText(getMaliciousAttribute("ui:fileUpload"));
		assertSafeContent(fileUpload);
	}
}
