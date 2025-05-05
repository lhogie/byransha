import ShikiHighlighter from "react-shiki";

const CustomCodeBlock = ({code, language}) => {
    if (code.length > 50000) {
        return <code style={{whiteSpace: "pre-wrap", padding: "10px", borderRadius: "5px"}}>
            {code.trim()}
        </code>
    } else {
        return <ShikiHighlighter language={language} theme="material-theme-lighter">
            {code.trim()}
        </ShikiHighlighter>
    }
}

export default CustomCodeBlock
