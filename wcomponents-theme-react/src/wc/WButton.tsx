import { Button, IconButton } from "@mui/material";
import type { WComponentNode } from "../data.ts";
import { Cancel, Refresh } from "@mui/icons-material";

function getIconFromClassName(className: string) {
	if (className.includes("fa-refresh")) {
		return <Refresh />;
	}
	if (className.includes("fa-times-circle")) {
		return <Cancel />;
	}
}

export default function WButton(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;
	console.log("button");
	const isIcon = wcNode.className.includes("wc-icon");

	if (isIcon) {
		return <IconButton>{getIconFromClassName(wcNode.className)}</IconButton>;
	}

	return <Button>{props.wcNode.value}</Button>;
}
