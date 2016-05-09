package com.github.bordertech.wcomponents.monitor;

import com.github.bordertech.wcomponents.AbstractMutableContainer;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.ObjectGraphDump;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An extension of {@link WContainer} that renders some statistics after its normal output. This class should only be
 * used during development.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class ProfileContainer extends AbstractMutableContainer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ProfileContainer.class);

	/**
	 * Override afterPaint to write the statistics after the component is painted.
	 *
	 * @param renderContext the renderContext to send output to.
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);

		// UIC serialization stats
		UicStats stats = new UicStats(UIContextHolder.getCurrent());

		stats.analyseWC(this);

		if (renderContext instanceof WebXmlRenderContext) {
			PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();

			writer.println("<h2>Serialization Profile of UIC</h2>");
			UicStatsAsHtml.write(writer, stats);

			// ObjectProfiler
			writer.println("<h2>ObjectProfiler - " + getClass().getName() + "</h2>");
			writer.println("<pre>");

			try {
				writer.println(ObjectGraphDump.dump(this).toFlatSummary());
			} catch (Exception e) {
				LOG.error("Failed to dump component", e);
			}

			writer.println("</pre>");
		}
	}
}
