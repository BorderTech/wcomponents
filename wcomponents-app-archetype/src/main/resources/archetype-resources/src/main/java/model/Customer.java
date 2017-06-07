package ${package}.model;

import java.io.Serializable;
import java.util.Date;

/**
 * A sample Customer bean.
 */
public class Customer implements Serializable
{
    /** The customer first name. */
    private String firstName;

    /** The customer last name. */
    private String lastName;

    /** The customer date of birth. */
    private Date dateOfBirth;

    /** The customer address. */
    private Address address = new Address();

    /** The customer id. */
    private int customerId;

    /**
     * @return the customer first name.
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * @param firstName The first name to set.
     */
    public void setFirstName(final String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * @return the customer last name.
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @param lastName The last name to set.
     */
    public void setLastName(final String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * @return the customer date of birth.
     */
    public Date getDateOfBirth()
    {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth The date of birth to set.
     */
    public void setDateOfBirth(final Date dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return the address
     */
    public Address getAddress()
    {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(final Address address)
    {
        this.address = address;
    }

    /**
     * @return the customer id
     */
    public int getCustomerId()
    {
        return customerId;
    }

    /**
     * @param customerId the customer id to set
     */
    public void setCustomerId(final int customerId)
    {
        this.customerId = customerId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getFirstName() + ' ' + getLastName();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return customerId;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        return obj instanceof Customer
                && customerId == ((Customer) obj).customerId;
    }
}
