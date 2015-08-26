package com.github.openborders.wcomponents.util.thumbnail;

import java.awt.Dimension;

import com.github.openborders.wcomponents.Image;

/**
 * An {@link Image} specified by a byte array.
 * 
 * @author Brian Kavanagh, 2014.
 */
public class BytesImage implements Image
{
    /** Java Serialisation Identifier. */
    private static final long serialVersionUID = 1494492251228372708L;

    /** The bytes which make up the image. */
    private final byte[] bytes;

    /** The MIME type of the Image. (i.e. "image/jpeg"). */
    private final String mimeType;

    /** A Textural description of the image. */
    private final String description;

    /** The x-y dimensions of the image. */
    private final Dimension size;

    /**
     * This constructor creates a {@link BytesImage} from the given byte array.
     * 
     * @param bytes The bytes which make up this {@link BytesImage}.
     * @param mimeType The MIME type of the {@link BytesImage}. (i.e. "image/jpeg")
     * @param description A description of the {@link BytesImage}.
     * @param size The x-y dimensions of the {@link BytesImage}.
     */
    public BytesImage(byte[] bytes, String mimeType, String description, Dimension size)
    {
        this.bytes = bytes;
        this.mimeType = mimeType;
        this.description = description;
        this.size = size;
    }

    /**
     * @return The bytes which make up this {@link BytesImage}.
     */
    @Override
    public byte[] getBytes()
    {
        if (bytes == null)
        {
            return new byte[0];
        }

        return bytes;
    }

    /**
     * @return A description of this {@link BytesImage}.
     */
    @Override
    public String getDescription()
    {
        return description;
    }

    /**
     * @return The MIME type of the {@link BytesImage}. (i.e. "image/jpeg")
     */
    @Override
    public String getMimeType()
    {
        return mimeType;
    }

    /**
     * @return The x-y dimensions of the {@link BytesImage}.
     */
    @Override
    public Dimension getSize()
    {
        return size;
    }
}
