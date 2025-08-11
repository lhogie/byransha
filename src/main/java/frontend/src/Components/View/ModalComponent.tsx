import React from "react";

interface ModalComponentProps {
	dataForModal: any;
	isModalOpen: boolean;
	setIsModalOpen: (isOpen: boolean) => void;
}

export const ModalComponent = ({
	dataForModal,
	isModalOpen,
	setIsModalOpen,
}: ModalComponentProps) => {
	// Component disabled - no raw JSON button or modal
	return null;
};
