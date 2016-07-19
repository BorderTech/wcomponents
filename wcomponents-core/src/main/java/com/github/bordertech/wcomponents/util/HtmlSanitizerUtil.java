package com.github.bordertech.wcomponents.util;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.ScanException;

/**
 * Basic HTML input sanitizer for WTextArea in RichText mode. Could be used for other HTML sanitization purposes. Uses
 * the AntiSamy HTML sanitizer. The default AntiSamy configuration file's location is set using WComponents property
 * "com.github.bordertech.wcomponents.AntiSamy.config". The default policy works with the default implementation of
 * timyMCE used by WTextArea and is quite strict.
 *
 * <p>
 * A method is provided to do lax sanitization. For this the Policy uses an XML config file set by the WComponents
 * property "com.github.bordertech.wcomponents.AntiSamyLax.config". This policy is quite permissive and should
 * <strong>only</strong> be used for output sanitization of unescaped components when the original source of the HTML is
 * unknown.</p>
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
	 * The strict AntiSamy policy. This is the default policy and should be used for all in-bound sanitization.
	 */
	private static final Policy STRICT_POLICY;

	/**
	 * The lax AntiSamy policy. This policy, if used at all, should only be used for sanitizing output where the HTML
	 * being output is of unverified origin.
	 */
	private static final Policy LAX_POLICY;

	static {
		Policy strictPolicy = null;
		Policy laxPolicy = null;

		ANTISAMY = new AntiSamy();

		// Get the strict AntiSamy policy.
		try {
			String path = ConfigurationProperties.getAntisamyStrictConfigurationFile();
			strictPolicy = Policy.getInstance(HtmlSanitizerUtil.class.getClassLoader().getResource(path));
		} catch (PolicyException ex) {
			LOG.error("Could not create strict AntiSamy Policy. ", ex);
		}

		// Get the lax AntiSamy policy.
		try {
			String path = ConfigurationProperties.getAntisamyLaxConfigurationFile();
			laxPolicy = Policy.getInstance(HtmlSanitizerUtil.class.getClassLoader().getResource(path));
		} catch (PolicyException ex) {
			LOG.error("Could not create lax AntiSamy Policy. ", ex);
		}

		STRICT_POLICY = strictPolicy;
		LAX_POLICY = laxPolicy;
	}

	/**
	 * Prevent instantiation of this class.
	 */
	private HtmlSanitizerUtil() {
	}

	/**
	 * Apply strict sanitization rules to a HTML string.
	 *
	 * @param input the (potentially) tainted HTML to sanitize
	 * @return sanitized HTML
	 * @throws ScanException thrown if the AntiSamy scan fails
	 * @throws PolicyException thrown if sanitization fails due to AntiSamy policy problem2
	 */
	public static String sanitize(final String input) throws ScanException, PolicyException {
		return sanitize(input, false);
	}

	/**
	 * Apply sanitization rules to a HTML string.
	 *
	 * @param input the (potentially) tainted HTML to sanitize
	 * @param lax if true use the lax policy, otherwise use the strict policy
	 * @return sanitized HTML
	 * @throws ScanException thrown if the AntiSamy scan fails
	 * @throws PolicyException thrown if sanitization fails due to AntiSamy policy problem
	 */
	public static String sanitize(final String input, final Boolean lax) throws ScanException, PolicyException {
		if (Util.empty(input)) {
			return input;
		}
		Policy policy = (lax && LAX_POLICY != null) ? LAX_POLICY : STRICT_POLICY;

		try {
			CleanResults results = ANTISAMY.scan(input, policy);
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
