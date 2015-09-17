package com.github.bordertech.wcomponents;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * This interface enables access to arbitrary document content such as a PDF. It could be fetched from the database or
 * it could be generated on the fly.</p>
 *
 * <p>
 * This extension of {@link ContentAccess} should be used to serve up large binary content (e.g. video), rather than
 * requiring the entire byte array to be held in memory.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface ContentStreamAccess extends ContentAccess {

	/**
	 * Retrieves the stream that make up the document content.
	 *
	 * @return the stream containing the document content.
	 * @throws IOException if there is an error retrieving the content.
	 */
	InputStream getStream() throws IOException;
}
