package com.github.bordertech.wcomponents;

/**
 * A menu item that can group other menu items.
 * <p>
 * Even though MenuItemGroup extends MenuContainer, it is not allowed to have a nested MenuItemGroup.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.2
 * @deprecated menu groups are not compatible with WCAG 2.0.
 */
@Deprecated
public interface MenuItemGroup extends MenuItem, MenuContainer {
}
