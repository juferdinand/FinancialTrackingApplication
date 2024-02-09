import { ApolloClient, InMemoryCache, HttpLink, ApolloLink } from '@apollo/client';
import { onError } from '@apollo/client/link/error';

// Error Handling Link
const errorLink = onError(({ graphQLErrors, networkError }) => {
    if (graphQLErrors)
        graphQLErrors.forEach(({ message, locations, path }) =>
            console.error(`[GraphQL error]: Message: ${message}, Location: ${locations}, Path: ${path}`)
        );
    if (networkError) console.error(`[Network error]: ${networkError}`);
});

// Funktion zur Erstellung des Apollo Clients mit dynamischer URL
export const createApolloClient = (uri: string) => {
    const httpLink = new HttpLink({
        uri,
    });

    const link = ApolloLink.from([errorLink, httpLink]);

    return new ApolloClient({
        link,
        cache: new InMemoryCache(),
    });
};
