package com.github.bordertech.wcomponents.examples.datatable;

import java.io.Serializable;
import java.util.Date;

/**
 * An example Person bean.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class PersonBean implements Serializable {

	/**
	 * The person's first name.
	 */
	private String firstName;

	/**
	 * The person's last name.
	 */
	private String lastName;

	/**
	 * The person's date of birth.
	 */
	private Date dateOfBirth;

	/**
	 * Creates a PersonBean.
	 *
	 * @param firstName the person's first name.
	 * @param lastName the person's last name.
	 * @param dateOfBirth the person's date of birth.
	 */
	public PersonBean(final String firstName, final String lastName, final Date dateOfBirth) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the person's first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName The first name to set.
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the person's last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName The last name to set.
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the person's date of birth.
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth The date of birth to set.
	 */
	public void setDateOfBirth(final Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getFirstName() + ' ' + getLastName();
	}
}
