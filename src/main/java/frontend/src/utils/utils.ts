import dayjs from "dayjs";

export const shortenAndFormatLabel = (label: string) => {
	if (!label) return "";
	const spaced = label
		.replace(/([a-z])([A-Z])/g, "$1 $2")
		.replace(/_/g, " ")
		.trim();
	return spaced.charAt(0).toUpperCase() + spaced.slice(1);
};

/***
 * Creates a unique key for a node based on its ID and name.
 * This is useful for identifying nodes in lists or maps.
 * @param {string} id - The unique identifier of the node.
 * @param {string} name - The name of the node.
 * @return {string} A string in the format "id@name" that uniquely identifies the node.
 */
export const createKey = (id: string | number, name: string) => `${id}@${name}`;

export const getErrorMessage = (
	type: string,
	_value: string | number | Date,
) => {
	switch (type) {
		case "EmailNode":
			return "Please enter a valid email address";
		case "IntNode":
			return "Please enter a valid integer";
		case "PhoneNumberNode":
			return "Please enter a valid phone number (digits only)";
		case "DateNode":
			return "Please enter a valid date";
		default:
			return "Invalid value";
	}
};

export const validateFieldValue = (type: string, value: string) => {
	// If value is null or undefined, it's valid (empty is allowed)
	if (value === null || value === undefined) return true;

	// For empty strings, consider them valid
	if (typeof value === "string" && value.trim() === "") return true;

	// Validate based on field type
	let isValid = true;
	switch (type) {
		case "EmailNode": {
			// Email validation using regex pattern from EmailNode.java
			const emailRegex = /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/;
			isValid = emailRegex.test(value);
			break;
		}

		case "IntNode":
			// Check if value is a valid integer
			isValid = /^-?\d+$/.test(value);
			break;

		case "PhoneNumberNode":
			// Check if value is a valid phone number (digits only)
			isValid = /^\d+$/.test(value);
			break;

		case "DateNode":
			// Check if value is a valid dayjs date object
			isValid = dayjs(value).isValid();
			break;

		default:
			// For other field types, consider them valid
			isValid = true;
			break;
	}

	return isValid;
};

export const inputTextField = [
	"StringNode",
	"EmailNode",
	"PhoneNumberNode",
	"IntNode",
];
export const checkboxField = ["BooleanNode"];
export const dateField = ["DateNode"];
export const imageField = ["ImageNode"];
export const dropdownField = ["DropdownNode"];
export const radioField = ["RadioNode"];
export const listCheckboxField = ["ListCheckboxNode"];
export const fileField = ["FileNode"];
export const listField = ["ListNode", "SetNode"];
export const typeComponent = [
	...inputTextField,
	...fileField,
	...checkboxField,
	...listCheckboxField,
	...dateField,
	...dropdownField,
	...radioField,
];
