package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.ImageResource;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.Video;
import com.github.bordertech.wcomponents.VideoResource;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WVideo;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import java.awt.Dimension;

/**
 * An example showing the basic use of the {@link WVideo} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WVideoExample extends WContainer {

	/**
	 * The WVideo used in this example.
	 */
	private final WVideo video = new WVideo();


	/**
	 * Used to set the autoplay option on video.
	 */
	private final WCheckBox cbAutoPlay = new WCheckBox();

	/**
	 * Used to set the loop option on video.
	 */
	private final WCheckBox cbLoop = new WCheckBox();

	/**
	 * Used to set the mute option on video.
	 */
	private final WCheckBox cbMute = new WCheckBox();

	/**
	 * Used to set the controls option on video to PLAY_PAUSE.
	 */
	private final WCheckBox cbControls = new WCheckBox(true);

	/**
	 * Used to hide video.
	 */
	private final WCheckBox cbHidden = new WCheckBox();

	/**
	 * A button used to apply new settings to the audio component.
	 */
	private final WButton btnApply = new WButton("Apply settings");
	/**
	 * Creates a WVideoExample.
	 */
	public WVideoExample() {
		Video[] videoClips
				= {
					//NOTE: NEVER use an avi file for delivery on the web!
					new VideoResource("/video/webm.webm", "WebM video file"),
					new VideoResource("/video/ogv.ogv", "Ogg video file"),
					new VideoResource("/video/mp4.mp4", "MPEG-4 video file"),
					new VideoResource("/video/mpg.mpg", "MPEG-1 video file"),
					new VideoResource("/video/wmv.wmv", "WMV video file")
				};

		video.setVideo(videoClips);
		video.setPoster(new ImageResource("/video/poster_image.png", "Video poster image"));
		video.setWidth(300);
		video.setHeight(200);
		btnApply.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				setupVideo();
			}
		});
		buildUI();
	}

	/**
	 * Build the UI for this example.
	 */
	private void buildUI() {
		// build the configuration options UI.
		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		layout.setMargin(new Margin(null, null, Size.LARGE, null));
		add(layout);
		layout.addField("Autoplay", cbAutoPlay);
		layout.addField("Loop", cbLoop);
		layout.addField("Mute", cbMute);
		layout.addField("Hide", cbHidden);
		layout.addField("Show default controls", cbControls);
		layout.addField((WLabel) null, btnApply);

		// add the video to the UI
		add(video);

		// show/hide video
		WSubordinateControl control = new WSubordinateControl();
		add(control);
		Rule rule = new Rule();
		rule.setCondition(new Equal(cbHidden, Boolean.TRUE.toString()));
		rule.addActionOnTrue(new Hide(video));
		rule.addActionOnFalse(new Show(video));
		control.addRule(rule);
		// Allow the config to be updated without reloading the whole UI.
		add(new WAjaxControl(btnApply, video));

		add(new WHeading(HeadingLevel.H2, "WVideo with single resource"));
		// also sets Dimension on the video
		VideoResource resource = new VideoResource("/video/ogv.ogv", "Ogg video file", new Dimension(360, 240));
		add(new WVideo(resource));
	}


	/**
	 * Set up the initial state of the video component.
	 * @param request The http request being processed.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request); //To change body of generated methods, choose Tools | Templates.
		if (!isInitialised()) {
			setInitialised(true);
			setupVideo();
		}
	}


	/**
	 * Set the video configuration options.
	 */
	private void setupVideo() {
		video.setAutoplay(cbAutoPlay.isSelected());
		video.setLoop(cbLoop.isSelected());
		video.setMuted(cbMute.isSelected());
		video.setRenderControls(cbControls.isSelected());
	}
}
