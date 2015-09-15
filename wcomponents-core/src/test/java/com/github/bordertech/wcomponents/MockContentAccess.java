package com.github.bordertech.wcomponents;

/**
 * MockContentAccess - a ContentAccess useful for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockContentAccess implements ContentAccess {

	/**
	 * The content data.
	 */
	private byte[] bytes;
	/**
	 * The content description.
	 */
	private String description;
	/**
	 * The content mime type.
	 */
	private String mimeType;

	/**
	 * @return Returns the content.
	 */
	@Override
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @param bytes The content to set.
	 */
	public void setBytes(final byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * @return Returns the description.
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return Returns the mimeType.
	 */
	@Override
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType The mimeType to set.
	 */
	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}
}
