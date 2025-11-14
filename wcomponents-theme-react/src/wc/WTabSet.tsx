import { Tab } from "@mui/material";
import { getWComponentNodeFromElement, type WComponentNode } from "../data.ts";
import { useState } from "react";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { WComponent, WComponentSet } from "../WComponent.tsx";

interface WTabMeta {
	tabNode: WComponentNode;
	labelNode?: WComponentNode;
	contentNodes: Element[];
}

function getWTabMetasFromWTabSetNode(wcNode: WComponentNode): WTabMeta[] {
	return wcNode.children.map((tab) => {
		const tabNode = getWComponentNodeFromElement(tab);
		return {
			tabNode,
			labelNode:
				tabNode.children[0]?.tagName === "ui:decoratedlabel"
					? getWComponentNodeFromElement(tabNode.children[0])
					: undefined,
			contentNodes:
				tabNode.children[1]?.tagName === "ui:tabcontent" ? Array.from(tabNode.children[1].children) : [],
		};
	});
}

export default function WTabSet(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const [tabIndex, setTabIndex] = useState<number>(0);

	const tabMetas = getWTabMetasFromWTabSetNode(wcNode);

	return (
		<TabContext value={tabIndex}>
			<TabList onChange={(_e, newValue) => setTabIndex(newValue)}>
				{tabMetas.map((t, i) => (
					<Tab label={t.labelNode ? <WComponent wcNode={t.labelNode} /> : `TAB ${i}`} value={i} />
				))}
			</TabList>
			{tabMetas.map((t, i) => (
				<TabPanel value={i}>
					<WComponentSet wcElements={t.contentNodes} />
				</TabPanel>
			))}
		</TabContext>
	);
}
