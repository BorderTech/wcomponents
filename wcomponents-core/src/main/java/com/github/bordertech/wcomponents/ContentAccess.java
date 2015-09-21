package com.github.bordertech.wcomponents;

import java.io.Serializable;

/**
 * <p>
 * This interface enables access to arbitrary document content such as a PDF. It could be fetched from the database or
 * it could be generated on the fly.</p>
 *
 * <p>
 * For larger content, consider using {@link ContentStreamAccess}, as it does not require the entire binary content to
 * be held in memory.</p>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public interface ContentAccess extends Serializable {

	/**
	 * The bytes that make up the document content.
	 *
	 * @return the bytes
	 */
	byte[] getBytes();

	/**
	 * Some text that describes the document content. This text could be the document filename or title, for instance.
	 *
	 * @return a short document description
	 */
	String getDescription();

	/**
	 * The mime type of the document. For example, "image/jpeg", "image/gif", "application/pdf" etc
	 *
	 * @return the mime type.
	 */
	String getMimeType();
}
