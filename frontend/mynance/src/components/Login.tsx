import React, {FormEvent, useEffect, useState} from 'react';
import {Button, Card, Form} from 'react-bootstrap';
import {useNavigate, useSearchParams} from 'react-router-dom';
import {useLazyQuery, useMutation} from '@apollo/client'; // Angenommen, Sie verwenden Apollo Client 3.x
import 'bootstrap/dist/css/bootstrap.min.css';
import {LOGIN_USER, REQUEST_PASSWORD_RESET} from "../graphql/GraphQLQueries";
import {REGISTER_USER} from "../graphql/GraphQLMutations";
import {useCookies} from "react-cookie";

const Login = () => {
    const [searchParams] = useSearchParams();
    const [email, setEmail] = useState('');
    const [firstname, setFirstname] = useState('');
    const [surname, setSurname] = useState('');
    const [password, setPassword] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('');
    const [formType, setFormType] = useState('login'); // 'login', 'register', 'forgotPassword'
    const navigate = useNavigate();
    const [cookies] = useCookies(['access']);

    useEffect(() => {
        const defaultRegister = searchParams.get('register');
        if (defaultRegister && formType !== 'register') {
            setFormType('register');

            // Kopiere alle aktuellen Suchparameter außer 'register'
            const newSearchParams = new URLSearchParams(searchParams);
            newSearchParams.delete('register');

            // Aktualisiere die URL ohne 'register', ohne die Seite neu zu laden
            navigate(`${window.location.pathname}?${newSearchParams.toString()}`, {replace: true});
        }
    }, [searchParams, formType, navigate]);


    const [executeLogin, {loading, error}] = useLazyQuery(LOGIN_USER, {
        variables: {email, password},
        onCompleted: (data) => {
            if (data.loginUser.success) {

                console.log(cookies);
                navigate('/dashboard');
            } else if (data.loginUser.statusCode === '12') {
                alert("You aren't verified yet");
            } else if (data.loginUser.statusCode === '5') {
                alert("Wrong email or password");
            } else {
                alert("An error occurred");
            }
        }
    });
    const [executeRegister] = useMutation(REGISTER_USER, {
        variables: {email, firstname, surname, password},
        onCompleted: (data) => {
            if (data.registerUser.success) {
                alert('Registration successful');
                setFormType('login');
            } else if (data.registerUser.statusCode === '6') {
                alert('Email already exists');
            } else {
                alert('An error occurred');
            }
        }
    })

    const [executeRequestPasswordReset] = useLazyQuery(REQUEST_PASSWORD_RESET, {
        variables: {email},
        onCompleted: (data) => {
            console.log(data);
            if (data.requestPasswordReset.success) {
                alert('Password reset email sent');
            } else if (data.requestPasswordReset.statusCode === '11') {
                alert('Email not found');
            } else if (data.requestPasswordReset.statusCode === '12') {
                alert('You cant reset your password if you are not verified yet');
            } else if (data.requestPasswordReset.statusCode === '17') {
                alert('You already requested a password reset email. Please check your inbox or wait a few minutes and try again.');
            } else {
                alert('An error occurred');
            }
        }
    });

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (formType === 'login') {
            await executeLogin();
        } else if (formType === 'register') {
            if (password !== repeatPassword) {
                alert('Passwords do not match');
                return;
            }
            await executeRegister();
        } else if (formType === 'forgotPassword') {
            // Logik für das Formular "Passwort vergessen"
            await executeRequestPasswordReset();
            // Fügen Sie hier Ihre Logik zum Zurücksetzen des Passworts hinzu
        }
    };

    function setTitle() {
        if (formType === 'login') {
            return 'Login';
        } else if (formType === 'register') {
            return 'Register';
        } else if (formType === 'forgotPassword') {
            return 'Forgot Password';
        }
    }

    return (
        <div className="d-flex justify-content-center align-items-center"
             style={{minHeight: "100vh"}}>
            <Card style={{width: '18rem'}}>
                <Card.Body>
                    <Card.Title className="text-center">{setTitle()}</Card.Title>
                    <Form onSubmit={handleSubmit}>
                        {formType === 'register' && (
                            <>
                                <div className="mb-3">
                                    <Form.Control
                                        type="text"
                                        placeholder="First name"
                                        value={firstname}
                                        onChange={(e) => setFirstname(e.target.value)}
                                    />
                                </div>
                                <div className="mb-3">
                                    <Form.Control
                                        type="text"
                                        placeholder="Surname"
                                        value={surname}
                                        onChange={(e) => setSurname(e.target.value)}
                                    />
                                </div>
                            </>
                        )}
                        <div className="mb-3">
                            <Form.Control
                                type="email"
                                placeholder="Enter email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                        {formType !== 'forgotPassword' && (
                            <>
                                <div className="mb-3">
                                    <Form.Control
                                        type="password"
                                        placeholder="Password"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                    />
                                </div>
                                {formType === 'register' && (
                                    <div className="mb-3">
                                        <Form.Control
                                            type="password"
                                            placeholder="Repeat password"
                                            value={repeatPassword}
                                            onChange={(e) => setRepeatPassword(e.target.value)}
                                        />
                                    </div>
                                )}
                            </>
                        )}

                        <div className="d-grid gap-2 mb-3">
                            <Button variant="primary" type="submit" disabled={loading}>
                                {formType === 'login' ? 'Sign in' : 'Submit'}
                            </Button>
                        </div>
                    </Form>
                    {formType === 'login' && (
                        <div className="mb-3 d-flex justify-content-between">
                            <Button variant="link" onClick={() => setFormType('forgotPassword')}>
                                Forgot Password?
                            </Button>
                            <Button variant="link" onClick={() => setFormType('register')}>
                                Register
                            </Button>
                        </div>
                    )}
                    {formType !== 'login' && (
                        <Button variant="link" onClick={() => setFormType('login')}>
                            Back to login
                        </Button>
                    )}
                    {error && <p>Error!</p>}
                </Card.Body>
            </Card>
        </div>
    );
};

export default Login;
