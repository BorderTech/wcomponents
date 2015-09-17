package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor.CacheType;
import com.github.bordertech.wcomponents.util.StreamUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An Escape subclass that bypasses the usual request -&gt; paint flow by directly producing the binary document
 * content.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class ContentEscape extends ActionEscape {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ContentEscape.class);

	/**
	 * The ContentAccess which will provide the content.
	 */
	private final ContentAccess contentAccess;

	/**
	 * A flag if the content can be cached.
	 */
	private boolean cacheable;

	/**
	 * A flag if the content should be displayed inline, or downloaded.
	 */
	private boolean displayInline = true;

	/**
	 * Creates a ContentEscape.
	 *
	 * @param contentAccess the ContentAccess which will provide the content.
	 */
	public ContentEscape(final ContentAccess contentAccess) {
		this.contentAccess = contentAccess;
	}

	/**
	 * Writes the content to the response.
	 *
	 * @throws IOException if there is an error writing the content.
	 */
	@Override
	public void escape() throws IOException {
		LOG.debug("...ContentEscape escape()");

		if (contentAccess == null) {
			LOG.warn("No content to output");
		} else {
			String mimeType = contentAccess.getMimeType();

			Response response = getResponse();
			response.setContentType(mimeType);

			if (isCacheable()) {
				getResponse().setHeader("Cache-Control", CacheType.CONTENT_CACHE.getSettings());
			} else {
				getResponse().setHeader("Cache-Control", CacheType.CONTENT_NO_CACHE.getSettings());
			}

			if (contentAccess.getDescription() != null) {
				String fileName = WebUtilities.encodeForContentDispositionHeader(contentAccess.
						getDescription());

				if (displayInline) {
					response.setHeader("Content-Disposition", "inline; filename=" + fileName);
				} else {
					response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				}
			}

			if (contentAccess instanceof ContentStreamAccess) {
				InputStream stream = null;

				try {
					stream = ((ContentStreamAccess) contentAccess).getStream();

					if (stream == null) {
						throw new SystemException(
								"ContentAccess returned null stream, access=" + contentAccess);
					}

					StreamUtil.copy(stream, response.getOutputStream());
				} finally {
					StreamUtil.safeClose(stream);
				}
			} else {
				byte[] bytes = contentAccess.getBytes();

				if (bytes == null) {
					throw new SystemException(
							"ContentAccess returned null data, access=" + contentAccess);
				}

				response.getOutputStream().write(bytes);
			}
		}
	}

	/**
	 * @return true if content can be cached.
	 */
	public boolean isCacheable() {
		return cacheable;
	}

	/**
	 * @param cacheable set true if the content can be cached.
	 */
	public void setCacheable(final boolean cacheable) {
		this.cacheable = cacheable;
	}

	/**
	 * Indicates whether the content should be displayed inline or downloaded.
	 *
	 * @return true if the content should be displayed inline, false if it should be downloaded.
	 */
	public boolean isDisplayInline() {
		return displayInline;
	}

	/**
	 * Sets whether the content should be displayed inline or downloaded.
	 *
	 * @param displayInline true if the content should be displayed inline, false if it should be downloaded.
	 */
	public void setDisplayInline(final boolean displayInline) {
		this.displayInline = displayInline;
	}
}
