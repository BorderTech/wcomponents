package com.github.bordertech.wcomponents.util;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;

import com.github.bordertech.wcomponents.file.FileItemWrap;

/**
 * Utility methods for {@link FileItemWrap} validation.
 *
 * @author Aswin Kandula
 * @since 1.4.12
 */
public final class FileValidationUtil {

	/**
	 * No instance methods here.
	 */
	private FileValidationUtil() {
	}
	
	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(FileValidationUtil.class);

	/**
	 * Is file type valid.
	 * 
	 * @param newFile checks against supplied fileTypes
	 * @param fileTypes allowed file types
	 * @return true/false
	 */
	public static boolean validateFileType(final FileItemWrap newFile, final List<String> fileTypes) {
		try {
			final Tika tika = new Tika();
			String mimeType = tika.detect(newFile.getInputStream());
			LOG.debug("File mime-type is: " + mimeType);
			
			for (String fileType : fileTypes) {
				if (StringUtils.equalsIgnoreCase(mimeType, fileType)) {
					return true;
				} else if (fileType.indexOf("*") == fileType.length() - 1) {
					fileType = fileType.substring(0, fileType.length() - 1);
					if (mimeType.indexOf(fileType) == 0) {
						return true;
					}
				}
			}
			return false;
		} catch (IOException e) {
			LOG.error("Invalid file type");
			return false;
		}
	}

	/**
	 * Is file size valid.
	 *
	 * @param newFile checks against supplied maxFileSize
	 * @param maxFileSize max file size
	 * @return true/false
	 */
	public static boolean validateFileSize(final FileItemWrap newFile, final long maxFileSize) {
		return (newFile.getSize() > maxFileSize) ? false : true;
	}
}
