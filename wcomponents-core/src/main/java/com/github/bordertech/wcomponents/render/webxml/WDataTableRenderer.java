package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.TableDataModel;
import com.github.bordertech.wcomponents.TableTreeNode;
import com.github.bordertech.wcomponents.TreeTableDataModel;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDataTable.ExpandMode;
import com.github.bordertech.wcomponents.WDataTable.PaginationMode;
import com.github.bordertech.wcomponents.WDataTable.SelectMode;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WTableColumn.Alignment;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.TreeNode;
import java.util.List;

/**
 * {@link Renderer} for the {@link WDataTable} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WDataTableRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WDataTable.
	 *
	 * @param component the WDataTable to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WDataTable table = (WDataTable) component;
		XmlStringBuilder xml = renderContext.getWriter();
		TableDataModel model = table.getDataModel();

		xml.appendTagOpen("ui:table");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("disabled", table.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", table.isHidden(), "true");
		xml.appendOptionalAttribute("caption", table.getCaption());
		xml.appendOptionalAttribute("summary", table.getSummary());
		xml.appendOptionalAttribute("showRowIndices", table.isShowRowIndices(), "true");
		xml.appendOptionalAttribute("activeFilters", getActiveFilterValues(table));

		switch (table.getType()) {
			case TABLE:
				xml.appendAttribute("type", "table");
				break;
			case HIERARCHIC:
				xml.appendAttribute("type", "hierarchic");
				break;
			default:
				throw new SystemException("Unknown table type: " + table.getType());
		}

		switch (table.getStripingType()) {
			case ROWS:
				xml.appendAttribute("striping", "rows");
				break;
			case COLUMNS:
				xml.appendAttribute("striping", "cols");
				break;
			case NONE:
				break;
			default:
				throw new SystemException("Unknown striping type: " + table.getStripingType());
		}

		switch (table.getSeparatorType()) {
			case HORIZONTAL:
				xml.appendAttribute("separators", "horizontal");
				break;
			case VERTICAL:
				xml.appendAttribute("separators", "vertical");
				break;
			case BOTH:
				xml.appendAttribute("separators", "both");
				break;
			case NONE:
				break;
			default:
				throw new SystemException("Unknown separator type: " + table.getSeparatorType());
		}

		xml.appendClose();

		if (table.getPaginationMode() != PaginationMode.NONE) {
			xml.appendTagOpen("ui:pagination");

			if (model instanceof TreeTableDataModel) {
				// For tree tables, we only include top-level nodes for pagination.
				TreeNode firstNode = ((TreeTableDataModel) model).getNodeAtLine(0);
				xml.appendAttribute("rows", firstNode == null ? 0 : firstNode.getParent().
						getChildCount());
			} else {
				xml.appendAttribute("rows", model.getRowCount());
			}

			xml.appendAttribute("rowsPerPage", table.getRowsPerPage());
			xml.appendAttribute("currentPage", table.getCurrentPage());

			switch (table.getPaginationMode()) {
				case CLIENT:
					xml.appendAttribute("mode", "client");
					break;
				case DYNAMIC:
				case SERVER:
					xml.appendAttribute("mode", "dynamic");
					break;
				case NONE:
					break;
				default:
					throw new SystemException("Unknown pagination mode: " + table.
							getPaginationMode());
			}

			xml.appendEnd();
		}

		if (table.getSelectMode() != SelectMode.NONE) {
			boolean multiple = table.getSelectMode() == SelectMode.MULTIPLE;

			xml.appendTagOpen("ui:rowSelection");
			xml.appendOptionalAttribute("multiple", multiple, "true");

			if (multiple) {
				switch (table.getSelectAllMode()) {
					case CONTROL:
						xml.appendAttribute("selectAll", "control");
						break;
					case TEXT:
						xml.appendAttribute("selectAll", "text");
						break;
					case NONE:
						break;
					default:
						throw new SystemException("Unknown select-all mode: " + table.
								getSelectAllMode());
				}
			}

			xml.appendOptionalAttribute("groupName", table.getSelectGroup());
			xml.appendOptionalAttribute("submitOnChange", table.isSubmitOnRowSelect(), "true");
			xml.appendEnd();
		}

		if (table.getExpandMode() != ExpandMode.NONE) {
			xml.appendTagOpen("ui:rowExpansion");

			switch (table.getExpandMode()) {
				case CLIENT:
					xml.appendAttribute("mode", "client");
					break;
				case SERVER:
					xml.appendAttribute("mode", "server");
					break;
				case LAZY:
					xml.appendAttribute("mode", "lazy");
					break;
				case DYNAMIC:
					xml.appendAttribute("mode", "dynamic");
					break;
				case NONE:
					break;
				default:
					throw new SystemException("Unknown expand mode: " + table.getExpandMode());
			}

			xml.appendOptionalAttribute("expandAll", table.isExpandAll(), "true");

			xml.appendEnd();
		}

		if (table.isSortable()) {
			int col = table.getSortColumnIndex();
			boolean ascending = table.isSortAscending();

			xml.appendTagOpen("ui:sort");
			xml.appendOptionalAttribute("col", col >= 0, col);
			xml.appendOptionalAttribute("descending", col >= 0 && !ascending, "true");

			switch (table.getSortMode()) {
				case DYNAMIC:
					xml.appendAttribute("mode", "dynamic");
					break;
				case SERVER:
					xml.appendAttribute("mode", "server");
					break;
				default:
					throw new SystemException("Unknown sort mode: " + table.getSortMode());
			}

			xml.appendEnd();
		}

		paintColumnHeadings(table, renderContext);
		paintRows(table, renderContext);
		paintTableActions(table, renderContext);

		xml.appendEndTag("ui:table");
	}

	/**
	 * Paints the table actions of the table.
	 *
	 * @param table the table to paint the table actions for.
	 * @param renderContext the RenderContext to paint to.
	 */
	private void paintTableActions(final WDataTable table, final WebXmlRenderContext renderContext) {
		XmlStringBuilder xml = renderContext.getWriter();
		List<WButton> tableActions = table.getActions();

		if (!tableActions.isEmpty()) {
			boolean hasActions = false;

			for (WButton button : tableActions) {
				if (!button.isVisible()) {
					continue;
				}

				if (!hasActions) {
					hasActions = true;
					xml.appendTag("ui:actions");
				}

				xml.appendTag("ui:action");

				List<WDataTable.ActionConstraint> constraints = table.getActionConstraints(button);

				if (constraints != null) {
					for (WDataTable.ActionConstraint constraint : constraints) {
						int minRows = constraint.getMinSelectedRowCount();
						int maxRows = constraint.getMaxSelectedRowCount();
						String message = constraint.getMessage();
						String type = constraint.isError() ? "error" : "warning";

						xml.appendTagOpen("ui:condition");
						xml.appendOptionalAttribute("minSelectedRows", minRows > 0, minRows);
						xml.appendOptionalAttribute("maxSelectedRows", maxRows > 0, maxRows);
						xml.appendAttribute("type", type);
						xml.appendAttribute("message", I18nUtilities.format(null, message));
						xml.appendEnd();
					}
				}

				button.paint(renderContext);

				xml.appendEndTag("ui:action");
			}

			if (hasActions) {
				xml.appendEndTag("ui:actions");
			}
		}
	}

	/**
	 * Paints the rows of the table.
	 *
	 * @param table the table to paint the rows for.
	 * @param renderContext the RenderContext to paint to.
	 */
	private void paintRows(final WDataTable table, final WebXmlRenderContext renderContext) {
		XmlStringBuilder xml = renderContext.getWriter();
		TableDataModel model = table.getDataModel();

		xml.appendTagOpen("ui:tbody");
		xml.appendAttribute("id", table.getRepeater().getId());
		xml.appendClose();

		if (model.getRowCount() == 0) {
			xml.appendTag("ui:noData");
			xml.appendEscaped(table.getNoDataMessage());
			xml.appendEndTag("ui:noData");
		} else {
			// If has at least one visible col, paint the rows.
			final int columnCount = table.getColumnCount();

			for (int i = 0; i < columnCount; i++) {
				if (table.getColumn(i).isVisible()) {
					doPaintRows(table, renderContext);
					break;
				}
			}
		}

		xml.appendEndTag("ui:tbody");
	}

	/**
	 * Override paintRow so that we only paint the first-level nodes for tree-tables.
	 *
	 * @param table the table to paint the rows for.
	 * @param renderContext the RenderContext to paint to.
	 */
	private void doPaintRows(final WDataTable table, final WebXmlRenderContext renderContext) {
		TableDataModel model = table.getDataModel();
		WRepeater repeater = table.getRepeater();
		List<?> beanList = repeater.getBeanList();
		final int rowCount = beanList.size();
		WComponent row = repeater.getRepeatedComponent();

		for (int i = 0; i < rowCount; i++) {
			if (model instanceof TreeTableDataModel) {
				Integer nodeIdx = (Integer) beanList.get(i);
				TableTreeNode node = ((TreeTableDataModel) model).getNodeAtLine(nodeIdx);

				if (node.getLevel() != 1) {
					// Handled by the layout, so don't paint the row.
					continue;
				}
			}

			// Each row has its own context. This is why we can reuse the same
			// WComponent instance for each row.
			UIContext rowContext = repeater.getRowContext(beanList.get(i), i);

			UIContextHolder.pushContext(rowContext);

			try {
				row.paint(renderContext);
			} finally {
				UIContextHolder.popContext();
			}
		}
	}

	/**
	 * Paints the column headings for the given table.
	 *
	 * @param table the table to paint the headings for.
	 * @param renderContext the RenderContext to paint to.
	 */
	private void paintColumnHeadings(final WDataTable table, final WebXmlRenderContext renderContext) {
		XmlStringBuilder xml = renderContext.getWriter();
		int[] columnOrder = table.getColumnOrder();
		TableDataModel model = table.getDataModel();
		final int columnCount = table.getColumnCount();

		xml.appendTagOpen("ui:thead");
		xml.appendOptionalAttribute("hidden", !table.isShowColumnHeaders(), "true");
		xml.appendClose();

		if (table.isShowRowHeaders()) {
			paintColumnHeading(table.getRowHeaderColumn(), false, renderContext);
		}

		for (int i = 0; i < columnCount; i++) {
			int colIndex = columnOrder == null ? i : columnOrder[i];
			WTableColumn col = table.getColumn(colIndex);

			if (col.isVisible()) {
				boolean sortable = model.isSortable(colIndex);
				paintColumnHeading(col, sortable, renderContext);
			}
		}

		xml.appendEndTag("ui:thead");
	}

	/**
	 * Paints a single column heading.
	 *
	 * @param col the column to paint.
	 * @param sortable true if the column is sortable, false otherwise
	 * @param renderContext the RenderContext to paint to.
	 */
	private void paintColumnHeading(final WTableColumn col, final boolean sortable,
			final WebXmlRenderContext renderContext) {
		XmlStringBuilder xml = renderContext.getWriter();
		int width = col.getWidth();
		Alignment align = col.getAlign();

		xml.appendTagOpen("ui:th");
		xml.appendOptionalAttribute("width", width > 0, width);
		xml.appendOptionalAttribute("sortable", sortable, "true");

		if (Alignment.RIGHT.equals(align)) {
			xml.appendAttribute("align", "right");
		} else if (Alignment.CENTER.equals(align)) {
			xml.appendAttribute("align", "center");
		}

		xml.appendClose();

		col.paint(renderContext);

		xml.appendEndTag("ui:th");
	}

	/**
	 * Retrieves the active filter values for the rows in the table.
	 *
	 * @param table the table to retrieve filter values for.
	 * @return a comma-separated list of active filters.
	 */
	private String getActiveFilterValues(final WDataTable table) {
		List<String> filterValues = table.getActiveFilters();

		if (filterValues == null || filterValues.isEmpty()) {
			return null;
		}

		StringBuffer sb = new StringBuffer(filterValues.get(0));

		for (int i = 1; i < filterValues.size(); i++) {
			sb.append(", ");
			sb.append(filterValues.get(i));
		}

		return sb.toString();
	}
}
