package ${package}.model;

import java.io.Serializable;

/**
 * A sample Address bean.
 */
public class Address implements Serializable
{
    /** Address line 1. */
    private String line1;

    /** Address line 2. */
    private String line2;

    /** Address town/city. */
    private String city;

    /** Address state/province. */
    private String state;

    /** Address postcode. */
    private String postcode;

    /** Address country. */
    private String country;

    /**
     * @return the first street line
     */
    public String getLine1()
    {
        return line1;
    }

    /**
     * @param line1 the first street line to set
     */
    public void setLine1(final String line1)
    {
        this.line1 = line1;
    }

    /**
     * @return the second street line
     */
    public String getLine2()
    {
        return line2;
    }

    /**
     * @param line2 second street line to set
     */
    public void setLine2(final String line2)
    {
        this.line2 = line2;
    }

    /**
     * @return the city
     */
    public String getCity()
    {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(final String city)
    {
        this.city = city;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(final String state)
    {
        this.state = state;
    }

    /**
     * @return the post code
     */
    public String getPostcode()
    {
        return postcode;
    }

    /**
     * @param postcode the post code to set
     */
    public void setPostcode(final String postcode)
    {
        this.postcode = postcode;
    }

    /**
     * @return the country
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(final String country)
    {
        this.country = country;
    }
}
