import {ApolloClient, InMemoryCache, ApolloLink, createHttpLink} from '@apollo/client';
import { onError } from '@apollo/client/link/error';

// Error Handling Link
const errorLink = onError(({ graphQLErrors, networkError }) => {
    if (graphQLErrors)
        graphQLErrors.forEach(({ message, locations, path }) =>
            console.error(`[GraphQL error]: Message: ${message}, Location: ${locations}, Path: ${path}`)
        );
    if (networkError) console.error(`[Network error]: ${networkError}`);
});

export const createApolloClient = (uri: string) => {
    const httpLink = createHttpLink({
        uri,
        credentials: 'include',
    });

    const link = ApolloLink.from([errorLink, httpLink]);

    return new ApolloClient({
        link,
        cache: new InMemoryCache()
    });
};
