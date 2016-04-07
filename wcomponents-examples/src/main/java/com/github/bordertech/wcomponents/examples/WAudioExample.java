package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Audio;
import com.github.bordertech.wcomponents.AudioResource;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WAudio;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * An example showing the basic use of the {@link WAudio} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WAudioExample extends WContainer {

	/**
	 * The WAudio component used in this example.
	 */
	private final WAudio audio = new WAudio();

	/**
	 * Used to set the autoplay option on audio.
	 */
	private final WCheckBox cbAutoPlay = new WCheckBox();

	/**
	 * Used to set the loop option on audio.
	 */
	private final WCheckBox cbLoop = new WCheckBox();

	/**
	 * Used to set the controls option on audio to PLAY_PAUSE.
	 */
	private final WCheckBox cbControls = new WCheckBox();

	/**
	 * Used to set the disabled option on audio.
	 */
	private final WCheckBox cbDisable = new WCheckBox();

	/**
	 * A button used to apply new settings to the audio component.
	 */
	private final WButton btnApply = new WButton("Apply settings");


	/**
	 * Creates a WAudioExample.
	 */
	public WAudioExample() {
		Audio[] audioClips
				= {
					new AudioResource("/audio/ogg.ogg", "Ogg audio file"),
					new AudioResource("/audio/flac.flac", "FLAC audio file"),
					new AudioResource("/audio/mp3.mp3", "MPEG3 audio file"),
					new AudioResource("/audio/wav.wav", "Wav audio file"),
					new AudioResource("/audio/au.au", "Sun audio file")
				};

		audio.setAudio(audioClips);
		audio.setAltText("Example WAudio content");
		btnApply.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				setupAudio();
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
		layout.addField("Disable", cbDisable);
		layout.addField("Show only play/pause", cbControls);
		layout.addField((WLabel) null, btnApply);

		// enable disable option only when control PLAY_PAUSE is used.
		WSubordinateControl control = new WSubordinateControl();
		add(control);
		Rule rule = new Rule();
		rule.setCondition(new Equal(cbControls, Boolean.TRUE.toString()));
		rule.addActionOnTrue(new Enable(cbDisable));
		rule.addActionOnFalse(new Disable(cbDisable));
		control.addRule(rule);

		// allow config to change without reloading the whole page.
		add(new WAjaxControl(btnApply, audio));

		// add the audio to the UI
		add(audio);
	}

	/**
	 * Set up the initial state of the audio component.
	 * @param request The http request being processed.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request); //To change body of generated methods, choose Tools | Templates.
		if (!isInitialised()) {
			setInitialised(true);
			setupAudio();
		}
	}

	/**
	 * Set the audio configuration options.
	 */
	private void setupAudio() {
		audio.setAutoplay(cbAutoPlay.isSelected());
		audio.setLoop(!cbLoop.isDisabled() && cbLoop.isSelected());
		audio.setControls(cbControls.isSelected() ? WAudio.Controls.PLAY_PAUSE : WAudio.Controls.NATIVE);
		audio.setDisabled(cbControls.isSelected() && cbDisable.isSelected());
	}
}
