package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.file.FileItemWrap;
import com.github.bordertech.wcomponents.util.FileUtil;
import com.github.bordertech.wcomponents.util.StreamUtil;
import com.github.bordertech.wcomponents.util.mock.MockFileItem;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.junit.Assert;
import org.junit.Test;

/**
 * WFileWidget_Test - unit test for {@link WFileWidget}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @author Aswin Kandula
 * @since 1.0.0
 */
public class WFileWidget_Test extends AbstractWComponentTestCase {

	/**
	 * Test file item 1.
	 */
	private static final FileItem TEST_FILE_ITEM = createFileItem("test1.bin", 10);
	/**
	 * Test file item 2.
	 */
	private static final FileItem TEST_FILE_ITEM2 = createFileItem("test2.bin", 20);

	/**
	 * Empty file item.
	 */
	private static final FileItem TEST_EMPTY_FILE_ITEM = createFileItem("", 0);

	/**
	 * File item wrap for file item 1.
	 */
	private static final FileItemWrap TEST_FILE_ITEM_WRAP = new FileItemWrap(TEST_FILE_ITEM);

	@Test
	public void testFileTypesAccessors() {
		assertAccessorsCorrect(new WFileWidget(), "fileTypes", Collections.EMPTY_LIST,
				Arrays.asList("text/plain", "image/gif"), Arrays.asList("image/jpeg"));
	}

	@Test
	public void testSetFileTypesAsArray() {
		final String[] types1 = new String[]{"text/plain", "image/gif"};

		// Set as array
		WFileWidget widget = new WFileWidget();
		widget.setFileTypes(types1);
		Assert.assertEquals("Incorrect file types returned", Arrays.asList(types1), widget.
				getFileTypes());
	}

	@Test
	public void testSetFileTypesAsNull() {
		WFileWidget widget = new WFileWidget();
		// Set types on widget
		widget.setFileTypes(Arrays.asList("image/jpeg"));

		// Set null array
		widget.setFileTypes((String[]) null);
		Assert.assertEquals("File types should be empty when set to null array",
				Collections.EMPTY_LIST,
				widget.getFileTypes());

		// Set types on widget
		widget.setFileTypes(Arrays.asList("image/jpeg"));

		// Set as null list
		widget.setFileTypes((List<String>) null);
		Assert.assertEquals("File types should be empty when set to null list",
				Collections.EMPTY_LIST,
				widget.getFileTypes());
	}

	@Test
	public void testMaxFileSizeAccessors() {
		assertAccessorsCorrect(new WFileWidget(), "maxFileSize", Long.valueOf(0), Long.valueOf(1),
				Long.valueOf(2));
	}

	@Test
	public void testDoHandleRequest() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());

		// Request - with file (changed)
		MockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM);
		boolean changed = widget.doHandleRequest(request);

		Assert.assertTrue("Request With File - Widget should have changed", changed);
		Assert.assertEquals("Request With File - Incorrect file item returned", TEST_FILE_ITEM.
				getName(), widget
				.getValue().getName());

		// Request - with same file (still change as any file uploaded is a change)
		request = setupFileUploadRequest(widget, TEST_FILE_ITEM);
		changed = widget.doHandleRequest(request);

		Assert
				.assertTrue(
						"Request With Same File - Widget should have changed as any file upload is considered a change",
						changed);
		Assert.assertEquals("Request With Same File - Incorrect file item returned", TEST_FILE_ITEM.
				getName(), widget
				.getValue().getName());

		// Request - with different file (change)
		request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
		changed = widget.doHandleRequest(request);

		Assert.assertTrue("Request With Different File - Widget should have changed", changed);
		Assert.assertEquals("Request With Different File - Incorrect file item returned",
				TEST_FILE_ITEM2.getName(),
				widget.getValue().getName());

		// Request - no file (change)
		request = setupFileUploadRequest(widget, TEST_EMPTY_FILE_ITEM);
		changed = widget.doHandleRequest(request);

		Assert.assertTrue("Request With Empty File - Widget should have changed", changed);
		Assert.assertNull("Request With Empty File - Incorrect file item returned", widget.
				getValue());

		// Request - no file (no change)
		request = setupFileUploadRequest(widget, TEST_EMPTY_FILE_ITEM);
		changed = widget.doHandleRequest(request);

		Assert.assertFalse("Request With Empty File - Widget should have not changed", changed);
		Assert.assertNull("Request With Empty File - Incorrect file item returned", widget.
				getValue());
	}

	@Test
	public void testGetActionCommand() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());

		Assert.assertNull("Action command should be null by default", widget.getActionCommand());

		// Set file item
		widget.setData(TEST_FILE_ITEM_WRAP);

		Assert.assertEquals("Action command should be the file name", TEST_FILE_ITEM.getName(),
				widget.getActionCommand());
	}

	@Test
	public void testGetRequestValue() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());

		// Set current file
		widget.setData(TEST_FILE_ITEM_WRAP);

		// Empty Request - should return current value
		FileUploadMockRequest request = new FileUploadMockRequest();
		Assert.assertEquals("Empty request should return the current value", TEST_FILE_ITEM_WRAP,
				widget.getRequestValue(request));

		// File on the request
		request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
		Assert.assertEquals("Request with a file item should return the file on the request",
				TEST_FILE_ITEM2.getName(), widget.getRequestValue(request).getName());

		// Empty File on the request
		request = setupFileUploadRequest(widget, TEST_EMPTY_FILE_ITEM);
		Assert.assertNull("Request with an empty file item should return null", widget.
				getRequestValue(request));
	}

	@Test
	public void testIsPresent() {
		WFileWidget widget = new WFileWidget();

		// Empty Request
		setActiveContext(createUIContext());
		FileUploadMockRequest request = new FileUploadMockRequest();
		Assert.assertFalse("IsPresent should return false", widget.isPresent(request));

		// File on the request
		request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
		Assert.assertTrue("IsPresent should return true", widget.isPresent(request));
	}

	@Test
	public void testGetBytes() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());

		Assert.assertNull("Bytes data should be null by default", widget.getBytes());

		// Set file on widget
		widget.setData(TEST_FILE_ITEM_WRAP);

		Assert.assertTrue("Incorrect bytes data", Arrays.equals(TEST_FILE_ITEM.get(), widget.
				getBytes()));
	}

	@Test
	public void testGetInputStream() throws IOException {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());

		Assert.assertNull("Stream data should be null by default", widget.getInputStream());

		// Set file on widget
		widget.setData(TEST_FILE_ITEM_WRAP);
		InputStream stream = widget.getInputStream();
		byte[] readBytes = StreamUtil.getBytes(stream);
		Assert.assertTrue("Incorrect stream data", Arrays.equals(TEST_FILE_ITEM.get(), readBytes));
	}

	@Test
	public void testGetSize() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());

		Assert.assertEquals("Size should be 0 by default", 0, widget.getSize());

		// Set file on widget
		widget.setData(TEST_FILE_ITEM_WRAP);
		Assert.assertEquals("Incorrect size returned", TEST_FILE_ITEM.getSize(), widget.getSize());
	}

	@Test
	public void testGetFileName() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());

		Assert.assertNull("File name should be null by default", widget.getFileName());

		// Set file on widget
		widget.setData(TEST_FILE_ITEM_WRAP);
		Assert.assertEquals("Incorrect file name returned", TEST_FILE_ITEM.getName(), widget.
				getFileName());
	}

	@Test
	public void testGetFile() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());

		Assert.assertNull("File should be null by default", widget.getFile());

		// Set file on widget
		widget.setData(TEST_FILE_ITEM_WRAP);
		Assert.assertEquals("Incorrect file item wrap returned", TEST_FILE_ITEM_WRAP, widget.
				getFile());
	}

	@Test
	public void testGetValue() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());

		Assert.assertNull("Value should be null by default", widget.getValue());

		// Set file on widget
		widget.setData(TEST_FILE_ITEM_WRAP);
		Assert.assertEquals("Incorrect file item wrap returned", TEST_FILE_ITEM_WRAP, widget.
				getValue());
	}
	
	@Test
	public void testValidateNoFile() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());
		
		// Empty File on the request
		MockRequest request = setupFileUploadRequest(widget, TEST_EMPTY_FILE_ITEM);
		boolean  changed = widget.doHandleRequest(request);
		
		Assert.assertEquals(widget.getFile(), null);
		Assert.assertEquals("No file uploaded", changed, false);
		Assert.assertNull("No file exists", widget.getFile());
	}

	@Test
	public void testValidateAnyFileTypeAndSize() throws IOException {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());
		
		// Set file on the request
		MockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM);
		boolean changed = widget.doHandleRequest(request);
		
		Assert.assertEquals("File uploaded, so file uploaded", changed, true);
		ArrayList<Diagnostic> diags = new ArrayList<Diagnostic>();
		widget.validate(diags);
		Assert.assertTrue("No validation messages exist", diags.size() == 0);
		Assert.assertTrue("No file type, then valid", widget.isFileTypeValid());
		Assert.assertTrue("No file size, then valid", widget.isFileSizeValid());
	}

	@Test
	public void testValidateValidFileType() throws IOException {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());
		widget.setFileTypes(Arrays.asList("image/gif"));
		
		// Set proper file on the request
		MockRequest request = new MockRequest();
		InputStream stream = getClass().getResourceAsStream("/image/x1.gif");
		byte[] bytes =  StreamUtil.getBytes(stream);
		request.setFileContents(widget.getId(), bytes);
		boolean changed = widget.doHandleRequest(request);
		
		Assert.assertEquals("File type valid, so file uploaded", changed, true);
		ArrayList<Diagnostic> diags = new ArrayList<Diagnostic>();
		widget.validate(diags);
		Assert.assertTrue("No validation messages exist", diags.size() == 0);
		Assert.assertTrue("File type valid", widget.isFileTypeValid());
		
		widget = new WFileWidget();
		setActiveContext(createUIContext());
		widget.setFileTypes(Arrays.asList("image/*"));
		
		request = new MockRequest();
		stream = getClass().getResourceAsStream("/image/x1.gif");
		bytes =  StreamUtil.getBytes(stream);
		request.setFileContents(widget.getId(), bytes);
		changed = widget.doHandleRequest(request);
		
		Assert.assertEquals("File type valid, so file uploaded", changed, true);
		widget.validate(diags);
		Assert.assertTrue("No validation messages exist", diags.size() == 0);
		Assert.assertTrue("File type valid", widget.isFileTypeValid());
	}

	@Test
	public void testValidateInvalidFileType() throws IOException {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());
		widget.setFileTypes(Arrays.asList("image/gif", "image/jpeg"));
		
		// Set file on the request
		MockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM);
		boolean changed = widget.doHandleRequest(request);
		
		Assert.assertEquals("File type invalid, no file uploaded", changed, false);
		ArrayList<Diagnostic> diags = new ArrayList<Diagnostic>();
		widget.validate(diags);
		Assert.assertTrue("File type invalid, so message returned", diags.size() == 1);
		Assert.assertFalse("File type invalid", widget.isFileTypeValid());
		String invalidMessage = FileUtil.getInvalidFileTypeMessage(widget.getFileTypes());
		Assert.assertEquals("Invalid file size message", diags.get(0).getDescription(), invalidMessage);
		
		// Try same request again, make sure duplicate messages are not returned
		changed = widget.doHandleRequest(request);
		Assert.assertEquals("File type invalid, no file uploaded", changed, false);
		diags.clear();
		widget.validate(diags);
		Assert.assertTrue("File type invalid, so message returned", diags.size() == 1);
		Assert.assertFalse("File type invalid", widget.isFileTypeValid());
	}
	
	@Test
	public void testValidateFileSize() throws IOException {
		WFileWidget widget = new WFileWidget();
		widget.setIdName("widgetId");
		setActiveContext(createUIContext());
		int maxSize = 10;
		widget.setMaxFileSize(maxSize);
		
		// Set file on the request
		MockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM);
		boolean changed = widget.doHandleRequest(request);
		
		Assert.assertEquals("File size valid, so file uploaded", changed, true);
		ArrayList<Diagnostic> diags = new ArrayList<Diagnostic>();
		widget.validate(diags);
		Assert.assertTrue("File size valid, so no messages returned", diags.size() == 0);
		Assert.assertTrue("File size valid", widget.isFileSizeValid());
		
		// Set file on the request
		request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
		changed = widget.doHandleRequest(request);
		
		Assert.assertEquals("File size invalid, but original file exists", changed, true);
		widget.validate(diags);
		Assert.assertTrue("File size invalid, so message returned", diags.size() == 1);
		Assert.assertFalse("File size invalid", widget.isFileSizeValid());
		String invalidMessage = FileUtil.getInvalidFileSizeMessage(maxSize);
		Assert.assertEquals("Invalid file size message", diags.get(0).getDescription(), invalidMessage);
	}
	
	@Test
	public void testInvalidFileTypeAndSize() throws IOException {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());
		widget.setFileTypes(Arrays.asList("image/gif", "image/jpeg"));
		widget.setMaxFileSize(10);

		MockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
		boolean changed = widget.doHandleRequest(request);
		
		Assert.assertEquals("File invalid, no file uploaded", changed, false);
		ArrayList<Diagnostic> diags = new ArrayList<Diagnostic>();
		widget.validate(diags);
		Assert.assertTrue("Both file type and size invalid", diags.size() == 2);
		Assert.assertFalse(widget.isFileTypeValid());
		Assert.assertFalse(widget.isFileSizeValid());
		
		// Try same request again, make sure duplicate messages are not returned
		changed = widget.doHandleRequest(request);
		Assert.assertEquals("File invalid, no file uploaded", changed, false);
		diags.clear();
		widget.validate(diags);
		Assert.assertTrue(diags.size() == 2);
		Assert.assertFalse(widget.isFileTypeValid());
		Assert.assertFalse(widget.isFileSizeValid());
		
		widget.setFileTypes(Collections.EMPTY_LIST);
		request = setupFileUploadRequest(widget, TEST_FILE_ITEM);
		changed = widget.doHandleRequest(request);
		Assert.assertEquals("File valid", changed, true);
		Assert.assertTrue(widget.isFileTypeValid());
		Assert.assertTrue(widget.isFileSizeValid());
	}
	
	@Test
	public void testFileTypeInvalidThenValid() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());
		widget.setFileTypes(Arrays.asList("image/gif", "image/jpeg"));
		MockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
		boolean changed = widget.doHandleRequest(request);
		Assert.assertEquals("File invalid, no file uploaded", changed, false);
		Assert.assertFalse(widget.isFileTypeValid());
		
		widget.setFileTypes(Collections.EMPTY_LIST);
		changed = widget.doHandleRequest(request);
		Assert.assertEquals("File valid", changed, true);
		Assert.assertTrue(widget.isFileTypeValid());
	}
	
	@Test
	public void testFileSizeInvalidThenValid() {
		WFileWidget widget = new WFileWidget();
		setActiveContext(createUIContext());
		widget.setMaxFileSize(10);
		MockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
		boolean changed = widget.doHandleRequest(request);
		Assert.assertEquals("File invalid, no file uploaded", changed, false);
		Assert.assertFalse(widget.isFileSizeValid());
		
		widget.setMaxFileSize(0);
		changed = widget.doHandleRequest(request);
		Assert.assertEquals("File valid", changed, true);
		Assert.assertTrue(widget.isFileTypeValid());
		
		widget.setMaxFileSize(-100);
		changed = widget.doHandleRequest(request);
		Assert.assertEquals("File valid", changed, true);
		Assert.assertTrue(widget.isFileTypeValid());
	}
	
	@Test
	public void testValidationMultiUIContext() {
		WFileWidget widget = new WFileWidget();
		int maxSize = 10;
		widget.setMaxFileSize(maxSize);
		widget.setLocked(true);
		
		// Context 1, pass the validation
		UIContext context1 = createUIContext();
		setActiveContext(context1);
		MockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM);
		widget.doHandleRequest(request);
		Assert.assertEquals("Context 1 file size valid", widget.isFileSizeValid(), true);
		
		// Context 2, fail the validation
		UIContext context2 = createUIContext();
		setActiveContext(context2);
		request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
		widget.doHandleRequest(request);
		Assert.assertEquals("Context 2 file size invalid", widget.isFileSizeValid(), false);
		
		// Context 1, fail the validation
		setActiveContext(context1);
		request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
		widget.doHandleRequest(request);
		Assert.assertEquals("Context 1 file size invalid", widget.isFileSizeValid(), false);
		
		// Context 2, pass the validation
		setActiveContext(context2);
		request = setupFileUploadRequest(widget, TEST_FILE_ITEM);
		widget.doHandleRequest(request);
		Assert.assertEquals("Context 2 file size valid", widget.isFileSizeValid(), true);
		
		// Context 1, verify the validation
		setActiveContext(context1);
		Assert.assertEquals("Verify context 1", widget.isFileSizeValid(), false);
		
		// Context 2, verify the validation
		setActiveContext(context2);
		Assert.assertEquals("Verify context 2", widget.isFileSizeValid(), true);
	}
	
	/**
	 * @param fileName the file name in the file item
	 * @param size the size of the file in the file item
	 * @return a file item
	 */
	private static FileItem createFileItem(final String fileName, final int size) {
		final byte[] testFile = new byte[size];
		for (int i = 0; i < testFile.length; i++) {
			testFile[i] = (byte) (i & 0xff);
		}

		MockFileItem fileItem = new MockFileItem();
		fileItem.set(testFile);
		fileItem.setName(fileName);
		return fileItem;
	}

	/**
	 * @param widget the widget the file file item is for on the request
	 * @param fileItem the file item to include on the request
	 * @return a request containing the file item for the widget
	 */
	private static FileUploadMockRequest setupFileUploadRequest(final WFileWidget widget,
			final FileItem fileItem) {
		fileItem.setFieldName(widget.getId());

		final FileUploadMockRequest request = new FileUploadMockRequest();
		request.uploadFile(fileItem);

		return request;
	}

	/**
	 * Extends MockRequest so that adding a file with a file name is possible.
	 */
	private static final class FileUploadMockRequest extends MockRequest {

		/**
		 * @param item the file item
		 */
		private void uploadFile(final FileItem item) {
			getFiles().put(item.getFieldName(), new FileItem[]{item});
		}
	}
}
