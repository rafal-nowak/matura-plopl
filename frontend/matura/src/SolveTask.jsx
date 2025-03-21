import {withAuthentication} from "./routeAuthentication.jsx";
import {useNavigate, useSearchParams} from "react-router-dom";
import {Subpage} from "./components/Subpage.jsx";
import {useEffect, useState} from "react";
import {
    Accordion, AccordionButton, AccordionIcon, AccordionItem, AccordionPanel, Alert, AlertIcon, Box,
    Button, Code,
    Flex, Menu,
    MenuButton,
    MenuDivider,
    MenuGroup,
    MenuItem,
    MenuList, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay,
    Stack,
    Text, useBreakpointValue, useDisclosure,
    useToast,
    VStack
} from "@chakra-ui/react";
import {Task} from "./services/taskService.js";
import {CodeEditor} from "./components/CodeEditor.jsx";
import {RenderMarkdown} from "./components/RenderMarkdown.jsx";
import {SubtaskResult} from "./services/subtaskResultService.js";
import * as PropTypes from "prop-types";
import {LoadingCard} from "./components/LoadingCard.jsx";
import {TestResult} from "./services/testResultService.js";

const CheckSubtaskMenuItem = ({subtaskNumber, onFastCheck, onFullCheck, isSubmitting}) => (
    <div>
        {subtaskNumber !== 1 && <MenuDivider/>}
        <MenuGroup title={`Podzadanie ${subtaskNumber}`}>
            <MenuItem onClick={onFastCheck} isDisabled={isSubmitting}>
                <i className="fa-fw fa-solid fa-forward"/>
                <Text marginLeft="5px">Sprawdzenie szybkie</Text>
            </MenuItem>

            <MenuItem onClick={onFullCheck} isDisabled={isSubmitting}>
                <i className="fa-fw fa-solid fa-check"/>
                <Text marginLeft="5px">Sprawdzenie pełne</Text>
            </MenuItem>
        </MenuGroup>
    </div>
)
CheckSubtaskMenuItem.propTypes = {
    subtaskNumber: PropTypes.number,
    onFastCheck: PropTypes.func,
    onFullCheck: PropTypes.func,
    isSubmitting: PropTypes.bool
}

const TestResultAccordionItem = ({testResult, index}) => {
    const verdictMapping = {
        'ACCEPTED': 'Zaliczony',
        'WRONG_ANSWER': 'Zła odpowiedź',
        'TIME_LIMIT_EXCEEDED': 'Przekroczono limit czasu',
        'MEMORY_LIMIT_EXCEEDED': 'Przekroczono limit pamięci',
        'RUNTIME_ERROR': 'Błąd wykonania',
        'COMPILATION_ERROR': 'Błąd kompilacji',
        'SYSTEM_ERROR': 'Błąd systemowy',
        'RULES_VIOLATION': 'Naruszenie zasad'
    }
    const statsVerdicts = ['ACCEPTED', 'WRONG_ANSWER', 'TIME_LIMIT_EXCEEDED', 'MEMORY_LIMIT_EXCEEDED']

    return <AccordionItem>
        <h2>
            <AccordionButton>
                <Box as="span" flex="1" textAlign="left" color={testResult.verdict === "ACCEPTED" ? "green" : "red"}>
                    Test {index}
                </Box>
                <AccordionIcon/>
            </AccordionButton>
        </h2>
        <AccordionPanel>
            <Box mb="10pt">
                <strong
                    color={testResult.verdict === "ACCEPTED" ? "green" : "red"}>{verdictMapping[testResult.verdict]}</strong>
            </Box>

            {statsVerdicts.includes(testResult.verdict) && (
                <Box mb="10pt">
                    <Text>Czas wykonania programu
                        wyniósł <Code>{Number(testResult.time / 1000).toFixed(2)} s.</Code></Text>
                    <Text>Program
                        wykorzystał <Code>{Number(testResult.memory / 1024).toFixed(2)} MB</Code> pamięci.</Text>
                </Box>
            )}

            {testResult.message !== null && (
                <>
                    Konsola:
                    <Box
                        bg="gray.900"
                        color="green.400"
                        p={4}
                        borderRadius="md"
                        fontFamily="monospace"
                        whiteSpace="pre-wrap"
                        boxShadow="lg"
                        border="1px solid"
                        borderColor="green.500"
                        overflow="auto"
                        maxH="400px"
                    >
                        <Text>{testResult.message}</Text>
                    </Box>
                </>
            )}
        </AccordionPanel>
    </AccordionItem>;
}
TestResultAccordionItem.propTypes = {
    testResult: PropTypes.any,
    index: PropTypes.number,
};

const SubtaskResultBody = ({result, testResults}) => {
    return <VStack align='start'>
        <Text as='b' mb='5px'>Zaliczono {result.score}% testów</Text>
        <Accordion width='100%' allowToggle>
            {testResults.map(
                (testResult, index) => <TestResultAccordionItem key={index} testResult={testResult} index={index + 1}/>
            )}
        </Accordion>
    </VStack>
}
SubtaskResultBody.propTypes = {
    result: PropTypes.instanceOf(SubtaskResult),
    testResults: PropTypes.arrayOf(TestResult)
}

const TaskResultBody = ({results, setIsCompleted}) => {
    useEffect(() => {
        const completionPercentage = Math.floor(
            results.reduce((sum, result) => sum + result[0].score, 0) / results.length
        );
        setIsCompleted(completionPercentage === 100);
    }, [results, setIsCompleted]);

    return (
        <VStack align='start'>
            <Text as='b' mb='5px'>Zaliczono {Math.floor(
                results.reduce((sum, result) => sum + result[0].score, 0) / results.length
            )}% testów.</Text>

            <Text>Wyniki:</Text>
            <Accordion allowToggle w='100%'>
                {results.map((result, idx) => (
                    <AccordionItem key={idx}>
                        <h2>
                            <AccordionButton>
                                <Box as='span' flex="1" textAlign="left"
                                     color={result[0].score === 100 ? 'green' : 'red'}>
                                    Zadanie {idx + 1}: {result[0].score}%
                                </Box>
                                <AccordionIcon/>
                            </AccordionButton>
                        </h2>
                        <AccordionPanel>
                            <Accordion width='100%' allowToggle>
                                {result[1].map((testResult, index) => (
                                    <TestResultAccordionItem testResult={testResult} index={index + 1} key={index}/>
                                ))}
                            </Accordion>
                        </AccordionPanel>
                    </AccordionItem>
                ))}
            </Accordion>
        </VStack>
    );
};
TaskResultBody.propTypes = {
    results: PropTypes.arrayOf(PropTypes.shape({
        subtaskResult: PropTypes.instanceOf(SubtaskResult),
        testResults: PropTypes.arrayOf(TestResult)
    })),
    setIsCompleted: PropTypes.func
}

const SolveTask = () => {
    const [searchParams] = useSearchParams()
    const taskId = Number(searchParams.get('task'))
    const navigate = useNavigate()
    const toast = useToast()

    const [loading, setLoading] = useState(true)
    const [task, setTask] = useState(null)
    const [template, setTemplate] = useState(null)
    const [fileContents, setFileContents] = useState('')
    const {isOpen: modalIsOpen, onOpen: modalOpen, onClose: modalClose} = useDisclosure()

    const [testName, setTestName] = useState('')
    const [testResultsBody, setTestResultsBody] = useState(<></>)

    const [isSubmitting, setIsSubmitting] = useState(false)

    const isMobile = useBreakpointValue({base: true, md: false});

    const editorLanguageMapping = {
        'PYTHON': 'python',
        'C_SHARP': 'csharp',
        'CPP': 'cpp',
        'JAVA': 'java'
    }

    const [isCompleted, setIsCompleted] = useState(false);
    const handleModalClose = () => {
        modalClose();
        if (isCompleted) {
            navigate('/tasklist');
        }
    };

    let verificationTypes = []

    useEffect(() => {
        setLoading(true);

        const fetchData = async () => {
            const task = await Task.findById(taskId);
            setTask(task);

            const template = await task.getTemplate();
            setTemplate(template);

            const file = await task.getFile()
            setFileContents(file)
        }

        fetchData().finally(() => setLoading(false));
    }, [taskId]);

    if (!loading && task === null)
        navigate(-1)

    if (!loading) {
        for (let i = 1; i <= template.numberOfSubtasks; i++)
            verificationTypes.push(
                <CheckSubtaskMenuItem key={i} isSubmitting={isSubmitting} subtaskNumber={i} onFastCheck={() => {
                    const promise = task.checkSubtask(fileContents, i, 'fast')
                    setIsSubmitting(true)

                    toast.promise(promise, {
                        success: {
                            title: 'Sprawdzanie zakończone',
                            description: 'Sprawdzanie dobiegło już końca! Wkrótce zobaczysz wynik.'
                        },
                        loading: {
                            title: 'Sprawdzanie w trakcie',
                            description: `Szybkie sprawdzanie podzadania ${i} w toku.`
                        },
                        error: {
                            title: 'Wystąpił błąd',
                            description: 'Coś poszło nie tak przy sprawdzaniu. Spróbuj ponownie później!'
                        }
                    })

                    promise
                        .then(result => {
                            setTestName(`Szybkie sprawdzenie podzadania ${i}`)
                            setTestResultsBody(<SubtaskResultBody result={result[0]} testResults={result[1]}/>)
                            modalOpen()
                        })
                        .finally(() => setIsSubmitting(false))
                }} onFullCheck={() => {
                    const promise = task.checkSubtask(fileContents, i, 'full')
                    setIsSubmitting(true)

                    toast.promise(promise, {
                        success: {
                            title: 'Sprawdzanie zakończone',
                            description: 'Sprawdzanie dobiegło już końca! Wkrótce zobaczysz wynik.'
                        },
                        loading: {
                            title: 'Sprawdzanie w trakcie',
                            description: `Pełne sprawdzanie podzadania ${i} w toku.`
                        },
                        error: {
                            title: 'Wystąpił błąd',
                            description: 'Coś poszło nie tak przy sprawdzaniu. Spróbuj ponownie później!'
                        }
                    })

                    promise.then(result => {
                        setTestName(`Pełne sprawdzenie podzadania ${i}`)
                        setTestResultsBody(<SubtaskResultBody result={result[0]} testResults={result[1]}/>)
                        modalOpen()
                        setIsSubmitting(false)
                    })
                }}/>
            )
    }

    return (
        <Subpage>
            {isMobile && (
                <>
                    <Alert status="info" mb={6} borderRadius="md" boxShadow="lg" p={4}>
                        <AlertIcon boxSize="40px" mr={4}/>
                        <VStack align="flex-start">
                            <Text fontSize="lg" fontWeight="bold" color="teal.600">
                                Używanie edytora kodu na telefonie jest trudne!
                            </Text>
                            <Text fontSize="md" textAlign="left">
                                Ten edytor kodu jest zoptymalizowany do używania na komputerze. Korzystanie z niego na
                                telefonie może być bardzo niewygodne i trudne.
                                <br/>
                                Prosimy o dostęp do tej strony z urządzenia z większym ekranem, aby zapewnić najlepsze
                                doświadczenia z korzystania ze strony.
                            </Text>
                        </VStack>
                    </Alert>

                    <Button onClick={() => navigate(-1)} colorScheme="teal" size="lg"
                            leftIcon={<i className="fa-solid fa-arrow-left"></i>}>
                        Wróć na poprzednią stronę
                    </Button>
                </>
            )}

            {!isMobile && loading && <LoadingCard/>}

            {!isMobile && !loading && (
                <>
                    <Modal isOpen={modalIsOpen} onClose={modalClose}>
                        <ModalOverlay/>
                        <ModalContent>
                            <ModalHeader>{testName}</ModalHeader>
                            <ModalCloseButton/>
                            <ModalBody>
                                {testResultsBody}
                            </ModalBody>

                            <ModalFooter>
                                {isCompleted ? (
                                    <Button colorScheme="green" onClick={() => navigate('/tasks')}>
                                        Zadanie zakończone! Przejdź do listy zadań
                                    </Button>
                                ) : (
                                    <Button onClick={handleModalClose}>Zamknij</Button>
                                )}
                            </ModalFooter>
                        </ModalContent>
                    </Modal>

                    <Stack direction='row' height='80vh' maxWidth='98dvw'>
                        <CodeEditor language={editorLanguageMapping[template.language]} startingCode={fileContents}
                                    onChangeCallback={setFileContents}/>

                        <VStack width='60dvw'>
                            <Flex as='nav' direction='row' justifyContent='space-around' width='90%'
                                  marginBottom='10px'>
                                <Menu>
                                    <MenuButton as={Button}>
                                        Sprawdź podzadanie <i className="fa-solid fa-fw fa-chevron-down"/>
                                    </MenuButton>

                                    <MenuList paddingTop='0'>
                                        {verificationTypes}
                                    </MenuList>
                                </Menu>

                                <Button isDisabled={isSubmitting} onClick={() => {
                                    toast.promise(task.saveFile(fileContents), {
                                        success: {
                                            title: 'Zapisano',
                                            description: 'Zmiany zostały zapisane pomyślnie',
                                            duration: 4000,
                                            isClosable: true
                                        },
                                        loading: {
                                            title: 'Zapisywanie'
                                        },
                                        error: {
                                            title: 'Wystąpił błąd',
                                            description: 'Zapisywanie nie powiodło się',
                                            duration: 3000,
                                            isClosable: true
                                        }
                                    })
                                }}>
                                    <i className="fa-solid fa-fw fa-cloud-arrow-up"/>
                                    <Text marginLeft='5px'>Zapisz</Text>
                                </Button>

                                <Button isDisabled={isSubmitting} onClick={() => {
                                    const promise = task.check(fileContents)
                                    setIsSubmitting(true)

                                    toast.promise(promise, {
                                        success: {
                                            title: 'Sprawdzanie zakończone',
                                            description: 'Sprawdzanie dobiegło już końca! Wkrótce zobaczysz wynik.'
                                        },
                                        loading: {
                                            title: 'Sprawdzanie w trakcie',
                                            description: `Pełne sprawdzanie zadania w toku.`
                                        },
                                        error: {
                                            title: 'Wystąpił błąd',
                                            description: 'Coś poszło nie tak przy sprawdzaniu. Spróbuj ponownie później!'
                                        }
                                    })

                                    promise
                                        .then(results => {
                                            setTestName('Sprawdzenie pełne')
                                            setTestResultsBody(<TaskResultBody results={results}
                                                                               setIsCompleted={setIsCompleted}/>)
                                            modalOpen()
                                        })
                                        .finally(() => setIsSubmitting(false))
                                }}>
                                    <i className="fa-fw fa-solid fa-check"/>
                                    <Text marginLeft='5px'>Sprawdź</Text>
                                </Button>
                            </Flex>

                            <RenderMarkdown document={template.statement}/>
                        </VStack>
                    </Stack>
                </>
            )}
        </Subpage>
    )
}

const SolveTaskWithAuth = withAuthentication(SolveTask)

export default SolveTaskWithAuth;
