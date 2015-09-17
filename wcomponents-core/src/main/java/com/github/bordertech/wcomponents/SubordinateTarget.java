package com.github.bordertech.wcomponents;

/**
 * <p>
 * This interface is used to mark components which can be targeted by a Subordinate control.</p>
 *
 * <p>
 * <b>NOTE:</b> Components must not implement this interface unless they already support client-side Subordinate
 * targeting. Implementing this interface does
 * <b>NOT</b> automatically enable a component to be used an Subordinate target.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface SubordinateTarget extends WComponent {
}
