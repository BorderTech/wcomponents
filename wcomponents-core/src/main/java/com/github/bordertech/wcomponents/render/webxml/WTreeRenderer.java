package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.TreeItemIdNode;
import com.github.bordertech.wcomponents.TreeItemImage;
import com.github.bordertech.wcomponents.TreeItemModel;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTree;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Renderer for the {@link WTree} component.
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
final class WTreeRenderer extends AbstractWebXmlRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WTreeRenderer.class);

	/**
	 * Paints the given WTree.
	 *
	 * @param component the WTree to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {

		WTree tree = (WTree) component;
		XmlStringBuilder xml = renderContext.getWriter();

		// Check if rendering an open item request
		String openId = tree.getOpenRequestItemId();
		if (openId != null) {
			handleOpenItemRequest(tree, xml, openId);
			return;
		}

		// Check the tree has tree items (WCAG requirement)
		TreeItemModel model = tree.getTreeModel();
		if (model == null || model.getRowCount() <= 0) {
			LOG.warn("Tree not rendered as it has no items.");
			return;
		}

		xml.appendTagOpen("ui:tree");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("htree", WTree.Type.HORIZONTAL == tree.getType(), "true");
		xml.appendOptionalAttribute("multiple", WTree.SelectMode.MULTIPLE == tree.getSelectMode(), "true");
		xml.appendOptionalAttribute("disabled", tree.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", tree.isHidden(), "true");
		xml.appendOptionalAttribute("required", tree.isMandatory(), "true");

		switch (tree.getExpandMode()) {
			case CLIENT:
				xml.appendAttribute("mode", "client");
				break;

			case DYNAMIC:
				xml.appendAttribute("mode", "dynamic");
				break;

			case LAZY:
				xml.appendAttribute("mode", "lazy");
				break;

			default:
				throw new IllegalStateException("Invalid expand mode: " + tree.getType());
		}
		xml.appendClose();

		// Render margin
		MarginRendererUtil.renderMargin(tree, renderContext);

		if (tree.getCustomTree() == null) {
			handlePaintItems(tree, xml);
		} else {
			handlePaintCustom(tree, xml);
		}

		DiagnosticRenderUtil.renderDiagnostics(tree, renderContext);
		xml.appendEndTag("ui:tree");
	}

	/**
	 * Paint the item that was on the open request.
	 *
	 * @param tree the WTree to render
	 * @param xml the XML string builder
	 * @param itemId the item id to open
	 */
	protected void handleOpenItemRequest(final WTree tree, final XmlStringBuilder xml, final String itemId) {
		TreeItemModel model = tree.getTreeModel();
		Set<String> selectedRows = new HashSet(tree.getSelectedRows());
		WTree.ExpandMode mode = tree.getExpandMode();

		// Only want to render the children of the "open item" so only include the open item id in the expanded rows
		Set<String> expandedRows = new HashSet();
		expandedRows.add(itemId);

		if (tree.getCustomTree() == null) {
			List<Integer> rowIndex = tree.getExpandedItemIdIndexMap().get(itemId);
			paintItem(tree, mode, model, rowIndex, xml, selectedRows, expandedRows);
		} else {
			TreeItemIdNode node = tree.getCustomIdNodeMap().get(itemId);
			paintCustomItem(tree, mode, model, node, xml, selectedRows, expandedRows);
		}
	}

	/**
	 * Paint the tree items.
	 *
	 * @param tree the WTree to render
	 * @param xml the XML string builder
	 */
	protected void handlePaintItems(final WTree tree, final XmlStringBuilder xml) {
		TreeItemModel model = tree.getTreeModel();
		int rows = model.getRowCount();

		if (rows > 0) {
			Set<String> selectedRows = new HashSet(tree.getSelectedRows());
			Set<String> expandedRows = new HashSet(tree.getExpandedRows());
			WTree.ExpandMode mode = tree.getExpandMode();
			for (int i = 0; i < rows; i++) {
				List<Integer> rowIndex = new ArrayList<>();
				rowIndex.add(i);
				paintItem(tree, mode, model, rowIndex, xml, selectedRows, expandedRows);
			}
		}

	}

	/**
	 * Iterate of over the rows to render the tree items.
	 *
	 * @param tree the WTree to render
	 * @param mode the expand mode
	 * @param model the tree model
	 * @param rowIndex the current row index
	 * @param xml the XML string builder
	 * @param selectedRows the set of selected rows
	 * @param expandedRows the set of expanded rows
	 */
	protected void paintItem(final WTree tree, final WTree.ExpandMode mode, final TreeItemModel model, final List<Integer> rowIndex,
			final XmlStringBuilder xml, final Set<String> selectedRows, final Set<String> expandedRows) {

		String itemId = model.getItemId(rowIndex);

		boolean selected = selectedRows.remove(itemId);

		boolean expandable = model.isExpandable(rowIndex) && model.hasChildren(rowIndex);
		boolean expanded = expandedRows.remove(itemId);

		TreeItemImage image = model.getItemImage(rowIndex);
		String url = null;
		if (image != null) {
			url = tree.getItemImageUrl(image, itemId);
		}

		xml.appendTagOpen("ui:treeitem");
		xml.appendAttribute("id", tree.getItemIdPrefix() + itemId);
		xml.appendAttribute("label", model.getItemLabel(rowIndex));
		xml.appendOptionalUrlAttribute("imageUrl", url);
		xml.appendOptionalAttribute("selected", selected, "true");
		xml.appendOptionalAttribute("expandable", expandable, "true");
		xml.appendOptionalAttribute("open", expandable && expanded, "true");
		xml.appendClose();

		if (expandable && (mode == WTree.ExpandMode.CLIENT || expanded)) {
			// Get actual child count
			int children = model.getChildCount(rowIndex);
			if (children > 0) {
				for (int i = 0; i < children; i++) {
					// Add next level
					List<Integer> nextRow = new ArrayList<>(rowIndex);
					nextRow.add(i);
					paintItem(tree, mode, model, nextRow, xml, selectedRows, expandedRows);
				}
			}

		}

		xml.appendEndTag("ui:treeitem");

	}

	/**
	 * Paint the custom tree layout.
	 *
	 * @param tree the WTree to render
	 * @param xml the XML string builder
	 */
	protected void handlePaintCustom(final WTree tree, final XmlStringBuilder xml) {
		TreeItemModel model = tree.getTreeModel();
		TreeItemIdNode root = tree.getCustomTree();

		Set<String> selectedRows = new HashSet(tree.getSelectedRows());
		Set<String> expandedRows = new HashSet(tree.getExpandedRows());
		WTree.ExpandMode mode = tree.getExpandMode();

		// Process root nodes
		for (TreeItemIdNode node : root.getChildren()) {
			paintCustomItem(tree, mode, model, node, xml, selectedRows, expandedRows);
		}
	}

	/**
	 * Iterate of over the nodes to render the custom layout of the tree items.
	 *
	 * @param tree the WTree to render
	 * @param mode the expand mode
	 * @param model the tree model
	 * @param node the current node in the custom tree layout
	 * @param xml the XML string builder
	 * @param selectedRows the set of selected rows
	 * @param expandedRows the set of expanded rows
	 */
	protected void paintCustomItem(final WTree tree, final WTree.ExpandMode mode, final TreeItemModel model, final TreeItemIdNode node,
			final XmlStringBuilder xml, final Set<String> selectedRows, final Set<String> expandedRows) {

		String itemId = node.getItemId();
		List<Integer> rowIndex = tree.getRowIndexForCustomItemId(itemId);

		boolean selected = selectedRows.remove(itemId);

		boolean expandable = node.hasChildren();
		boolean expanded = expandedRows.remove(itemId);

		TreeItemImage image = model.getItemImage(rowIndex);
		String url = null;
		if (image != null) {
			url = tree.getItemImageUrl(image, itemId);
		}

		xml.appendTagOpen("ui:treeitem");
		xml.appendAttribute("id", tree.getItemIdPrefix() + itemId);
		xml.appendAttribute("label", model.getItemLabel(rowIndex));
		xml.appendOptionalUrlAttribute("imageUrl", url);
		xml.appendOptionalAttribute("selected", selected, "true");
		xml.appendOptionalAttribute("expandable", expandable, "true");
		xml.appendOptionalAttribute("open", expandable && expanded, "true");
		xml.appendClose();

		// Paint child items
		if (expandable && (mode == WTree.ExpandMode.CLIENT || expanded)) {
			for (TreeItemIdNode childNode : node.getChildren()) {
				paintCustomItem(tree, mode, model, childNode, xml, selectedRows, expandedRows);
			}
		}

		xml.appendEndTag("ui:treeitem");

	}

}
