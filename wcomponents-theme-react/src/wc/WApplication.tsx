import { WComponentSet } from "../WComponent.tsx";
import type { WComponentNode } from "../data.ts";

export default function WApplication(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	return (
		<div id={wcNode.id}>
			<WComponentSet wcElements={wcNode.children} />
		</div>
	);
}
