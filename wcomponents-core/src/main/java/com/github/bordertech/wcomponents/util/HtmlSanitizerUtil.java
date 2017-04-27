package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.WebUtilities;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
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
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(HtmlSanitizerUtil.class);

	/**
	 * The AntiSamy instance used by this class. Everything is static because we really want this to be a singleton.
	 */
	private static final AntiSamy ANTISAMY = new AntiSamy();

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
		// Get the strict AntiSamy policy.
		try {
			String path = ConfigurationProperties.getAntisamyStrictConfigurationFile();
			STRICT_POLICY = Policy.getInstance(HtmlSanitizerUtil.class.getClassLoader().getResource(path));
		} catch (PolicyException ex) {
			throw new SystemException("Could not create strict AntiSamy Policy. " + ex.getMessage(), ex);
		}

		// Get the lax AntiSamy policy.
		try {
			String path = ConfigurationProperties.getAntisamyLaxConfigurationFile();
			LAX_POLICY = Policy.getInstance(HtmlSanitizerUtil.class.getClassLoader().getResource(path));
		} catch (PolicyException ex) {
			throw new SystemException("Could not create lax AntiSamy Policy. " + ex.getMessage(), ex);
		}
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
	 */
	public static String sanitize(final String input) {
		return sanitize(input, false);
	}

	/**
	 * Apply sanitization rules to a HTML string.
	 *
	 * @param input the (potentially) tainted HTML to sanitize
	 * @param lax if true use the lax policy, otherwise use the strict policy
	 * @return sanitized HTML
	 */
	public static String sanitize(final String input, final boolean lax) {
		return sanitize(input, lax ? LAX_POLICY : STRICT_POLICY);
	}

	/**
	 * Apply sanitization rules to a HTML string.
	 *
	 * @param input the (potentially) tainted HTML to sanitize
	 * @param policy the AntiSamy policy to apply
	 * @return sanitized HTML
	 */
	public static String sanitize(final String input, final Policy policy) {
		if (policy == null) {
			throw new SystemException("AntiSamy policy cannot be null");
		}
		if (Util.empty(input)) {
			return input;
		}
		try {
			CleanResults results = ANTISAMY.scan(input, policy);
			String result = results.getCleanHTML();
			// Escape brackets for handlebars
			result = WebUtilities.encodeBrackets(result);
			return result;
		} catch (ScanException | PolicyException ex) {
			throw new SystemException("Cannot sanitize " + ex.getMessage(), ex);
		}
	}

	/**
	 * @param text the output text to sanitize
	 * @return the sanitized text
	 */
	public static String sanitizeOutputText(final String text) {
		if (Util.empty(text)) {
			return text;
		}
		return sanitize(text, true);
	}

	/**
	 * @param text the input text to sanitize
	 * @return the sanitized text
	 */
	public static String sanitizeInputText(final String text) {
		if (Util.empty(text)) {
			return text;
		}
		return sanitize(text);
	}

	/**
	 * Create a Policy from a named local resource.
	 *
	 * @param resourceName the path to AntiSamy policy file
	 * @return the AntiSamy Policy
	 */
	public static Policy createPolicy(final String resourceName) {
		if (StringUtils.isBlank(resourceName)) {
			throw new SystemException("AntiSamy Policy resourceName cannot be null ");
		}
		URL resource = HtmlSanitizerUtil.class.getClassLoader().getResource(resourceName);
		if (resource == null) {
			throw new SystemException("Could not find AntiSamy Policy XML resource.");
		}
		try {
			return Policy.getInstance(resource);
		} catch (PolicyException ex) {
			throw new SystemException("Could not create AntiSamy Policy" + ex.getMessage(), ex);
		}
	}

}
