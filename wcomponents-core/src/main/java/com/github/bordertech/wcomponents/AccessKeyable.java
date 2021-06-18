package com.github.bordertech.wcomponents;

/**
 * Components that can have an access key.
 */
public interface AccessKeyable extends WComponent {

	/**
	 * @return the component's access key.
	 */
	char getAccessKey();

	/**
	 * Set the access key on the component.
	 *
	 * @param accessKey the key that will form a keyboard shortcut to the component.
	 */
	void setAccessKey(final char accessKey);

	/**
	 * Returns the access key character as a String. If the character is not a letter or digit then <code>null</code> is
	 * returned.
	 *
	 * @return the access key character as a String (may be <code>null</code>).
	 */
	default String getAccessKeyAsString() {
		char accessKey = getAccessKey();

		if (Character.isLetterOrDigit(accessKey)) {
			return String.valueOf(accessKey);
		}

		return null;
	}

}
