import {withAuthentication} from "./routeAuthentication.jsx";
import {Navigate, useLocation, useNavigate} from "react-router-dom";
import {Subpage} from "./components/Subpage.jsx";
import {useEffect, useState} from "react";
import {
    Box,
    Button,
    Card,
    CardBody, CardFooter, CardHeader, Flex, Grid, Heading, HStack,
    Spinner,
    Text, useToast
} from "@chakra-ui/react";
import {motion} from 'framer-motion';
import {getTasks, Task, TaskPage} from "./services/taskService.js";
import {User} from "./services/userService.js";
import PropTypes from "prop-types";
import {LanguageIcon} from "./components/LanguageIcon.jsx";
import {PaginationLinks} from "./components/PaginationLinks.jsx";
import {LoadingCard} from "./components/LoadingCard.jsx";

const MotionCard = motion(Card);

const TaskCard = ({task}) => {
    const [loading, setLoading] = useState(true)
    const [template, setTemplate] = useState({})
    const [assigningUsername, setAssigningUsername] = useState('')

    const navigate = useNavigate()

    useEffect(() => {
        setLoading(true)

        const fetch = async () => {
            const template = await task.getTemplate()
            setTemplate(template)

            const username = await task.getAssigningUsername()
            setAssigningUsername(username)
        }

        fetch().then(() => {
            setLoading(false)
        })
    }, [task]);

    return (
        <MotionCard
            maxW={{ base: '100%', sm: '300px' }}
            margin='10px'
            borderRadius='lg'
            boxShadow='md'
            whileHover={{ scale: 1.05 }}
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
                        <Button colorScheme="teal" width='100%' onClick={() => navigate(`/solve?task=${task.id}`)}>
                            <i className="fa-solid fa-code fa-fw"/>
                            <Text marginLeft='5px'>Rozwiąż</Text>
                        </Button>
                    </CardFooter>
                </>
            )}
        </MotionCard>
    )
}

TaskCard.propTypes = {
    task: PropTypes.instanceOf(Task).isRequired,
}

const ActiveTaskList = () => {
    const location = useLocation();
    const toast = useToast();
    const [taskPage, setTaskPage] = useState(new TaskPage());
    const [loading, setLoading] = useState(true);

    let page = Number((new URLSearchParams(location.search)).get('page') || 0);

    if (page < 0)
        window.location = '/activeTasks?page=0';

    useEffect(() => {
        setLoading(true);
        getTasks(page, 15, User.fromLocalStorage().id, ["CREATED", "PROCESSING"]).then(
            taskPage => {
                setTaskPage(taskPage);
                setLoading(false);
            }
        );
    }, [page]);

    useEffect(() => {
        if (taskPage.totalElements === 0) {
            toast({
                title: 'Brak wyników',
                description: 'Nie masz żadnych nierozwiązanych zadań 😎',
                status: 'success',
                duration: 4000,
                isClosable: true
            })
        }
    }, [taskPage, toast]);

    return (
        <Subpage>
            {page >= taskPage.totalPages ? <Navigate to="/activeTasks"/> : null}

            {loading && (
                <Flex alignItems="center" justifyContent="center" height="200px">
                    <Spinner size="xl"/>
                    <Text marginLeft='10px'>Ładowanie zadań...</Text>
                </Flex>
            )}

            <Grid
                templateColumns={{ base: "1fr", md: "repeat(3, 1fr)", lg: "repeat(5, 1fr)" }}
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

export const ActiveTaskListWithAuth = withAuthentication(ActiveTaskList);
