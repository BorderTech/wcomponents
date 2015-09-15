package com.github.bordertech.wcomponents;

/**
 * MockTrack - implementation of Track useful for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockTrack extends MockContentStreamAccess implements Track {

	/**
	 * The kind of track, e.g. subtitle.
	 */
	private Kind kind;

	/**
	 * The ISO 639-1 language code for this track.
	 */
	private String language;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Kind getKind() {
		return kind;
	}

	/**
	 * @param kind The kind to set.
	 */
	public void setKind(final Kind kind) {
		this.kind = kind;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language The language to set.
	 */
	public void setLanguage(final String language) {
		this.language = language;
	}
}
