package com.github.bordertech.wcomponents;

/**
 * Details of the WComponent and XML Validation Error.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class DebugValidateXMLErrorDetail {

	/**
	 * The validation error message text.
	 */
	private final String errorMessage;
	/**
	 * The description of the component which has the validation error.
	 */
	private final String componentDescription;

	/**
	 * Creates a DebugValidateXMLErrorDetail.
	 *
	 * @param errorMessage the error message text.
	 * @param componentDesription the description of the component.
	 */
	public DebugValidateXMLErrorDetail(final String errorMessage, final String componentDesription) {
		this.errorMessage = errorMessage;
		this.componentDescription = componentDesription;
	}

	/**
	 * @return the error message text.
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * @return the description of the component.
	 */
	public String getComponentDescription() {
		return this.componentDescription;
	}

	/**
	 * Indicates whether this error details is equal to the given object.
	 *
	 * Two DebugValidateXMLErrorDetails are considered equal if they have the same error message and component
	 * description.
	 *
	 * @param obj the object to test for equality.
	 * @return true if the given object is a DebugValidateXMLErrorDetail and is equal to this one, otherwise false.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DebugValidateXMLErrorDetail)) {
			return false;
		}

		DebugValidateXMLErrorDetail other = (DebugValidateXMLErrorDetail) obj;
		return other.errorMessage.equals(this.errorMessage) && other.componentDescription.equals(
				this.componentDescription);
	}

	/**
	 * @return the hash code for this DebugValidateXMLErrorDetail.
	 */
	@Override
	public int hashCode() {
		String hash = this.errorMessage + this.componentDescription;
		return hash.hashCode();
	}
}
