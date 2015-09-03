package ${package}.ui;

import com.github.openborders.wcomponents.Action;
import com.github.openborders.wcomponents.ActionEvent;
import com.github.openborders.wcomponents.WMenu;
import com.github.openborders.wcomponents.WMenuItem;
import com.github.openborders.wcomponents.WContainer;

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
