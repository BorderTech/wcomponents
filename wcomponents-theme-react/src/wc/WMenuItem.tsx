import type { WComponentNode } from "../data.ts";
import { MenuItem } from "@mui/material";
import { WComponentSet } from "../WComponent.tsx";

export default function WMenuItem(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	return (
		<MenuItem id={wcNode.id}>
			<WComponentSet xmlNodes={wcNode.children} />
		</MenuItem>
	);
}
