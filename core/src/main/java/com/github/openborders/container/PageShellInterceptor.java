package com.github.openborders.container;

import java.io.PrintWriter;

import com.github.openborders.Headers;
import com.github.openborders.RenderContext;
import com.github.openborders.Request;
import com.github.openborders.WebUtilities;
import com.github.openborders.servlet.WebXmlRenderContext;
import com.github.openborders.util.Factory;

/**
 * HtmlComponent adds in some HTTP headers and elements commonly used in HTML-based 
 * web apps. This interceptor is used when running in a servlet environment, without
 * any theme and skin.
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class PageShellInterceptor extends InterceptorComponent
{
    /**
     * Override preparePaint in order to prepare the headers.
     * 
     * @param request the request being responded to.
     */
    @Override
    public void preparePaint(final Request request)
    {
        Headers headers = this.getUI().getHeaders();
        headers.reset();
        
        headers.setContentType(WebUtilities.CONTENT_TYPE_XML);
        
        super.preparePaint(request);
    }

    /**
     * Produce the html output.
     * 
     * @param renderContext the renderContext to send the output to.
     */
    @Override
    public void paint(final RenderContext renderContext)
    {
        WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;
        PrintWriter writer = webRenderContext.getWriter();
        
        beforePaint(writer);
        getBackingComponent().paint(renderContext);
        afterPaint(writer);
    }

    /**
     * Renders the content before the backing component.
     * @param writer the writer to write to.
     */
    protected void beforePaint(final PrintWriter writer)
    {
        PageShell pageShell = Factory.newInstance(PageShell.class);

        pageShell.openDoc(writer);
        pageShell.writeHeader(writer);
    }

    /**
     * Renders the content after the backing component.
     * @param writer the writer to write to.
     */
    protected void afterPaint(final PrintWriter writer)
    {
        PageShell pageShell = Factory.newInstance(PageShell.class);

        pageShell.writeFooter(writer);
        pageShell.closeDoc(writer);
    }
}
