import ShikiHighlighter from "react-shiki";

const CustomCodeBlock = ({code, language}) => {
    if (code.length > 1000) {
        return <code>
            {code.trim()}
        </code>
    } else {
        return <ShikiHighlighter language={language} theme="material-theme-lighter">
            {code.trim()}
        </ShikiHighlighter>
    }
}

export default CustomCodeBlock
