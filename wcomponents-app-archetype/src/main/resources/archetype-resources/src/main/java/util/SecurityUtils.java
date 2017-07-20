package ${package}.util;

/**
 * User security-related utility methods.
 * This is just a simple implementation.
 */
public class SecurityUtils
{
    /** Prevent instantiation of utility class. */
    private SecurityUtils()
    {
    }

    /**
     * Authenticates a user.
     *
     * @param userId the user id to check.
     * @param password the password to check.
     * @return true if the user and password combination are valid, false otherwise.
     */
    public static boolean authenticate(final String userId, final String password)
    {
        // This implementation just checks that some details have been entered.
        return userId != null && userId.length() > 1
                && password != null && password.length() > 1;
    }
}
