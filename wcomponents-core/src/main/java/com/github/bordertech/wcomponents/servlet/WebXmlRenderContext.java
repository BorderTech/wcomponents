package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * The WComponent web-xml render context.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WebXmlRenderContext implements RenderContext {

	/**
	 * The PrintWriter where the rendered output should be sent to.
	 */
	private final XmlStringBuilder writer;

	/**
	 * Creates a WebXmlRenderContext.
	 *
	 * @param writer the PrintWriter where the rendered output should be sent to.
	 */
	public WebXmlRenderContext(final PrintWriter writer) {
		if (writer instanceof XmlStringBuilder) {
			this.writer = (XmlStringBuilder) writer;
		} else {
			this.writer = new XmlStringBuilder(writer);
		}
	}

	/**
	 * Creates a WebXmlRenderContext.
	 *
	 * @param writer the PrintWriter where the rendered output should be sent to.
	 * @param locale the Locale to use for translating messages.
	 */
	public WebXmlRenderContext(final PrintWriter writer, final Locale locale) {
		this.writer = new XmlStringBuilder(writer, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRenderPackage() {
		return "com.github.bordertech.wcomponents.render.webxml";
	}

	/**
	 * @return the PrintWriter where the rendered output should be sent to.
	 */
	public XmlStringBuilder getWriter() {
		return writer;
	}
}
