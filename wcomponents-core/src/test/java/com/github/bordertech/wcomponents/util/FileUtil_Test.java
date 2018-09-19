package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.file.FileItemWrap;
import com.github.bordertech.wcomponents.util.mock.MockFileItem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.fileupload.FileItem;
import org.junit.Assert;
import org.junit.Test;

/**
 * FileUtil_Test - unit test for {@link FileUtil}.
 * @author Aswin Kandula
 * @since 1.5
 */
public class FileUtil_Test {

	@Test
	public void testValidateFileTypeImageFile() throws IOException {
		FileItem newFileItem = createFileItem("/image/x1.gif");
		boolean validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList("text/plain", "image/gif"));
		Assert.assertTrue(validateFileType);

		newFileItem = createFileItem(null);
		validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList("text/plain", "image/gif"));
		Assert.assertFalse(validateFileType);
	}

	@Test
	public void testValidateFileTypeAnyFile() throws IOException {
		boolean validateFileType = FileUtil.validateFileType(null, null);
		Assert.assertFalse(validateFileType);
		
		FileItem newFileItem = createFileItem(null);
		validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Collections.EMPTY_LIST);
		Assert.assertTrue(validateFileType);
	}

	@Test
	public void testValidateFileTypePdfFile() throws IOException {
		FileItem newFileItem = createFileItem("/content/test.pdf");
		boolean validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList("application/pdf"));
		Assert.assertTrue(validateFileType);
	}
	
	@Test
	public void testValidateFileTypeTr5File() throws IOException {
		// 'tr5' file has no mime type, so validation will pass with extension only.
		FileItem newFileItem = createFileItem("/content/test.tr5");
		boolean validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList(".tr5"));
		Assert.assertTrue(validateFileType);
		
		newFileItem = createFileItem("/content/test.tr5");
		validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList("text/plain"));
		Assert.assertTrue(validateFileType);
		
		newFileItem = createFileItem("/content/test.tr5");
		validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList("image/jpg"));
		Assert.assertFalse(validateFileType);
	}
	
	@Test
	public void testValidateFileTypeDodgyTr5File() throws IOException {
		// 'tr5' file has no mime type, so validation will pass with extension only.
		FileItem newFileItem = createFileItem("/content/dodgy.pdf.tr5");
		boolean validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList(".tr5"));
		Assert.assertTrue(validateFileType);
		
		newFileItem = createFileItem("/content/dodgy.pdf.tr5");
		validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList("text/plain"));
		Assert.assertFalse(validateFileType);
	}
	
	@Test
	public void testValidateFileSize() throws IOException {
		FileItem newFileItem = createFileItem(null);
		boolean validateFileSize = FileUtil.validateFileSize(new FileItemWrap(newFileItem), 200);
		Assert.assertTrue(validateFileSize);

		validateFileSize = FileUtil.validateFileSize(new FileItemWrap(newFileItem), 50);
		Assert.assertFalse(validateFileSize);
		
		FileUtil.validateFileSize(null, 0);
		Assert.assertFalse(validateFileSize);
		
		validateFileSize = FileUtil.validateFileSize(new FileItemWrap(newFileItem), -1000);
		Assert.assertTrue(validateFileSize);
	}
	
	@Test
	public void testReadableFileSize() {
		String readableFileSize = FileUtil.readableFileSize(10101);
		Assert.assertEquals("10.1 KB", readableFileSize);
	}
	
	@Test
	public void testGetInvalidFileTypeMessage() {
		String invalidFileTypeMessage = FileUtil.getInvalidFileTypeMessage(null);
		Assert.assertEquals(null, invalidFileTypeMessage);

		invalidFileTypeMessage = FileUtil.getInvalidFileTypeMessage(Arrays.asList("*"));
		Assert.assertEquals("The file you have selected is not of an accepted type. Only the following type/s are accepted: *.", invalidFileTypeMessage);
	}
	
	@Test
	public void testGetInvalidFileSizeMessage() {
		String invalidFileSizeMessage = FileUtil.getInvalidFileSizeMessage(1111);
		Assert.assertEquals("The file you have selected is too large. Maximum file size is 1.1 KB.", invalidFileSizeMessage);
	}
	
	/**
	 * Create a new fileitem.
	 * 
	 * @param fileResource if {@code null} dummy byte[] are set on file, otherwise given file resource.
	 * @return a file item
	 */
	private FileItem createFileItem(String fileResource) throws IOException {
		byte[] testFileContent;
		if (fileResource == null) {
			testFileContent = new byte[100];
			for (int i = 0; i < testFileContent.length; i++) {
				testFileContent[i] = (byte) (i & 0xff);
			}
		}
		else {
			InputStream stream = getClass().getResourceAsStream(fileResource);
			if (stream == null) {
				throw new IOException("File resource not found: " + fileResource);
			}
			testFileContent =  StreamUtil.getBytes(stream);
		}
		MockFileItem fileItem = new MockFileItem();
		fileItem.set(testFileContent);
		fileItem.setFieldName(fileResource);
		if (fileResource  != null) {
			String[] tokens = fileResource.split(".+?/(?=[^/]+$)");
			fileItem.setName(tokens[1]);
		}
		return fileItem;
	}
}
