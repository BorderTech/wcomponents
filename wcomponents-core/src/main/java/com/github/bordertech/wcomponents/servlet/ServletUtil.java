package com.github.bordertech.wcomponents.servlet;

import au.com.flyingkite.mobiledetect.UAgentInfo;
import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.InternalResource;
import com.github.bordertech.wcomponents.InternalResourceMap;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.container.AjaxCleanupInterceptor;
import com.github.bordertech.wcomponents.container.AjaxDebugStructureInterceptor;
import com.github.bordertech.wcomponents.container.AjaxErrorInterceptor;
import com.github.bordertech.wcomponents.container.AjaxInterceptor;
import com.github.bordertech.wcomponents.container.AjaxPageShellInterceptor;
import com.github.bordertech.wcomponents.container.AjaxSetupInterceptor;
import com.github.bordertech.wcomponents.container.ContextCleanupInterceptor;
import com.github.bordertech.wcomponents.container.DataListInterceptor;
import com.github.bordertech.wcomponents.container.DebugStructureInterceptor;
import com.github.bordertech.wcomponents.container.FormInterceptor;
import com.github.bordertech.wcomponents.container.TemplateRenderInterceptor;
import com.github.bordertech.wcomponents.container.InterceptorComponent;
import com.github.bordertech.wcomponents.container.PageShellInterceptor;
import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor;
import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor.CacheType;
import com.github.bordertech.wcomponents.container.SessionTokenAjaxInterceptor;
import com.github.bordertech.wcomponents.container.SessionTokenContentInterceptor;
import com.github.bordertech.wcomponents.container.SessionTokenInterceptor;
import com.github.bordertech.wcomponents.container.SubordinateControlInterceptor;
import com.github.bordertech.wcomponents.container.TargetableErrorInterceptor;
import com.github.bordertech.wcomponents.container.TargetableInterceptor;
import com.github.bordertech.wcomponents.container.TransformXMLInterceptor;
import com.github.bordertech.wcomponents.container.UIContextDumpInterceptor;
import com.github.bordertech.wcomponents.container.ValidateXMLInterceptor;
import com.github.bordertech.wcomponents.container.WWindowInterceptor;
import com.github.bordertech.wcomponents.container.WhitespaceFilterInterceptor;
import com.github.bordertech.wcomponents.container.WrongStepAjaxInterceptor;
import com.github.bordertech.wcomponents.container.WrongStepContentInterceptor;
import com.github.bordertech.wcomponents.container.WrongStepServerInterceptor;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.DeviceType;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.RequestUtil;
import com.github.bordertech.wcomponents.util.StreamUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.ThemeUtil;
import com.github.bordertech.wcomponents.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class to provide http servlet functionality.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class ServletUtil {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private ServletUtil() {
	}

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ServletUtil.class);

	/**
	 * Theme resource path parameter.
	 */
	private static final String THEME_RESOURCE_PATH_PARAM = "/" + Environment.THEME_RESOURCE_PATH_NAME + "/";

	/**
	 * Prefix for translation resource request.
	 */
	private static final String THEME_TRANSLATION_RESOURCE_PREFIX = "resource/translation";

	/**
	 * The resource path for project translation resources.
	 */
	private static final String THEME_PROJECT_TRANSLATION_RESOURCE_PATH = "/wc/theme/i18n";

	/**
	 * The parameters extracted from multi part saved on the request.
	 */
	private static final String REQUEST_PARAMETERS_KEY = "wc_req_params";

	/**
	 * The parameters extracted from multi part saved on the request.
	 */
	private static final String REQUEST_FILES_KEY = "wc_req_files";

	/**
	 * The flag that the request has been processed allowing for multi part forms.
	 */
	private static final String REQUEST_PROCESSED_KEY = "wc_req_processed";

	/**
	 * @return true if enable sub sessions
	 */
	public static boolean isEnableSubSessions() {
		return ConfigurationProperties.getServletEnableSubsessions();
	}

	/**
	 * Check if the request is for a resource (eg static, theme...).
	 *
	 * @param request the http servlet request.
	 * @param response the http servlet response.
	 * @return true to continue processing
	 * @throws ServletException a servlet exception
	 * @throws IOException an IO Exception
	 */
	public static boolean checkResourceRequest(final HttpServletRequest request,
			final HttpServletResponse response)
			throws ServletException, IOException {
		// Static resource
		if (isStaticResourceRequest(request)) {
			handleStaticResourceRequest(request, response);
			return false;
		} else if (isThemeResourceRequest(request)) {  // Theme resource
			handleThemeResourceRequest(request, response);
			return false;
		}

		String method = request.getMethod();

		if ("HEAD".equals(method)) {
			response.setContentType(WebUtilities.CONTENT_TYPE_XML);
			return false;
		} else if (!"POST".equals(method) && !"GET".equals(method)) {
			response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
			return false;
		}

		return true;
	}

	/**
	 * This method does the real work in servicing the http request. It integrates wcomponents into a servlet
	 * environment via a servlet specific helper class.
	 *
	 * @param helper the servlet helper
	 * @param ui the application ui
	 * @param interceptorChain the chain of interceptors
	 * @throws ServletException a servlet exception
	 * @throws IOException an IO Exception
	 */
	public static void processRequest(final HttpServletHelper helper, final WComponent ui,
			final InterceptorComponent interceptorChain) throws ServletException, IOException {

		try {
			// Tell the support container about the top most web component
			// that will service the request/response.
			if (interceptorChain == null) {
				helper.setWebComponent(ui);
			} else {
				interceptorChain.attachUI(ui);
				helper.setWebComponent(interceptorChain);
			}

			// Prepare user context
			UIContext uic = helper.prepareUserContext();

			synchronized (uic) {
				// Process the action phase.
				helper.processAction();

				// Process the render phase.
				helper.render();
			}
		} finally {
			// We need to ensure that the AJAX operation is cleared
			// The interceptors can not guarantee this
			// TODO: Investigate changing to not use a thread-local
			AjaxHelper.clearCurrentOperationDetails();
		}
	}

	/**
	 * @param req the request being processed
	 * @return true if requesting a static resource
	 */
	public static boolean isStaticResourceRequest(final HttpServletRequest req) {
		return req.getParameter(WServlet.STATIC_RESOURCE_PARAM_NAME) != null;
	}

	/**
	 * Handles a request for static resources.
	 *
	 * @param request the http request.
	 * @param response the http response.
	 */
	public static void handleStaticResourceRequest(final HttpServletRequest request,
			final HttpServletResponse response) {
		String staticRequest = request.getParameter(WServlet.STATIC_RESOURCE_PARAM_NAME);

		try {
			InternalResource staticResource = InternalResourceMap.getResource(staticRequest);
			boolean headersOnly = "HEAD".equals(request.getMethod());

			if (staticResource == null) {
				LOG.warn("Static resource [" + staticRequest + "] not found.");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			InputStream resourceStream = staticResource.getStream();
			if (resourceStream == null) {
				LOG.warn(
						"Static resource [" + staticRequest + "] not found. Stream for content is null.");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			int size = resourceStream.available();
			String fileName = WebUtilities.encodeForContentDispositionHeader(staticRequest.
					substring(staticRequest
							.lastIndexOf('/') + 1));

			if (size > 0) {
				response.setContentLength(size);
			}

			response.setContentType(WebUtilities.getContentType(staticRequest));
			response.setHeader("Cache-Control", CacheType.CONTENT_CACHE.getSettings());

			String param = request.getParameter(WContent.URL_CONTENT_MODE_PARAMETER_KEY);
			if ("inline".equals(param)) {
				response.setHeader("Content-Disposition", "inline; filename=" + fileName);
			} else if ("attach".equals(param)) {
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			} else {
				// added "filename=" to comply with https://tools.ietf.org/html/rfc6266
				response.setHeader("Content-Disposition", "filename=" + fileName);
			}

			if (!headersOnly) {
				StreamUtil.copy(resourceStream, response.getOutputStream());
			}
		} catch (IOException e) {
			LOG.warn("Could not process static resource [" + staticRequest + "]. ", e);
			response.reset();
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * @param req the request being processed
	 * @return true if requesting a theme resource
	 */
	public static boolean isThemeResourceRequest(final HttpServletRequest req) {
		String path = req.getPathInfo();
		return path != null && path.startsWith(THEME_RESOURCE_PATH_PARAM);
	}

	/**
	 * Serves up a file from the theme. In practice it is generally a bad idea to use this servlet to serve up static
	 * resources. Instead it would make more sense to move CSS, JS, HTML resources to a CDN or similar.
	 *
	 *
	 * @param req the request with the file name in parameter "f", or following the servlet path.
	 * @param resp the response to write to.
	 * @throws ServletException on error.
	 * @throws IOException if there is an error reading the file / writing the response.
	 */
	public static void handleThemeResourceRequest(final HttpServletRequest req,
			final HttpServletResponse resp)
			throws ServletException, IOException {

		if (req.getHeader("If-Modified-Since") != null) {
			resp.reset();
			resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		String fileName = req.getParameter("f");

		String path = req.getPathInfo();
		if (fileName == null && !Util.empty(path)) {
			int offset = path.startsWith(THEME_RESOURCE_PATH_PARAM) ? THEME_RESOURCE_PATH_PARAM.
					length() : 1;
			fileName = path.substring(offset);
		}

		if (fileName == null || !checkThemeFile(fileName)) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		InputStream resourceStream = null;

		try {
			URL url = null;

			// Check for project translation file
			if (fileName.startsWith(THEME_TRANSLATION_RESOURCE_PREFIX)) {
				String resourceFileName = fileName.substring(THEME_TRANSLATION_RESOURCE_PREFIX.length());
				url = ServletUtil.class.getResource(THEME_PROJECT_TRANSLATION_RESOURCE_PATH + resourceFileName);
			}

			// Load from the theme path
			if (url == null) {
				String resourceName = ThemeUtil.getThemeBase() + fileName;
				url = ServletUtil.class.getResource(resourceName);
			}

			if (url == null) {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				URLConnection connection = url.openConnection();
				resourceStream = connection.getInputStream();
				int size = resourceStream.available();
				if (size > 0) {
					resp.setContentLength(size);
				}

				/*
				I have commented out the setting of the Content-Disposition on static theme resources because, well why is it there?
				If this needs to be reinstated please provide a thorough justification comment here so the reasons are clear.

				Note that setting this header breaks Polymer 1.0 when it is present on HTML imports.

				String encodedName = WebUtilities.encodeForContentDispositionHeader(fileName.
						substring(fileName
								.lastIndexOf('/') + 1));
				resp.setHeader("Content-Disposition", "filename=" + encodedName);  // "filename=" to comply with https://tools.ietf.org/html/rfc6266
				 */
				resp.setContentType(WebUtilities.getContentType(fileName));
				resp.setHeader("Cache-Control", CacheType.THEME_CACHE.getSettings());

				resp.setHeader("Expires", "31536000");
				resp.setHeader("ETag", "\"" + WebUtilities.getProjectVersion() + "\"");
				// resp.setHeader("Last-Modified", "Mon, 02 Jan 2015 01:00:00 GMT");
				long modified = connection.getLastModified();
				resp.setDateHeader("Last-Modified", modified);
				StreamUtil.copy(resourceStream, resp.getOutputStream());
			}
		} finally {
			StreamUtil.safeClose(resourceStream);
		}
	}

	/**
	 * Performs basic sanity checks on the file being requested.
	 *
	 * @param name the file name
	 * @return true if the requested file name is ok, false if not.
	 */
	private static boolean checkThemeFile(final String name) {
		return !(Util.empty(name) // name must exist
				|| name.contains("..") // prevent directory traversal
				|| name.charAt(0) == '/' // all theme references should be relative
				|| name.indexOf(':') != -1 // forbid use of protocols such as jar:, http: etc.
				);
	}

	/**
	 * Creates a new interceptor chain to handle the given request.
	 *
	 * @param request the request to handle
	 * @return a new interceptor chain for the request.
	 */
	public static InterceptorComponent createInterceptorChain(final HttpServletRequest request) {

		// Allow for multi part parameters
		Map<String, String[]> parameters = getRequestParameters(request);

		InterceptorComponent[] chain;

		if (parameters.get(WServlet.DATA_LIST_PARAM_NAME) != null) { // Datalist
			chain = new InterceptorComponent[]{
				new TransformXMLInterceptor(),
				new DataListInterceptor()};

		} else if (parameters.get(WServlet.AJAX_TRIGGER_PARAM_NAME) != null) { // AJAX
			chain = new InterceptorComponent[]{
				new TemplateRenderInterceptor(),
				new TransformXMLInterceptor(),
				new ValidateXMLInterceptor(),
				new AjaxErrorInterceptor(),
				new SessionTokenAjaxInterceptor(),
				new ResponseCacheInterceptor(CacheType.NO_CACHE),
				new UIContextDumpInterceptor(),
				new AjaxSetupInterceptor(),
				new WWindowInterceptor(true),
				new WrongStepAjaxInterceptor(),
				new ContextCleanupInterceptor(),
				new WhitespaceFilterInterceptor(),
				new SubordinateControlInterceptor(),
				new AjaxPageShellInterceptor(),
				new AjaxDebugStructureInterceptor(),
				new AjaxInterceptor()};

		} else if (parameters.get(WServlet.TARGET_ID_PARAM_NAME) != null) { // Targetted Content
			chain = new InterceptorComponent[]{
				new TargetableErrorInterceptor(),
				new SessionTokenContentInterceptor(),
				new UIContextDumpInterceptor(),
				new TargetableInterceptor(),
				new WWindowInterceptor(false),
				new WrongStepContentInterceptor()};

		} else {
			chain = new InterceptorComponent[]{ // Page submit
				new TemplateRenderInterceptor(),
				new TransformXMLInterceptor(),
				new ValidateXMLInterceptor(),
				new SessionTokenInterceptor(),
				new ResponseCacheInterceptor(CacheType.NO_CACHE),
				new UIContextDumpInterceptor(),
				new WWindowInterceptor(true),
				new WrongStepServerInterceptor(),
				new AjaxCleanupInterceptor(),
				new ContextCleanupInterceptor(),
				new WhitespaceFilterInterceptor(),
				new SubordinateControlInterceptor(),
				new PageShellInterceptor(),
				new FormInterceptor(),
				new DebugStructureInterceptor()};
		}

		// Link the interceptors together in a chain.
		for (int i = 0; i < chain.length - 1; i++) {
			chain[i].setBackingComponent(chain[i + 1]);
		}

		// Return the top of the chain.
		return chain[0];
	}

	/**
	 * Called if a Throwable is caught by the top-level service method. By default we display an error and terminate the
	 * session.
	 *
	 * @param helper the current servlet helper
	 * @param throwable the throwable
	 * @throws ServletException a servlet exception
	 * @throws IOException an IO Exception
	 */
	public static void handleError(final HttpServletHelper helper, final Throwable throwable) throws
			ServletException,
			IOException {
		HttpServletRequest httpServletRequest = helper.getBackingRequest();
		HttpServletResponse httpServletResponse = helper.getBackingResponse();

		// Allow for multi part requests
		Map<String, String[]> parameters = getRequestParameters(httpServletRequest);

		// Set error code for AJAX, Content or data requests
		boolean dataRequest = parameters.get(WServlet.DATA_LIST_PARAM_NAME) != null;
		Object target = parameters.get(WServlet.AJAX_TRIGGER_PARAM_NAME);
		if (target == null) {
			target = parameters.get(WServlet.TARGET_ID_PARAM_NAME);
		}
		if (target != null || dataRequest) {
			httpServletResponse.sendError(500, "Internal Error");
			return;
		}

		// Decide whether we should use the ErrorPageFactory.
		boolean handleErrorWithFatalErrorPageFactory = ConfigurationProperties.getHandleErrorWithFatalErrorPageFactory();

		// use the new technique and delegate to the ErrorPageFactory.
		if (handleErrorWithFatalErrorPageFactory) {
			helper.handleError(throwable);
			helper.dispose();
		} else { // use the old technique and just display a raw message.
			// First, decide whether we are in friendly mode or not.
			boolean friendly = ConfigurationProperties.getDeveloperErrorHandling();

			String message = InternalMessages.DEFAULT_SYSTEM_ERROR;

			// If we are unfriendly, terminate the session
			if (!friendly) {
				HttpSession session = httpServletRequest.getSession(true);
				session.invalidate();

				message = InternalMessages.DEFAULT_SYSTEM_ERROR_SEVERE;
			}

			// Display an error to the user.
			UIContext uic = helper.getUIContext();
			Locale locale = uic == null ? null : uic.getLocale();
			message = I18nUtilities.format(locale, message);
			httpServletResponse.getWriter().println(message);
		}
	}

	/**
	 * @param request the request being processed
	 * @return true if a multi part form request
	 */
	public static boolean isMultipart(final HttpServletRequest request) {
		String contentType = request.getContentType();
		boolean isMultipart = (contentType != null && contentType.toLowerCase().startsWith(
				"multipart/form-data"));
		return isMultipart;
	}

	/**
	 * Get a map of request parameters allowing for multi part form fields.
	 *
	 * @param request the request being processed
	 * @return a map of parameters on the request
	 */
	public static Map<String, String[]> getRequestParameters(final HttpServletRequest request) {
		if (request.getAttribute(REQUEST_PROCESSED_KEY) == null) {
			setupRequestParameters(request);
		}
		return (Map<String, String[]>) request.getAttribute(REQUEST_PARAMETERS_KEY);
	}

	/**
	 * Get a value for a request parameter allowing for multi part form fields.
	 *
	 * @param request the request being processed
	 * @param key the parameter key to return
	 * @return the parameter value
	 */
	public static String getRequestParameterValue(final HttpServletRequest request, final String key) {
		String[] values = getRequestParameterValues(request, key);
		return values == null || values.length == 0 ? null : values[0];
	}

	/**
	 * Get the values for a request parameter allowing for multi part form fields.
	 *
	 * @param request the request being processed
	 * @param key the parameter key to return
	 * @return the parameter values
	 */
	public static String[] getRequestParameterValues(final HttpServletRequest request, final String key) {
		return getRequestParameters(request).get(key);
	}

	/**
	 * Get a map of file items in the request allowing for multi part form fields.
	 *
	 * @param request the request being processed
	 * @return a map of files on the request
	 */
	public static Map<String, FileItem[]> getRequestFileItems(final HttpServletRequest request) {
		if (request.getAttribute(REQUEST_PROCESSED_KEY) == null) {
			setupRequestParameters(request);
		}
		return (Map<String, FileItem[]>) request.getAttribute(REQUEST_FILES_KEY);
	}

	/**
	 * Get a file item value from the request allowing for multi part form fields.
	 *
	 * @param request the request being processed
	 * @param key the file parameter key to return
	 * @return the file item value
	 */
	public static FileItem getRequestFileItemValue(final HttpServletRequest request, final String key) {
		FileItem[] values = getRequestFileItemValues(request, key);
		return values == null || values.length == 0 ? null : values[0];
	}

	/**
	 * Get file item values from the request allowing for multi part form fields.
	 *
	 * @param request the request being processed
	 * @param key the file parameter key to return
	 * @return the file item values
	 */
	public static FileItem[] getRequestFileItemValues(final HttpServletRequest request, final String key) {
		return getRequestFileItems(request).get(key);
	}

	/**
	 * Process the request parameters allowing for multi part form fields.
	 *
	 * @param request the request being processed
	 */
	public static void setupRequestParameters(final HttpServletRequest request) {

		// Check already processed
		if (request.getAttribute(REQUEST_PROCESSED_KEY) != null) {
			return;
		}

		Map<String, String[]> parameters = new HashMap<>();
		Map<String, FileItem[]> files = new HashMap<>();

		extractParameterMap(request, parameters, files);

		request.setAttribute(REQUEST_PROCESSED_KEY, "Y");
		request.setAttribute(REQUEST_PARAMETERS_KEY, Collections.unmodifiableMap(parameters));
		request.setAttribute(REQUEST_FILES_KEY, Collections.unmodifiableMap(files));
	}

	/**
	 * Extract the parameters and file items allowing for multi part form fields.
	 *
	 * @param request the request being processed
	 * @param parameters the map to store non-file request parameters in.
	 * @param files the map to store the uploaded file parameters in.
	 */
	public static void extractParameterMap(final HttpServletRequest request, final Map<String, String[]> parameters,
			final Map<String, FileItem[]> files) {

		if (isMultipart(request)) {
			ServletFileUpload upload = new ServletFileUpload();
			upload.setFileItemFactory(new DiskFileItemFactory());
			try {
				List fileItems = upload.parseRequest(request);

				uploadFileItems(fileItems, parameters, files);
			} catch (FileUploadException ex) {
				throw new SystemException(ex);
			}
			// Include Query String Parameters (only if parameters were not included in the form fields)
			for (Object entry : request.getParameterMap().entrySet()) {
				Map.Entry<String, String[]> param = (Map.Entry<String, String[]>) entry;
				if (!parameters.containsKey(param.getKey())) {
					parameters.put(param.getKey(), param.getValue());
				}
			}
		} else {
			parameters.putAll(request.getParameterMap());
		}
	}

	/**
	 * <p>
	 * {@link FileItem} classes (if attachements) will be kept as part of the request. The default behaviour of the file
	 * item is to store the upload in memory until it reaches a certain size, after which the content is streamed to a
	 * temp file.</p>
	 *
	 * <p>
	 * If, in the future, performance of uploads becomes a focus we can instead look into using the Jakarta Commons
	 * Streaming API. In this case, the content of the upload isn't stored anywhere. It will be up to the user to
	 * read/store the content of the stream.</p>
	 *
	 * @param fileItems a list of {@link FileItem}s corresponding to POSTed form data.
	 * @param parameters the map to store non-file request parameters in.
	 * @param files the map to store the uploaded file parameters in.
	 */
	public static void uploadFileItems(final List<FileItem> fileItems, final Map<String, String[]> parameters,
			final Map<String, FileItem[]> files) {

		for (FileItem item : fileItems) {
			String name = item.getFieldName();
			boolean formField = item.isFormField();

			if (LOG.isDebugEnabled()) {
				LOG.debug(
						"Uploading form " + (formField ? "field" : "attachment") + " \"" + name + "\"");
			}

			if (formField) {
				String value;
				try {
					// Without specifying UTF-8, apache commons DiskFileItem defaults to ISO-8859-1.
					value = item.getString("UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new SystemException("Encoding error on formField item", e);
				}
				RequestUtil.addParameter(parameters, name, value);
			} else {
				// Form attachment
				RequestUtil.addFileItem(files, name, item);
				String value = item.getName();
				RequestUtil.addParameter(parameters, name, value);
			}
		}

	}

	/**
	 * Determine the user's device type from the {@link HttpServletRequest}.
	 *
	 * @param request the request being processed
	 * @return the device type
	 */
	public static DeviceType getDevice(final HttpServletRequest request) {
		// User agent
		String userAgent = ((HttpServletRequest) request).getHeader("User-Agent");
		if (Util.empty(userAgent)) {
			LOG.warn("No User-Agent details in the request headers. Will assume normal device.");
			return DeviceType.NORMAL;
		}

		// Check for device type
		UAgentInfo agentInfo = new UAgentInfo(userAgent, null);
		if (agentInfo.detectMobileQuick()) {
			return DeviceType.MOBILE;
		} else if (agentInfo.detectTierTablet()) {
			return DeviceType.TABLET;
		}
		return DeviceType.NORMAL;
	}

	/**
	 * Helper method that uses the current WComponents {@link Request} interface to determine the user's device type.
	 *
	 * @param request the request being processed
	 * @return the device type
	 */
	public static DeviceType getDevice(final Request request) {
		if (request instanceof ServletRequest) {
			return getDevice(((ServletRequest) request).getBackingRequest());
		}
		return DeviceType.NORMAL;
	}
}
