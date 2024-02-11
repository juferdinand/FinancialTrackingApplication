import {useSearchParams} from "react-router-dom";
import {useState} from "react";
import {useMutation, useQuery} from "@apollo/client";
import {VERIFY_TOKEN_FOR_PASSWORD_RESET} from "../graphql/GraphQLQueries";
import {RESET_PASSWORD} from "../graphql/GraphQLMutations";

const ResetPassword = () => {

    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const [verifyTokenStatus, setVerifyTokenStatus] = useState('loading');
    const [resetPasswordStatus, setResetPasswordStatus] = useState('loading');
    const [password, setPassword] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('');

    useQuery(VERIFY_TOKEN_FOR_PASSWORD_RESET, {
        variables: {token},
        onCompleted: (data) => {
            if (data.verifyTokenForPasswordReset.success) {
                setVerifyTokenStatus('success');
            } else {
                setVerifyTokenStatus('invalid-token');
            }
        },
        onError: () => setVerifyTokenStatus('invalid-token'),
    });

    const [executeResetPassword] = useMutation(RESET_PASSWORD, {
        variables: {token, password},
        onCompleted: (data) => {
            if (data.resetPassword.success) {
                setResetPasswordStatus('success');
            } else {
                setResetPasswordStatus('error');
            }
        },
        onError: () => alert('An error occurred'),
    });

    function resetPassword() {
        if (password.length < 8) {
            alert('Password must be at least 8 characters long');
            return;
        }
        if (password !== repeatPassword) {
            alert('Passwords do not match');
            return;
        }
        setVerifyTokenStatus('empty')
        executeResetPassword();
    }

    return (
        <div>
            {verifyTokenStatus === 'loading' && <p>Verifying token...</p>}
            {verifyTokenStatus === 'success' && (
                <div>
                    <p>Token verified. You can now reset your password.</p>
                    <input type="password" onChange={e => setPassword(e.target.value)}
                           placeholder="New password"/>
                    <input type="password" onChange={e => setRepeatPassword(e.target.value)}
                           placeholder="Repeat new password"/>
                    <button onClick={resetPassword}>Reset password</button>
                </div>
            )}
            {verifyTokenStatus === 'invalid-token' && (
                <p>Invalid or expired token. Please request a new password reset. <a
                    href="/?forgot-password=true">Request new password reset</a></p>
            )}
            {verifyTokenStatus === 'empty' && resetPasswordStatus === 'loading' && (
                <p>Resetting password...</p>
            )}
            {resetPasswordStatus === 'success' && (
                <p>Password reset successful. You can now <a href="/">log in</a>.</p>
            )}
            {resetPasswordStatus === 'error' && (
                <p>An error occurred. Please try again later.</p>
            )}
        </div>
    );
}

export default ResetPassword;