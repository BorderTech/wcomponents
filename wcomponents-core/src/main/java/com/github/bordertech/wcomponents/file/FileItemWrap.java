package com.github.bordertech.wcomponents.file;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.fileupload.FileItem;

/**
 * A {@link File} implementation that is backed by a {@link FileItem}.
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class FileItemWrap implements File {

	/**
	 * The file item backing this instance.
	 */
	private final FileItem backing;

	/**
	 * Constructor. Set the backing FileItem.
	 *
	 * @param item The FileItem that backs this DefaultFileImpl.
	 */
	public FileItemWrap(final FileItem item) {
		backing = item;
	}

	/**
	 * The name of the file as supplied by the client. Depending on the client this may or may not include the full path
	 * to the file.
	 *
	 * @return The name of the file.
	 */
	@Override
	public String getName() {
		return backing.getName();
	}

	/**
	 * @return the byte content of the file.
	 */
	@Override
	public byte[] getBytes() {
		return backing.get();
	}

	/**
	 * Returns the name of the file.
	 *
	 * @return file name.
	 */
	@Override
	public String getDescription() {
		return backing.getName();
	}

	/**
	 * Returns the content type passed by the browser or <code>null</code> if not defined.
	 *
	 * @return The content type passed by the browser or <code>null</code> if not defined.
	 */
	@Override
	public String getMimeType() {
		String contentType = backing.getContentType();

		// IE6 (and apparently IE7) are broken and send the MIME type "image/pjpeg"
		// regardless of whether the image is a progressive jpeg or not. Sending the
		// incorrect MIME type back can cause display issues. It is always safe to
		// change it to just be "image/jpeg".
		if ("image/pjpeg".equals(contentType)) {
			contentType = "image/jpeg";
		}

		return contentType;
	}

	/**
	 * Returns an {@link java.io.InputStream InputStream} that can be used to retrieve the contents of the file.
	 *
	 * @return An {@link java.io.InputStream InputStream} that can be used to retrieve the contents of the file.
	 * @throws IOException unable to access file.
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return backing.getInputStream();
	}

	/**
	 * Returns the size of the file item.
	 *
	 * @return The size of the file item, in bytes.
	 */
	@Override
	public long getSize() {
		return backing.getSize();
	}

	/**
	 * Constructs a {@link java.io.File} instance using the name of the file, and invokes {@link java.io.File#getName()}
	 * to retrieve the name of the file minus its extension.
	 *
	 * @return The name of the file or directory denoted by this abstract pathname, or the empty string if this
	 * pathname's name sequence is empty
	 */
	@Override
	public String getFileName() {
		return new java.io.File(getName()).getName();
	}

	/**
	 * Returns a string representation of this object.
	 *
	 * @return a string representation of this object.
	 */
	@Override
	public String toString() {
		return "name=" + backing.getName();
	}
}
