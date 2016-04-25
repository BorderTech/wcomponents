package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.TableDataModel;
import com.github.bordertech.wcomponents.TableTreeNode;
import com.github.bordertech.wcomponents.TreeTableDataModel;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDataTable.ExpandMode;
import com.github.bordertech.wcomponents.WDataTable.SelectMode;
import com.github.bordertech.wcomponents.WDataTableRowRenderer;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WRepeater.SubUIContext;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.TreeNode;
import java.util.Iterator;
import java.util.List;

/**
 * {@link Renderer} for the {@link WDataTableRowRenderer} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WDataTableRowRendererRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WDataTableRowRenderer.
	 *
	 * @param component the WDataTableRowRenderer to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WDataTableRowRenderer renderer = (WDataTableRowRenderer) component;
		XmlStringBuilder xml = renderContext.getWriter();
		WDataTable table = renderer.getTable();
		TableDataModel dataModel = table.getDataModel();
		UIContext uic = UIContextHolder.getCurrent();

		final int numCols = table.getColumnCount();
		int[] columnOrder = table.getColumnOrder();
		int rowIndex = getRowIndex(table, (SubUIContext) uic);
		boolean unselectable = table.getSelectMode() != SelectMode.NONE
				&& !dataModel.isSelectable(rowIndex);

		xml.appendTagOpen("ui:tr");
		xml.appendAttribute("rowIndex", rowIndex);
		xml.appendOptionalAttribute("unselectable", unselectable, "true");
		xml.appendOptionalAttribute("selected", table.getSelectedRows().contains(rowIndex), "true");
		xml.appendOptionalAttribute("disabled", dataModel.isDisabled(rowIndex), "true");
		xml.appendOptionalAttribute("filterValues", getFilterValues(dataModel, rowIndex));

		if (table.getExpandMode() != WDataTable.ExpandMode.NONE && dataModel instanceof TreeTableDataModel) {
			TableTreeNode node = ((TreeTableDataModel) dataModel).getNodeAtLine(rowIndex);
			boolean expandable = !node.isLeaf() && !node.isExpanded();
			xml.appendOptionalAttribute("expandable", expandable, "true");
		}

		xml.appendClose();

		if (table.isShowRowHeaders()) {
			xml.appendTag("ui:th");
			renderer.getRowHeader().paint(renderContext);
			xml.appendEndTag("ui:th");
		}

		for (int i = 0; i < numCols; i++) {
			int colIndex = columnOrder == null ? i : columnOrder[i];
			WTableColumn col = table.getColumn(colIndex);

			if (col.isVisible()) {
				xml.appendTag("ui:td");
				renderer.getRenderer(colIndex).paint(renderContext);
				xml.appendEndTag("ui:td");
			}
		}

		if (table.getExpandMode() != WDataTable.ExpandMode.NONE && dataModel instanceof TreeTableDataModel) {
			TreeTableDataModel treeModel = (TreeTableDataModel) dataModel;
			TableTreeNode node = treeModel.getNodeAtLine(rowIndex);

			if (!node.isLeaf()) {
				// TODO Do not paint empty subtr. Made temporary change to table.xsd to allow empty subtr. Waiting for a
				// fix in T&S in WC6.
				xml.appendTagOpen("ui:subtr");
				xml.appendOptionalAttribute("open", node.isExpanded(), "true");
				xml.appendClose();

				if (node.isExpanded() || table.getExpandMode() == ExpandMode.CLIENT) {
					WRepeater repeater = table.getRepeater();

					// If there a renderer specified by any child, we only paint content that has a specified renderer
					boolean rendererPresent = false;

					for (Iterator<TreeNode> i = node.children(); !rendererPresent && i.hasNext();) {
						TableTreeNode child = (TableTreeNode) i.next();

						if (child.getRendererClass() != null) {
							rendererPresent = true;
						}
					}

					// Paint immediate children only.
					if (rendererPresent) {
						xml.appendTagOpen("ui:content");
						xml.appendOptionalAttribute("spanAllCols", node.isRendererSpansAllCols(),
								"true");
						xml.appendClose();

						for (Iterator<TreeNode> i = node.children(); i.hasNext();) {
							TableTreeNode child = (TableTreeNode) i.next();
							Integer rowId = child.getRowIndex() - 1;
							UIContext nodeContext = repeater.getRowContext(rowId);

							WComponent expandedRenderer = renderer.getExpandedTreeNodeRenderer(
									child.getRendererClass());

							if (expandedRenderer != null) {
								UIContextHolder.pushContext(nodeContext);

								try {
									expandedRenderer.paint(renderContext);
								} finally {
									UIContextHolder.popContext();
								}
							}
						}

						xml.appendEndTag("ui:content");
					} else {
						for (Iterator<TreeNode> i = node.children(); i.hasNext();) {
							TableTreeNode child = (TableTreeNode) i.next();
							Integer rowId = child.getRowIndex() - 1;
							UIContext nodeContext = repeater.getRowContext(rowId);

							UIContextHolder.pushContext(nodeContext);

							try {
								render(component, renderContext);
							} finally {
								UIContextHolder.popContext();
							}
						}
					}
				}

				xml.appendEndTag("ui:subtr");
			}
		}

		xml.appendEndTag("ui:tr");
	}

	/**
	 * Retrieves the filter values for the given row in the data model, as a comma-separated string.
	 *
	 * @param dataModel the data model.
	 * @param rowIndex the row index.
	 * @return the filter values string.
	 */
	private String getFilterValues(final TableDataModel dataModel, final int rowIndex) {
		List<String> filterValues = dataModel.getFilterValues(rowIndex);

		if (filterValues == null || filterValues.isEmpty()) {
			return null;
		}

		StringBuffer buf = new StringBuffer(filterValues.get(0));

		for (int i = 1; i < filterValues.size(); i++) {
			buf.append(", ");
			buf.append(filterValues.get(i));
		}

		return buf.toString();
	}

	/**
	 * @param table the table that the row renderer belongs to
	 * @param uic the SubUIContext to determine the row index for
	 * @return the row index for the specified SubUIContext.
	 */
	private int getRowIndex(final WDataTable table, final SubUIContext uic) {
		// SubUIContext does not cater for pagination
		return (Integer) table.getRepeater().getBeanList().get(uic.getRowIndex());
	}
}
