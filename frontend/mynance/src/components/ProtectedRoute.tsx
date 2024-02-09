import React from 'react';
import {Outlet, Navigate } from 'react-router-dom';
import { useCookies } from 'react-cookie';
import {jwtDecode} from "jwt-decode";

const ProtectedRoute = () => {
    //get cookie and check if still valid
    const [cookies] = useCookies(['access']);
    if (isTokenExpired(cookies.access)) {
        return <Navigate to="/" />;
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