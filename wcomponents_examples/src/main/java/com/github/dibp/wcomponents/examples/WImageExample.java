package com.github.dibp.wcomponents.examples;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.Image;
import com.github.dibp.wcomponents.ImageResource;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WImage;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.layout.FlowLayout;
import com.github.dibp.wcomponents.layout.FlowLayout.Alignment;
import com.github.dibp.wcomponents.util.StreamUtil;

/**
 * Example showing use of rendering an image with a {@link WImage}.
 *
 * @author Kishan Bisht
 * @since 1.0.0
 */
public class WImageExample extends WPanel
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(WImageExample.class);

    /** The example image. */
    private final WImage wImage = new WImage();

    /** A button used to change which image is displayed. */
    private final WButton changeImageBtn = new WButton("Change Image");

    /** Creates a WImageExample. */
    public WImageExample()
    {
        setLayout(new FlowLayout(Alignment.VERTICAL, 0, 6));

        // This image is a static resource, and will be cached on the client.
        final ImageResource bannerImage = new ImageResource("/com/github/dibp/wcomponents/examples/picker/wclogo_small.gif", "Logo");

        // Even though this image is a static resource, it will not be cached on the client.
        // To enable images to be cached, either use ImageResource for static images, or set the cache key on the WImage.
        // See WImage.setCacheKey(String).
        final ExampleImage portraitImage = new ExampleImage("com/github/dibp/wcomponents/examples/portlet-portrait.jpg");
        //If you do not set the description in the constructor then you should set it explicitly.
        //There are some crcumstances where a WImage should not have a description to meet accessibility requirements.
        //If the image is *purely* decorative or repeats information set in visible text within the *same* context
        //then the WImage *must not* have a description.
        portraitImage.setDescription("Portrait");
        portraitImage.setMimeType("image/jpg");

        wImage.setImage(bannerImage);

        changeImageBtn.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                if (wImage.getImage() == bannerImage)
                {
                    wImage.setImage(portraitImage);
                }
                else
                {
                    wImage.setImage(bannerImage);
                }
            }
        });

        add(wImage);
        add(changeImageBtn);
    }

    /** @return the change image button. */
    public WButton getChangeImageButton()
    {
        return changeImageBtn;
    }

    /** @return the example image. */
    public WImage getWImage()
    {
        return wImage;
    }

    /**
     * Example implementation of {@link Image}.
     * @author Kishan Bisht
     */
    public static class ExampleImage implements Image
    {
        /** The binary image file data. */
        private byte[] imageBytes;

        /** The image mime-type. */
        private String mimeType;

        /** The image description (alt text). */
        private String desc;

        /** The image size, in pixels. */
        private Dimension size;

        /**
         * Creates an ExampleImage.
         * @param resource the path to the image file.
         */
        public ExampleImage(final String resource)
        {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

            if (in != null)
            {
                try
                {
                    imageBytes = StreamUtil.getBytes(in);
                    in.close();
                }
                catch (IOException ex)
                {
                    log.error("Cannot load example image.", ex);
                }
            }
        }

        /** @return the image mime type, e.g. image/jpg. */
        public String getMimeType()
        {
            return mimeType;
        }

        /**
         * Sets the imge mime type.
         * @param mimeType the image mime type, e.g. image/jpg.
         */
        public void setMimeType(final String mimeType)
        {
            this.mimeType = mimeType;
        }

        /**
         * @return the image size.
         */
        public Dimension getSize()
        {
            return size;
        }

        /**
         * Sets the image size.
         * @param size the image size.
         */
        public void setSize(final Dimension size)
        {
            this.size = size;
        }

        /**
         * @return the image data.
         */
        public byte[] getBytes()
        {
            return imageBytes;
        }

        /**
         * @return the image description.
         */
        public String getDescription()
        {
            return desc;
        }

        /**
         * Sets the image description.
         * @param aDesc the image description.
         */
        public void setDescription(final String aDesc)
        {
            desc = aDesc;
        }
    }
}
