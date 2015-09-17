package com.github.bordertech.wcomponents.file;

import com.github.bordertech.wcomponents.ContentAccess;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides access to the contents of a file.
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public interface File extends ContentAccess {

	/**
	 * Returns the original filename in the client's filesystem, as provided by the browser (or other client software).
	 *
	 * @return The original filename in the client's filesystem.
	 */
	String getName();

	/**
	 * Returns an {@link java.io.InputStream InputStream} that can be used to retrieve the contents of the file.
	 *
	 * @return An {@link java.io.InputStream InputStream} that can be used to retrieve the contents of the file.
	 * @throws IOException if an error occurs.
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Returns the size of the file.
	 *
	 * @return The size of the file, in bytes.
	 */
	long getSize();

	/**
	 * Returns the name of the file without the file extension.
	 *
	 * @return the name of the file
	 */
	String getFileName();

}
