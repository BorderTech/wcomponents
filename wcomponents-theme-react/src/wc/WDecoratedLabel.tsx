import type { WComponentNode } from "../data.ts";
import { WComponentSet } from "../WComponent.tsx";

// TODO: This component does not yet fully implement wc.ui.decoratedlabel.xsl
export default function WDecoratedLabel(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const type = wcNode.attributes["type"];
	const className = `wc-decoratedlabel${type ? " wc-decoratedlabel-type-" + type : ""}${wcNode.className ? " " + wcNode.className : ""}`;

	return (
		<span id={wcNode.id} className={className}>
			<WComponentSet wcElements={wcNode.children} />
		</span>
	);
}
