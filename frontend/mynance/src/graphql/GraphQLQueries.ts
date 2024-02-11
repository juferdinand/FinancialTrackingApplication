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

export const VERIFY_TOKEN_FOR_PASSWORD_RESET = gql`
    query VerifyTokenForPasswordReset($token: String!) {
        verifyTokenForPasswordReset(token: $token) {
            success
            statusCode
            message
            timestamp
        }
    }
`;

export const LOGOUT_USER = gql`
    query LogoutUser {
        logoutUser {
            success
            statusCode
            message
            timestamp
        }
    }
`;