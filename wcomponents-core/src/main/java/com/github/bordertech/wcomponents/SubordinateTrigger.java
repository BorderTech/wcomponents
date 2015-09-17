package com.github.bordertech.wcomponents;

/**
 * <p>
 * This interface is used to mark components which are able to trigger a client-side Subordinate.</p>
 *
 * <p>
 * <b>NOTE:</b> Components must not implement this interface unless they already support triggering client-side
 * Subordinates. Implementing this interface does
 * <b>NOT</b> automatically enable a component to be used an Subordinate trigger.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface SubordinateTrigger extends WComponent {
}
