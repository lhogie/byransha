export const shortenAndFormatLabel = (label) => {
	if (!label) return '';
	const spaced = label
		.replace(/([a-z])([A-Z])/g, '$1 $2')
		.replace(/_/g, ' ')
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
export const createKey = (id, name) => `${id}@${name}`;

export const inputTextField = ["StringNode", "EmailNode", "PhoneNumberNode", "IntNode"];
export const checkboxField =  ["BooleanNode"];
export const dateField = ["DateNode"];
export const imageField =  ["ImageNode"];
export const dropdownField = ["DropdownNode"];
export const typeComponent = [...inputTextField, ...checkboxField, ...dateField, ...dropdownField];
