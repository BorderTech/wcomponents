package com.github.openborders.examples;

import com.github.openborders.ImageResource;
import com.github.openborders.Video;
import com.github.openborders.VideoResource;
import com.github.openborders.WPanel;
import com.github.openborders.WVideo;

/**
 * An example showing the basic use of the {@link WVideo} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WVideoExample extends WPanel
{
    /** Creates a WVideoExample. */
    public WVideoExample()
    {
        Video[] videoClips =
        {
            //NOTE: NEVER use an avi file for delivery on the web!
             //new VideoResource("/video/avi.avi", "AVI video file"),
             new VideoResource("/video/webm.webm", "WebM video file"),
             new VideoResource("/video/ogv.ogv", "Ogg video file"),
             new VideoResource("/video/mp4.mp4", "MPEG-4 video file"),
             new VideoResource("/video/mpg.mpg", "MPEG-1 video file"),
             new VideoResource("/video/wmv.wmv", "WMV video file")
        };

        WVideo video = new WVideo(videoClips);
        video.setPoster(new ImageResource("/video/poster_image.png", "Video poster image"));
        video.setWidth(300);
        video.setHeight(200);
        video.setAltText("Example WVideo content");
        add(video);
    }
}
