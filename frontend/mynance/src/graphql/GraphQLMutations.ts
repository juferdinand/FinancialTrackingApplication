import { gql } from '@apollo/client';
export const REGISTER_USER = gql `
    mutation RegisterUser($email: String!, $firstname:String!, $surname:String!, $password: String!) {
        registerUser(email: $email, firstname: $firstname, surname: $surname, password: $password) {
            success
            statusCode
            message
            timestamp
        }
    }
`;

export const VERIFY_USER = gql `
    mutation VerifyUser($token: String!) {
        verifyUser(token: $token) {
            success
            statusCode
            message
            timestamp
        }
    }
`;