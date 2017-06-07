package ${package}.ui;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WContainer;

/**
 * The application navigation/menu bar.
 */
public class NavigationBar extends WContainer
{
    /** The backing menu. */
    private final WMenu menu = new WMenu();

    /**
     * Creates the navigation bar.
     */
    public NavigationBar()
    {
        add(menu);

        menu.add(new WMenuItem("Customer search", new Action()
        {
            public void execute(final ActionEvent event)
            {
                MyApp.getInstance(NavigationBar.this).navigateToSearch();
            }
        }));

        menu.add(new WMenuItem("Create new Customer", new Action()
        {
            public void execute(final ActionEvent event)
            {
                MyApp.getInstance(NavigationBar.this).navigateToCreateCustomer();
            }
        }));
    }
}
