package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.file.File;
import com.github.bordertech.wcomponents.file.FileItemWrap;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;

/**
 * Utility methods for {@link File}.
 *
 * @author Aswin Kandula
 * @since 1.4
 */
public final class FileUtil {

	/**
	 * No instance methods here.
	 */
	private FileUtil() {
	}
	
	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(FileUtil.class);

	/**
	 * Checks if the file item is one among the supplied file types.
	 * 
	 * @param newFile the file to be checked, if null then return false otherwise validate
	 * @param fileTypes allowed file types, if null or empty return true, otherwise validate
	 * @return {@code true} if file type is valid
	 */
	public static boolean validateFileType(final FileItemWrap newFile, final List<String> fileTypes) {
		// If newFile to validate is null, then return false
		if (newFile == null) {
		    return false;
		}
		// If fileTypes to validate is null or empty, then assume newFile is valid
		if (fileTypes == null || fileTypes.isEmpty()) {
		    return true;
		}
		
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
	 * Checks if the file item size is within the supplied max file size.
	 *
	 * @param newFile the file to be checked, if null then return false otherwise validate
	 * @param maxFileSize max file size in bytes, if zero or negative return true, otherwise validate
	 * @return {@code true} if file size is valid.
	 */
	public static boolean validateFileSize(final FileItemWrap newFile, final long maxFileSize) {
		// If newFile to validate is null, then return false
		if (newFile == null) {
		    return false;
		}
		// If maxFileSize to validate is zero or negative, then assume newFile is valid
		if (maxFileSize < 1) {
		    return true;
		}
		
		return (newFile.getSize() <= maxFileSize);
	}

	/**
	 * Format file size as B, KB, MB, GB.
	 *
	 * @param size of file
	 * @return human readable file size
	 */
	public static String readableFileSize(final long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[] {"B", "KB", "MB", "GB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1000));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1000, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 * Returns invalid fileTypes error message.
	 * @param fileTypes allowed fileTypes
	 * @return human readable message
	 */
	public static String getInvalidFileTypeMessage(final List<String> fileTypes) {
		if (fileTypes  == null) {
		    return null;
		}
		return String.format(I18nUtilities.format(null, 
				InternalMessages.DEFAULT_VALIDATION_ERROR_FILE_WRONG_TYPE), 
				StringUtils.join(fileTypes.toArray(new Object[fileTypes.size()]), ","));
	}

	/**
	 * Returns invalid fileSize error message.
	 * @param maxFileSize allowed fileSize
	 * @return human readable message
	 */
	public static String getInvalidFileSizeMessage(final long maxFileSize) {
		return String.format(I18nUtilities.format(null, InternalMessages.DEFAULT_VALIDATION_ERROR_FILE_WRONG_SIZE), 
				FileUtil.readableFileSize(maxFileSize));
	}
}
