package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.StreamUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Plain text template renderer.
 * <p>
 * Used for templates that require no processing (ie static) and have no tagged components or parameters.
 * </p>
 * <p>
 * Has the following engine options:-
 * </p>
 * <ul>
 * <li>{@link #XML_ENCODE} - Include to activate</li>
 * </ul>
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public class PlainTextRendererImpl implements TemplateRenderer {

	/**
	 * Cache name.
	 */
	private static final String CACHE_NAME = "wc-plaintext-templates";

	/**
	 * XML encode the template engine option.
	 */
	public static final String XML_ENCODE = "encode";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(PlainTextRendererImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderTemplate(final String templateName, final Map<String, Object> context, final Map<String, WComponent> taggedComponents, final Writer writer, final Map<String, Object> options) {

		LOG.debug("Rendering plain text template " + templateName);

		// Expects path to be absolute.
		String name = templateName.startsWith("/") ? templateName : "/" + templateName;

		boolean xmlEncode = options.containsKey(XML_ENCODE);

		String cacheKey = templateName + "-" + xmlEncode;
		InputStream stream = null;

		try {
			String output = null;
			if (isCaching()) {
				output = getCache().get(cacheKey);
			}
			if (output == null) {
				stream = getClass().getResourceAsStream(name);
				if (stream == null) {
					throw new SystemException("Could not find plain text template [" + templateName + "].");
				}
				output = new String(StreamUtil.getBytes(stream));
				if (xmlEncode) {
					output = WebUtilities.encode(output);
				}
				if (isCaching()) {
					getCache().put(cacheKey, output);
				}
			}
			writer.write(output);
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw new SystemException("Problems with plain text template [" + templateName + "]. " + e.getMessage(), e);
		} finally {
			StreamUtil.safeClose(stream);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderInline(final String templateInline, final Map<String, Object> context, final Map<String, WComponent> taggedComponents, final Writer writer, final Map<String, Object> options) {

		LOG.debug("Rendering inline plain text template.");

		boolean xmlEncode = options.containsKey(XML_ENCODE);

		try {
			String output = templateInline;
			if (xmlEncode) {
				output = WebUtilities.encode(output);
			}
			writer.write(output);
		} catch (Exception e) {
			throw new SystemException("Problems with inline plain text template. " + e.getMessage(), e);
		}
	}

	/**
	 * @return the cache instance
	 */
	protected synchronized Cache<String, String> getCache() {
		Cache<String, String> cache = Caching.getCache(CACHE_NAME, String.class, String.class);
		if (cache == null) {
			final CacheManager mgr = Caching.getCachingProvider().getCacheManager();
			MutableConfiguration<String, String> config = new MutableConfiguration<>();
			config.setTypes(String.class, String.class);
			config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.HOURS, 12)));
			// No need to serialize the result
			config.setStoreByValue(false);
			cache = mgr.createCache(CACHE_NAME, config);
		}
		return cache;
	}

	/**
	 * @return true if use caching
	 */
	protected boolean isCaching() {
		return Config.getInstance().getBoolean("bordertech.wcomponents.plaintext.cache.enabled", Boolean.TRUE);
	}

}
