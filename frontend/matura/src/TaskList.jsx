import {withAuthentication} from "./routeAuthentication.jsx";
import {Navigate, useLocation, useNavigate} from "react-router-dom";
import {Subpage} from "./components/Subpage.jsx";
import {getAvailableLanguages, getTemplates, Template, TemplatePage} from "./services/templateService.js";
import {useEffect, useState} from "react";
import {
    Box,
    Button,
    Card,
    CardHeader,
    Flex,
    Grid,
    Input,
    Modal,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalHeader,
    ModalOverlay,
    Select,
    Text,
    useToast
} from "@chakra-ui/react";
import PropTypes from "prop-types";
import {motion} from 'framer-motion'
import {Formik, Form} from "formik";
import {PaginationLinks} from "./components/PaginationLinks.jsx";
import {RenderMarkdown} from "./components/RenderMarkdown.jsx";
import {Task} from "./services/taskService.js";
import {LanguageIcon} from "./components/LanguageIcon.jsx";
import {LoadingCard} from "./components/LoadingCard.jsx";

const MotionBox = motion(Box);

const TemplateCard = ({ template, ...props }) => {
    const toast = useToast();
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false);

    return (
        <MotionBox
            whileHover={{ scale: 1.02 }}
            transition={{ duration: 0.2 }}
        >
            <Card {...props} marginY='4px' borderRadius='lg' shadow='md'>
                <CardHeader>
                    <Flex justifyContent='space-between' alignItems='center'>
                        <Flex alignItems='center'>
                            <LanguageIcon language={template.language} boxSize='50px'/>
                            <Text marginX='5px'>{template.source}</Text>
                        </Flex>

                        <Flex flexDirection='column'>
                            <Button
                                marginY='5px'
                                colorScheme="teal"
                                onClick={() => {
                                    const id = Task.createOrGet(template.id);

                                    toast.promise(id, {
                                        success: {
                                            title: 'Ładowanie zakończone',
                                            description: 'Wkrótce nastąpi przekierowanie',
                                        },
                                        error: {
                                            title: 'Wystąpił błąd',
                                            description: 'Zadanie nie mogło zostać przypisane',
                                        },
                                        loading: {
                                            title: 'Ładowanie...',
                                        },
                                    });

                                    id.then(value => {
                                        navigate(`/solve?task=${value}`);
                                    });
                                }}
                            >
                                <i className="fa-solid fa-code fa-fw"/>
                                <Text marginLeft='5px'>Rozwiąż</Text>
                            </Button>
                            <Button
                                colorScheme="orange"
                                onClick={() => setIsModalOpen(true)}
                                marginY='5px'
                            >
                                <i className="fa-solid fa-file-lines"/>
                                <Text marginLeft='5px'>Polecenie</Text>
                            </Button>
                        </Flex>
                    </Flex>
                </CardHeader>

                <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
                    <ModalOverlay />
                    <ModalContent
                        maxWidth={["90%", "80%", "70%"]}
                        w="auto"
                        mx="auto"
                        p={4}
                        my={["15px", "auto"]}
                    >
                        <ModalHeader textAlign="center">Polecenie</ModalHeader>
                        <ModalCloseButton />
                        <ModalBody
                            overflowY="auto"
                            p={3}
                        >
                            <RenderMarkdown document={template.statement} />
                        </ModalBody>
                    </ModalContent>
                </Modal>

            </Card>
        </MotionBox>
    );
};

TemplateCard.propTypes = {
    template: PropTypes.instanceOf(Template)
};

const languagesText = {
    'C_SHARP': 'C#',
    'PYTHON': 'Python',
    'JAVA': 'Java',
    'CPP': 'C++'
};

const TaskList = () => {
    const location = useLocation();
    const toast = useToast();
    const [templatePage, setTemplatePage] = useState(new TemplatePage());
    const [languages, setLanguages] = useState([]);
    const [loading, setLoading] = useState(true);

    const [templateLanguage, setTemplateLanguage] = useState('')
    const [templateSource, setTemplateSource] = useState('')

    let page = Number((new URLSearchParams(location.search)).get('page') || 0);

    if (page < 0)
        window.location = '/tasks?page=0';

    useEffect(() => {
        setLoading(true);
        getTemplates(page, 8, templateLanguage, templateSource).then(
            templatePage => {
                setTemplatePage(templatePage);
                setLoading(false);
            }
        );
    }, [page, templateLanguage, templateSource]);

    useEffect(() => {
        if (templatePage.totalElements === 0) {
            toast({
                title: 'Brak wyników',
                description: 'Nie znaleziono zadań spełniających określone kryteria',
                status: 'error',
                duration: 4000,
                isClosable: false
            })
        }
    }, [templatePage, toast]);

    useEffect(() => {
        getAvailableLanguages().then(languages => {
            setLanguages(languages)
        })
    }, [])

    return (
        <Subpage>
            {page >= templatePage.totalPages ? <Navigate to="/tasks"/> : null}

            {loading && <LoadingCard/>}

            {!loading && (
                <>
                    <Box marginBottom='15px'>
                        <Formik initialValues={{language: templateLanguage, source: templateSource}} onSubmit={(values, {setSubmitting}) => {
                            setSubmitting(true);
                            setTemplateLanguage(values.language);
                            setTemplateSource(values.source);
                            setSubmitting(false);
                        }}>
                            {({values, isSubmitting, handleChange, handleBlur}) => (
                                <Form>
                                    <Flex flexDirection={{ base: 'column', md: 'row' }} gap='10px'>
                                        <Select
                                            placeholder='Wybierz język'
                                            name='language'
                                            value={values.language}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                        >
                                            {languages.map(language => (
                                                <option key={language} value={language}>
                                                    {languagesText[language]}
                                                </option>
                                            ))}
                                        </Select>

                                        <Input
                                            type='text'
                                            placeholder='Zadanie'
                                            name='source'
                                            value={values.source}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                        />

                                        <Button
                                            type='submit'
                                            isLoading={isSubmitting}
                                            colorScheme="blue"
                                        >
                                            <i className="fa-solid fa-magnifying-glass fa-fw"/>
                                        </Button>
                                    </Flex>
                                </Form>
                            )}
                        </Formik>
                    </Box>

                    <Grid templateColumns={{ base: "1fr", md: "repeat(2, 1fr)" }} gap='15px'>
                        {templatePage.templates.map((template) => (
                            <TemplateCard template={template} id={template.id} key={template.id}/>
                        ))}
                    </Grid>

                    <Flex justifyContent='center' marginTop='15px'>
                        <PaginationLinks totalPages={templatePage.totalPages} currentPage={page}/>
                    </Flex>
                </>
            )}
        </Subpage>
    )
};

export const TaskListWithAuth = withAuthentication(TaskList)
