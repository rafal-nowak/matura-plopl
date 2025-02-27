import { useState, useEffect } from "react";
import { Editor } from "@monaco-editor/react";
import PropTypes from "prop-types";
import useWebSocket from "react-use-websocket";
import { v4 as uuidv4 } from "uuid";
import * as monaco from "monaco-editor";

export const CodeEditor = ({ language, startingCode, onChangeCallback }) => {
    const [isInitialized, setIsInitialized] = useState(false);
    const [editorInstance, setEditorInstance] = useState(null);
    const { sendJsonMessage, lastJsonMessage } = useWebSocket("ws://localhost:7777", {
        onOpen: () => {
            console.log("✅ Connected to PyLSP");
            initializeLsp();
        },
        onMessage: (event) => {
            const response = JSON.parse(event.data);
            console.log("📥 Received:", response);

            if (response.id === "initialize") {
                setIsInitialized(true);
            }

            // Handle completion response
            if (response.result?.items) {
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
            }

            // Handle hover response
            if (response.result?.contents) {
                monaco.languages.registerHoverProvider("python", {
                    provideHover: () => ({
                        contents: [{ value: response.result.contents }]
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
                clientInfo: { name: "MonacoLSPClient", version: "1.0" },
            },
        });

        // sendJsonMessage({
        //     jsonrpc: "2.0",
        //     id: "initialized",
        //     method: "initialized",
        //     params: {},
        // });
    };

    const handleEditorDidMount = (editor) => {
        setEditorInstance(editor);
    };

    const handleChange = (value) => {
        if (onChangeCallback) {
            onChangeCallback(value);
        }

        if (isInitialized) {
            sendJsonMessage({
                jsonrpc: "2.0",
                id: uuidv4(),
                method: "textDocument/completion",
                params: {
                    textDocument: {
                        uri: "file://dummy.py",
                        version: 1,
                    },
                    contentChanges: [{ text: value }],
                },
            });

            // sendJsonMessage({
            //     jsonrpc: "2.0",
            //     id: uuidv4(),
            //     method: "textDocument/hover",
            //     params: {
            //         textDocument: {
            //             uri: "file://dummy.py",
            //             version: 1,
            //         },
            //         position: { line: 0, character: value.length },
            //     },
            // });
        }
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
