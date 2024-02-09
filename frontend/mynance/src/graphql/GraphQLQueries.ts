import {gql} from '@apollo/client';

export const LOGIN_USER = gql`
    query LoginUser($email: String!, $password: String!) {
        loginUser(email: $email, password: $password) {
            success
            statusCode
            message
            timestamp
        }
    }
`;

export const REGISTER_USER = gql`
    mutation RegisterUser($email: String!, $firstname:String!, $surname:String!, $password: String!) {
        registerUser(email: $email, firstname: $firstname, surname: $surname password: $password) {
            success
            statusCode
            message
            timestamp
        }
    }
`;