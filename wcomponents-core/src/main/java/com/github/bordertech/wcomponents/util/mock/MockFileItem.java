package com.github.bordertech.wcomponents.util.mock;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.io.FileUtils;

/**
 * Mock implementation of {@link FileItem}.
 *
 * @author Yiannis Paschalidis
 * @author Anthony O'Connor - extracted from WFileWidget_Test.
 * @since 1.0.0
 */
public final class MockFileItem implements FileItem {

	/**
	 * The field name used to reference this file item.
	 */
	private String fieldName;

	/**
	 * <code>True</code> if the instance represents a simple form field, or <code>false</code> if it represents an
	 * uploaded file.
	 */
	private boolean formField = true;

	/**
	 * The original filename in the client's filesystem.
	 */
	private String fileName;

	/**
	 * The file's mime-type.
	 */
	private String contentType;

	/**
	 * Flags whether the {@link #delete()} method has been called.
	 */
	private boolean deleted = false;

	/**
	 * The binary data contained in the file.
	 */
	private byte[] contents;

	private FileItemHeaders headers;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		deleted = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] get() {
		if (deleted) {
			throw new IllegalStateException("delete() called");
		}

		if (contents != null) {
			byte[] data = new byte[contents.length];
			System.arraycopy(contents, 0, data, 0, contents.length);
			return data;
		}
		return null;
	}

	/**
	 * Sets the binary data for this MockFileItem.
	 *
	 * @param binaryContents the binary content.
	 */
	public void set(final byte[] binaryContents) {
		formField = false;
		this.contents = binaryContents;
	}

	/**
	 * Sets the content MIME-type for thos MockFileItem.
	 *
	 * @param contentType the contentType.
	 */
	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(get());
	}

	/**
	 * Returns the file item headers.
	 *
	 * @return The file items headers.
	 */
	@Override
	public FileItemHeaders getHeaders() {
		return headers;
	}

	/**
	 * Set the file item headers.
	 */
	@Override
	public void setHeaders(final FileItemHeaders headers) {
		this.headers = headers;
	}

	/**
	 * Sets the name of the uploaded file for this MockFileItem.
	 *
	 * @param name the name of the uploaded file.
	 */
	public void setName(final String name) {
		this.fileName = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getSize() {
		return contents == null ? 0 : contents.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString() {
		return new String(get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(final String encoding) throws UnsupportedEncodingException {
		return new String(get(), encoding);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFormField() {
		return formField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInMemory() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFieldName(final String name) {
		this.fieldName = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFormField(final boolean state) {
		this.formField = state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final File file) throws IOException {
		FileUtils.writeByteArrayToFile(file, get());
	}
}
