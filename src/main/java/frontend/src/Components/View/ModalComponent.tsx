interface ModalComponentProps {
	dataForModal: any;
	isModalOpen: boolean;
	setIsModalOpen: (isOpen: boolean) => void;
}

export const ModalComponent = ({
	dataForModal: _dataForModal,
	isModalOpen: _isModalOpen,
	setIsModalOpen: _setIsModalOpen,
}: ModalComponentProps) => {
	return null;
};

export default ModalComponent;
