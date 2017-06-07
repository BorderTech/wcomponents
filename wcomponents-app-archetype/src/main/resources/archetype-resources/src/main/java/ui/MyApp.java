package ${package}.ui;

import java.util.Date;

import ${package}.model.Customer;
import ${package}.util.DatabaseUtils;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.MessageContainer;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WebUtilities;

/**
 * <p>This class is the application's main component.</p>
 *
 * <p>In a servlet container, this component is returned
 * by the {@link my.groupid.servlet.MyAppServlet#getUI(Object)}
 * method.</p>
 *
 * <p>This component is run from the LDE by setting the configuration
 * parameter <code>bordertech.wcomponents.lde.component.to.launch=${package}</code>
 * in a file named <code>local_app.properties</code> placed in the
 * current working directory.</p>
 */
public class MyApp extends WApplication implements MessageContainer
{
	/** This card manager contains the application's main/top-level screens. */
	private final WCardManager screens = new WCardManager();

	/**
	 * The WMessages component is used to display errors and other
	 * informational messages to the user. Typically, a single instance is
	 * placed near the top of the UI.
	 */
	private final WMessages messages = new WMessages();

    /** The application navigation bar. */
    private final NavigationBar navigationBar = new NavigationBar();

	/** The log-in screen. */
	private final LoginScreen loginScreen = new LoginScreen((new Action()
    {
		public void execute(final ActionEvent event)
		{
		    doLogin();
		}
	}));

    /** The search screen. */
    private final SearchScreen searchScreen = new SearchScreen();

    /** The edit screen. */
    private final EditScreen editScreen = new EditScreen();

    /**
     * Creates a new MyApp.
     *
     * <p>The top-level application component must have a
     * public no-args constructor so that it can be
     * instantiated by the UIRegistry.</p>
     */
    public MyApp()
    {
    	// Header
    	WPanel headerPanel = new WPanel(WPanel.Type.HEADER);
    	headerPanel.add(new WHeading(WHeading.TITLE, "My WComponent Application"));
    	add(headerPanel);
    	add(navigationBar);
    	add(messages);

    	// Main screens
        screens.add(loginScreen);
        screens.add(editScreen);
        screens.add(searchScreen);
        add(screens);

        // Footer
    	WPanel footerPanel = new WPanel(WPanel.Type.FOOTER);
    	footerPanel.add(new WText()
    	{
    		@Override
    		public String getText()
    		{
    		    StringBuffer text = new StringBuffer();
    		    String userId = getUserId();

    		    if (userId != null)
    		    {
    		        text.append("Welcome ").append(userId).append(". ");
    		    }

    			text.append("The current time is ").append(new Date());

    			return text.toString();
    		}
    	});

    	add(footerPanel);

    	// Set default state
    	navigationBar.setVisible(false);
    	screens.makeVisible(loginScreen);
    }

    /**
     * Implementation of MessageContainer - returns the messages instance.
     * @return the messages instance.
     */
    public WMessages getMessages()
    {
    	return messages;
    }

    /**
     * Performs login actions.
     */
    private void doLogin()
    {
        ((MyAppModel) getOrCreateComponentModel()).userId = loginScreen.getUserId();

        screens.reset();
        screens.makeVisible(searchScreen);
        navigationBar.setVisible(true);
    }

    /**
     * Returns a new component model which is appropriate for this component.
     * @return a new MyAppModel.
     */
    @Override
    protected MyAppModel newComponentModel()
    {
        return new MyAppModel();
    }

    /**
     * Returns the id of the currently logged-in user.
     * This method will return null if the login procedure has not completed
     *
     * @return the current user's user id, or null if not logged-in.
     */
    public String getUserId()
    {
        return ((MyAppModel) getComponentModel()).userId;
    }

    /**
     * Navigates to the create customer screen.
     */
    public void navigateToCreateCustomer()
    {
        screens.reset();
        editScreen.setCustomer(new Customer());
        screens.makeVisible(editScreen);
    }

    /**
     * Navigates to the search screen.
     */
    public void navigateToSearch()
    {
        screens.reset();
        screens.makeVisible(searchScreen);
    }

    /**
     * Navigates to the customer details screen.
     * @param customerId the customer to display.
     */
    public void navigateToEdit(final int customerId)
    {
        screens.reset();
        editScreen.setCustomer(DatabaseUtils.read(customerId));
        screens.makeVisible(editScreen);
    }

    /**
     * Retrieves the MyApp ancestor of the given component.
     * @param descendant the component to find the MyApp ancestor of.
     * @return the MyApp ancestor for the given component, or null if not found.
     */
    public static MyApp getInstance(final WComponent descendant)
    {
        return (MyApp) WebUtilities.getAncestorOfClass(MyApp.class, descendant);
    }

    /**
     * This model holds the application-level state information.
     */
    public static final class MyAppModel extends WApplicationModel
    {
        /** The id of the currently logged in user. */
        private String userId;
    }
}
