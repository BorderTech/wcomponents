package com.github.bordertech.wcomponents.util;

import java.io.File;
import java.net.URISyntaxException;
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
public final class HtmlSanitizerUtil {
	/**
	 * The log for this instance.
	 */
	private static final Log LOG = LogFactory.getLog(HtmlSanitizerUtil.class);

	/**
	 * The AntiSamy instance used by this class. Everything is static because we really want this to be a singleton.
	 */
	private static final AntiSamy ANTISAMY;

	/**
	 * The parameter name used to get the AntiSamy configuration file.
	 */
	private static final String CONFIG_PARAM = "com.github.bordertech.wcomponents.AntiSamy.config";

	/**
	 * We may catch an exception in the static construction phase but ignore it until
	 * the {@link #sanitize(java.lang.String) method is called. This will allow us to
	 * retrieve the exception for later processing.
	 */
	private static Exception exception;

	static {
		String path = Config.getInstance().getString(CONFIG_PARAM,
				"com/github/bordertech/wcomponents/sanitizers/antisamy-wc.xml");
		AntiSamy antiLocal = null;

		try {
			File config = new File(HtmlSanitizerUtil.class.getClassLoader().getResource(path).toURI());
			Policy policy = Policy.getInstance(config);
			antiLocal = new AntiSamy(policy);
		} catch (PolicyException | URISyntaxException ex) {
			LOG.error("Could not create AntiSamy Policy. ", ex);
			exception = ex;
		}

		if (antiLocal == null) {
			LOG.error("AntiSamy instance not created.");
		}
		ANTISAMY = antiLocal;
	}

	/**
	 * Prevent instantiation of this class.
	 */
	private HtmlSanitizerUtil() {
	}

	/**
	 * Apply sanitization rules to a HTML string.
	 *
	 * @param input the (potentially) tainted HTML to sanitize
	 * @return sanitized HTML
	 * @throws ScanException thrown if the AntiSamy scan fails
	 * @throws PolicyException thrown if sanitization fails due to AntiSamy policy problem
	 * @throws HTMLSanitizerException thrown if AntiSamy instance is not correctly instantiated
	 */
	public static String sanitize(final String input) throws ScanException, PolicyException, HTMLSanitizerException {
		if (Util.empty(input)) {
			return input;
		}

		if (ANTISAMY == null) {
			LOG.error("AntiSamy instance not created.");
			throw new HTMLSanitizerException("Cannot sanitize: AntiSamy not initialised.", exception);
		}

		try {
			CleanResults results = ANTISAMY.scan(input);
			return results.getCleanHTML();
		} catch (ScanException ex) {
			LOG.error("Cannot sanitize HTML due to AntiSamy scan exception.", ex);
			throw new ScanException(ex);
		} catch (PolicyException ex) {
			LOG.error("Cannot sanitize HTML due to AntiSamy policy exception.", ex);
			throw new PolicyException(ex);
		}
	}
}
