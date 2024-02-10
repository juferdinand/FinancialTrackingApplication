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

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={
                    <ApolloProvider client={createApolloClient("http://localdev.de:80/graphql")}>
                        <Login/>
                    </ApolloProvider>}/>
                <Route path='/verify' element={
                    <ApolloProvider client={createApolloClient("http://localdev.de:80/graphql")}>
                        <Verify/>
                    </ApolloProvider>
                }/>
                <Route element={<ProtectedRoute/>}>
                    <Route path='/dashboard' element={<Dashboard/>}/>
                </Route>
            </Routes>
        </BrowserRouter>
    )
}

export default App;
