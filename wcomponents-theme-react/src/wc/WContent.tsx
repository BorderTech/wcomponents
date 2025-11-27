import { WComponentSet } from "../WComponent.tsx";
import type { WComponentNode } from "../data.ts";

export default function WContent(props: { wcNode: WComponentNode }) {
	return (
		<div className="wc-content">
			<WComponentSet xmlNodes={props.wcNode.children} />
		</div>
	);
}
