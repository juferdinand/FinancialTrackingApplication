import React, { Component, FormEvent } from 'react';
import { Button, Card, Form } from 'react-bootstrap';
import { ApolloConsumer } from '@apollo/client'; // Import ApolloConsumer
import 'bootstrap/dist/css/bootstrap.min.css';
import { LOGIN_USER } from "../graphql/GraphQLQueries";

interface LoginState {
    email: string;
    password: string;
}

class Login extends Component<{}, LoginState> {
    constructor(props: {}) {
        super(props);
        this.state = {
            email: '',
            password: '',
        };
    }

    handleSubmit = async (event: FormEvent<HTMLFormElement>, client: any) => {
        event.preventDefault();
        try {
            const { data } = await client.query({
                query: LOGIN_USER,
                variables: {
                    email: this.state.email,
                    password: this.state.password
                }
            });
            // Handle the response data as needed
            console.log('Data:', data);
        } catch (error) {
            // Handle any errors
            console.error('Error logging in:', error);
        }
    };

    render() {
        return (
            <ApolloConsumer>
                {client => (
                    <div className="d-flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
                        <Card style={{ width: '18rem' }}>
                            <Card.Body>
                                <Card.Title className="text-center">Sign in</Card.Title>
                                <Form onSubmit={(e) => this.handleSubmit(e, client)}>
                                    <Form.Group className="mb-3">
                                        <Form.Control
                                            type="email"
                                            placeholder="Enter email"
                                            value={this.state.email}
                                            onChange={e => this.setState({ email: e.target.value })}
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3">
                                        <Form.Control
                                            type="password"
                                            placeholder="Password"
                                            value={this.state.password}
                                            onChange={e => this.setState({ password: e.target.value })}
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3 d-flex justify-content-between">
                                        <Button variant="link" onClick={() => { /* Passwort vergessen Logik */ }}>
                                            Forgot Password?
                                        </Button>
                                    </Form.Group>

                                    <div className="d-grid gap-2 mb-3">
                                        <Button variant="primary" type="submit">
                                            Sign in
                                        </Button>
                                        <Button variant="secondary" onClick={() => { /* Registrieren logik*/ }}>
                                            Sign up
                                        </Button>
                                    </div>
                                </Form>
                            </Card.Body>
                        </Card>
                    </div>
                )}
            </ApolloConsumer>
        );
    }
}

export default Login;
