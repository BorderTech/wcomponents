package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.file.FileItemWrap;
import com.github.bordertech.wcomponents.util.StreamUtil;
import com.github.bordertech.wcomponents.util.mock.MockFileItem;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.fileupload.FileItem;
import org.junit.Test;

/**
 * WFileWidget_Test - unit test for {@link WFileWidget}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
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
