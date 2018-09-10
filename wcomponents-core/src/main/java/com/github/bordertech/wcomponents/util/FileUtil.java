package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.file.File;
import com.github.bordertech.wcomponents.file.FileItemWrap;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
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
	 * This first checks against file extensions, then against file mime types
	 *
	 * @param newFile the file to be checked, if null then return false
	 * otherwise validate
	 * @param fileTypes allowed file types, if null or empty return true,
	 * otherwise validate
	 * @return {@code true} if either extension or mime-type match is successful
	 */
	public static boolean validateFileType(final FileItemWrap newFile, final List<String> fileTypes) {
		// The newFile is null, then return false
		if (newFile == null) {
			return false;
		}
		// If fileTypes to validate is null or empty, then assume newFile is valid
		if (fileTypes == null || fileTypes.isEmpty()) {
			return true;
		}

		// filter mime types from fileTypes.
		final List<String> fileMimes = fileTypes.stream()
			.filter(fileType -> fileType.matches("[^/\\n]+\\/[^/\\n]+"))
			.collect(Collectors.toList());
		// filter extensions from fileTypes.
		final List<String> fileExts = fileTypes.stream()
			.filter(fileType -> !fileMimes.contains(fileType))
			.collect(Collectors.toList());

		// First validate newFile against fileExts list
		boolean extMatch = false;
		// If extensions are supplied, then check if newFile has a name
		if (fileExts.size() > 0 && newFile.getName() != null) {
			// Then see if newFile has an extension
			String[] split = newFile.getName().split(("\\.(?=[^\\.]+$)"));
			// If it exists
			if (split.length == 2) {
				// Then check if it matches supplied extension(s)
				extMatch = fileExts.stream().anyMatch(ext -> ext.matches("(.*)" + split[1]));
			}
		}
		// If extension match is unsucessful, then move to fileMimes list
		boolean mimeMatch = false;
		if (!extMatch && fileMimes.size() > 0) {
			try {
				final Tika tika = new Tika();
				String mimeType = tika.detect(newFile.getInputStream());
				LOG.debug("File mime-type is: " + mimeType);
				for (String fileMime : fileMimes) {
					if (StringUtils.equalsIgnoreCase(mimeType, fileMime)) {
						mimeMatch = true;
					} else if (fileMime.indexOf("*") == fileMime.length() - 1) {
						fileMime = fileMime.substring(0, fileMime.length() - 1);
						if (mimeType.indexOf(fileMime) == 0) {
							mimeMatch = true;
						}
					}
				}
			} catch (IOException e) {
				LOG.error("Invalid file type");
			}
		}
		// return true if either extension or mime-type match is successful
		return extMatch || mimeMatch;
	}

	/**
	 * Checks if the file item size is within the supplied max file size.
	 *
	 * @param newFile the file to be checked, if null then return false
	 * otherwise validate
	 * @param maxFileSize max file size in bytes, if zero or negative return
	 * true, otherwise validate
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
		final String[] units = new String[]{"B", "KB", "MB", "GB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1000));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1000, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 * Returns invalid fileTypes error message.
	 *
	 * @param fileTypes allowed fileTypes
	 * @return human readable message
	 */
	public static String getInvalidFileTypeMessage(final List<String> fileTypes) {
		if (fileTypes == null) {
			return null;
		}
		return String.format(I18nUtilities.format(null,
			InternalMessages.DEFAULT_VALIDATION_ERROR_FILE_WRONG_TYPE),
			StringUtils.join(fileTypes.toArray(new Object[fileTypes.size()]), ","));
	}

	/**
	 * Returns invalid fileSize error message.
	 *
	 * @param maxFileSize allowed fileSize
	 * @return human readable message
	 */
	public static String getInvalidFileSizeMessage(final long maxFileSize) {
		return String.format(I18nUtilities.format(null, InternalMessages.DEFAULT_VALIDATION_ERROR_FILE_WRONG_SIZE),
			FileUtil.readableFileSize(maxFileSize));
	}
}
