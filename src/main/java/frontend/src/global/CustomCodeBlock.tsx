const CustomCodeBlock = ({ code }: { code: string; language: string }) => {
	return (
		<code
			style={{ whiteSpace: "pre-wrap", padding: "10px", borderRadius: "5px" }}
		>
			{code.trim()}
		</code>
	);
};

export default CustomCodeBlock;
