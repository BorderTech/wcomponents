package com.github.bordertech.wcomponents.lde;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextDelegate;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.monitor.UicStats;
import com.github.bordertech.wcomponents.monitor.UicStatsAsHtml;
import com.github.bordertech.wcomponents.util.AbstractTreeNode;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.TreeNode;
import com.github.bordertech.wcomponents.velocity.VelocityEngineFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * <p>
 * This component adds a "Developer Toolkit" to the UI.</p>
 *
 * <p>
 * This class is WComponent-like, but doesn't use any of the WComponent classes as we don't want it to get in the way
 * when debugging in the LDE.</p>
 *
 * <p>
 * <b>NOTE:</b> The dev toolkit affects the entire VM, so no effort has been made to make it function correctly when
 * there are multiple users accessing the LDE.</p>.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class DevToolkit {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(DevToolkit.class);

	/**
	 * Indicates whether to display the WComponent UI hierarchy tree view.
	 */
	private boolean showTree = false;
	/**
	 * Indicates whether to display the current WComponent Configuration.
	 */
	private boolean showConfig = false;
	/**
	 * Indicates whether to display statistics on the active UIContext.
	 */
	private boolean showUicStats = false;
	/**
	 * Indicates whether to display information about the current HTTP request.
	 */
	private boolean showRequest = false;
	/**
	 * Indicates whether to display component ids.
	 */
	private boolean showIds = false;

	/**
	 * The current request parameters.
	 */
	private String[][] requestParameters;
	/**
	 * The current request headers.
	 */
	private String[][] requestHeaders;
	/**
	 * The current request method (ie. "GET", "POST", or "PUT".
	 */
	private String requestMethod;

	/**
	 * Determines whether the toolkit is enabled, based on the application Parameters.
	 *
	 * @return true if the toolkit is enabled, false otherwise.
	 */
	public static boolean isEnabled() {
		return Config.getInstance().getBoolean("bordertech.wcomponents.lde.devToolkit.enabled",
				false);
	}

	/**
	 * <p>
	 * Reads the state of the dev toolkit user interface.</p>
	 *
	 * <p>
	 * The toolkit UI is implemented in a separate form with a hidden field to identify it, so any request to the main
	 * WComponent application being run in the LDE should skip this processing.</p>
	 *
	 * @param request the current request being responded to
	 */
	protected void serviceRequest(final HttpServletRequest request) {
		requestParameters = null;
		requestHeaders = null;

		if (!isEnabled()) {
			return;
		} else if (request.getParameter("wc_devToolkit") == null) {
			// The request is for the main WComponent application, so
			// gather information on the HTTP request for display later.
			if (showRequest) {
				requestParameters = getRequestParameters(request);
				requestHeaders = getRequestHeaders(request);
				requestMethod = request.getMethod();
			}

			return;
		}

		showTree = "true".equals(request.getParameter("devToolkit_showTree"));
		showConfig = "true".equals(request.getParameter("devToolkit_showConfig"));
		showUicStats = "true".equals(request.getParameter("devToolkit_showUicStats"));
		showRequest = "true".equals(request.getParameter("devToolkit_showRequest"));
		showIds = "true".equals(request.getParameter("devToolkit_showIds"));

		boolean whitespaceEnabled = "true".equals(request.getParameter(
				"devToolkit_whitespaceFilterEnabled"));
		Config.getInstance().setProperty("bordertech.wcomponents.whitespaceFilter.enabled",
				whitespaceEnabled);

		// devToolkit_refreshPage is no-op
		if (request.getParameter("devToolkit_resetSession") != null) {
			HttpSession session = request.getSession(false);

			if (session != null) {
				session.invalidate();
			}
		}

		if (request.getParameter("devToolkit_rootComponentSelect") != null) {
			Config.getInstance().setProperty(PlainLauncher.COMPONENT_TO_LAUNCH_PARAM_KEY,
					request.getParameter(
							"devToolkit_rootComponent"));
		}

		boolean debug = "true".equals(request.getParameter("devToolkit_debugEnabled"));
		Config.getInstance().setProperty("bordertech.wcomponents.debug.enabled", debug);
		Config.getInstance().setProperty("bordertech.wcomponents.debug.clientSide.enabled", debug);

		// If the toolkit has been used, chances are that the WComponent configuration has changed.
		// Update all configuration listeners.
		Config.notifyListeners();
	}

	/**
	 * Retrieves the headers for the last request. The headers are returned as an array of key-value pairs.
	 *
	 * @return the headers for the current request.
	 */
	public String[][] getRequestHeaders() {
		return requestHeaders;
	}

	/**
	 * Retrieves the HTTP method for the last request.
	 *
	 * @return the HTTP method - "GET", "POST" or "PUT".
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * Reads the request headers from the given request.
	 *
	 * @param request the request to read the headers from.
	 * @return an array of header key-value pairs, sorted by key.
	 */
	private String[][] getRequestHeaders(final HttpServletRequest request) {
		List<String> headerKeys = new ArrayList<>();

		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			headerKeys.add((String) e.nextElement());
		}

		Collections.sort(headerKeys);

		String[][] headers = new String[headerKeys.size()][2];

		for (int i = 0; i < headers.length; i++) {
			headers[i][0] = headerKeys.get(i);
			headers[i][1] = request.getHeader(headers[i][0]);
		}

		return headers;
	}

	/**
	 * Retrieves the parameters for the last request. The parameters are returned as an array of key-value pairs. Note
	 * that this will not return any results for multi-part encoded form data.
	 *
	 * @return the parameters for the current request.
	 */
	public String[][] getRequestParameters() {
		return requestParameters;
	}

	/**
	 * Reads the request parameters from the given request.
	 *
	 * @param request the request to read the parameters from.
	 * @return an array of parameter key-value pairs, sorted by key.
	 */
	private String[][] getRequestParameters(final HttpServletRequest request) {
		List<String> paramKeys = new ArrayList<>();

		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			paramKeys.add((String) e.nextElement());
		}

		Collections.sort(paramKeys);

		String[][] params = new String[paramKeys.size()][2];

		for (int i = 0; i < params.length; i++) {
			params[i][0] = paramKeys.get(i);
			params[i][1] = request.getParameter(params[i][0]);
		}

		return params;
	}

	/**
	 * Retrieves a tree representation of the WComponent UI being served by the LDE, in a format suitable for rendering
	 * to the UI.
	 *
	 * @return a tree representation of the WComponent UI.
	 */
	public TreeNode getTree() {
		UIContext uic = UIContextDelegate.getPrimaryUIContext(UIContextHolder.getCurrent());

		String[] debugTree = uic.getUI().toString().split("\n");
		TreeNode root = new UITreeNode(debugTree[0]);
		TreeNode parent = root;
		TreeNode last = root;

		for (int i = 1; i < debugTree.length; i++) {
			String line = debugTree[i].trim();

			if (line.charAt(0) == '[') {
				parent = last;
				TreeNode child = new UITreeNode(debugTree[++i]);
				parent.add(child);
				last = child;
			} else if (line.charAt(0) == ']') {
				parent = parent.getParent();
			} else {
				TreeNode child = new UITreeNode(debugTree[i]);
				parent.add(child);
				last = child;
			}
		}

		return root;
	}

	/**
	 * Retrieves statistics on the active UIContext, in a format suitable for rendering to the UI.
	 *
	 * @return statistics on the active UIContext.
	 */
	public String getUicStats() {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);

		UicStats stats = new UicStats(UIContextHolder.getCurrent());
		UicStatsAsHtml.write(printWriter, stats);

		printWriter.close();

		return writer.toString();
	}

	/**
	 * @return true if the WComponent UI hierarchy tree view should be displayed, false otherwise.
	 */
	public boolean isShowTree() {
		return showTree;
	}

	/**
	 * @return true if the current WComponent Configuration should be displayed, false otherwise.
	 */
	public boolean isShowConfig() {
		return showConfig;
	}

	/**
	 * @return true if statistics on the active UIContext should be displayed, false otherwise.
	 */
	public boolean isShowUicStats() {
		return showUicStats;
	}

	/**
	 * @return true if information about the current HTTP request should be displayed, false otherwise.
	 */
	public boolean isShowRequest() {
		return showRequest;
	}

	/**
	 * @return true if the WComponent whitespace filter is enabled.
	 */
	public boolean isWhitespaceFilterEnabled() {
		return Config.getInstance().getBoolean("bordertech.wcomponents.whitespaceFilter.enabled");
	}

	/**
	 * @return true if component ids should be displayed, false otherwise.
	 */
	public boolean isShowIds() {
		return showIds;
	}

	/**
	 * @return the fully qualified class name for the component being served by the LDE.
	 */
	public String getRootComponent() {
		return Config.getInstance().getString(PlainLauncher.COMPONENT_TO_LAUNCH_PARAM_KEY);
	}

	/**
	 * @return true if the client-side WComponent debugging features are enabled.
	 */
	public boolean isDebugEnabled() {
		return Config.getInstance().getBoolean("bordertech.wcomponents.debug.enabled", false)
				&& Config.getInstance()
				.getBoolean("bordertech.wcomponents.debug.clientSide.enabled", false);
	}

	/**
	 * @return the current configuration information as an array of key-value pairs, sorted by key.
	 */
	public String[][] getConfig() {
		List<String> keys = new ArrayList<>();
		Configuration config = Config.getInstance();

		for (Iterator i = config.getKeys(); i.hasNext();) {
			keys.add((String) i.next());
		}

		Collections.sort(keys);
		String[][] configTable = new String[keys.size()][2];

		for (int i = 0; i < keys.size(); i++) {
			configTable[i][0] = keys.get(i);
			configTable[i][1] = config.getString(configTable[i][0]);
		}

		return configTable;
	}

	/**
	 * XML-encodes the given input. This is called by the velocity template.
	 *
	 * @param input the input to encode.
	 * @return the encoded input.
	 */
	public String encode(final String input) {
		return WebUtilities.encode(input);
	}

	/**
	 * Renders the DevToolkit content which must appear before the main WComponent UI.
	 *
	 * @param writer the writer to send the content to.
	 */
	public void paintHeader(final PrintWriter writer) {
		paint("com/github/bordertech/wcomponents/lde/DevToolkit_header.vm", writer);
	}

	/**
	 * Renders the DevToolkit content which must after before the main WComponent UI.
	 *
	 * @param writer the writer to send the content to.
	 */
	public void paintFooter(final PrintWriter writer) {
		paint("com/github/bordertech/wcomponents/lde/DevToolkit_footer.vm", writer);
	}

	/**
	 * Paints the DevToolkit content.
	 *
	 * @param templateName the resource name of the Velocity template to use.
	 * @param writer the writer to send the content to.
	 */
	private void paint(final String templateName, final PrintWriter writer) {
		if (!isEnabled()) {
			return;
		}
		try {
			Template template = VelocityEngineFactory.getVelocityEngine().getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			context.put("this", this);
			UIContext uic = UIContextDelegate.getPrimaryUIContext(UIContextHolder.getCurrent());
			context.put("uic", uic);
			context.put("ui", uic.getUI());
			template.merge(context, writer);
		} catch (Exception e) {
			LOG.error("Unable to render dev toolkit", e);
		}
	}

	/**
	 * Simple node extension for displaying the UI structure.
	 */
	public static final class UITreeNode extends AbstractTreeNode {

		/**
		 * The node text.
		 */
		private final String text;

		/**
		 * Creates a tree node.
		 *
		 * @param text the tree node text.
		 */
		public UITreeNode(final String text) {
			if (text.charAt(text.length() - 1) == ',') {
				this.text = text.substring(0, text.length() - 1).trim();
			} else {
				this.text = text.trim();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return WebUtilities.encode(text);
		}

		/**
		 * @return the children as an array.
		 */
		public TreeNode[] getChildren() {
			TreeNode[] children = new TreeNode[getChildCount()];

			for (int i = 0; i < children.length; i++) {
				children[i] = getChildAt(i);
			}

			return children;
		}
	}
}
