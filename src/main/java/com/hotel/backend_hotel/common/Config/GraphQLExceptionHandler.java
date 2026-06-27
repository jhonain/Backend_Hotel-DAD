package com.hotel.backend_hotel.common.Config;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof AccessDeniedException) {
            return GraphqlErrorBuilder.newError(env)
                    .message("No tienes los permisos necesarios para realizar esta acción.")
                    .errorType(ErrorType.FORBIDDEN) // Mapea a código de estado 403 en GraphQL
                    .build();
        }
        return null; // Permite que otras excepciones sigan su flujo normal
    }
}
