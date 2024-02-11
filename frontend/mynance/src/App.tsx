import React from 'react';
import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Login from "./components/Login";
import 'bootstrap/dist/css/bootstrap.min.css';
import {ApolloProvider} from "@apollo/client";
import {createApolloClient} from "./graphql/ApolloClient";
import Dashboard from "./components/Dashboard";
import Verify from "./components/Verify";
import ProtectedRoute from "./components/ProtectedRoute";
import RedirectRoute from "./components/RedirectRoute";
import ResetPassword from "./components/ResetPassword";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<RedirectRoute/>}>
                <Route path='/' element={
                    <ApolloProvider client={createApolloClient("https://localdev.de/graphql")}>
                        <Login/>
                    </ApolloProvider>}/>
                </Route>
                <Route path='/reset-password' element={
                    <ApolloProvider client={createApolloClient("https://localdev.de/graphql")}>
                        <ResetPassword/>
                    </ApolloProvider>
                }/>
                <Route path='/verify' element={
                    <ApolloProvider client={createApolloClient("https://localdev.de/graphql")}>
                        <Verify/>
                    </ApolloProvider>
                }/>
                <Route element={<ProtectedRoute/>}>
                    <Route path='/dashboard' element={
                        <ApolloProvider client={createApolloClient("https://localdev.de/graphql")}>
                            <Dashboard/>
                        </ApolloProvider>
                    }/>
                </Route>
            </Routes>
        </BrowserRouter>
    )
}

export default App;
