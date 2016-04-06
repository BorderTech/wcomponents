package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.ImageResource;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Video;
import com.github.bordertech.wcomponents.VideoResource;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WVideo;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

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
	private final WCheckBox cbControls = new WCheckBox();

	/**
	 * Used to set the disabled option on video.
	 */
	private final WCheckBox cbDisable = new WCheckBox();

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
		video.setAltText("Example WVideo content");
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
		layout.setMargin(new Margin(0, 0, 12, 0));
		add(layout);
		layout.addField("Autoplay", cbAutoPlay);
		layout.addField("Loop", cbLoop);
		layout.addField("Mute", cbMute);
		layout.addField("Disable", cbDisable);
		layout.addField("Show separate play/pause", cbControls);
		layout.addField((WLabel) null, btnApply);

		// add the video to the UI
		add(video);

		// disable mute and enable disable if PLAY_PAUSE is used
		WSubordinateControl control = new WSubordinateControl();
		add(control);
		Rule rule = new Rule();
		rule.setCondition(new Equal(cbControls, Boolean.TRUE.toString()));
		rule.addActionOnTrue(new Disable(cbMute));
		rule.addActionOnTrue(new Enable(cbDisable));
		rule.addActionOnFalse(new Enable(cbMute));
		rule.addActionOnFalse(new Disable(cbDisable));
		control.addRule(rule);
		// Allow the config to be updated without reloading the whole UI.
		add(new WAjaxControl(btnApply, video));
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
		video.setMuted(!cbMute.isDisabled() && cbMute.isSelected());
		video.setControls(cbControls.isSelected() ? WVideo.Controls.PLAY_PAUSE : WVideo.Controls.NATIVE);
		video.setDisabled(cbControls.isSelected() && cbDisable.isSelected());
	}
}
