package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WMultiFileWidget.FileWidgetUpload;
import com.github.bordertech.wcomponents.file.FileItemWrap;
import com.github.bordertech.wcomponents.util.mock.MockFileItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.junit.Test;

/**
 * WMultiFileWidget_Test - unit test for {@link WMultiFileWidget}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @author Rick Brown
 * @since 1.0.0
 */
public class WMultiFileWidget_Test extends AbstractWComponentTestCase {

	/**
	 * Test file item 1.
	 */
	private static final FileItem TEST_FILE_ITEM1 = createFileItem("test1.bin", 10);
	/**
	 * Test file item 2.
	 */
	private static final FileItem TEST_FILE_ITEM2 = createFileItem("test2.bin", 20);

	/**
	 * File item wrap for file item 1.
	 */
	private static final FileWidgetUpload TEST_FILE_ITEM_WRAP1 = new FileWidgetUpload("1",
			new FileItemWrap(TEST_FILE_ITEM1));

	/**
	 * File item wrap for file item 2.
	 */
	private static final FileWidgetUpload TEST_FILE_ITEM_WRAP2 = new FileWidgetUpload("2",
			new FileItemWrap(TEST_FILE_ITEM2));

	/**
	 * Selected Item 1.
	 */
	private static final List<FileWidgetUpload> UPLOADED_1 = Arrays.asList(TEST_FILE_ITEM_WRAP1);

	/**
	 * Selected Item 1 and Item 2.
	 */
	private static final List<FileWidgetUpload> UPLOADED_1_2 = Arrays.asList(TEST_FILE_ITEM_WRAP1,
			TEST_FILE_ITEM_WRAP2);

	@Test
	public void testGetValueAsString() {
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setLocked(true);
		setActiveContext(createUIContext());

		// Null
		Assert.assertNull("Value as String should be null", widget.getValueAsString());

		// Upload file1
		widget.setData(UPLOADED_1);
		Assert.assertEquals("Value as String should be Item1", "name=" + TEST_FILE_ITEM1.getName(),
				widget.getValueAsString());

		// Upload file1, file2
		widget.setData(UPLOADED_1_2);
		Assert.assertEquals("Value as String should be Item1, Item2", "name=" + TEST_FILE_ITEM1.
				getName() + ", "
				+ "name=" + TEST_FILE_ITEM2.getName(),
				widget.getValueAsString());
	}

	@Test
	public void testGetValue() {
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setLocked(true);
		setActiveContext(createUIContext());

		// Null
		Assert.assertTrue("Value should be empty by default", widget.getValue().isEmpty());

		// Set file1, file2 as uploaded
		widget.setData(UPLOADED_1_2);
		Assert.assertEquals("Value should be list containing Item1, Item2", UPLOADED_1_2, widget.
				getValue());
	}

	@Test
	public void testGetFiles() {
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setLocked(true);
		setActiveContext(createUIContext());

		// Null
		Assert.assertTrue("Files should be empty by default", widget.getFiles().isEmpty());

		// Set file1, file2 as uploaded
		widget.setData(UPLOADED_1_2);
		Assert.assertEquals("Files should be list containing Item1, Item2", UPLOADED_1_2, widget.
				getFiles());
	}

	@Test
	public void testGetFile() {
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setLocked(true);
		setActiveContext(createUIContext());

		// Null
		FileWidgetUpload file = widget.getFile("X");
		Assert.assertNull("Shuld be null for invalid file id", file);

		// Set file1, file2 as uploaded
		widget.setData(UPLOADED_1_2);
		Assert.assertEquals("File2 should be returned for index 1", TEST_FILE_ITEM_WRAP2, widget.
				getFile("2"));
	}

	@Test
	public void testClearFiles() {
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setLocked(true);
		setActiveContext(createUIContext());

		// Set file1, file2 as uploaded
		widget.setData(UPLOADED_1_2);

		// Clear Files
		widget.clearFiles();
		Assert.assertTrue("Value should be empty by default", widget.getValue().isEmpty());
		Assert.assertNull("Date should be null after clearing files", widget.getData());
	}

//    @Test
//    TODO reinstate a test that checks user contexts are not mixed up without checking that get returns the same instance as set
//    public void testFileTypesAccessors()
//    {
//        assertAccessorsCorrect(new WMultiFileWidget(), "fileTypes", Collections.EMPTY_LIST,
//                               Arrays.asList("text/plain", "image/gif"), Arrays.asList("image/jpeg"));
//    }
	@Test
	public void testSetFileTypesAsArray() {
		final String[] types1 = new String[]{"text/plain", "image/gif"};

		// Set as array
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setFileTypes(types1);

		Assert.assertTrue("Incorrect file types returned", CollectionUtils.isEqualCollection(Arrays.
				asList(types1), widget.getFileTypes()));
	}

	@Test
	public void testSetFileTypesAsList() {
		List<String> types1 = new ArrayList<>();
		types1.add("text/plain");
		types1.add("image/*");
		types1.add(".vis");

		// Set as array
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setFileTypes(types1);

		Assert.assertTrue("Incorrect file types returned", CollectionUtils.isEqualCollection(types1,
				widget.getFileTypes()));
	}

	@Test
	public void testSetFileTypesAsSet() {
		Set<String> types1 = new HashSet<>();
		types1.add("text/plain");
		types1.add("image/*");
		types1.add(".vis");

		// Set as array
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setFileTypes(types1);

		Assert.assertTrue("Incorrect file types returned", CollectionUtils.isEqualCollection(types1,
				widget.getFileTypes()));
	}

	@Test
	public void testSetFileTypesWithDuplicates() {
		List<String> types1 = new ArrayList<>();
		types1.add("text/plain");
		types1.add("image/*");
		types1.add(".vis");
		List<String> expected = new ArrayList<>(types1);
		types1.add("text/plain");
		types1.add("TEXT/PLAIN");
		types1.add(".VIS");

		// Set as array
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setFileTypes(types1);

		Assert.assertTrue("Duplicate file types should not be honored", CollectionUtils.
				isEqualCollection(expected, widget.getFileTypes()));
	}

	@Test
	public void testSetFileTypesClonesList() {
		List<String> types1 = new ArrayList<>();
		types1.add("text/plain");
		types1.add("image/*");
		types1.add(".vis");
		List<String> expected = new ArrayList<>(types1);

		// Set as array
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setFileTypes(types1);

		// add some more valid file types to the list we passed in
		types1.add("text/javascript");
		types1.add(".doc");

		Assert.assertTrue("Modifiying the list after calling 'set' should not update state",
				CollectionUtils.isEqualCollection(expected, widget.getFileTypes()));
	}

	@Test
	public void testSetFileTypesClearsAll() {
		List<String> types1 = new ArrayList<>();
		types1.add("text/plain");
		types1.add("image/*");
		types1.add(".vis");
		List<String> types2 = new ArrayList<>();
		types2.add("text/javascript");
		types2.add("image/*");

		// Set as array
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setFileTypes(types1);
		widget.setFileTypes(types2);  // this should not ADD to the existing acceepted file types, it should replace it

		Assert.assertTrue("Calling set multiple times should not be additive", CollectionUtils.
				isEqualCollection(types2, widget.getFileTypes()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetFileTypesWithInvalidType() {
		List<String> types1 = new ArrayList<>();
		types1.add("*.txt");

		// Set as array
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setFileTypes(types1);
	}

	@Test
	public void testSetFileTypesWithInvalidTypeNotModified() {
		List<String> types1 = new ArrayList<>();
		types1.add("text/plain");

		// Set as array
		WMultiFileWidget widget = new WMultiFileWidget();
		widget.setFileTypes(types1);
		List<String> expected = widget.getFileTypes();
		types1.add("image/*");
		types1.add("*.txt");  // the invalid one should be in the middle to ensure the one before and the one after are not added before the exception
		types1.add(".vis");

		try {
			widget.setFileTypes(types1);
		} catch (IllegalArgumentException ignore) {
			// ignore
		}
		Assert.assertTrue("Calling set with an invalid file type does not modify the widget",
				CollectionUtils.isEqualCollection(expected, widget.getFileTypes()));
	}

	@Test
	public void testSetFileTypesAsNullOrEmptyList() {
		WMultiFileWidget widget = new WMultiFileWidget();
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

		// Set types on widget
		widget.setFileTypes(Arrays.asList("image/jpeg"));

		// Set as empty list
		widget.setFileTypes(new ArrayList<String>());
		Assert.assertEquals("File types should be empty when set to empty list",
				Collections.EMPTY_LIST,
				widget.getFileTypes());
	}

	@Test
	public void testMaxFileSizeAccessors() {
		assertAccessorsCorrect(new WMultiFileWidget(), "maxFileSize", (long) 10240000, (long) 1,
				(long) 2);
	}

	@Test
	public void testMaxFilesAccessors() {
		assertAccessorsCorrect(new WMultiFileWidget(), "maxFiles", 0, 1, 2);
	}

//    @Test
//    public void testDoHandleRequestAjaxUploadWithMultiFiles()
//    {
//        WMultiFileWidget widget = new WMultiFileWidget();
//        widget.setLocked(true);
//        setActiveContext(createUIContext());
//
//        // Setup AJAX operation
//        try
//        {
//            AjaxHelper.setCurrentOperationDetails(new AjaxOperation(widget.getId(), "X"), null);
//
//            // Setup request with file1
//            FileUploadMockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM_1_2);
//            boolean changed = widget.doHandleRequest(request);
//            Assert.assertFalse("For ajax upload should have returned no change.", changed);
//
//            // Check Uploaded file1
//            Assert.assertEquals("Uploaded files should have 2 files", 2, widget.getValue().size());
//            Assert.assertEquals("Uploaded files should contain file 1.", TEST_FILE_ITEM1.getName(), widget.getValue()
//                .get(0).getFile().getName());
//
//            Assert.assertEquals("Uploaded files should contain file 2.", TEST_FILE_ITEM2.getName(), widget.getValue()
//                .get(1).getFile().getName());
//
//            // Clear AJAX
//            AjaxHelper.clearCurrentOperationDetails();
//
//            // Process a "page" request
//            changed = widget.doHandleRequest(request);
//            Assert.assertTrue("After ajax uploads should have returned true.", changed);
//
//            // Do prepare paint (should clear change flag)
//            widget.preparePaintComponent(request);
//
//            changed = widget.doHandleRequest(request);
//            Assert.assertFalse("After ajax uploads and PreparePaint should have returned false.", changed);
//
//        }
//        finally
//        {
//            AjaxHelper.clearCurrentOperationDetails();
//        }
//    }
//
//    @Test
//    public void testDoHandleRequestPageRequest()
//    {
//        WMultiFileWidget widget = new WMultiFileWidget();
//        widget.setLocked(true);
//        setActiveContext(createUIContext());
//
//        // Empty Request
//        MockRequest request = new MockRequest();
//        boolean changed = widget.doHandleRequest(request);
//
//        Assert.assertFalse("For an empty request should have returned no change.", changed);
//
//        // Set uploaded file file1, file2
//        widget.setData(UPLOADED_1_2);
//
//        // Check selected files are File 1 and File2
//        Assert.assertEquals("Selected files should be file 1 and file2.", UPLOADED_1_2, widget.getSelectedFiles());
//
//        // Setup request to select both files
//        request = new MockRequest();
//        request.setParameter(widget.getId() + ".selected", new String[] { "0", "1" });
//        changed = widget.doHandleRequest(request);
//
//        // Check selected files are still File 1 and File2
//        Assert.assertFalse("Should have returned no change.", changed);
//        Assert.assertEquals("Selected files should be file 1 and file2.", UPLOADED_1_2, widget.getSelectedFiles());
//
//        // Setup request to only select file1
//        request = new MockRequest();
//        request.setParameter(widget.getId() + ".selected", new String[] { "0" });
//        changed = widget.doHandleRequest(request);
//
//        // Check selected files is File 1
//        Assert.assertFalse("Should have returned no change.", changed);
//        Assert.assertEquals("Selected file should be file 1.", UPLOADED_1, widget.getSelectedFiles());
//
//        // Setup request to select no files
//        request = new MockRequest();
//        widget.doHandleRequest(request);
//
//        // Check selected is empty
//        Assert.assertTrue("Selected files should be empty.", widget.getSelectedFiles().isEmpty());
//    }
//
//    @Test
//    public void testDoHandleRequestAjaxUpload()
//    {
//        WMultiFileWidget widget = new WMultiFileWidget();
//        widget.setLocked(true);
//        setActiveContext(createUIContext());
//
//        // Setup AJAX operation
//        try
//        {
//            AjaxHelper.setCurrentOperationDetails(new AjaxOperation(widget.getId(), "X"), null);
//
//            // Setup request with file1
//            FileUploadMockRequest request = setupFileUploadRequest(widget, TEST_FILE_ITEM1);
//            boolean changed = widget.doHandleRequest(request);
//            Assert.assertFalse("For ajax upload should have returned no change.", changed);
//
//            // Check Uploaded file1
//            Assert.assertEquals("Uploaded files should have 1 file", 1, widget.getValue().size());
//            Assert.assertEquals("Uploaded files should contain file 1.", TEST_FILE_ITEM1.getName(), widget.getValue()
//                .get(0).getFile().getName());
//
//            // Setup request with file2
//            request = setupFileUploadRequest(widget, TEST_FILE_ITEM2);
//            changed = widget.doHandleRequest(request);
//            Assert.assertFalse("For ajax upload should have returned no change.", changed);
//
//            // Check Uploaded file1 and file2
//            Assert.assertEquals("Uploaded files should have 2 files", 2, widget.getValue().size());
//            Assert.assertEquals("Uploaded files should contain file 1.", TEST_FILE_ITEM1.getName(), widget.getValue()
//                .get(0).getFile().getName());
//            Assert.assertEquals("Uploaded files should contain file 2.", TEST_FILE_ITEM2.getName(), widget.getValue()
//                .get(1).getFile().getName());
//
//            // Clear AJAX
//            AjaxHelper.clearCurrentOperationDetails();
//
//            // Process a "page" request
//            changed = widget.doHandleRequest(request);
//            Assert.assertTrue("After ajax uploads should have returned true.", changed);
//
//            // Do prepare paint (should clear change flag)
//            widget.preparePaintComponent(request);
//
//            changed = widget.doHandleRequest(request);
//            Assert.assertFalse("After ajax uploads and PreparePaint should have returned false.", changed);
//
//        }
//        finally
//        {
//            AjaxHelper.clearCurrentOperationDetails();
//        }
//    }
//
//    @Test
//    public void testIsPresent()
//    {
//        WMultiFileWidget widget = new WMultiFileWidget();
//        widget.setLocked(true);
//
//        // Empty Request
//        setActiveContext(createUIContext());
//        MockRequest request = new MockRequest();
//        Assert.assertFalse("IsPresent should return false", widget.isPresent(request));
//
//        // Widget on the request
//        request = new MockRequest();
//        request.setParameter(widget.getId(), "x");
//        Assert.assertTrue("IsPresent should return true", widget.isPresent(request));
//
//        // Current AJAX operation
//        // Setup AJAX operation - is target
//        try
//        {
//            AjaxHelper.setCurrentOperationDetails(new AjaxOperation(widget.getId(), "X"), null);
//
//            // Setup request with file1
//            request = setupFileUploadRequest(widget, TEST_FILE_ITEM1);
//            Assert.assertTrue("IsPresent should return true with ajax target", widget.isPresent(request));
//        }
//        finally
//        {
//            AjaxHelper.clearCurrentOperationDetails();
//        }
//    }
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
//
//	/**
//	 * @param widget the widget the file file item is for on the request
//	 * @param fileItems the file items to include on the request
//	 * @return a request containing the file item for the widget
//	 */
//	private static FileUploadMockRequest setupFileUploadRequest(final WMultiFileWidget widget,
//			final FileItem[] fileItems) {
//		final FileUploadMockRequest request = new FileUploadMockRequest();
//		for (FileItem fileItem : fileItems) {
//			fileItem.setFieldName(widget.getId());
//			request.uploadFile(fileItem);
//		}
//		return request;
//	}

//	/**
//	 * Extends MockRequest so that adding a file with a file name is possible.
//	 */
//	private static final class FileUploadMockRequest extends MockRequest {
//
//		/**
//		 * @param item the file item
//		 */
//		private void uploadFile(final FileItem item) {
//			RequestUtil.addFileItem(getFiles(), item.getFieldName(), item);
//		}
//	}
}
