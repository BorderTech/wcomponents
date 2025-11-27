import { Folder, FolderOpen, InsertDriveFileOutlined } from "@mui/icons-material";
import type { WComponentNode } from "../data.ts";
import { RichTreeView, type TreeViewBaseItem } from "@mui/x-tree-view";

function convertXMLTreeElementsToTreeViewBaseItems(wcElements: Element[]): TreeViewBaseItem[] {
	return wcElements.map((element) => ({
		id: element.id,
		label: element.getAttribute("label") ?? "",
		children: convertXMLTreeElementsToTreeViewBaseItems(Array.from(element.children)),
	}));
}

export default function WTree(props: { wcNode: WComponentNode }) {
	return (
		<RichTreeView
			items={convertXMLTreeElementsToTreeViewBaseItems(props.wcNode.children as Element[])}
			slots={{ collapseIcon: FolderOpen, expandIcon: Folder, endIcon: InsertDriveFileOutlined }}
		/>
	);
}
