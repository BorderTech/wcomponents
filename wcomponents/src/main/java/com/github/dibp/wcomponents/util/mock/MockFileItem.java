package com.github.dibp.wcomponents.util.mock; 

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.io.FileUtils;

/**
 * Mock implementation of {@link FileItem}.
 * 
 * @author Yiannis Paschalidis
 * @author Anthony O'Connor - extracted from WFileWidget_Test.
 * @since 1.0.0
 */
public final class MockFileItem implements FileItem
{
    /** The field name used to reference this file item. */
    private String fieldName;
    
    /** <code>True</code> if the instance represents a simple form field, or <code>false</code> if it represents an uploaded file. */
    private boolean formField = true;
    
    /** The original filename in the client's filesystem. */
    private String fileName;
    
    /** The file's mime-type. */
    private String contentType;
    
    /** Flags whether the {@link #delete()} method has been called. */
    private boolean deleted = false;
    
    /** The binary data contained in the file. */
    private byte[] contents;
    
    private FileItemHeaders headers;

    /** {@inheritDoc} */
    public void delete()
    {
        deleted = true;
    }

    /** {@inheritDoc} */
    public byte[] get()
    {
        if (deleted)
        {
            throw new IllegalStateException("delete() called");
        }
        
        byte[] data = new byte[contents.length];
        System.arraycopy(contents, 0, data, 0, contents.length);
        return data;
    }

    /**
     * Sets the binary data for this MockFileItem.
     * @param contents the binary content.
     */
    public void set(final byte[] contents)
    {
        formField = false;
        this.contents = contents;
    }

    /**
     * Sets the content MIME-type for thos MockFileItem.
     * @param contentType the contentType.
     */
    public void setContentType(final String contentType)
    {
        this.contentType = contentType;
    }
    
    /** {@inheritDoc} */
    public String getContentType()
    {
        return contentType;
    }

    /** {@inheritDoc} */
    public String getFieldName()
    {
        return fieldName;
    }

    /** {@inheritDoc} */
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(get());
    }
    
    /**
     * Returns the file item headers.
     * @return The file items headers.
     */
    public FileItemHeaders getHeaders() {
        return headers;
    }
    
    /**
     * Set the file item headers.
     */
    public void setHeaders(FileItemHeaders headers) {
        this.headers = headers;
    }
    
    
    /**
     * Sets the name of the uploaded file for this MockFileItem.
     * @param name the name of the uploaded file.
     */
    public void setName(final String name)
    {
        this.fileName = name;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return fileName;
    }
    
    /** {@inheritDoc} */
    public OutputStream getOutputStream() throws IOException
    {
        return null;
    }

    /** {@inheritDoc} */
    public long getSize()
    {
        return contents.length;
    }
    
    /** {@inheritDoc} */
    public String getString()
    {
        return new String(get());
    }

    /** {@inheritDoc} */
    public String getString(final String encoding) throws UnsupportedEncodingException
    {
        return new String(get(), encoding);
    }

    /** {@inheritDoc} */
    public boolean isFormField()
    {
        return formField;
    }

    /** {@inheritDoc} */
    public boolean isInMemory()
    {
        return true;
    }

    /** {@inheritDoc} */
    public void setFieldName(final String name)
    {
        this.fieldName = name;
    }

    /** {@inheritDoc} */
    public void setFormField(final boolean state)
    {
        this.formField = state;
    }

    /** {@inheritDoc} */
    public void write(final File file) throws IOException
    {
        FileUtils.writeByteArrayToFile(file, get());
    }
}
