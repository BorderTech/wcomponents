import { Folder, FolderOpen, InsertDriveFileOutlined } from "@mui/icons-material";
import type { WComponentNode } from "../data.ts";
import { RichTreeView, type TreeViewBaseItem } from "@mui/x-tree-view";
import { useContext, useMemo, useState } from "react";
import { RequestContext } from "../contexts.ts";

// Converts the WComponents XML representation of a tree into the JSON expected by the MUI RichTreeView component.
function convertXMLTreeElementsToTreeViewBaseItems(wcElements: Element[]): {
	tree: TreeViewBaseItem[];
	expandedItems: string[];
} {
	const expandedItems: string[] = [];

	const convertElements = (arr: Element[]): TreeViewBaseItem[] => {
		return arr.map((element) => {
			if (element.getAttribute("open") === "true") {
				expandedItems.push(element.id);
			}
			return {
				id: element.id,
				label: element.getAttribute("label") ?? "",
				children: convertElements(Array.from(element.children)),
			};
		});
	};

	return { tree: convertElements(wcElements), expandedItems };
}

export default function WTree(props: { wcNode: WComponentNode }) {
	// Need to provide a random key to force remount/state reset when XML changes.
	return <WTreeContent key={Math.random()} wcNode={props.wcNode} />;
}

// Uses the MUI Tree View component (https://mui.com/x/react-tree-view/) to render WComponent tree menus.
function WTreeContent(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const processAjax = useContext(RequestContext);

	// Avoid regenerating the TreeView data from XML on every render.
	const { tree, expandedItems: initialExpandedItems } = useMemo(
		() => convertXMLTreeElementsToTreeViewBaseItems(wcNode.children as Element[]),
		[wcNode.children],
	);

	const [expandedItems, setExpandedItems] = useState<string[]>(initialExpandedItems);

	return (
		<>
			<RichTreeView
				items={tree}
				expandedItems={expandedItems}
				onExpandedItemsChange={(_e, itemIds) => {
					setExpandedItems(itemIds);
				}}
				isItemSelectionDisabled={(item) => !!item.children && item.children.length > 0}
				onItemSelectionToggle={(_e, itemId, isSelected) => {
					if (isSelected) {
						processAjax(wcNode.id, itemId, true);
					}
				}}
				slots={{ collapseIcon: FolderOpen, expandIcon: Folder, endIcon: InsertDriveFileOutlined }}
			/>
			{expandedItems.map((expandedItemId) => (
				<input type="hidden" name={`${wcNode.id}.open`} value={expandedItemId} />
			))}
			<input type="hidden" name={`${wcNode.id}-h`} value="x" />
		</>
	);
}
