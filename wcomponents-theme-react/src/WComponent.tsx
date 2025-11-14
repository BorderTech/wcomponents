import { type JSX, lazy, type LazyExoticComponent, memo, Suspense } from "react";
import { getWComponentNodeFromElement, type WComponentNode } from "./data.ts";

const WCOMPONENTS_META: { [key: string]: LazyExoticComponent<(props: { wcNode: WComponentNode }) => JSX.Element> } = {
	"ui:application": lazy(() => import("./wc/WApplication.tsx")),
	"ui:columnlayout": lazy(() => import("./wc/WColumnLayout.tsx")),
	"ui:content": lazy(() => import("./wc/WContent.tsx")),
	"ui:decoratedlabel": lazy(() => import("./wc/WDecoratedLabel.tsx")),
	"ui:heading": lazy(() => import("./wc/WHeading.tsx")),
	"ui:js": lazy(() => import("./wc/WNoOp.tsx")),
	"ui:labelbody": lazy(() => import("./wc/WLabelChild.tsx")),
	"ui:labelhead": lazy(() => import("./wc/WLabelChild.tsx")),
	"ui:labeltail": lazy(() => import("./wc/WLabelChild.tsx")),
	"ui:margin": lazy(() => import("./wc/WNoOp.tsx")),
	"ui:menu": lazy(() => import("./wc/WMenu.tsx")),
	"ui:menuitem": lazy(() => import("./wc/WMenuItem.tsx")),
	"ui:panel": lazy(() => import("./wc/WPanel.tsx")),
	"ui:param": lazy(() => import("./wc/WNoOp.tsx")),
	"ui:section": lazy(() => import("./wc/WSection.tsx")),
	"ui:skiplinks": lazy(() => import("./wc/WNoOp.tsx")),
	"ui:tabset": lazy(() => import("./wc/WTabSet.tsx")),
	"ui:text": lazy(() => import("./wc/WText.tsx")),
	"ui:tree": lazy(() => import("./wc/WTree.tsx")),
	"wc-ajaxtrigger": lazy(() => import("./wc/WNoOp.tsx")),

	//"ui:button": lazy(() => import("./wc/WButton.tsx")),
	//"ui:checkbox": lazy(() => import("./wc/WCheckbox.tsx")),
	//"ui:dialog": lazy(() => import("./wc/WDialog.tsx")),
	//"ui:text": lazy(() => import("./wc/WText.tsx")),
};

export const WComponent = memo(function WComponent(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;
	console.log("render wcomponent");
	const Component = WCOMPONENTS_META[wcNode.tagName];
	if (!Component) {
		return <div>Component not found: {wcNode.tagName}</div>;
	}
	return (
		<Suspense fallback={<p>loading...</p>}>
			<Component wcNode={wcNode} />
		</Suspense>
	);
});

export function WComponentSet(props: { wcElements: Element[]; extraAttributes?: { [key: string]: string } }) {
	const { wcElements, extraAttributes } = props;
	return (
		<>
			{wcElements.map((wcElement) => (
				<WComponent wcNode={getWComponentNodeFromElement(wcElement, extraAttributes)} />
			))}
		</>
	);
}
