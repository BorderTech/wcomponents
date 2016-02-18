package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WRepeater.SubUIContext;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.TreeUtil;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.configuration.Configuration;

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
	 * The parameter for the current project version.
	 */
	private static final String PROJECT_VERSION_PARAMETER_KEY = "bordertech.wcomponents.version";

	/**
	 * Prevent instantiation of this class.
	 */
	private WebUtilities() {
	}

	/**
	 * @return the project version of WComponents.
	 */
	public static String getProjectVersion() {
		String version = Config.getInstance().getString(PROJECT_VERSION_PARAMETER_KEY);
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
	 * Escapes the given string to make it presentable in a URL. This follows RFC 3986, with some extensions for UTF-8.
	 *
	 * @param input the String to escape.
	 * @return an escaped copy of the string.
	 */
	public static String escapeForUrl(final String input) {
		if (input == null || input.length() == 0) {
			return input;
		}

		final StringBuffer buffer = new StringBuffer(input.length() * 2); // worst-case

		for (int i = 0; i < input.length(); ++i) {
			final char ch = input.charAt(i);

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

		StringBuffer buffer = new StringBuffer(input.length());

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);

			if (c == '<') {
				buffer.append(LT_ESCAPE);
			} else if (c == '>') {
				buffer.append(GT_ESCAPE);
			} else if (c == '&') {
				buffer.append(AMP_ESCAPE);
			} else if (c == '"') {
				buffer.append(QUOT_ESCAPE);
			} else if (c >= 32 || c == '\n' || c == '\r' || c == '\t') {
				// All other unicode characters can be sent as is, with the
				// exception of control codes, which are illegal
				buffer.append(c);
			}
		}

		return buffer.toString();
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

		String decoded = encoded.replaceAll(LT_ESCAPE, "<")
				.replaceAll(GT_ESCAPE, ">")
				.replaceAll(AMP_ESCAPE, "&")
				.replaceAll(QUOT_ESCAPE, "\"");

		return decoded;
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
	 * Returns the primary context for the given component.
	 *
	 * @param uic the current user's UIContext.
	 * @param component the component to retrieve the primary context for.
	 * @return the primary context for the given component.
	 */
	public static UIContext getPrimaryContext(final UIContext uic, final WComponent component) {
		UIContext result = uic;

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
			PrintWriter writer = new PrintWriter(buffer);
			component.paint(new WebXmlRenderContext(writer));
			writer.close();

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
		Configuration config = Config.getInstance();

		String suffix = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
		String mimeType = config.getString("bordertech.wcomponents.mimeType." + suffix);

		if (mimeType == null) {
			mimeType = URLConnection.guessContentTypeFromName(fileName);
			if (mimeType == null) {
				mimeType = config.getString("bordertech.wcomponents.mimeType.defaultMimeType", "application/octet-stream");
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

}
