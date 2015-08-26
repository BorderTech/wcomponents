package com.github.openborders.wcomponents.container;

import java.io.PrintWriter;

import org.apache.commons.configuration.Configuration;

import com.github.openborders.wcomponents.RenderContext;
import com.github.openborders.wcomponents.UIContextHolder;
import com.github.openborders.wcomponents.servlet.WebXmlRenderContext;
import com.github.openborders.wcomponents.util.Config;
import com.github.openborders.wcomponents.util.WhiteSpaceFilterPrintWriter;

/**
 * WhitespaceFilterInterceptor is an interceptor that removes
 * HTML comments and redundant white space from HTML output.
 *
 * For local testing/troubleshooting, the filter can be disabled by setting the following {@link Config configuration} parameter:
 * <pre>
 * com.github.openborders.wcomponents.container.WhitespaceFilterInterceptor.enabled=false
 * </pre>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WhitespaceFilterInterceptor extends InterceptorComponent
{
    /** The key used to look up the enable flag in the current {@link Config configuration}. */
    private static final String PARAMETERS_KEY = "wcomponent.whitespaceFilter.enabled";

    /**
     * Paints the component.
     * @param renderContext the renderContext to send the output to.
     */
    @Override
    public void paint(final RenderContext renderContext)
    {
        Configuration config = Config.getInstance();
        boolean enabled = "true".equalsIgnoreCase(config.getString(PARAMETERS_KEY, "true"));

        if (enabled && renderContext instanceof WebXmlRenderContext)
        {
            PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();
            writer = new WhiteSpaceFilterPrintWriter(writer);

            WebXmlRenderContext filteredContext = new WebXmlRenderContext(writer, UIContextHolder.getCurrent().getLocale());
            filteredContext.getWriter().turnIndentingOff();
            super.paint(filteredContext);
        }
        else
        {
            super.paint(renderContext);
        }
    }
}
