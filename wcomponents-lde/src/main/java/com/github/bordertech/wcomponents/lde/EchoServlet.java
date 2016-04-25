package com.github.bordertech.wcomponents.lde;

import com.github.bordertech.wcomponents.WebUtilities;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An echo servlet used for internal testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class EchoServlet extends HttpServlet {

	@Override
	protected void service(final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter writer = res.getWriter();

		List<String> paramKeys = new ArrayList<>();
		for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
			paramKeys.add((String) e.nextElement());
		}

		for (String paramKey : paramKeys) {
			for (String parameterValue : req.getParameterValues(paramKey)) {
				if ("coffee".equals(parameterValue)) {
					res.sendError(418, "short and stout");
					return;
				}
			}
		}

		writer.println("<!DOCTYPE html>");
		writer.println("<html lang=\"en-AU\">");
		writer.println("    <head>");
		writer.println("        <title>Feedback Page</title>");
		writer.println("    </head>");
		writer.println("    <body>");
		writer.println("        <h1>Feedback Page</h1>");

		String referer = req.getHeader("referer");
		String refererUrl = WebUtilities.escapeForUrl(referer);

		String exampleRoot = "";
		if (referer != null) {
			writer.println("        <h2>Referred from</h2>");
			writer.println("        <p><a href='" + refererUrl + "'>" + refererUrl + "</a></p>");
			exampleRoot = referer.substring(0, referer.lastIndexOf('/') + 1);
			writer.println("        <p><a href='" + WebUtilities.escapeForUrl(exampleRoot) + "'>Back to example list</a></p>");
		}

		writer.println("        <h2>Form Method</h2>");
		writer.println("        <p>" + WebUtilities.encode(req.getMethod()) + "</p>");
		writer.println("        <h2>Query String</h2>");
		writer.println("        <p>" + WebUtilities.encode(req.getQueryString()) + "</p>");
		writer.println("        <h2>Parameters</h2>");

		Collections.sort(paramKeys);

		writer.println("<table><tr><th>Name</th><th>Value</th></tr>");

		for (String paramKey : paramKeys) {
			for (String parameterValue : req.getParameterValues(paramKey)) {
				writer.println("<tr><td>" + WebUtilities.encode(paramKey) + "</td>");
				writer.println("<td>" + WebUtilities.encode(parameterValue) + "</td></tr>");
			}
		}

		writer.println("        </table>");
		writer.println("        <h2>Cookies</h2>");
		writer.println("        <table><tr><th>Name</th><th>Value</th></tr>");

		for (Cookie cookie : req.getCookies()) {
			writer.println(
					"<tr><td>" + WebUtilities.encode(cookie.getName()) + "</td><td>" + WebUtilities.encode(cookie.getValue()) + "</td></tr>");
		}

		writer.println("        </table>");
		writer.println("        <h2>Headers</h2>");
		writer.println("        <table><tr><th>Name</th><th>Value</th></tr>");

		for (String[] header : getRequestHeaders(req)) {
			writer.println("<tr><td>" + WebUtilities.encode(header[0]) + "</td>");
			writer.println("<td>" + WebUtilities.encode(header[1]) + "</td></tr>");
		}

		writer.println("        </table>");

		if (!("".equals(exampleRoot))) {
			writer.println("        <p><a href='" + WebUtilities.encode(exampleRoot) + "'>Back to example list</a></p>");
		}
		writer.println("    </body>");
		writer.println("</html>");
	}

	/**
	 * Reads the request headers from the given request.
	 *
	 * @param request the request to read the headers from.
	 * @return an array of header key-value pairs, sorted by key.
	 */
	private static String[][] getRequestHeaders(final HttpServletRequest request) {
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
}
