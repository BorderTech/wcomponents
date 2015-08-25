package com.github.openborders.monitor; 

import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.AbstractMutableContainer;
import com.github.openborders.RenderContext;
import com.github.openborders.UIContextHolder;
import com.github.openborders.WContainer;
import com.github.openborders.servlet.WebXmlRenderContext;
import com.github.openborders.util.ObjectGraphDump;

/**
 * An extension of {@link WContainer} that renders some statistics after its normal output.
 * This class should only be used during development.
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class ProfileContainer extends AbstractMutableContainer
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(ProfileContainer.class);
    
    /**
     * Override afterPaint to write the statistics after the component is painted.
     * 
     * @param renderContext the renderContext to send output to.
     */
    @Override
    protected void afterPaint(final RenderContext renderContext)
    {
        super.afterPaint(renderContext);

        // UIC serialization stats
        UicStats stats = new UicStats(UIContextHolder.getCurrent());
        
        stats.analyseWC(this);

        if (renderContext instanceof WebXmlRenderContext)
        {
            // TODO: This must not emit mark-up
            PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();
            
            writer.println("<hr />");
            writer.println("<h2>Serialization Profile of UIC</h2>");
            UicStatsAsHtml.write(writer, stats);
            
            // ObjectProfiler
            writer.println("<hr />");
            writer.println("<h2>ObjectProfiler - " + getClass().getName() + "</h2>");
            writer.println("<pre>");
            
            try
            {
                writer.println(ObjectGraphDump.dump(this).toFlatSummary());
            }
            catch (Exception e)
            {
                log.error("Failed to dump component", e);
            }
            
            writer.println("</pre>");
        }
    }
}
