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
	value: string | number | Date | any[] | null | undefined,
	validations: any,
) => {
	if (
		validations?.required &&
		(value === null ||
			value === undefined ||
			(typeof value === "string" && value.trim() === "") ||
			(Array.isArray(value) && value.length === 0))
	) {
		return "Ce champ est requis";
	}

	if (
		validations?.min !== undefined &&
		typeof value === "number" &&
		value < validations.min
	) {
		return `La valeur doit être au moins ${validations.min}`;
	}

	if (
		validations?.max !== undefined &&
		typeof value === "number" &&
		value > validations.max
	) {
		return `La valeur doit être au plus ${validations.max}`;
	}

	if (validations?.size) {
		const len = Array.isArray(value)
			? value.length
			: typeof value === "string"
				? value.length
				: -1;
		if (len !== -1) {
			if (validations.size.min !== undefined && len < validations.size.min) {
				return `Doit contenir au moins ${validations.size.min} caractères/éléments`;
			}
			if (validations.size.max !== undefined && len > validations.size.max) {
				return `Doit contenir au plus ${validations.size.max} caractères/éléments`;
			}
		}
	}

	if (
		validations?.pattern &&
		typeof value === "string" &&
		!new RegExp(validations.pattern).test(value)
	) {
		return `Doit correspondre au modèle : ${validations.pattern}`;
	}

	switch (type) {
		case "EmailNode":
			return "Veuillez entrer une adresse e-mail valide";
		case "IntNode":
			return "Veuillez entrer un entier valide";
		case "PhoneNumberNode":
			return "Veuillez entrer un numéro de téléphone valide";
		case "DateNode":
			return "Veuillez entrer une date valide";
		default:
			return "Valeur invalide";
	}
};

export const validateFieldValue = (
	type: string,
	value: string | number | Date | any[] | null | undefined,
	validations: any,
) => {
	let isValid = true;
	let message = "";

	// Required validation
	if (
		validations?.required &&
		(value === null ||
			value === undefined ||
			(typeof value === "string" && value.trim() === "") ||
			(Array.isArray(value) && value.length === 0))
	) {
		isValid = false;
		message = "Ce champ est requis";
		return { isValid, message };
	}

	// If not required and value is empty, it's valid
	if (
		!validations?.required &&
		(value === null ||
			value === undefined ||
			(typeof value === "string" && value.trim() === "") ||
			(Array.isArray(value) && value.length === 0))
	) {
		return { isValid: true, message: "" };
	}

	// Min validation
	if (
		validations?.min !== undefined &&
		typeof value === "number" &&
		value < validations.min
	) {
		isValid = false;
		message = `La valeur doit être au moins ${validations.min}`;
		return { isValid, message };
	}

	// Max validation
	if (
		validations?.max !== undefined &&
		typeof value === "number" &&
		value > validations.max
	) {
		isValid = false;
		message = `La valeur doit être au plus ${validations.max}`;
		return { isValid, message };
	}

	// Size validation
	if (validations?.size) {
		const len = Array.isArray(value)
			? value.length
			: typeof value === "string"
				? value.length
				: -1;
		if (len !== -1) {
			if (validations.size.min !== undefined && len < validations.size.min) {
				isValid = false;
				message = `Doit contenir au moins ${validations.size.min} caractères/éléments`;
				return { isValid, message };
			}
			if (validations.size.max !== undefined && len > validations.size.max) {
				isValid = false;
				message = `Doit contenir au plus ${validations.size.max} caractères/éléments`;
				return { isValid, message };
			}
		}
	}

	// Pattern validation
	if (
		validations?.pattern &&
		typeof value === "string" &&
		!new RegExp(validations.pattern).test(value)
	) {
		isValid = false;
		message = `Doit correspondre au modèle : ${validations.pattern}`;
		return { isValid, message };
	}

	switch (type) {
		case "IntNode":
			isValid = /^-?\d+$/.test(value as string);
			if (!isValid) message = "Veuillez entrer un entier valide";
			break;
		case "DateNode":
			isValid = dayjs(value as string).isValid();
			if (!isValid) message = "Veuillez entrer une date valide";
			break;
	}

	return { isValid, message };
};

export const inputTextField = [
	"StringNode",
	"EmailNode",
	"PhoneNumberNode",
	"IntNode",
	"HistoryChangeNode",
	"MimeTypeNode",
];
export const checkboxField = ["BooleanNode", "HideNode"];
export const documentField = ["DocumentNode"];
export const dateField = ["DateNode"];
export const listField = ["ListNode"];
export const colorField = ["ColorNode"];
export const typeComponent = [
	...inputTextField,
	...documentField,
	...checkboxField,
	...dateField,
	...colorField,
];
