import type { WComponentNode } from "../data.ts";
import { MenuList } from "@mui/material";
import { WComponentSet } from "../WComponent.tsx";

export default function WMenu(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	return (
		<MenuList id={wcNode.id}>
			<WComponentSet wcElements={wcNode.children} />
		</MenuList>
	);
}
