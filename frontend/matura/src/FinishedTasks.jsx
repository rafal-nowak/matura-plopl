import {withAuthentication} from "./routeAuthentication.jsx";
import {Subpage} from "./components/Subpage.jsx";
import {Navigate, useLocation, useNavigate} from "react-router-dom";
import {
    Box,
    Button,
    Card,
    CardBody,
    CardFooter,
    CardHeader,
    Flex,
    Grid,
    Heading,
    HStack,
    Spinner,
    Text,
    useToast, VStack
} from "@chakra-ui/react";
import {useEffect, useState} from "react";
import {getTasks, Task, TaskPage} from "./services/taskService.js";
import {User} from "./services/userService.js";
import {PaginationLinks} from "./components/PaginationLinks.jsx";
import {LanguageIcon} from "./components/LanguageIcon.jsx";
import PropTypes from "prop-types";
import {LoadingCard} from "./components/LoadingCard.jsx";
import {motion} from 'framer-motion';

const MotionCard = motion(Card);

const TaskCard = ({task}) => {
    const [loading, setLoading] = useState(true);
    const [template, setTemplate] = useState({});
    const [assigningUsername, setAssigningUsername] = useState('');

    const navigate = useNavigate();
    const toast = useToast();

    useEffect(() => {
        setLoading(true);

        const fetch = async () => {
            const template = await task.getTemplate();
            setTemplate(template);

            const username = await task.getAssigningUsername();
            setAssigningUsername(username);
        };

        fetch().then(() => {
            setLoading(false);
        });
    }, [task]);

    return (
        <MotionCard
            maxW={{base: '100%', sm: '300px'}}
            margin='10px'
            borderRadius='lg'
            boxShadow='md'
            whileHover={{scale: 1.05}}
            transition="all 0.3s ease"
        >
            {loading && <LoadingCard/>}

            {!loading && (
                <>
                    <CardHeader>
                        <Heading size='md' textAlign='center'>{template.source}</Heading>
                    </CardHeader>

                    <CardBody>
                        <HStack spacing='10px'>
                            <LanguageIcon language={template.language} boxSize='80px'/>

                            <Box>
                                <Text>Przypisał: <strong>{assigningUsername}</strong></Text>
                                <Text>Przypisano {task.createdAt.toLocaleDateString().replaceAll('/', '.')}</Text>
                            </Box>
                        </HStack>
                    </CardBody>

                    <CardFooter justifyContent='center'>
                        <VStack>
                            <Button colorScheme="teal" width='100%' onClick={() => navigate(`/solve?task=${task.id}`)}>
                                <i className="fa-solid fa-rotate-left fa-fw"/>
                                <Text marginLeft='5px'>Rozwiąż ponownie</Text>
                            </Button>
                            {/*<Button colorScheme="teal" width='100%' onClick={() => navigate(`/solve?task=${task.id}`)}>*/}
                            {/*    <i className="fa-solid fa-code fa-fw"/>*/}
                            {/*    <Text marginLeft='5px'>Wyświetl kod</Text>*/}
                            {/*</Button>*/}
                        </VStack>
                    </CardFooter>
                </>
            )}
        </MotionCard>
    );
};

TaskCard.propTypes = {
    task: PropTypes.instanceOf(Task).isRequired,
};

const FinishedTasks = () => {
    const location = useLocation();
    const toast = useToast();
    const [taskPage, setTaskPage] = useState(new TaskPage());
    const [loading, setLoading] = useState(true);

    let page = Number((new URLSearchParams(location.search)).get('page') || 0);

    if (page < 0) window.location = '/finishedTasks?page=0';

    useEffect(() => {
        setLoading(true);
        getTasks(page, 10, User.fromLocalStorage().id, ["FINISHED"]).then(taskPage => {
            setTaskPage(taskPage);
            setLoading(false);
        });
    }, [page]);

    useEffect(() => {
        if (taskPage.totalElements === 0) {
            toast({
                title: 'Brak wyników',
                description: 'Nie masz żadnych rozwiązanych zadań. Czas się wziąć do roboty!',
                status: 'info',
                duration: 4000,
                isClosable: true
            });
        }
    }, [taskPage, toast]);

    return (
        <Subpage>
            {page >= taskPage.totalPages ? <Navigate to="/finishedTasks"/> : null}

            {loading && (
                <Flex alignItems="center" justifyContent="center" height="200px">
                    <Spinner size="xl"/>
                    <Text marginLeft='10px'>Ładowanie zadań...</Text>
                </Flex>
            )}

            <Grid
                templateColumns={{base: "1fr", md: "repeat(3, 1fr)", lg: "repeat(5, 1fr)"}}
                gap='15px'
            >
                {!loading && (
                    taskPage.tasks.map((task) => (
                        <TaskCard task={task} key={task.id}/>
                    ))
                )}
            </Grid>

            <Flex justifyContent='center' marginTop='15px'>
                <PaginationLinks totalPages={taskPage.totalPages} currentPage={taskPage.currentPage - 1}/>
            </Flex>
        </Subpage>
    );
};

export const FinishedTasksWithAuth = withAuthentication(FinishedTasks);
