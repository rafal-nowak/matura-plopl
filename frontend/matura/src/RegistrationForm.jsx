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
import { register, User } from "./services/userService.js";
import { Navigate, useNavigate } from "react-router-dom";

const InputField = ({ label, type, ...props }) => {
    const [field, meta] = useField(props);
    const [showPassword, setShowPassword] = useState(false);

    return (
        <FormControl id={props.name} mb={4}>
            <FormLabel htmlFor={props.name}>{label}</FormLabel>
            <Flex flexDirection='row'>
                <Input
                    id={props.id || props.name}
                    name={props.name}
                    value={field.value || ""}
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

export const RegistrationForm = () => {
    let navigate = useNavigate();
    const toast = useToast();

    try {
        if (User.fromLocalStorage() !== null && User.fromLocalStorage().validate())
            return <Navigate to='/dashboard' />;
    } catch (e) { /* empty */ }

    return (
        <Formik
            initialValues={{ username: '', email: '', password: '', passwordAgain: '' }}
            validationSchema={
                Yup.object({
                    username: Yup.string().required("Pole nie może być puste"),
                    email: Yup.string().email("Podana wartość musi być poprawnym adresem email").required("Pole nie może być puste"),
                    password: Yup.string().required("Pole nie może być puste"),
                    passwordAgain: Yup.string().oneOf([Yup.ref('password'), null], "Hasła muszą być identyczne").required('Pole nie może być puste'),
                })
            }
            validateOnMount={true}

            onSubmit={(values, { setSubmitting }) => {
                setSubmitting(true);
                register(values.username, values.email, values.password)
                    .then(() => {
                        toast({
                            title: 'Zarejestrowano pomyślnie',
                            description: 'Wkrótce zostaniesz przeniesiony do zbioru zadań.',
                            status: 'success',
                            duration: 4000,
                            isClosable: true,
                        });
                        navigate('/dashboard');
                    })
                    .catch((error) => {
                        console.log(error);
                        toast({
                            title: 'Wystąpił błąd',
                            description: 'Rejestracja nie powiodła się.',
                            status: 'error',
                            duration: 3000,
                            isClosable: true,
                        });
                    })
                    .finally(() => setSubmitting(false));
            }}>

            {({ isValid, isSubmitting }) => (
                <Form>
                    <Flex justifyContent="center" alignItems="center" height="100vh" p={4} my={['20px', 0]}>
                        <Card variant='elevated' width={['90%', '70%', '400px']} maxWidth='400px' padding='25px' boxShadow='xl' borderRadius='xl'>
                            <CardHeader textAlign='center'>
                                <Heading>Rejestracja</Heading>
                            </CardHeader>

                            <CardBody>
                                <Stack spacing={4}>
                                    <InputField name='username' label={<><i className="fa-solid fa-user" /> Nazwa użytkownika:</>} type='text' />
                                    <InputField name='email' label={<><i className="fa-solid fa-envelope" /> Adres email:</>} type='email' />
                                    <InputField name='password' label={<><i className="fa-solid fa-lock" /> Hasło:</>} type='password' />
                                    <InputField name='passwordAgain' label={<><i className="fa-solid fa-lock" /> Powtórz hasło:</>} type='password' />
                                </Stack>

                                <Button
                                    type='submit'
                                    marginY='10px'
                                    colorScheme='blue'
                                    disabled={!isValid || isSubmitting}
                                    isLoading={isSubmitting}
                                    loadingText='Zarejestruj się'
                                    width='100%'
                                >
                                    Zarejestruj się
                                </Button>

                                <Button
                                    colorScheme='green'
                                    variant='outline'
                                    onClick={() => navigate('/login')}
                                    width='100%'
                                >
                                    Masz już konto? Zaloguj się
                                </Button>
                            </CardBody>
                        </Card>
                    </Flex>
                </Form>
            )}
        </Formik>
    );
};

export default RegistrationForm;
