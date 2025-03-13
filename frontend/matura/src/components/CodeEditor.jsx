import {useState, useRef} from "react";
import {Editor} from "@monaco-editor/react";
import PropTypes from "prop-types";
import useWebSocket from "react-use-websocket";
import {v4 as uuidv4} from "uuid";

export const CodeEditor = ({language, startingCode, onChangeCallback}) => {
    const [isInitialized, setIsInitialized] = useState(false);
    const [editorInstance, setEditorInstance] = useState(null);
    const completionProviderRegistered = useRef(false);  // Ref to track registration

    const {sendJsonMessage, lastJsonMessage} = useWebSocket(import.meta.env.VITE_PYLSP_URL, {
        onOpen: () => {
            initializeLsp();
            console.log("✅ Connected to PyLSP");
        },
        onMessage: (event) => {
            const response = JSON.parse(event.data);

            if (response.id === "initialize") {
                setIsInitialized(true);
            }

            if (Array.isArray(response.result.items)) {
                // Clear previous completion items provider if registered
                if (completionProviderRegistered.current) {
                    monaco.languages.unregisterCompletionItemProvider("python");
                }

                // Register new completion items provider
                monaco.languages.registerCompletionItemProvider("python", {
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

                // Mark the provider as registered
                completionProviderRegistered.current = true;
            }

            // Handle hover response
            if (typeof response.result.contents === "string") {
                console.log('docs')
                console.log(response.result.contents);
                monaco.languages.registerHoverProvider("python", {
                    provideHover: () => ({
                        contents: [{value: response.result.contents}]
                    })
                });
            }
        },
        shouldReconnect: () => true,
    });

    const initializeLsp = () => {
        sendJsonMessage({
            jsonrpc: "2.0",
            id: "initialize",
            method: "initialize",
            params: {
                processId: null,
                rootUri: null,
                capabilities: {},
                clientInfo: {name: "MonacoLSPClient", version: "1.0"},
            },
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
            id: uuidv4(),
            method: "textDocument/completion",
            params: {
                textDocument: {
                    uri: "file://dummy.py",
                    version: 1,
                },
                contentChanges: [{text: value}],
            },
        });

        sendJsonMessage({
            jsonrpc: "2.0",
            id: uuidv4(),
            method: "textDocument/hover",
            params: {
                textDocument: {
                    uri: "file://dummy.py",
                    version: 1,
                },
                position: {line: 2, character: value.length},
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
                minimap: {enabled: false},
                inlayHints: {enabled: "on"},
                scrollbar: {horizontalScrollbarSize: 0},
            }}
        />
    );
};

CodeEditor.propTypes = {
    language: PropTypes.string.isRequired,
    startingCode: PropTypes.string,
    onChangeCallback: PropTypes.func,
};
