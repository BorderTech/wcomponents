import { DesktopDatePicker } from "@mui/x-date-pickers";
import { type WComponentNode } from "../data.ts";

// Date field implementation using MUI Date Picker (https://mui.com/x/react-date-pickers/date-picker/).
// TODO: This component is a stub. Need to wire up props to the DesktopDatePicker.
export default function WDateField(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	return <DesktopDatePicker label={wcNode.attributes["labelText"]} />;
}
