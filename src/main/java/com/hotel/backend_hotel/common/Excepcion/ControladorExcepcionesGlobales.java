package com.hotel.backend_hotel.common.Excepcion;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ControladorExcepcionesGlobales {

    @GraphQlExceptionHandler
    public GraphQLError ManejoDeExcepcionNoEncontrada(ExcepcionNoEncontrada ex){
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.DataFetchingException)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError ManejoDeExcepcionEmpresarial(ExcepcionEmpresarial ex){
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.ValidationError)
                .build();
    }


}
