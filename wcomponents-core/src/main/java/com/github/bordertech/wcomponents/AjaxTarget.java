package com.github.bordertech.wcomponents;

/**
 * <p>
 * This interface is used to mark components which are to be individually targeted for replacement with AJAX.</p>
 *
 * <p>
 * <b>NOTE:</b> Components must not implement this interface unless they already support client-side AJAX targeting.
 * Implementing this interface does
 * <b>NOT</b> automatically enable a component to be used an AJAX target.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface AjaxTarget extends WComponent {
}
