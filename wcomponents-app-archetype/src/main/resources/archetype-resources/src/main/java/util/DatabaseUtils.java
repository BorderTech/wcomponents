package ${package}.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ${package}.model.Address;
import ${package}.model.Customer;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * Data-related utility methods.
 * The "database" is a set of hard-coded data.
 */
public class DatabaseUtils
{
    /** The list of customers in the "database". */
    private static final Set<Customer> customers = new HashSet<Customer>();
    
    /** Prevent instantiation of utility class. */
    private DatabaseUtils()
    {
    }

    static
    {
        // Populate the customer data
        String[][] data =
        {
            { "Mable", "Poppe", "1975-09-28", "1023 Heather Autoroute", "Michopdo", "Montana", "59724-4868", "US" },
            { "Edwin", "Padua", "1984-01-12", "1130 Lazy Rise Towers", "Rimbey", "Wisconsin", "54519-9776", "US" },
            { "Desiree", "Harkins", "1983-09-25", "1148 Indian Pioneer Mews", "Invincible", "Northwest Territories", "X5L-3I7", "CA" },
            { "Troy", "Dattilo", "1963-07-31", "1667 Little Bluff Expressway", "Cost", "Britsh Columbia", "V6Z-1W2", "CA" },
            { "Marcus", "Degraff", "1964-05-05", "1900 Harvest Blossom By-pass", "Nanton", "Pennsylvania", "15217-2892", "US" },
            { "Francisco", "Sanor", "1985-09-24", "220 High Promenade", "Craven", "Arkansas", "72699-7442", "US" },
            { "Nadine", "Mcphearson", "1960-11-18", "2414 Tawny Place", "Normalville", "Wisconsin", "53724-1538", "US" },
            { "Sophie", "Graney", "1972-04-11", "3046 Bright View Row", "Marengo", "Virginia", "22836-8614", "US" },
            { "Krystal", "Gravitt", "1978-12-29", "3240 Hazy Bear Gardens", "Waterwitch", "Iowa", "50354-2614", "US" },
            { "Alfred", "Elliston", "1968-08-24", "3657 Broad Pine Thicket", "Bermuda", "Montana", "59220-0202", "US" },
            { "Kendra", "Stiver", "1972-08-02", "3708 Dewy Run", "Yznaga", "West Virginia", "24859-4793", "US" },
            { "Estelle", "Huth", "1982-07-29", "4626 Silver Terrace", "Dismal Key", "Massachusetts", "02610-6142", "US" },
            { "Lee", "Mackenzie", "1984-01-28", "4724 Easy Corner", "Missionary", "Virginia", "23401-2873", "US" },
            { "Kyle", "Wrigley", "1964-12-03", "4806 Amber Bay", "Tallyho", "Mississippi", "38888-2114", "US" },
            { "Christie", "Perrigo", "1985-08-09", "5442 Honey Hill", "Flippen", "North Dakota", "58649-6721", "US" },
            { "Doreen", "Griggs", "1973-04-26", "5452 Merry Sky Bank", "Tenstrike", "Michigan", "49526-8014", "US" },
            { "Kendra", "Elsea", "1978-09-03", "5738 Wishing Pony Drive", "Laird", "New Mexico", "87313-4365", "US" },
            { "Edwin", "Stanger", "1987-04-23", "595 Iron Port", "Totstalahoeetska", "Britsh Columbia", "V0J-8M4", "CA" },
            { "Kari", "Oberle", "1964-03-25", "6090 Cotton Canyon", "Ah Fong Village", "Pennsylvania", "17832-6636", "US" },
            { "Alfred", "Laplante", "1968-04-09", "6949 Grand Range", "Curtin", "Florida", "32120-8133", "US" },
            { "Chad", "Barefoot", "1969-01-19", "7180 Burning Elk Trail", "Thunder Hawk", "New Jersey", "08569-4493", "US" },
            { "Francis", "Galentine", "1988-10-27", "7652 Clear Fox Impasse", "Aquashicola", "Montana", "59343-6353", "US" },
            { "Francisco", "Stabile", "1966-08-04", "7916 Green Hills Crossing", "Gum Hill", "New Hampshire", "03333-0192", "US" },
            { "Mercedes", "Krieg", "1970-09-05", "8083 Sleepy Butterfly Lookout", "Pipestone", "New Mexico", "87608-8497", "US" },
            { "Marcus", "Rosenberry", "1987-11-09", "8184 Dusty Branch Trace", "Original", "Kansas", "66509-4399", "US" },
            { "Marcus", "Applewhite", "1970-08-07", "8485 Cozy Deer Turnabout", "Supplee", "Indiana", "47386-4582", "US" },
            { "Frederick", "Razo", "1966-08-25", "8705 Middle Isle", "Warman", "Mississippi", "38906-1283", "US" },
            { "Melvin", "Mcfadden", "1969-01-11", "8714 Shady Glade", "Sturgis", "New Mexico", "88193-8870", "US" },
            { "Lana", "Lederman", "1983-04-02", "8888 Sunny Cloud Extension", "Fable", "New Mexico", "88302-9730", "US" },
            { "Leroy", "Lach", "1969-01-31", "9186 Lost Highway", "Big Cut", "Kansas", "67299-5335", "US" }
        };

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int customerId = 1;

        for (String[] row : data)
        {
            Customer customer = new Customer();
            customer.setFirstName(row[0]);
            customer.setLastName(row[1]);
            customer.setCustomerId(customerId++);

            try
            {
                customer.setDateOfBirth(dateFormat.parse(row[2]));
            }
            catch (ParseException ignored)
            {
                // not going to happen for hard-coded data
            }

            Address address = new Address();
            address.setLine1(row[3]);
            address.setCity(row[4]);
            address.setState(row[5]);
            address.setPostcode(row[6]);
            address.setCountry(row[7]);
            customer.setAddress(address);

            customers.add(customer);
        }
    }

    /**
     * Performs a search of the customer data.
     * @param criteria the search criteria.
     * @return the list of customers matching the criteria. May be empty.
     */
    public static synchronized List<Customer> search(final Customer criteria)
    {
        List<Customer> results = new ArrayList<Customer>();

        for (Customer customer : customers)
        {
            if (
                   (criteria.getCustomerId() == 0 || criteria.getCustomerId() == customer.getCustomerId())
                   && isMatch(criteria.getFirstName(), customer.getFirstName())
                   && isMatch(criteria.getLastName(), customer.getLastName())
                   && isMatch(criteria.getDateOfBirth(), customer.getDateOfBirth())
                )
            {
                results.add(customer);
            }
        }

        return results;
    }

    /**
     * Reads the customer with the given id.
     * @param customerId the customer id.
     * @return the customer with the given id, or null if not found.
     */
    public static synchronized Customer read(final int customerId)
    {
        for (Customer customer : customers)
        {
            if (customerId == customer.getCustomerId())
            {
                return customer;
            }
        }

        return null;
    }

    /**
     * Saves a customer to the database.
     * @param customer the customer to save
     */
    public static synchronized void save(final Customer customer)
    {
        if (customer.getCustomerId() == 0)
        {
            // fake an ID sequence
            int maxId = 0;

            for (Customer existing : customers)
            {
                maxId = Math.max(existing.getCustomerId(), maxId);
            }

            customer.setCustomerId(maxId + 1);
        }

        customers.add(clone(customer));
    }

    /**
     * Deletes a customer from the database.
     * @param customer the customer to delete.
     */
    public static synchronized void delete(final Customer customer)
    {
        customers.remove(customer);
    }

    /**
     * Copies a Customer bean via serialization.
     * This method is used to return copies of the internal data structure.
     *
     * @param customer the Customer bean to clone.
     * @return the cloned Customer bean.
     */
    private static Customer clone(final Customer customer)
    {
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(customer);
            oos.close();

            byte[] bytes = bos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object out = ois.readObject();
            ois.close();
            
            return (Customer) out;
        }
        catch (Exception ex)
        {
            throw new SystemException("Failed to clone " + customer, ex);
        }
    }

    /**
     * Checks whether the criteria matches the object.
     * @param criteria the criteria to check.
     * @param actual the actual object to check.
     * @return true if the criteria matches, false otherwise.
     */
    private static boolean isMatch(final Object criteria, final Object actual)
    {
        if (criteria == null || "".equals(criteria))
        {
            return true;
        }
        else if (criteria instanceof String)
        {
            return ((String) actual).toLowerCase().contains(((String) criteria).toLowerCase());
        }

        return criteria.equals(actual);
    }
}
