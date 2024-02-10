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

export const REQUEST_PASSWORD_RESET = gql`
    query RequestPasswordReset($email: String!) {
        requestPasswordReset(email: $email) {
            success
            statusCode
            message
            timestamp
        }
    }
`;