package com.github.bordertech.wcomponents;

/**
 * A track provides additional information relating to the video, for example subtitles. Note that all themes support
 * tracks in WebVTT format. These files are encoded as UTF-8 and must be served with MIME type "text/vtt". Themes may
 * provide additional support for different track formats.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface Track extends ContentStreamAccess {

	/**
	 * Describes the kind of track. This is used by the client to determine how the track should be handled.
	 */
	enum Kind {
		/**
		 * Transcription or translation of the dialogue, suitable for when the sound is available but not understood
		 * (e.g. because the user does not understand the language of the media resource's audio track). Overlaid on the
		 * video.
		 */
		SUBTITLES,
		/**
		 * Transcription or translation of the dialogue, sound effects, relevant musical cues, and other relevant audio
		 * information, suitable for when sound is unavailable or not clearly audible (e.g. because it is muted,
		 * drowned-out by ambient noise, or because the user is deaf). Overlaid on the video; labeled as appropriate for
		 * the hard-of-hearing.
		 */
		CAPTIONS,
		/**
		 * Textual descriptions of the video component of the media resource, intended for audio synthesis when the
		 * visual component is obscured, unavailable, or not usable (e.g. because the user is interacting with the
		 * application without a screen while driving, or because the user is blind). Synthesized as audio.
		 */
		DESCRIPTIONS,
		/**
		 * Chapter titles, intended to be used for navigating the media resource. Displayed as an interactive
		 * (potentially nested) list in the user agent's interface.
		 */
		CHAPTERS,
		/**
		 * Tracks intended for use from script. Not displayed by the user agent.
		 */
		METADATA
	}

	/**
	 * @return the ISO 639-1 language code for this track.
	 */
	String getLanguage();

	/**
	 * @return the type of this track.
	 */
	Kind getKind();
}
