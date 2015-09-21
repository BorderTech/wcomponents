package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * An example Person bean.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class PersonBean implements Serializable {

	/**
	 * The person id.
	 */
	private final String personId;

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
	 * More beans.
	 */
	private List<PersonBean> more;

	/**
	 * Document beans.
	 */
	private List<TravelDoc> documents;

	/**
	 * Creates a PersonBean.
	 *
	 * @param personId the person's unique id
	 * @param firstName the person's first name.
	 * @param lastName the person's last name.
	 * @param dateOfBirth the person's date of birth.
	 */
	public PersonBean(final String personId, final String firstName, final String lastName,
			final Date dateOfBirth) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the person id;
	 */
	public String getPersonId() {
		return personId;
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

	/**
	 * @return the more details.
	 */
	public List<PersonBean> getMore() {
		return more;
	}

	/**
	 * @param more the more details to set.
	 */
	public void setMore(final List<PersonBean> more) {
		this.more = more;
	}

	/**
	 * @return the documents.
	 */
	public List<TravelDoc> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set.
	 */
	public void setDocuments(final List<TravelDoc> documents) {
		this.documents = documents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object o) {
		return (o instanceof PersonBean) && Util.equals(personId, ((PersonBean) o).getPersonId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return personId.hashCode();
	}

	/**
	 * Example travel document bean.
	 */
	public static final class TravelDoc implements Serializable {

		/**
		 * The travel document number.
		 */
		private String documentNumber;
		/**
		 * The country which issued the travel document.
		 */
		private String countryOfIssue;
		/**
		 * The place where the travel document was issued.
		 */
		private String placeOfIssue;
		/**
		 * The date when the travel document was issued.
		 */
		private Date issueDate;
		/**
		 * The date when the travel document expires.
		 */
		private Date expiryDate;

		/**
		 * Creates a TravelDoc.
		 *
		 * @param documentNumber the document number
		 * @param countryOfIssue the country of issue
		 * @param placeOfIssue the place of issue
		 * @param issueDate the date of issue
		 * @param expiryDate the expiry date
		 */
		public TravelDoc(final String documentNumber, final String countryOfIssue,
				final String placeOfIssue,
				final Date issueDate, final Date expiryDate) {
			this.documentNumber = documentNumber;
			this.countryOfIssue = countryOfIssue;
			this.placeOfIssue = placeOfIssue;
			this.issueDate = issueDate;
			this.expiryDate = expiryDate;
		}

		/**
		 * @return Returns the documentNumber.
		 */
		public String getDocumentNumber() {
			return documentNumber;
		}

		/**
		 * @param documentNumber The documentNumber to set.
		 */
		public void setDocumentNumber(final String documentNumber) {
			this.documentNumber = documentNumber;
		}

		/**
		 * @return Returns the countryOfIssue.
		 */
		public String getCountryOfIssue() {
			return countryOfIssue;
		}

		/**
		 * @param countryOfIssue The countryOfIssue to set.
		 */
		public void setCountryOfIssue(final String countryOfIssue) {
			this.countryOfIssue = countryOfIssue;
		}

		/**
		 * @return Returns the placeOfIssue.
		 */
		public String getPlaceOfIssue() {
			return placeOfIssue;
		}

		/**
		 * @param placeOfIssue The placeOfIssue to set.
		 */
		public void setPlaceOfIssue(final String placeOfIssue) {
			this.placeOfIssue = placeOfIssue;
		}

		/**
		 * @return Returns the issueDate.
		 */
		public Date getIssueDate() {
			return issueDate;
		}

		/**
		 * @param issueDate The issueDate to set.
		 */
		public void setIssueDate(final Date issueDate) {
			this.issueDate = issueDate;
		}

		/**
		 * @return Returns the expiryDate.
		 */
		public Date getExpiryDate() {
			return expiryDate;
		}

		/**
		 * @param expiryDate The expiryDate to set.
		 */
		public void setExpiryDate(final Date expiryDate) {
			this.expiryDate = expiryDate;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object o) {
			return (o instanceof TravelDoc) && Util.equals(documentNumber, ((TravelDoc) o).
					getDocumentNumber());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return documentNumber.hashCode();
		}

	}

}
