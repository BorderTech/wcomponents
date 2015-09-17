package com.github.bordertech.wcomponents;

/**
 * This repeater extension is necessary to ensure that tree-tables are painted correctly.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 * @deprecated Use {@link WTable} instead.
 */
@Deprecated
public class WTableRepeater extends WRepeater {

	/**
	 * Parent table.
	 */
	private final WDataTable table;

	/**
	 * @param table the parent table.
	 */
	public WTableRepeater(final WDataTable table) {
		this.table = table;
		setNamingContext(true);
	}

	/**
	 * Override paintComponent, as the table renderer does all the work.
	 *
	 * @param renderContext the RenderContext to send output to.
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return table.getId() + ID_CONTEXT_SEPERATOR + getIdName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdName() {
		return "row";
	}

}
