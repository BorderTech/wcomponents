package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Audio;
import com.github.bordertech.wcomponents.AudioResource;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WAudio;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.subordinate.Condition;
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

	private final WCheckBox cbAutoPlay = new WCheckBox();
	private final WCheckBox cbLoop = new WCheckBox();
	// private final WCheckBox cbMute = new WCheckBox();
	private final WCheckBox cbControls = new WCheckBox();


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

		final WAudio audio = new WAudio(audioClips);
		audio.setAltText("Example WAudio content");

		WFieldLayout layout = new WFieldLayout();
		add(layout);
		layout.addField("Autoplay", cbAutoPlay);
		layout.addField("Loop", cbLoop);
		// layout.addField("Mute", cbMute);
		layout.addField("Show controls", cbControls);

		WButton btnApply = new WButton("Apply settings");
		btnApply.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				audio.setAutoplay(cbAutoPlay.isSelected());
				audio.setLoop(!cbLoop.isDisabled() && cbLoop.isSelected());
				// audio.setMute(!cbMute.isDisabled() && cbMute.isSelected());
				audio.setControls(cbControls.isSelected() ? WAudio.Controls.NATIVE : WAudio.Controls.NONE);
			}
		});
		layout.addField((String) null, btnApply);

		WSubordinateControl control = new WSubordinateControl();
		Rule rule = new Rule();
		rule.setCondition(new Equal(cbControls, Boolean.TRUE.toString()));
		rule.addActionOnTrue(new Disable(cbLoop));
		// rule.addActionOnTrue(new Disable(cbMute));
		rule.addActionOnFalse(new Enable(cbLoop));
		// rule.addActionOnFalse(new Enable(cbMute));

		add(audio);

		// add (new WAjaxControl(btnApply, audio));

	}
}
