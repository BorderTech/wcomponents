package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Headers;
import com.github.bordertech.wcomponents.WebUtilities;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

/**
 * Static utility methods used in construction of WComponent web pages.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public final class PageContentHelper {

	private static final String OPEN_JAVASCRIPT = "\n<script type=\"text/javascript\">";

	private static final String CLOSE_JAVASCRIPT = "\n</script>";

	/**
	 * Prevent instance creation because this class only contains static utility methods.
	 */
	private PageContentHelper() {
	}

	/**
	 * Shortcut method for adding all the headline entries stored in the WHeaders.
	 *
	 * @param writer the writer to write to.
	 * @param headers contains all the headline entries.
	 */
	public static void addAllHeadlines(final PrintWriter writer, final Headers headers) {
		PageContentHelper.addHeadlines(writer, headers.getHeadLines());
		PageContentHelper.addJsHeadlines(writer, headers.getHeadLines(Headers.JAVASCRIPT_HEADLINE));
		PageContentHelper.addCssHeadlines(writer, headers.getHeadLines(Headers.CSS_HEADLINE));
	}

	/**
	 * Add a list of html headline entries intended to be added only once to the page.
	 *
	 * @param writer the writer to write to.
	 * @param headlines a list of html entries to be added to the page as a whole.
	 */
	public static void addHeadlines(final PrintWriter writer, final List headlines) {
		if (headlines == null || headlines.isEmpty()) {
			return;
		}

		writer.write("\n<!-- Start general headlines -->");

		Iterator iter = headlines.iterator();
		while (iter.hasNext()) {
			String line = (String) iter.next();
			writer.write("\n" + line);
		}

		writer.println("\n<!-- End general headlines -->");
	}

	/**
	 * Add a list of javascript headline entries intended to be added only once to the page.
	 *
	 * @param writer the writer to write to.
	 * @param jsHeadlines a list of javascript entries to be added to the page as a whole.
	 */
	public static void addJsHeadlines(final PrintWriter writer, final List jsHeadlines) {
		if (jsHeadlines == null || jsHeadlines.isEmpty()) {
			return;
		}

		writer.println();
		writer.write("\n<!-- Start javascript headlines -->"
				+ OPEN_JAVASCRIPT);

		for (Iterator iter = jsHeadlines.iterator(); iter.hasNext();) {
			String line = (String) iter.next();
			writer.write("\n" + line);
		}

		writer.write(CLOSE_JAVASCRIPT
				+ "\n<!-- End javascript headlines -->");
	}

	/**
	 * Add a list of css headline entries intended to be added only once to the page.
	 *
	 * @param writer the writer to write to.
	 * @param cssHeadlines a list of css entries to be added to the page as a whole.
	 */
	public static void addCssHeadlines(final PrintWriter writer, final List cssHeadlines) {
		if (cssHeadlines == null || cssHeadlines.isEmpty()) {
			return;
		}

		writer.write("<!-- Start css headlines -->"
				+ "\n<style type=\"" + WebUtilities.CONTENT_TYPE_CSS + "\" media=\"screen\">");

		for (Iterator iter = cssHeadlines.iterator(); iter.hasNext();) {
			String line = (String) iter.next();
			writer.write("\n" + line);
		}

		writer.write("\n</style>"
				+ "<!-- End css headlines -->");
	}
}
