package com.github.bordertech.wcomponents;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * UIContextDelegate implements UIContext, but delegates all the work to a backing context. This allows us to easily
 * write UIContext extensions that only need to change the behaviour of a few methods.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class UIContextDelegate implements UIContext {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(UIContextDelegate.class);

	/**
	 * The backing context.
	 */
	private UIContext backing;

	/**
	 * Creates a UIContextDelegate. This is marked protected, as it doesn't make sense to create an instance of the
	 * delegate without changing behaviour.
	 *
	 * @param backing the backing context.
	 */
	protected UIContextDelegate(final UIContext backing) {
		this.backing = backing;
	}

	/**
	 * Sets the backing context.
	 *
	 * @param backing the backing context.
	 */
	protected void setBacking(final UIContext backing) {
		this.backing = backing;
	}

	@Override
	public void clearScratchMap() {
		backing.clearScratchMap();
	}

	@Override
	public void clearScratchMap(final WComponent component) {
		backing.clearScratchMap(component);
	}

	@Override
	public void doInvokeLaters() {
		backing.doInvokeLaters();
	}

	@Override
	public Set getComponents() {
		return backing.getComponents();
	}

	@Override
	public long getCreationTime() {
		return backing.getCreationTime();
	}

	@Override
	public Environment getEnvironment() {
		return backing.getEnvironment();
	}

	@Override
	public WComponent getFocussed() {
		return backing.getFocussed();
	}

	@Override
	public String getFocussedId() {
		return backing.getFocussedId();
	}

	@Override
	public Object getFwkAttribute(final String name) {
		return backing.getFwkAttribute(name);
	}

	@Override
	public Set getFwkAttributeNames() {
		return backing.getFwkAttributeNames();
	}

	@Override
	public Headers getHeaders() {
		return backing.getHeaders();
	}

	@Override
	public WebModel getModel(final WebComponent component) {
		return backing.getModel(component);
	}

	@Override
	public Map getScratchMap(final WComponent component) {
		return backing.getScratchMap(component);
	}

	@Override
	public WComponent getUI() {
		return backing.getUI();
	}

	@Override
	public void invokeLater(final Runnable runnable) {
		backing.invokeLater(runnable);
	}

	@Override
	public void invokeLater(final UIContext uic, final Runnable runnable) {
		backing.invokeLater(uic, runnable);
	}

	@Override
	public boolean isDummyEnvironment() {
		return backing.isDummyEnvironment();
	}

	@Override
	public boolean isFocusRequired() {
		return backing.isFocusRequired();
	}

	@Override
	public void removeFwkAttribute(final String name) {
		backing.removeFwkAttribute(name);
	}

	@Override
	public void removeModel(final WebComponent component) {
		backing.removeModel(component);
	}

	@Override
	public void setEnvironment(final Environment environment) {
		backing.setEnvironment(environment);
	}

	@Override
	public void setFocusRequired(final boolean b) {
		backing.setFocusRequired(b);
	}

	@Override
	public void setFocussed(final WComponent component, final UIContext uic) {
		backing.setFocussed(component, uic);
	}

	@Override
	public void setFocussed(final WComponent component) {
		backing.setFocussed(component);
	}

	@Override
	public void setFwkAttribute(final String name, final Object value) {
		backing.setFwkAttribute(name, value);
	}

	@Override
	public void setModel(final WebComponent component, final WebModel model) {
		backing.setModel(component, model);
	}

	@Override
	public void setUI(final WComponent ui) {
		backing.setUI(ui);
	}

	@Override
	public Locale getLocale() {
		return backing.getLocale();
	}

	@Override
	public void setLocale(final Locale locale) {
		backing.setLocale(locale);
	}

	/**
	 * @return the backing context.
	 */
	public UIContext getBacking() {
		return backing;
	}

	@Override
	public Map<Object, Object> getRequestScratchMap(final WComponent component) {
		return backing.getRequestScratchMap(component);
	}

	@Override
	public void clearRequestScratchMap(final WComponent component) {
		backing.clearRequestScratchMap(component);
	}

	@Override
	public void clearRequestScratchMap() {
		backing.clearRequestScratchMap();
	}

	/**
	 * A utility function to iterate to the primary (top most) context and return it.
	 *
	 * @param uic the UIContext to retrieve the primary context for.
	 * @return the primary context for the given context.
	 * @deprecated Use {@link UIContextHolder#getPrimaryUIContext(com.github.bordertech.wcomponents.UIContext)} instead.
	 */
	@Deprecated
	public static UIContext getPrimaryUIContext(final UIContext uic) {
		return UIContextHolder.getPrimaryUIContext(uic);
	}
}
