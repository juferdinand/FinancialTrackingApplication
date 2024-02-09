import React from 'react';
import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Login from "./components/Login";
import 'bootstrap/dist/css/bootstrap.min.css';
import {ApolloProvider} from "@apollo/client";
import {createApolloClient} from "./graphql/ApolloClient";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={
                    <ApolloProvider client={createApolloClient("http://localhost:80/graphql")}>
                        <Login/>
                    </ApolloProvider>}/>
            </Routes>
        </BrowserRouter>
    )
}

export default App;
