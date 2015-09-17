package com.github.bordertech.wcomponents.examples.petstore.model;

import java.io.Serializable;

/**
 * A bean encapsulating an address.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AddressBean implements Serializable {

	/**
	 * The first line of the street address.
	 */
	private String line1;
	/**
	 * The second line of the street address.
	 */
	private String line2;
	/**
	 * The address suburb/town.
	 */
	private String suburb;
	/**
	 * The address state/territory/province.
	 */
	private String state;
	/**
	 * The address post code.
	 */
	private String postcode;
	/**
	 * The address country.
	 */
	private String country;

	/**
	 * @return Returns the country.
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country The country to set.
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	/**
	 * @return Returns the line1.
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @param line1 The line1 to set.
	 */
	public void setLine1(final String line1) {
		this.line1 = line1;
	}

	/**
	 * @return Returns the line2.
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @param line2 The line2 to set.
	 */
	public void setLine2(final String line2) {
		this.line2 = line2;
	}

	/**
	 * @return Returns the postcode.
	 */
	public String getPostcode() {
		return postcode;
	}

	/**
	 * @param postcode The postcode to set.
	 */
	public void setPostcode(final String postcode) {
		this.postcode = postcode;
	}

	/**
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * @return Returns the suburb.
	 */
	public String getSuburb() {
		return suburb;
	}

	/**
	 * @param suburb The suburb to set.
	 */
	public void setSuburb(final String suburb) {
		this.suburb = suburb;
	}
}
