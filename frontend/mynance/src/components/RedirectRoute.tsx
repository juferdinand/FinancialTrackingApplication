import React from 'react';
import {Outlet, Navigate } from 'react-router-dom';
import { useCookies } from 'react-cookie';
import {jwtDecode} from "jwt-decode";

const RedirectRoute = () => {
    const [cookies] = useCookies(['access']);
    const isExpired = isTokenExpired(cookies.access);

    console.log(isExpired);
    if (!isExpired) {
        return <Navigate to="/dashboard" />;
    }
    return <Outlet />;
}

const isTokenExpired = (token:string) => {
    try {
        const decoded = jwtDecode(token);
        const currentDate = new Date();

        //check if  no expiration date is set
        if (typeof decoded.exp === 'undefined') {
            return true;
        }

        // JWT exp is in seconds
        return decoded.exp * 1000 < currentDate.getTime();
    } catch (error) {
        // if there is an error, the token is invalid
        return true;
    }
};

export default RedirectRoute;