import React, {Component} from 'react';
import {useLazyQuery} from "@apollo/client";
import {LOGOUT_USER} from "../graphql/GraphQLQueries";

const Dashboard = () => {

    const [executeLogout] = useLazyQuery(LOGOUT_USER)

    function logout() {
        executeLogout();
    }

    return (
        <div>
            <h1>Dashboard</h1>
            <button onClick={logout}>Logout</button>
        </div>
    );
}

export default Dashboard;