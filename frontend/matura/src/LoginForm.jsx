import {
    Card,
    CardHeader,
    CardBody,
    Flex,
    FormControl,
    FormLabel,
    Input,
    Stack,
    Heading,
    Button,
    AlertIcon,
    Alert,
    useToast
} from "@chakra-ui/react";
import { Form, Formik, useField } from "formik";
import PropTypes from "prop-types";
import * as Yup from "yup";
import { useState } from "react";
import { login, User } from "./services/userService.js";
import { Navigate, useNavigate } from "react-router-dom";

const InputField = ({ label, type, ...props }) => {
    const [field, meta] = useField(props);
    const [showPassword, setShowPassword] = useState(false);

    return (
        <FormControl id={props.name} mb={4}>
            <FormLabel htmlFor={props.name}>{label}</FormLabel>
            <Flex flexDirection='row'>
                <Input
                    borderTopRightRadius={type === 'password' ? '0' : 'md'}
                    borderBottomRightRadius={type === 'password' ? '0' : 'md'}
                    {...field}
                    {...props}
                    type={type !== 'password' ? type : showPassword ? 'text' : 'password'}
                    autoComplete='on'
                />
                {type === 'password' && (
                    <Button
                        borderBottomLeftRadius='0'
                        borderTopLeftRadius='0'
                        onClick={() => setShowPassword(!showPassword)}
                    >
                        {showPassword ? (
                            <i className="fa-solid fa-eye-slash" />
                        ) : (
                            <i className="fa-solid fa-eye" />
                        )}
                    </Button>
                )}
            </Flex>
            {meta.touched && meta.error && (
                <Alert className='error' status='error' mt='2'>
                    <AlertIcon />
                    {meta.error}
                </Alert>
            )}
        </FormControl>
    );
};

InputField.propTypes = {
    label: PropTypes.node,
    name: PropTypes.string.isRequired,
    type: PropTypes.string,
    placeholder: PropTypes.string,
    id: PropTypes.string
};

export const LoginForm = () => {
    let navigate = useNavigate();
    const toast = useToast();

    try {
        if (User.fromLocalStorage() !== null && User.fromLocalStorage().validate())
            return <Navigate to='/dashboard' />;
    } catch (e) { /* empty */ }

    return (
        <Formik
            initialValues={{ email: '', password: '' }}
            validationSchema={
                Yup.object({
                    email: Yup.string().email("Podana wartość musi być poprawnym adresem email").required("Pole nie może być puste"),
                    password: Yup.string().required("Pole nie może być puste")
                })
            }
            validateOnMount={true}

            onSubmit={(values, { setSubmitting }) => {
                setSubmitting(true);
                login(values.email, values.password)
                    .then(() => navigate('/dashboard'))
                    .catch(() => {
                        toast({
                            title: "Błąd logowania",
                            description: "Podano niepoprawne dane logowania",
                            status: 'error',
                            duration: 8000,
                            isClosable: true,
                        });
                    })
                    .finally(() => setSubmitting(false));
            }}>

            {({ isValid, isSubmitting }) => (
                <Form>
                    <Flex justifyContent="center" alignItems="center" height="100vh" p={4}>
                        <Card variant='elevated' width={['100%', '100%', '400px']} padding='25px' boxShadow='xl' borderRadius='xl'>
                            <CardHeader textAlign='center'>
                                <Heading>Logowanie</Heading>
                            </CardHeader>

                            <CardBody>
                                <Stack spacing={4}>
                                    <InputField name={'email'} label={<><i className="fa-solid fa-envelope" /> Adres email:</>} type='email' />
                                    <InputField name={'password'} label={<><i className="fa-solid fa-lock" /> Hasło:</>} type='password' />

                                    <Button
                                        type='submit'
                                        marginY={'10px'}
                                        colorScheme='blue'
                                        disabled={!isValid || isSubmitting}
                                        isLoading={isSubmitting}
                                        loadingText={'Zaloguj się'}
                                    >
                                        Zaloguj się
                                    </Button>

                                    <Button
                                        colorScheme='green'
                                        variant='outline'
                                        onClick={() => navigate('/register')}
                                    >
                                        Nie masz konta? Zarejestruj się
                                    </Button>
                                </Stack>
                            </CardBody>
                        </Card>
                    </Flex>
                </Form>
            )}
        </Formik>
    );
};

export default LoginForm;
