package com.github.openborders.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.github.openborders.Response;

/**
 * An implementation of {@link Response} using HttpServletResponse.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public class ServletResponse implements Response
{
    /** The backing HttpServletResponse. */
    private final HttpServletResponse backing;

    /**
     * Creates a ServletResponse.
     * @param backing the backing HttpServletResponse.
     */
    public ServletResponse(final HttpServletResponse backing)
    {
        this.backing = backing;
    }

    /** {@inheritDoc} */
    public PrintWriter getWriter() throws IOException
    {
        return backing.getWriter();
    }

    /** {@inheritDoc} */
    public OutputStream getOutputStream() throws IOException
    {
        return backing.getOutputStream();
    }

    /** {@inheritDoc} */
    public void sendRedirect(final String redirect) throws IOException
    {
        backing.sendRedirect(redirect);
    }

    /** {@inheritDoc} */
    public void setContentType(final String contentType)
    {
        backing.setContentType(contentType);
    }

    /** {@inheritDoc} */
    public void setHeader(final String name, final String value)
    {
        backing.setHeader(name, value);
    }
    
    /** {@inheritDoc} */
    public void sendError(final int code, final String description) throws IOException
    {
        backing.sendError(code, description);
    }
}
