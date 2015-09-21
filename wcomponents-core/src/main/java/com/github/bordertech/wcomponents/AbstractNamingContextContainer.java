package com.github.bordertech.wcomponents;

/**
 * This abstract container implements NamingContextable.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractNamingContextContainer extends AbstractContainer implements
		NamingContextable {

	/**
	 * A naming context is only considered active if it has been set active via {@link #setNamingContext(boolean)} and
	 * also has an id name set via {@link #setIdName(String)}.
	 *
	 * @param context set true if this is a naming context.
	 */
	public void setNamingContext(final boolean context) {
		setFlag(ComponentModel.NAMING_CONTEXT_FLAG, context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNamingContext() {
		return isFlagSet(ComponentModel.NAMING_CONTEXT_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamingContextId() {
		return getId();
	}
}
