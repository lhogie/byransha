interface CustomCodeBlockProps {
	code: string;
	language: string;
}

const CustomCodeBlock = ({ code, language }: CustomCodeBlockProps) => {
	return (
		<code
			style={{
				whiteSpace: "pre-wrap",
				padding: "10px",
				borderRadius: "5px",
				fontFamily: "Monaco, 'Courier New', monospace",
				fontSize: "14px",
				backgroundColor: "#f5f5f5",
				display: "block",
				overflow: "auto",
			}}
			className={`language-${language}`}
		>
			{code.trim()}
		</code>
	);
};

export default CustomCodeBlock;
