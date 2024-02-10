import React, { useEffect, useState } from 'react';
import {Link, useNavigate, useSearchParams} from 'react-router-dom';
import { useMutation } from '@apollo/client';
import { VERIFY_USER } from '../graphql/GraphQLMutations'; // Stelle sicher, dass der Pfad korrekt ist

const Verify = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const token = searchParams.get('token');
    const [verificationStatus, setVerificationStatus] = useState('loading'); // 'loading', 'success', 'error'

    const [executeVerify, { loading }] = useMutation(VERIFY_USER, {
        variables: { token },
        onCompleted: (data) => {
            if (data.verifyUser.success) {
                setVerificationStatus('success');
            } else {
                setVerificationStatus('invalid-token');
            }
        },
        onError: () => navigate('/'),
    });

    useEffect(() => {
        if (token) {
            executeVerify();
        } else {
            navigate('/'); // Kein Token, navigiere zur Startseite
        }
    }, [token, executeVerify, navigate]);

    return (
        <div>
            {verificationStatus === 'loading' && <p>Your account is being verified...</p>}
            {verificationStatus === 'success' && (
                <p>Your account has been verified. You can now <a href="/login">log in</a>.</p>
            )}
            {verificationStatus === 'invalid-token' && (
                <p>Invalid or expired token. Please register again. Wanna try again? <Link to="/?register=true">Click here</Link>.</p>
            )}
        </div>
    );
};

export default Verify;
