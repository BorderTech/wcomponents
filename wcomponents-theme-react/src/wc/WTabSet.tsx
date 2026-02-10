import { Tab } from "@mui/material";
import { getWComponentNodeFromElement, type WComponentNode } from "../data.ts";
import { useContext, useMemo, useState } from "react";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { WComponent, WComponentSet } from "../WComponent.tsx";
import { TabSetContext } from "../contexts.ts";

interface WTabMeta {
	tabNode: WComponentNode;
	labelNode: WComponentNode | null;
	contentNodes: ChildNode[];
}

// Converts the WComponents XML representation of tabs into the JSON expected by the MUI TabList components.
function getWTabMetasFromWTabSetNode(wcNode: WComponentNode): { tabMetas: WTabMeta[]; tabIndex: number } {
	let tabIndex = 0;
	const tabMetas = wcNode.children.map((tab, i) => {
		const tabNode = getWComponentNodeFromElement(tab)!;
		if (tabNode.attributes["open"] === "true") {
			tabIndex = i;
		}
		return {
			tabNode,
			labelNode:
				(tabNode.children[0] as Element)?.tagName === "ui:decoratedlabel"
					? getWComponentNodeFromElement(tabNode.children[0])
					: null,
			contentNodes:
				(tabNode.children[1] as Element)?.tagName === "ui:tabcontent"
					? Array.from(tabNode.children[1].childNodes)
					: [],
		};
	});
	return { tabMetas, tabIndex };
}

export default function WTabSet(props: { wcNode: WComponentNode }) {
	// Need to provide a random key to force remount/state reset when XML changes.
	return <WTabSetContent key={Math.random()} wcNode={props.wcNode} />;
}

// We're using the experimental TabList from @mui/lab (https://mui.com/material-ui/api/tab-list/) because it provides
// a cleaner API compared to the original Tabs (https://mui.com/material-ui/react-tabs/).
// Eventually TabList will be standardised and removed from the experimental lab library. It should then be
// imported from @mui/material like everything else.
function WTabSetContent(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const processRequest = useContext(TabSetContext);

	// Avoid regenerating the TabSet data from XML on every render.
	const { tabMetas, tabIndex: initialTabIndex } = useMemo(() => getWTabMetasFromWTabSetNode(wcNode), [wcNode]);

	const [tabIndex, setTabIndex] = useState<number>(initialTabIndex);

	return (
		<TabContext value={tabIndex}>
			<TabList
				onChange={(_e, newValue) => {
					setTabIndex(newValue);
					if (
						tabMetas[newValue].tabNode.attributes["mode"] === "lazy" &&
						!tabMetas[newValue].contentNodes.length
					) {
						processRequest(wcNode.id, tabMetas[tabIndex].tabNode.id, newValue);
					}
				}}
			>
				{tabMetas.map((t, i) => (
					<Tab
						label={t.labelNode ? <WComponent wcNode={t.labelNode} /> : `TAB ${i}`}
						sx={{ textTransform: "none" }}
						value={i}
					/>
				))}
			</TabList>
			{tabMetas.map((t, i) => (
				<TabPanel value={i}>
					<WComponentSet xmlNodes={t.contentNodes} />
				</TabPanel>
			))}
		</TabContext>
	);
}
