import { useState, useRef } from "react";
import { Editor } from "@monaco-editor/react";
import PropTypes from "prop-types";
import useWebSocket from "react-use-websocket";
import { v4 as uuidv4 } from "uuid";

export const CodeEditor = ({ language, startingCode, onChangeCallback }) => {
    const [isInitialized, setIsInitialized] = useState(false);
    const [editorInstance, setEditorInstance] = useState(null);
    const completionProviderRef = useRef(null);

    const { sendJsonMessage } = useWebSocket(import.meta.env.VITE_PYLSP_URL, {
        onOpen: () => {
            console.log("✅ Połączono z serwerem LSP");
            initializeLsp();
        },
        onMessage: (event) => {
            try {
                const response = JSON.parse(event.data);
                console.log("📥 Odebrano pełną odpowiedź:", JSON.stringify(response, null, 2));

                if (response.error) {
                    console.error("❌ Błąd LSP:", response.error);
                    return;
                }

                if (response.id === "initialize") {
                    console.log("✅ LSP zainicjalizowany");
                    setIsInitialized(true);
                } else if (response.result) {
                    if (Array.isArray(response.result.items)) {
                        console.log("💡 Odebrane podpowiedzi:", response.result.items);

                        if (completionProviderRef.current) {
                            completionProviderRef.current.dispose();
                        }

                        completionProviderRef.current = monaco.languages.registerCompletionItemProvider("python", {
                            provideCompletionItems: () => ({
                                suggestions: response.result.items.map(item => ({
                                    label: item.label,
                                    kind: monaco.languages.CompletionItemKind.Function,
                                    insertText: item.insertText || item.label,
                                    detail: item.detail || "",
                                    documentation: item.documentation || ""
                                }))
                            })
                        });
                    }

                    if (typeof response.result.contents === "string") {
                        monaco.languages.registerHoverProvider("python", {
                            provideHover: () => ({
                                contents: [{ value: response.result.contents }]
                            })
                        });
                    }
                }
            } catch (error) {
                console.error("❌ Błąd parsowania JSON:", error);
            }
        },
        shouldReconnect: () => true,
    });

    const initializeLsp = () => {
        sendJsonMessage({
            jsonrpc: "2.0",
            id: "initialize",
            method: "initialize",
            params: { processId: null, rootUri: null, capabilities: {}, clientInfo: { name: "ReactLSPClient", version: "1.0" }}
        });
        sendJsonMessage({
            jsonrpc: "2.0",
            id: "initialized",
            method: "initialized",
            params: {}
        });
        sendJsonMessage({
            jsonrpc: "2.0",
            id: uuidv4(),
            method: "textDocument/didOpen",
            params: {
                textDocument: { uri: "file://dummy.py", languageId: "python", version: 1, text: startingCode || "" }
            }
        });
    };

    const handleEditorDidMount = (editor) => {
        setEditorInstance(editor);
    };

    const handleChange = (value) => {
        if (onChangeCallback) {
            onChangeCallback(value);
        }

        if (!isInitialized) return;

        sendJsonMessage({
            jsonrpc: "2.0",
            method: "textDocument/didChange",
            params: {
                textDocument: { uri: "file://dummy.py" },
                contentChanges: [{ text: value }],
            },
        });

        setTimeout(() => handleCompletion(value), 50);
    };

    const handleCompletion = (newCode) => {
        if (!isInitialized) return;

        const lines = newCode.split("\n");
        const lastLineIndex = lines.length - 1;
        const lastLineLength = lines[lastLineIndex]?.length || 0;

        sendJsonMessage({
            jsonrpc: "2.0",
            id: uuidv4(),
            method: "textDocument/completion",
            params: {
                textDocument: { uri: "file://dummy.py" },
                position: { line: lastLineIndex, character: lastLineLength },
            },
        });
    };

    return (
        <Editor
            defaultLanguage={language}
            defaultValue={startingCode}
            onMount={handleEditorDidMount}
            onChange={handleChange}
            theme="vs-dark"
            height="100%"
            width="100dvw"
            options={{
                wordWrap: "on",
                minimap: { enabled: false },
                inlayHints: { enabled: "on" },
                scrollbar: { horizontalScrollbarSize: 0 },
            }}
        />
    );
};

CodeEditor.propTypes = {
    language: PropTypes.string.isRequired,
    startingCode: PropTypes.string,
    onChangeCallback: PropTypes.func,
};
