import React from 'react'
import ReactDOM from 'react-dom/client'

import {createBrowserRouter, RouterProvider} from "react-router-dom";
import LoginForm from "./LoginForm.jsx";
import {ChakraProvider} from "@chakra-ui/react";
import {DashboardWithAuth} from "./Dashboard.jsx";
import theme from './theme'
import {TaskListWithAuth} from "./TaskList.jsx";
import {ActiveTaskListWithAuth} from "./ActiveTasks.jsx";
import SolveTaskWithAuth from "./SolveTask.jsx";
import {FinishedTasksWithAuth} from "./FinishedTasks.jsx";
import {ErrorElement} from "./ErrorElement.jsx";
import {RegistrationForm} from './RegistrationForm.jsx';
import {Home} from "./Home.jsx";

const router = createBrowserRouter([
    {
        path: '/',
        element: <Home/>,
        errorElement: <ErrorElement/>
    },
    {
        path: '/login',
        element: <LoginForm/>,
        errorElement: <ErrorElement/>
    },
    {
        path: '/dashboard',
        element: <DashboardWithAuth/>,
        errorElement: <ErrorElement/>
    },
    {
        path: '/tasks',
        element: <TaskListWithAuth/>,
        errorElement: <ErrorElement/>
    },
    {
        path: '/activeTasks',
        element: <ActiveTaskListWithAuth/>,
        errorElement: <ErrorElement/>
    },
    {
        path: '/finishedTasks',
        element: <FinishedTasksWithAuth/>,
        errorElement: <ErrorElement/>
    },
    {
        path: '/solve',
        element: <SolveTaskWithAuth/>,
        errorElement: <ErrorElement/>
    },
    {
        path: '/register',
        element: <RegistrationForm/>,
        errorElement: <ErrorElement/>
    }
])

ReactDOM
    .createRoot(document.getElementById('root'))
    .render(
            <React.StrictMode>
                <ChakraProvider theme={theme}>
                    <RouterProvider router={router}/>
                </ChakraProvider>
            </React.StrictMode>
    )
