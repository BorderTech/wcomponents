import { Button, IconButton } from "@mui/material";
import type { WComponentNode } from "../data.ts";
import { Cancel, Refresh } from "@mui/icons-material";

// TODO: This is only temporary handling of some icons for testing/demo purposes.
// Ideally this should support any icon in a more robust manner.
function getIconFromClassName(className: string) {
	if (className.includes("fa-refresh")) {
		return <Refresh />;
	}
	if (className.includes("fa-times-circle")) {
		return <Cancel />;
	}
}

// Example implementation of <button> using MUI Button (https://mui.com/material-ui/react-button/)
// and IconButton (https://mui.com/material-ui/api/icon-button/).
export default function WButton(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const isIcon = wcNode.className.includes("wc-icon");

	if (isIcon) {
		return <IconButton>{getIconFromClassName(wcNode.className)}</IconButton>;
	}

	return (
		<Button
			id={wcNode.id}
			value={wcNode.attributes["value"]}
			name={wcNode.attributes["name"]}
			type={wcNode.attributes["type"] as "button" | "submit" | "reset" | undefined}
			variant="outlined"
			disabled={wcNode.attributes["disabled"] === "disabled"}
			sx={{ textTransform: "none" }}
		>
			{wcNode.value}
		</Button>
	);
}
