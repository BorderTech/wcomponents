package com.github.bordertech.wcomponents.util;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.ScanException;

/**
 * Basic HTML input sanitizer for WTextArea in RichText mode. Could be used for other HTML sanitization purposes.
 * Uses the AntiSamy HTML sanitizer. The AntiSamy configuration file's location is set using WComponents property
 * "com.github.bordertech.wcomponents.AntiSamy.config". The default policy works with the default implementation of
 * timyMCE used by WTextArea and is quite strict.
 *
 * @author Mark Reeves
 * @since 1.2.0
 */
public final class HtmlSanitizer {
	/**
	 * The log for this instance.
	 */
	private static final Log LOG = LogFactory.getLog(HtmlSanitizer.class);

	/**
	 * The AntiSamy instance used by this class. Everything is static because we really want this to be a singleton.
	 */
	private static final AntiSamy ANTISAMY;

	/**
	 * The parameter name used to get the AntiSamy configuration file.
	 */
	private static final String CONFIG_PARAM = "com.github.bordertech.wcomponents.AntiSamy.config";

	static {
		String path = Config.getInstance().getString(CONFIG_PARAM,
				"com/github/bordertech/wcomponents/sanitizers/antisamy-wc.xml");
		AntiSamy antiLocal = null;

		try {
			File config = new File(HtmlSanitizer.class.getClassLoader().getResource(path).toURI());
			Policy policy = Policy.getInstance(config);
			antiLocal = new AntiSamy(policy);
		} catch (PolicyException | URISyntaxException ex) {
			LOG.error("Could not create AntiSamy Policy. ", ex);
		}

		if (antiLocal == null) {
			LOG.error("AntiSamy instance not created.");
		}
		ANTISAMY = antiLocal;
	}

	/**
	 * Prevent instantiation of this class.
	 */
	private HtmlSanitizer() {
	}

	/**
	 * Apply sanitization rules to a HTML string.
	 *
	 * @param input the (potentially) tainted HTML to sanitize
	 * @return sanitized HTML
	 * @throws NullPointerException thrown if AntiSamy instance is not correctly instantiated.
	 * @throws Exception thrown if the AntiSamy scan fails.
	 */
	public static String sanitize(final String input) throws NullPointerException, Exception {
		if (Util.empty(input)) {
			return input;
		}

		if (ANTISAMY == null) {
			LOG.error("AntiSamy instance not created.");
			throw new NullPointerException("Attempt to use uninstantiated instance of AntiSamy sanitizer.");
		}

		try {
			CleanResults results = ANTISAMY.scan(input);

			List<String> errors = results.getErrorMessages();
			if (CollectionUtils.isNotEmpty(errors)) {
				LOG.info("Errors encountered while sanitizing HTML input/output: "
						+ StringUtils.join(results.getErrorMessages(), "\n"));
			}

			LOG.debug("Sanitization time: " + String.valueOf(results.getScanTime()));
			return results.getCleanHTML();
		} catch (ScanException | PolicyException ex) {
			LOG.error("Cannot sanitize HTML: ", ex);
			throw new Exception("Cannot sanitize input HTML", ex);
		}
	}
}
