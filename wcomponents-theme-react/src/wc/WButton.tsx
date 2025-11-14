import { Button } from "@mui/material";
import type { WComponentNode } from "../data.ts";

// TODO: This component is a stub.
export default function WButton(props: { wcNode: WComponentNode }) {
	console.log("button");
	return <Button>{props.wcNode.value}</Button>;
}
