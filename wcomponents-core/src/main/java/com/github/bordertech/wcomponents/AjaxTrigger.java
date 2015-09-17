package com.github.bordertech.wcomponents;

/**
 * <p>
 * This interface is used to mark components which are able to trigger an AJAX request.</p>
 *
 * <p>
 * <b>NOTE:</b> Components must not implement this interface unless they already support client-side AJAX triggering.
 * Implementing this interface does
 * <b>NOT</b> automatically enable a component to be used an AJAX trigger.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface AjaxTrigger extends WComponent {
}
