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
	public void testvalidateFileType() throws IOException {
		FileItem newFileItem = createFileItem(false);
		boolean validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList("text/plain", "image/gif"));
		Assert.assertTrue(validateFileType);

		newFileItem = createFileItem(true);
		validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Arrays.asList("text/plain", "image/gif"));
		Assert.assertFalse(validateFileType);
		
		validateFileType = FileUtil.validateFileType(null, null);
		Assert.assertFalse(validateFileType);
		
		newFileItem = createFileItem(true);
		validateFileType = FileUtil.validateFileType(new FileItemWrap(newFileItem), Collections.EMPTY_LIST);
		Assert.assertTrue(validateFileType);
	}
	
	@Test
	public void testvalidateFileSize() throws IOException {
		FileItem newFileItem = createFileItem(true);
		boolean validateFileSize = FileUtil.validateFileSize(new FileItemWrap(newFileItem), 200);
		Assert.assertTrue(validateFileSize);

		validateFileSize = FileUtil.validateFileSize(new FileItemWrap(newFileItem), 50);
		Assert.assertFalse(validateFileSize);
		
		FileUtil.validateFileSize(null, 0);
		Assert.assertFalse(validateFileSize);
		
		validateFileSize = FileUtil.validateFileSize(new FileItemWrap(newFileItem), -1000);
		Assert.assertFalse(validateFileSize);
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
	public void testgetInvalidFileSizeMessage() {
		String invalidFileSizeMessage = FileUtil.getInvalidFileSizeMessage(1111);
		Assert.assertEquals("The file you have selected is too large. Maximum file size is 1.1 KB.", invalidFileSizeMessage);
	}
	
	/**
	 * Create a new fileitem.
	 * 
	 * @param fakeFile if {@code true} dummy byte[] are set on file, otherwise real file byte[]
	 * @return a file item
	 */
	private FileItem createFileItem(boolean fakeFile) throws IOException {
		byte[] testFile;
		if (fakeFile) {
			testFile = new byte[100];
			for (int i = 0; i < testFile.length; i++) {
				testFile[i] = (byte) (i & 0xff);
			}
		}
		else {
			InputStream stream = getClass().getResourceAsStream("/image/x1.gif");
			testFile =  StreamUtil.getBytes(stream);
		}
		MockFileItem fileItem = new MockFileItem();
		fileItem.set(testFile);
		return fileItem;
	}
}
