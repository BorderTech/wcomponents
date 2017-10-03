package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WRepeater.SubUIContext;
import com.github.bordertech.wcomponents.container.InterceptorComponent;
import com.github.bordertech.wcomponents.container.PageShellInterceptor;
import com.github.bordertech.wcomponents.container.TemplateRenderInterceptor;
import com.github.bordertech.wcomponents.container.TransformXMLInterceptor;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.TreeUtil;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.CodePointTranslator;
import org.apache.commons.lang3.text.translate.LookupTranslator;

/**
 * WComponent and HTML related utility methods.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public final class WebUtilities {

	/**
	 * HTML Content Type.
	 */
	public static final String CONTENT_TYPE_HTML = "text/html";

	/**
	 * Javascript Content Type.
	 */
	public static final String CONTENT_TYPE_JS = "application/javascript";

	/**
	 * CSS Content Type.
	 */
	public static final String CONTENT_TYPE_CSS = "text/css";

	/**
	 * XML Content Type.
	 */
	public static final String CONTENT_TYPE_XML = "text/xml; charset=utf-8";

	/**
	 * JSON Content Type.
	 */
	public static final String CONTENT_TYPE_JSON = "application/json";

	// These are standard XML escape sequences. See
	// http://www.w3.org/TR/2006/REC-xml-20060816/#dt-escape
	/**
	 * The HTML escape sequence for a double quote (").
	 */
	public static final String QUOT_ESCAPE = "&quot;";

	/**
	 * The HTML escape sequence for an ampersand (&amp;).
	 */
	public static final String AMP_ESCAPE = "&amp;";

	/**
	 * The HTML escape sequence for an open bracket (&#123;).
	 */
	public static final String OPEN_BRACKET_ESCAPE = "&#123;";

	/**
	 * The HTML escape sequence for a close bracket (&#125;).
	 */
	public static final String CLOSE_BRACKET_ESCAPE = "&#125;";

	/**
	 * The HTML escape sequence for an open bracket with ampersand double escaped (&#123;).
	 */
	public static final String OPEN_BRACKET_DOUBLE_ESCAPE = "&amp;#123;";

	/**
	 * The HTML escape sequence for a close bracket with ampersand double escaped (&#125;).
	 */
	public static final String CLOSE_BRACKET_DOUBLE_ESCAPE = "&amp;#125;";

	/**
	 * The HTML escape sequence for less than (&lt;).
	 */
	public static final String LT_ESCAPE = "&lt;";

	/**
	 * The HTML escape sequence for greater than (&gt;).
	 */
	public static final String GT_ESCAPE = "&gt;";

	/**
	 * Counter used in combination with a timestamp to make random string.
	 */
	private static final AtomicLong ATOMIC_COUNT = new AtomicLong();

	/**
	 * Used to doubly encode template tokens.
	 */
	private static final CharSequenceTranslator DOUBLE_ENCODE_BRACKETS = new LookupTranslator(
			new String[][]{
				{OPEN_BRACKET_ESCAPE, OPEN_BRACKET_DOUBLE_ESCAPE},
				{CLOSE_BRACKET_ESCAPE, CLOSE_BRACKET_DOUBLE_ESCAPE}
			});

	/**
	 * Used to decode doubly-encoded template tokens.
	 */
	private static final CharSequenceTranslator DOUBLE_DECODE_BRACKETS = new LookupTranslator(
			new String[][]{
				{OPEN_BRACKET_DOUBLE_ESCAPE, OPEN_BRACKET_ESCAPE},
				{CLOSE_BRACKET_DOUBLE_ESCAPE, CLOSE_BRACKET_ESCAPE}
			});

	/**
	 * Used to encode template tokens.
	 */
	private static final CharSequenceTranslator ENCODE_BRACKETS = new LookupTranslator(
			new String[][]{
				{"{", OPEN_BRACKET_ESCAPE},
				{"}", CLOSE_BRACKET_ESCAPE}
			});

	/**
	 * Used to decode template tokens.
	 */
	private static final CharSequenceTranslator DECODE_BRACKETS = new LookupTranslator(
			new String[][]{
				{OPEN_BRACKET_ESCAPE, "{"},
				{CLOSE_BRACKET_ESCAPE, "}"}
			});

	/**
	 * Used to decode any escaped characters.
	 */
	private static final CharSequenceTranslator DECODE = new LookupTranslator(
			new String[][]{
				{LT_ESCAPE, "<"},
				{GT_ESCAPE, ">"},
				{AMP_ESCAPE, "&"},
				{QUOT_ESCAPE, "\""},
				{OPEN_BRACKET_ESCAPE, "{"},
				{CLOSE_BRACKET_ESCAPE, "}"}
			});

	/**
	 * Used to encode characters as necessary.
	 */
	private static final CharSequenceTranslator ENCODE = new AggregateTranslator(
			new LookupTranslator(
					new String[][]{
						{"<", LT_ESCAPE},
						{">", GT_ESCAPE},
						{"&", AMP_ESCAPE},
						{"\"", QUOT_ESCAPE},
						{"{", OPEN_BRACKET_ESCAPE},
						{"}", CLOSE_BRACKET_ESCAPE}
					}),
			WebUtilities.NumericEntityIgnorer.between(0x00, 0x08),
			WebUtilities.NumericEntityIgnorer.between(0x0b, 0x0c),
			WebUtilities.NumericEntityIgnorer.between(0x0e, 0x1f)
	);

	/**
	 * Prevent instantiation of this class.
	 */
	private WebUtilities() {
	}

	/**
	 * @return the project version of WComponents.
	 */
	public static String getProjectVersion() {
		String version = ConfigurationProperties.getProjectVersion();
		if (version == null) {
			throw new SystemException("The project version parameter has not been defined.");
		}
		return version;
	}

	/**
	 * Retrieves a "path" of component classes from the given component to the root node. The path is formatted with one
	 * component on each line, with the first line being the root node.
	 *
	 * @param component the component to retrieve the path for
	 * @return a "path" of class names from the component to the root.
	 */
	public static String getPathToRoot(final WComponent component) {
		StringBuffer buf = new StringBuffer();

		for (WComponent node = component; node != null; node = node.getParent()) {
			if (buf.length() != 0) {
				buf.insert(0, '\n');
			}

			buf.insert(0, node.getClass().getName());
		}

		return buf.toString();
	}

	/**
	 * Attempts to find a component which is an ancestor of the given component, and that is assignable to the given
	 * class.
	 *
	 * @param clazz the class to look for
	 * @param comp the component to start at.
	 * @return the matching ancestor, if found, otherwise null.
	 *
	 * @param <T> the ancestor class
	 */
	public static <T> T getAncestorOfClass(final Class<T> clazz, final WComponent comp) {
		if (comp == null || clazz == null) {
			return null;
		}

		WComponent parent = comp.getParent();
		while (parent != null) {
			if (clazz.isInstance(parent)) {
				return (T) parent;
			}
			parent = parent.getParent();
		}

		return null;
	}

	/**
	 * Attempts to find the nearest component (may be the component itself) that is assignable to the given class.
	 *
	 * @param clazz the class to look for
	 * @param comp the component to start at.
	 * @return the component or matching ancestor, if found, otherwise null.
	 *
	 * @param <T> the class to find
	 */
	public static <T> T getClosestOfClass(final Class<T> clazz, final WComponent comp) {
		if (comp == null) {
			return null;
		}

		if (clazz.isInstance(comp)) {
			return (T) comp;
		}

		return getAncestorOfClass(clazz, comp);
	}

	/**
	 * Retrieves the top-level WComponent in the tree.
	 *
	 * @param comp the component branch to start from.
	 * @return the top-level WComponent in the tree.
	 */
	public static WComponent getTop(final WComponent comp) {
		WComponent top = comp;

		for (WComponent parent = top.getParent(); parent != null; parent = parent.getParent()) {
			top = parent;
		}

		return top;
	}

	/**
	 * Encode URL for XML.
	 *
	 * @param urlStr the URL to escape
	 * @return the URL percent encoded
	 */
	public static String encodeUrl(final String urlStr) {
		if (Util.empty(urlStr)) {
			return urlStr;
		}
		// Percent Encode
		String percentEncode = percentEncodeUrl(urlStr);
		// XML Enocde
		return encode(percentEncode);
	}

	/**
	 * Percent encode a URL to include in HTML.
	 *
	 * @param urlStr the URL to escape
	 * @return the URL percent encoded
	 */
	public static String percentEncodeUrl(final String urlStr) {
		if (Util.empty(urlStr)) {
			return urlStr;
		}

		try {
			// Avoid double encoding
			String decode = URIUtil.decode(urlStr);
			URI uri = new URI(decode, false);
			return uri.getEscapedURIReference();
		} catch (Exception e) {
			return urlStr;
		}
	}

	/**
	 * Escapes the given string to make it presentable in a URL. This follows RFC 3986, with some extensions for UTF-8.
	 *
	 * @param input the String to escape.
	 * @return an escaped copy of the string.
	 */
	public static String escapeForUrl(final String input) {
		if (input == null || input.length() == 0) {
			return input;
		}

		final StringBuilder buffer = new StringBuilder(input.length() * 2); // worst-case
		char[] characters = input.toCharArray();

		for (int i = 0, len = input.length(); i < len; ++i) {
			final char ch = characters[i];

			// Section 2.3 - Unreserved chars
			if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')
					|| ch == '-' || ch == '_' || ch == '.' || ch == '~') {
				buffer.append(ch);
			} else if (ch <= 127) { // Other ASCII characters must be escaped
				final String hexString = Integer.toHexString(ch);

				if (hexString.length() == 1) {
					buffer.append("%0").append(hexString);
				} else {
					buffer.append('%').append(hexString);
				}
			} else if (ch <= 0x07FF) { // Other non-ASCII chars must be UTF-8 encoded
				buffer.append('%').append(Integer.toHexString(0xc0 | (ch >> 6)));
				buffer.append('%').append(Integer.toHexString(0x80 | (ch & 0x3F)));
			} else {
				buffer.append('%').append(Integer.toHexString(0xe0 | (ch >> 12)));
				buffer.append('%').append(Integer.toHexString(0x80 | ((ch >> 6) & 0x3F)));
				buffer.append('%').append(Integer.toHexString(0x80 | (ch & 0x3F)));
			}
		}

		return buffer.toString();
	}

	/**
	 * Encode all the special characters found in the given string to their escape sequences according to the XML
	 * specification, and returns the resultant string. Eg. "cat&amp;dog &gt; ant" becomes "cat&amp;amp;dog &amp;gt;
	 * ant".
	 *
	 * @param input the String to encode
	 * @return an encoded copy of the input String.
	 */
	public static String encode(final String input) {
		if (input == null || input.length() == 0) {
			return input;
		}
		return ENCODE.translate(input);
	}

	/**
	 * <p>
	 * Encodes the given fileName for output in the HTTP Content-Disposition header.</p>
	 *
	 * <p>
	 * This is a complete mess. There are multiple standards (rfc 2183, rfc 2231, rfc 5987) and some browsers don't even
	 * adhere to those properly. We take a cautious approach and just replace all extended characters with
	 * underscores.</p>
	 *
	 * @param fileName the file name to encode.
	 * @return the encoded file name.
	 */
	public static String encodeForContentDispositionHeader(final String fileName) {
		return fileName.replaceAll("[^A-Za-z0-9_\\. -]", "_");
	}

	/**
	 * This method is required on occasion because WebSphere Portal by default escapes "&lt;" and "&gt;" characters for
	 * security reasons.
	 *
	 * Decode any escape sequences to their original character, and return the resultant string.
	 *
	 * Eg. "cat&amp;amp;dog &amp;gt; ant" becomes "cat&amp;dog &gt; ant"
	 *
	 * @param encoded the String to decode
	 * @return a decoded copy of the input String.
	 */
	public static String decode(final String encoded) {
		if (encoded == null || encoded.length() == 0 || encoded.indexOf('&') == -1) {
			return encoded;
		}
		return DECODE.translate(encoded);
	}

//	/**
//	 * Check if the input String contains brackets.
//	 *
//	 * @param input the String to test if it contains open or closed brackets
//	 * @return true if String contains open or closed brackets
//	 */
//	public static boolean containsBrackets(final String input) {
//		if (Util.empty(input)) {
//			return false;
//		}
//		return input.contains("{") || input.contains("}");
//	}
	/**
	 * Encode open or closed brackets in the input String.
	 *
	 * @param input the String to encode open or closed brackets
	 * @return the String with encoded open or closed brackets
	 */
	public static String encodeBrackets(final String input) {
		if (input == null || input.length() == 0) {  // For performance reasons don't use Util.empty
			return input;
		}
		return ENCODE_BRACKETS.translate(input);
	}

	/**
	 * Decode open or closed brackets in the input String.
	 *
	 * @param input the String to decode open or closed brackets
	 * @return the String with decode open or closed brackets
	 */
	public static String decodeBrackets(final String input) {
		if (input == null || input.length() == 0) {  // For performance reasons don't use Util.empty
			return input;
		}
		return DECODE_BRACKETS.translate(input);
	}

	/**
	 * Double encode open or closed brackets in the input String.
	 *
	 * @param input the String to double encode open or closed brackets
	 * @return the String with double encoded open or closed brackets
	 */
	public static String doubleEncodeBrackets(final String input) {
		if (input == null || input.length() == 0) {  // For performance reasons don't use Util.empty
			return input;
		}
		return DOUBLE_ENCODE_BRACKETS.translate(input);
	}

	/**
	 * Decode double encoded open or closed brackets in the input String.
	 *
	 * @param input the String to decode double encoded open or closed brackets
	 * @return the String with decoded double encoded open or closed brackets
	 */
	public static String doubleDecodeBrackets(final String input) {
		if (input == null || input.length() == 0) {  // For performance reasons don't use Util.empty
			return input;
		}
		return DOUBLE_DECODE_BRACKETS.translate(input);
	}

	/**
	 * Adds GET parameters to a path.
	 *
	 * @param url the existing url path
	 * @param parameters are put into the URL as get parameters.
	 * @return the complete url eg http://localhost/app?step=1
	 */
	public static String getPath(final String url, final Map<String, String> parameters) {
		return getPath(url, parameters, false);
	}

	/**
	 * Adds GET parameters to a path.
	 *
	 * @param url the existing url path
	 * @param parameters are put into the URL as get parameters.
	 * @param javascript true if the URL is intended for use within a javascript function
	 * @return the complete url eg http://localhost/app?step=1
	 */
	public static String getPath(final String url, final Map<String, String> parameters,
			final boolean javascript) {
		// Have we already got some parameters?
		int index = url.indexOf('?');
		boolean hasVars = false;

		if (index != -1) {
			hasVars = true;
		}

		StringBuffer vars = new StringBuffer();

		if (parameters != null) {
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				if (javascript) {
					appendGetParamForJavascript(key, value, vars, hasVars);
				} else {
					appendGetParam(key, value, vars, hasVars);
				}

				hasVars = true;
			}
		}

		return url + vars.toString();
	}

	/**
	 * This is a slightly different version of appendGetParam that doesn't encode the ampersand seperator. It is
	 * intended to be used in urls that are generated for javascript functions.
	 *
	 * @param key the key to append
	 * @param value the value to append
	 * @param vars the existing query string
	 * @param existingVars true if there are already existing query string key/value pairs
	 */
	public static void appendGetParamForJavascript(final String key, final String value,
			final StringBuffer vars, final boolean existingVars) {
		vars.append(existingVars ? '&' : '?');
		vars.append(key).append('=').append(WebUtilities.escapeForUrl(value));
	}

	/**
	 * <p>
	 * Appends a key/value pair to a query string.</p>
	 *
	 * <p>
	 * A '{@literal ?}' or '{@literal &}' token will first be appended to the end of the vars StringBuffer, according to
	 * the presence of other vars. We quote the '{@literal &}' using XML character entity, because otherwise the
	 * resulting URL will be invalid XML parsed character data and so we can't generate XHTML.</p>
	 *
	 * @param key the key to append
	 * @param value the value to append
	 * @param vars the existing query string
	 * @param existingVars true if there are already existing query string key/value pairs
	 */
	public static void appendGetParam(final String key, final String value,
			final StringBuffer vars, final boolean existingVars) {
		vars.append(existingVars ? "&amp;" : "?");
		vars.append(key).append('=').append(WebUtilities.escapeForUrl(value));
	}

	/**
	 * Generates a random String. Can be useful for creating unique URLs by adding the String as a query parameter to
	 * the URL.
	 *
	 * @return a random string
	 */
	public static String generateRandom() {
		long next = ATOMIC_COUNT.incrementAndGet();
		StringBuffer random = new StringBuffer();
		random.append(new Date().getTime()).append('-').append(next);
		return random.toString();
	}

	/**
	 * Indicates whether a component is an ancestor of another.
	 *
	 * @param component1 a possible ancestor.
	 * @param component2 the component to check.
	 * @return true if <code>component1</code> is an ancestor of <code>component2</code>, false otherwise.
	 */
	public static boolean isAncestor(final WComponent component1, final WComponent component2) {
		for (WComponent parent = component2.getParent(); parent != null; parent = parent.getParent()) {
			if (parent == component1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Indicates whether a component is a descendant of another.
	 *
	 * @param component1 a possible descendent.
	 * @param component2 the component to check.
	 * @return true if <code>component1</code> is a descendant of <code>component2</code>, false otherwise.
	 */
	public static boolean isDescendant(final WComponent component1, final WComponent component2) {
		return isAncestor(component2, component1);
	}

	/**
	 * Returns the context for the given component.
	 *
	 * @param uic the current user's UIContext.
	 * @param component the component to retrieve its context for.
	 * @return the context for the given component.
	 * @deprecated Badly named Method. Use {@link #getContextForComponent(com.github.bordertech.wcomponents.WComponent)}
	 * instead
	 */
	@Deprecated
	public static UIContext getPrimaryContext(final UIContext uic, final WComponent component) {
		return getContextForComponent(component);
	}

	/**
	 * Returns the context for this component. The component may not be in the current context.
	 *
	 * @param component the component to find the context it belongs to
	 * @return the component's context
	 */
	public static UIContext getContextForComponent(final WComponent component) {
		// Start with the current Context
		UIContext result = UIContextHolder.getCurrent();
		// Go through the contexts until we find the component
		while (result instanceof SubUIContext && !((SubUIContext) result).isInContext(component)) {
			result = ((SubUIContext) result).getBacking();
		}
		return result;
	}

	/**
	 * Finds a component by its id.
	 * <p>
	 * Searches visible and not visible components.
	 * </p>
	 *
	 * @param id the id of the component to search for.
	 * @return the component and context for the given id, or null if not found.
	 */
	public static ComponentWithContext getComponentById(final String id) {
		return getComponentById(id, false);
	}

	/**
	 * Finds a component by its id.
	 *
	 * @param id the id of the component to search for.
	 * @param visibleOnly true if process visible only
	 * @return the component and context for the given id, or null if not found.
	 */
	public static ComponentWithContext getComponentById(final String id, final boolean visibleOnly) {
		UIContext uic = UIContextHolder.getCurrent();
		WComponent root = uic.getUI();
		ComponentWithContext comp = TreeUtil.getComponentWithContextForId(root, id, visibleOnly);
		return comp;
	}

	/**
	 * Finds the closest context for the given component id. This handles the case where the component no longer exists
	 * due to having been removed from the UI, or having a SubUIContext removed.
	 *
	 * @param id the id of the component to search for.
	 * @return the component and context for the given id, or null if not found.
	 */
	public static UIContext findClosestContext(final String id) {
		UIContext uic = UIContextHolder.getCurrent();
		WComponent root = uic.getUI();
		UIContext closest = TreeUtil.getClosestContextForId(root, id);
		return closest;
	}

	/**
	 * Updates the bean value with the current value of the component and all its bean-bound children.
	 *
	 * @param component the component whose contents need to be copied to the bean.
	 * @param visibleOnly - whether to include visible components only.
	 */
	public static void updateBeanValue(final WComponent component, final boolean visibleOnly) {
		// Do not process if component is invisble and ignore visible is true. Will ignore entire branch from this point.
		if (!component.isVisible() && visibleOnly) {
			return;
		}

		if (component instanceof WBeanComponent) {
			((WBeanComponent) component).updateBeanValue();
		}

		// These components recursively update bean values themselves,
		// as they have special requirements due to repeating data.
		if (component instanceof WDataTable || component instanceof WTable || component instanceof WRepeater) {
			return;
		}

		if (component instanceof Container) {
			for (int i = ((Container) component).getChildCount() - 1; i >= 0; i--) {
				updateBeanValue(((Container) component).getChildAt(i), visibleOnly);
			}
		}
	}

	/**
	 * Updates the bean value with the current value of the component and all its bean-bound children. By default this
	 * method will only process visible components.
	 *
	 * @param component the component whose contents need to be copied to the bean.
	 */
	public static void updateBeanValue(final WComponent component) {
		updateBeanValue(component, true);
	}

	/**
	 * Renders the given WComponent to a String outside of the context of a Servlet. This is good for getting hold of
	 * the XML for debugging, unit testing etc. Also it is good for using the WComponent framework as a more generic
	 * templating framework.
	 *
	 * @param component the root WComponent to render.
	 * @return the rendered output as a String.
	 */
	public static String render(final WComponent component) {
		return render(new MockRequest(), component);
	}

	/**
	 * Renders the given WComponent to a String outside of the context of a Servlet. This is good for getting hold of
	 * the XML for debugging, unit testing etc. Also it is good for using the WComponent framework as a more generic
	 * templating framework.
	 *
	 * @param request the request being responded to.
	 * @param component the root WComponent to render.
	 * @return the rendered output as a String.
	 */
	public static String render(final Request request, final WComponent component) {
		boolean needsContext = UIContextHolder.getCurrent() == null;

		if (needsContext) {
			UIContextHolder.pushContext(new UIContextImpl());
		}

		try {
			StringWriter buffer = new StringWriter();

			component.preparePaint(request);
			try (PrintWriter writer = new PrintWriter(buffer)) {
				component.paint(new WebXmlRenderContext(writer));
			}
			return buffer.toString();
		} finally {
			if (needsContext) {
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * Renders and transforms the given WComponent to a HTML String outside of the context of a Servlet.
	 *
	 * @param component the root WComponent to render
	 * @return the rendered output as a String
	 */
	public static String renderWithTransformToHTML(final WComponent component) {
		return renderWithTransformToHTML(new MockRequest(), component, true);
	}

	/**
	 * Renders and transforms the given WComponent to a HTML String outside of the context of a Servlet.
	 *
	 * @param request the request being responded to
	 * @param component the root WComponent to render
	 * @param includePageShell true if include page shell
	 * @return the rendered output as a String.
	 */
	public static String renderWithTransformToHTML(final Request request, final WComponent component, final boolean includePageShell) {

		// Setup a context (if needed)
		boolean needsContext = UIContextHolder.getCurrent() == null;
		if (needsContext) {
			UIContextHolder.pushContext(new UIContextImpl());
		}

		try {

			// Link Interceptors
			InterceptorComponent templateRender = new TemplateRenderInterceptor();
			InterceptorComponent transformXML = new TransformXMLInterceptor();
			templateRender.setBackingComponent(transformXML);
			if (includePageShell) {
				transformXML.setBackingComponent(new PageShellInterceptor());
			}

			// Attach Component and Mock Response
			InterceptorComponent chain = templateRender;
			chain.attachUI(component);
			chain.attachResponse(new MockResponse());

			// Render chain
			StringWriter buffer = new StringWriter();
			chain.preparePaint(request);
			try (PrintWriter writer = new PrintWriter(buffer)) {
				chain.paint(new WebXmlRenderContext(writer));
			}
			return buffer.toString();
		} finally {
			if (needsContext) {
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * Attempts to guess the content-type for the given file name.
	 *
	 * @param fileName the file name to return the content-type for.
	 * @return the content-type for the given fileName, or a generic type if unknown.
	 */
	public static String getContentType(final String fileName) {

		if (Util.empty(fileName)) {
			return ConfigurationProperties.getDefaultMimeType();
		}

		String mimeType = null;

		if (fileName.lastIndexOf('.') > -1) {
			String suffix = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
			mimeType = ConfigurationProperties.getFileMimeTypeForExtension(suffix);
		}

		if (mimeType == null) {
			mimeType = URLConnection.guessContentTypeFromName(fileName);
			if (mimeType == null) {
				mimeType = ConfigurationProperties.getDefaultMimeType();
			}
		}

		return mimeType;
	}

	/**
	 * Determine if this component is an active naming context.
	 * <p>
	 * Can only be considered active if an id name has been set and flagged as a naming context.
	 * </p>
	 *
	 * @param component the component to test for naming context
	 * @return true if component is an active naming context
	 */
	public static boolean isActiveNamingContext(final WComponent component) {
		if (component instanceof NamingContextable) {
			NamingContextable naming = (NamingContextable) component;
			boolean active = naming.isNamingContext() && naming.getIdName() != null;
			return active;
		}
		return false;
	}

	/**
	 * Get this component's parent naming context.
	 *
	 * @param component the component to process
	 * @return true the parent naming context or null
	 */
	public static NamingContextable getParentNamingContext(final WComponent component) {
		if (component == null) {
			return null;
		}

		WComponent child = component;
		NamingContextable parent = null;
		while (true) {
			NamingContextable naming = WebUtilities.getAncestorOfClass(NamingContextable.class,
					child);
			if (naming == null) {
				break;
			}
			if (WebUtilities.isActiveNamingContext(naming)) {
				parent = naming;
				break;
			}
			child = naming;
		}

		return parent;
	}

	/**
	 * <p>
	 * Implementation of the CodePointTranslator to throw away the matching characters. This is copied from
	 * org.apache.commons.lang3.text.translate.NumericEntityEscaper, but has been changed to discard the characters
	 * rather than attempting to encode them.</p>
	 * <p>
	 * Discarding the characters is necessary because certain invalid characters (e.g. decimal 129) cannot be encoded
	 * for HTML. An existing library was not available for this function because no HTML page should ever contain these
	 * characters.</p>
	 */
	public static final class NumericEntityIgnorer extends CodePointTranslator {

		private final int below;
		private final int above;
		private final boolean between;

		/**
		 * <p>
		 * Constructs a <code>NumericEntityEscaper</code> for the specified range. This is the underlying method for the
		 * other constructors/builders. The <code>below</code> and <code>above</code> boundaries are inclusive when
		 * <code>between</code> is <code>true</code> and exclusive when it is <code>false</code>. </p>
		 *
		 * @param below int value representing the lowest codepoint boundary
		 * @param above int value representing the highest codepoint boundary
		 * @param between whether to escape between the boundaries or outside them
		 */
		private NumericEntityIgnorer(final int below, final int above, final boolean between) {
			this.below = below;
			this.above = above;
			this.between = between;
		}

		/**
		 * <p>
		 * Constructs a <code>NumericEntityEscaper</code> between the specified values (inclusive). </p>
		 *
		 * @param codepointLow above which to escape
		 * @param codepointHigh below which to escape
		 * @return the newly created {@code NumericEntityEscaper} instance
		 */
		public static NumericEntityIgnorer between(final int codepointLow, final int codepointHigh) {
			return new NumericEntityIgnorer(codepointLow, codepointHigh, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean translate(final int codepoint, final Writer out) throws IOException {
			if (between) {
				if (codepoint < below || codepoint > above) {
					return false;
				}
			} else if (codepoint >= below && codepoint <= above) {
				return false;
			}
// Commented out from org.apache.commons.lang3.text.translate.NumericEntityEscaper
// these characters cannot be handled in any way - write no output.

//			out.write("&#");
//			out.write(Integer.toString(codepoint, 10));
//			out.write(';');
//			if (LOG.isWarnEnabled()) {
//				LOG.warn("Illegal HTML character stripped from XML. codepoint=" + codepoint);
//			}
			return true;
		}
	}
}
