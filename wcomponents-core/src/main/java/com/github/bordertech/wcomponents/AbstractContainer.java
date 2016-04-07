package com.github.bordertech.wcomponents;

import java.util.List;

/**
 * This abstract implements the {@link Container} interface to expose methods for retrieving the contents of the
 * container. This class is extended by most WComponents which may contain other components.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class AbstractContainer extends AbstractWComponent implements Container {

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getChildCount() {
		return super.getChildCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	@Override
	public List<WComponent> getChildren() {
		return super.getChildren();
	}
}
