import { type ElementType, type JSX, lazy, type LazyExoticComponent, memo, Suspense } from "react";
import { getWComponentNodeFromElement, type WComponentNode } from "./data.ts";
import { Alert, CircularProgress } from "@mui/material"; // Map of all supported components.

// Map of all supported components.
// After implementing a new component, it must be registered here before use.
const WCOMPONENTS_META: { [key: string]: LazyExoticComponent<(props: { wcNode: WComponentNode }) => JSX.Element> } = {
	button: lazy(() => import("./wc/WButton.tsx")),
	script: lazy(() => import("./wc/WScript.tsx")),
	"ui:columnlayout": lazy(() => import("./wc/WColumnLayout.tsx")),
	"ui:content": lazy(() => import("./wc/WContent.tsx")),
	"ui:datefield": lazy(() => import("./wc/WDateField.tsx")),
	"ui:decoratedlabel": lazy(() => import("./wc/WDecoratedLabel.tsx")),
	"ui:definitionlist": lazy(() => import("./wc/WDefinitionList.tsx")),
	"ui:field": lazy(() => import("./wc/WField.tsx")),
	"ui:fieldlayout": lazy(() => import("./wc/WFieldLayout.tsx")),
	"ui:flowlayout": lazy(() => import("./wc/WFlowLayout.tsx")),
	"ui:heading": lazy(() => import("./wc/WHeading.tsx")),
	"ui:js": lazy(() => import("./wc/WNoOp.tsx")),
	"ui:label": lazy(() => import("./wc/WLabel.tsx")),
	"ui:labelbody": lazy(() => import("./wc/WLabelChild.tsx")),
	"ui:labelhead": lazy(() => import("./wc/WLabelChild.tsx")),
	"ui:labeltail": lazy(() => import("./wc/WLabelChild.tsx")),
	"ui:listlayout": lazy(() => import("./wc/WListLayout.tsx")),
	"ui:margin": lazy(() => import("./wc/WNoOp.tsx")),
	"ui:menu": lazy(() => import("./wc/WMenu.tsx")),
	"ui:panel": lazy(() => import("./wc/WPanel.tsx")),
	"ui:param": lazy(() => import("./wc/WNoOp.tsx")),
	"ui:section": lazy(() => import("./wc/WSection.tsx")),
	"ui:skiplinks": lazy(() => import("./wc/WNoOp.tsx")),
	"ui:tabset": lazy(() => import("./wc/WTabSet.tsx")),
	"ui:text": lazy(() => import("./wc/WText.tsx")),
	"ui:textfield": lazy(() => import("./wc/WTextField.tsx")),
	"ui:tree": lazy(() => import("./wc/WTree.tsx")),
	"wc-ajaxtrigger": lazy(() => import("./wc/WAjaxTrigger.tsx")),
};

// The top level WComponent serves as the first step to determining how to handle a wcNode.
// Attempts to load and render the component based on tag name in WCOMPONENTS_META above.
// Unsupported tags will show a warning message.
// Native HTML will be passed through and rendered as is.
export const WComponent = memo(function WComponent(props: { wcNode: WComponentNode | null }) {
	const { wcNode } = props;
	if (!wcNode) {
		return <></>;
	}
	const Component = WCOMPONENTS_META[wcNode.tagName];
	if (!Component) {
		if (wcNode.tagName.includes(":") || wcNode.tagName.includes("-")) {
			return (
				<Alert severity="info">
					Component not implemented: <b>{wcNode.tagName}</b>
				</Alert>
			);
		}
		return <NativeHTML wcNode={wcNode} />;
	}
	return (
		<Suspense fallback={<CircularProgress />}>
			<Component wcNode={wcNode} />
		</Suspense>
	);
});

// Renders a WComponent for each XML node.
// This should be added somewhere within every component which may have children.
export function WComponentSet(props: { xmlNodes: ChildNode[]; extraAttributes?: { [key: string]: string } }) {
	const { xmlNodes, extraAttributes } = props;
	return (
		<>
			{xmlNodes.map((xmlNode, i) => (
				<WComponent key={i} wcNode={getWComponentNodeFromElement(xmlNode, extraAttributes)} />
			))}
		</>
	);
}

// Passthrough for native HTML encountered in the application XML structure.
function NativeHTML(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	if (!wcNode.tagName) {
		return <>{wcNode.value}</>;
	}

	const NativeTag = wcNode.tagName as ElementType;
	return (
		<>
			{wcNode.children.length ? (
				<NativeTag {...wcNode.attributes}>
					<WComponentSet xmlNodes={wcNode.children} />
				</NativeTag>
			) : (
				<NativeTag {...wcNode.attributes} />
			)}
		</>
	);
}
