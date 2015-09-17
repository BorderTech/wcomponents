package com.github.bordertech.wcomponents.examples.petstore.model;

import java.io.Serializable;

/**
 * A bean encapsulating the details necessary to fulfil an order.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ConfirmOrderBean implements Serializable {

	/**
	 * The client's first name.
	 */
	private String firstName;
	/**
	 * The client's last name.
	 */
	private String lastName;

	/**
	 * The client's address.
	 */
	private AddressBean address = new AddressBean();

	/**
	 * The client's home phone number.
	 */
	private String homePhone;
	/**
	 * The client's work phone number.
	 */
	private String workPhone;
	/**
	 * The client's email address.
	 */
	private String emailAddress;

	/**
	 * The payment type for the transaction.
	 */
	private String paymentType;

	/**
	 * @return Returns the address.
	 */
	public AddressBean getAddress() {
		return address;
	}

	/**
	 * Sets the client's address.
	 *
	 * @param address the address to set.
	 */
	public void setAddress(final AddressBean address) {
		this.address = address == null ? new AddressBean() : address;
	}

	/**
	 * @return Returns the client's first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the client's first name.
	 *
	 * @param firstName The first name to set.
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return Returns the client's home phone number.
	 */
	public String getHomePhone() {
		return homePhone;
	}

	/**
	 * Sets the client's home phone number.
	 *
	 * @param homePhone The home phone number to set.
	 */
	public void setHomePhone(final String homePhone) {
		this.homePhone = homePhone;
	}

	/**
	 * @return Returns the client's last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the client's last name.
	 *
	 * @param lastName The last name to set.
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return Returns the payment type.
	 */
	public String getPaymentType() {
		return paymentType;
	}

	/**
	 * Sets the payment type for the transaction.
	 *
	 * @param paymentType The payment type to set.
	 */
	public void setPaymentType(final String paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * @return Returns the workPhone.
	 */
	public String getWorkPhone() {
		return workPhone;
	}

	/**
	 * @param workPhone The workPhone to set.
	 */
	public void setWorkPhone(final String workPhone) {
		this.workPhone = workPhone;
	}

	/**
	 * @return Returns the client's email address.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the client's email address.
	 *
	 * @param emailAddress The email address to set.
	 */
	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
