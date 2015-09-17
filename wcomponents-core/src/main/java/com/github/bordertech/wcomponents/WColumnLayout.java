package com.github.bordertech.wcomponents;

/**
 * <p>
 * This component is used to group together two components into a two column (left and right) display.
 * </p><p>
 * A common usage would be two wrap two {@link WFieldLayout} instances into two columns.
 * </p>
 *
 * @author Adam Millard
 * @since 1.0.0
 */
public class WColumnLayout extends AbstractNamingContextContainer {

	/**
	 * The row which contains the columns.
	 */
	private final WRow row = new WRow();

	/**
	 * The left column.
	 */
	private final WColumn leftColumn;

	/**
	 * The right column.
	 */
	private final WColumn rightColumn;

	/**
	 * Creates a column layout with no heading.
	 */
	public WColumnLayout() {
		this((WHeading) null);
	}

	/**
	 * Creates a column layout with the given section heading.
	 *
	 * @param heading the heading text.
	 */
	public WColumnLayout(final String heading) {
		this(new WHeading(WHeading.SECTION, heading));
	}

	/**
	 * Creates a column layout with the given heading.
	 *
	 * @param heading the heading.
	 */
	public WColumnLayout(final WHeading heading) {
		if (heading != null) {
			add(heading);
		}

		leftColumn = new WColumn(50);
		leftColumn.setVisible(false);
		row.add(leftColumn);

		rightColumn = new WColumn(50);
		rightColumn.setVisible(false);
		row.add(rightColumn);

		add(row);
	}

	/**
	 * Sets the left column content.
	 *
	 * @param content the content.
	 */
	public void setLeftColumn(final WComponent content) {
		setLeftColumn((WHeading) null, content);
	}

	/**
	 * Sets the left column content.
	 *
	 * @param heading the column heading text.
	 * @param content the content.
	 */
	public void setLeftColumn(final String heading, final WComponent content) {
		setLeftColumn(new WHeading(WHeading.MINOR, heading), content);
	}

	/**
	 * Sets the left column content.
	 *
	 * @param heading the column heading.
	 * @param content the content.
	 */
	public void setLeftColumn(final WHeading heading, final WComponent content) {
		setContent(leftColumn, heading, content);
	}

	/**
	 * Sets the right column content.
	 *
	 * @param content the content.
	 */
	public void setRightColumn(final WComponent content) {
		setRightColumn((WHeading) null, content);
	}

	/**
	 * Sets the right column content.
	 *
	 * @param heading the column heading text.
	 * @param content the content.
	 */
	public void setRightColumn(final String heading, final WComponent content) {
		setRightColumn(new WHeading(WHeading.MINOR, heading), content);
	}

	/**
	 * Sets the right column content.
	 *
	 * @param heading the column heading.
	 * @param content the content.
	 */
	public void setRightColumn(final WHeading heading, final WComponent content) {
		setContent(rightColumn, heading, content);
	}

	/**
	 * Sets the content of the given column and updates the column widths and visibilities.
	 *
	 * @param column the column being updated.
	 * @param heading the column heading.
	 * @param content the content.
	 */
	private void setContent(final WColumn column, final WHeading heading, final WComponent content) {
		column.removeAll();

		if (heading != null) {
			column.add(heading);
		}

		if (content != null) {
			column.add(content);
		}

		// Update column widths & visibility
		if (hasLeftContent() && hasRightContent()) {
			// Set columns 50%
			leftColumn.setWidth(50);
			rightColumn.setWidth(50);
			// Set both visible
			leftColumn.setVisible(true);
			rightColumn.setVisible(true);
		} else {
			// Set columns 100% (only one visible)
			leftColumn.setWidth(100);
			rightColumn.setWidth(100);
			// Set visibility
			leftColumn.setVisible(hasLeftContent());
			rightColumn.setVisible(hasRightContent());
		}
	}

	/**
	 * For the DIMAv2 theme and above, this can be used to change the left column attributes.
	 *
	 * @return the left column.
	 */
	public WColumn getLeftColumn() {
		return leftColumn;
	}

	/**
	 * For the DIMAv2 theme and above, this can be used to change the right column attributes.
	 *
	 * @return the right column.
	 */
	public WColumn getRightColumn() {
		return rightColumn;
	}

	/**
	 * @return true if there is content in the left column.
	 */
	public boolean hasLeftContent() {
		return leftColumn.getChildCount() > 0;
	}

	/**
	 * @return true if there is content in the right column.
	 */
	public boolean hasRightContent() {
		return rightColumn.getChildCount() > 0;
	}
}
